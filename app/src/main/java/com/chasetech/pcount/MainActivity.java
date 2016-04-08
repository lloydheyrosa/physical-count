package com.chasetech.pcount;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chasetech.pcount.Woosim.WoosimPrinter;
import com.chasetech.pcount.autoupdate.AutoUpdateApk;
import com.chasetech.pcount.database.SQLLib;
import com.chasetech.pcount.database.SQLiteHelper;
import com.chasetech.pcount.library.HomeWatcher;
import com.chasetech.pcount.library.MainLibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private int aLinesMain = 0;
    private String[] arrayMain;

    PowerManager powerman;
    PowerManager.WakeLock wlStayAwake;

    String password;
    String username = "";
    String urlDownload;
    String urlGet;
    String urlLogout;
    String urlConnect;
    String urlDownloadperFile;

    private ProgressDialog progressDL;
    private SQLLib db;
    SQLLib sql;
    SQLiteHelper sqlLite;

    int STORE_TXT = 1;
    int ITEMS_TXT = 2;
    int ASSORTMENT_TXT = 3;
    int[] ARRAY_LISTS = {STORE_TXT, ITEMS_TXT, ASSORTMENT_TXT};
    private static final int BUFFER_SIZE = 4096;

    File FDIR;
    File dlPath;
    File appFolder;

    boolean isNew = false;

    ArrayList<String> arrPrnFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoUpdateApk autoupdate = new AutoUpdateApk(this);

        MainLibrary.gStrDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);;

        PackageManager pm = this.getPackageManager();
        String packageName = this.getPackageName();
        int flags = PackageManager.GET_PERMISSIONS;
        PackageInfo pmInfo = null;
        String versionName = "";
        arrPrnFiles = new ArrayList<>();
        isNew = true;

        try {
            pmInfo = pm.getPackageInfo(packageName, flags);
            versionName = pmInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException nex) {
            nex.printStackTrace();
            Log.e("NameNotFoundException", nex.getMessage());
        }

        MainLibrary.LoadFolders();


        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setVisibility(View.GONE);

        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();


        sqlLite = new SQLiteHelper(this);
        db = new SQLLib(this);

        urlConnect = MainLibrary.API_URL;
        urlGet =  urlConnect + "/api/auth?";
        urlLogout =  urlConnect + "/api/logout?";
        urlDownload = urlConnect + "/api/download?";

        appFolder = new File(getExternalFilesDir(null),""); //getExternalFilesDir(null)
        dlPath =  new File(appFolder, "Downloads");
        dlPath.mkdirs();

        powerman = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wlStayAwake = powerman.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "wakelocktag");

        final Button btnLogin = (Button) this.findViewById(R.id.btnLogin);
        final EditText editUsername = (EditText) this.findViewById(R.id.edit_username);
        final EditText editPassword = (EditText) this.findViewById(R.id.edit_password);
        final TextView tvwVersion = (TextView) findViewById(R.id.tvwVersion);

        tvwVersion.setText("v. " + versionName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username = editUsername.getText().toString(); //"PCN10";
                password = editPassword.getText().toString(); //"password"; //

                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                wlStayAwake.acquire();
                new AsyncGetUser(false).execute();
            }
        });

        MainLibrary.sprefUsers = getSharedPreferences(getString(R.string.pcount_sharedprefKey), Context.MODE_PRIVATE);

        boolean isLoggedIn = MainLibrary.sprefUsers.getBoolean(getString(R.string.logged_pref_key), false);

        if(isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, DateLocPickerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        urlDownload = urlConnect + "/api/download?";
        super.onResume();
    }

    public class AsyncGetUser extends AsyncTask<Void, Void, String> {

        private boolean isLoggedOut = false;

        public AsyncGetUser(boolean logout)
        {
            this.isLoggedOut = logout;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDL = ProgressDialog.show(MainActivity.this, "", "Logging in. Please Wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try{

                String urlfinal = urlGet + "email=" + username + "&pwd=" + password + "&device_id=" + MainLibrary.gStrDeviceId;
                if(isLoggedOut)
                    urlfinal = urlLogout + "email=" + username + "&pwd=" + password + "&device_id=" + MainLibrary.gStrDeviceId;

                URL url = new URL(urlfinal);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    urlConnection.disconnect();
                    return stringBuilder.toString();
                }finally {

                }

            } catch(Exception e){
                progressDL.dismiss();
                Log.e("ERROR", e.getMessage(), e);
                final String ex = e.getMessage();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity.this,"Cannot Connect to the Internet: " + ex,Toast.LENGTH_SHORT).show();
                    }
                });

                progressDL.dismiss();
                return null;

            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            try {
                progressDL.dismiss();

                JSONObject data = new JSONObject(response);
                String usercode = data.getString("id");
                String name = data.getString("name");
                String hash = data.getString("hash");
               // Boolean lupdate = data.getBoolean("update"); //if with update on data or non
                db.TruncateTable("user");
                int uid = Integer.parseInt(usercode);
                db.insertToUser(uid, name, hash);
                urlDownload = urlDownload + "id=" + usercode;
                urlDownload = urlDownload + "id=" + usercode;
                MainLibrary.gStrCurrentUserID = Integer.parseInt(usercode);
                MainLibrary.gStrCurrentUserName = name;

                SharedPreferences.Editor spEditor = MainLibrary.sprefUsers.edit();
                spEditor.putBoolean(getString(R.string.logged_pref_key), true);
                spEditor.putInt(getString(R.string.pref_userid), Integer.parseInt(usercode));
                spEditor.putString(getString(R.string.pref_username), name);
                spEditor.commit();

                if (MainLibrary.gLUpdate) {

                    new AsyncDownloadFile().execute();

                }else{

                    final EditText editUsername = (EditText) MainActivity.this.findViewById(R.id.edit_username);
                    final EditText editPassword = (EditText) MainActivity.this.findViewById(R.id.edit_password);

                    editUsername.setText("");
                    editPassword.setText("");

                    Intent intent = new Intent(MainActivity.this, DateLocPickerActivity.class);
                    startActivity(intent);
                }
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                try {
                   JSONObject data = new JSONObject(response);
                    String msg = data.getString("msg");
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
                catch (Exception e) { }
            }

        }
    }

    public class GetPrnFiles extends AsyncTask<Void, Void, Boolean> {

        String response;

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(MainActivity.this, "", "Downloading required items.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean bReturn = false;

            try {
                String urlfinal = MainLibrary.API_GET_PRNFILENAMES;
                URL url = new URL(urlfinal);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }

                try {
                    JSONObject data = new JSONObject(response);
                    if(data != null) {
                        String msg = data.getString("msg");
                        JSONArray jArray = data.getJSONArray("files");

                        String saveDir = Uri.fromFile(MainLibrary.PRN_FOLDER).getPath();

                        // DOWNLOAD PRN FILES
                        URL urlDownload = null;
                        if(jArray != null) {
                            for (int i = 0; i < jArray.length(); i++) {
                                String prnfile = jArray.getString(i);

                                String strUrl = MainLibrary.API_GET_PRNFILES + prnfile;

                                urlDownload = new URL(strUrl);
                                HttpURLConnection httpConn = (HttpURLConnection) urlDownload.openConnection();
                                final int responseConde = httpConn.getResponseCode();
                                if(responseConde == HttpURLConnection.HTTP_OK) {
                                    String fileName = "";
                                    String disposition = httpConn.getHeaderField("Content-Disposition");

                                    if (disposition != null) {
                                        // extracts file name from header field
                                        int index = disposition.indexOf("filename=");
                                        if (index > 0) {
                                            fileName = disposition.substring(index + 10,
                                                    disposition.length() - 1);
                                        }
                                    } else {
                                        // extracts file name from URL
                                        fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1,
                                                strUrl.length());
                                    }

                                    String saveFilePath = saveDir + File.separator + fileName;
                                    File fprn = new File(saveFilePath);
                                    if(fprn.exists()) fprn.delete();

                                    InputStream inputStream = httpConn.getInputStream();
                                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                                    int bytesRead = -1;
                                    byte[] buffer = new byte[BUFFER_SIZE];

                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }

                                    outputStream.close();
                                    inputStream.close();
                                }
                            }
                        }
                        bReturn = true;
                    }
                    else response = "No response returned.";
                }
                catch (JSONException jex) {
                    Log.e("JSONException", jex.getMessage());
                    Toast.makeText(MainActivity.this, jex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            catch(UnknownHostException e) {
                Log.e("UnknownHostException", e.getMessage(), e);
                response = e.getMessage();
            }
            catch(MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage(), e);
                response = e.getMessage();
            }
            catch(IOException e) {
                Log.e("IOException", e.getMessage(), e);
                response = e.getMessage();
            }

            return bReturn;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDL.dismiss();
            if(!aBoolean) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, DateLocPickerActivity.class);
            intent.putExtra("lupdate", MainLibrary.gLUpdate);
            startActivity(intent);
        }
    }

    // DOWNLOADING FILE
    public class AsyncDownloadFile extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDL = ProgressDialog.show(MainActivity.this, "", "Downloading File ....", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String saveDir = Environment.getExternalStorageDirectory() + File.separator; //Uri.fromFile(dlPath).getPath(); //

            try{
                for (int type : ARRAY_LISTS) {
                    urlDownloadperFile = urlDownload + "&type=" + type;
                    URL url = new URL(urlDownloadperFile);
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setReadTimeout(10000 /* milliseconds */);
                    httpConn.setConnectTimeout(15000 /* milliseconds */);

                    httpConn.setRequestMethod("GET");
                    httpConn.setDoInput(true);

                    httpConn.connect();
                    final int responseCode = httpConn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String fileName = "";
                        String disposition = httpConn.getHeaderField("Content-Disposition");
                        String contentType = httpConn.getContentType();
                        int contentLength = httpConn.getContentLength();
                        if (disposition != null) {
                            // extracts file name from header field
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                fileName = disposition.substring(index + 10,
                                        disposition.length() - 1);
                            }
                        } else {
                            // extracts file name from URL
                            fileName = urlDownload.substring(urlDownload.lastIndexOf("/") + 1,
                                    urlDownload.length());
                        }

/*                        //MERVIN

                        InputStream is = null;
                        int len = 2000;

                        is = httpConn.getInputStream();
                        String contentAsString = readIt(is, len);
                        Log.d("MERVINDIZON",contentAsString);
                        is.close();

                        //MERVIN*/

                        InputStream inputStream = null;
                        inputStream = httpConn.getInputStream();

                        String saveFilePath = saveDir + File.separator + fileName;

                        File fileStore = new File(dlPath, fileName);
                        if (fileStore.exists())
                        {
                            fileStore.delete();
                        }
                        FDIR = fileStore;

                        // opens an output stream to save into file
                        FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                        int bytesRead = -1;
                        byte[] buffer = new byte[4 * 1024];


                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.flush();
                        outputStream.close();

                        inputStream.close();

                        System.gc();

                        httpConn.disconnect();

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Response:" + responseCode,Toast.LENGTH_SHORT ).show();
                            }
                        });
                    }

                }
            }
            catch(IOException e) {
                e.printStackTrace();
                Log.e("IOException", e.getMessage());
            }
            catch (NullPointerException nex) {
                nex.printStackTrace();
                Log.e("NullPointerException", nex.getMessage());
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDL.dismiss();

            final EditText editUsername = (EditText) MainActivity.this.findViewById(R.id.edit_username);
            final EditText editPassword = (EditText) MainActivity.this.findViewById(R.id.edit_password);

            editUsername.setText("");
            editPassword.setText("");

            new GetPrnFiles().execute();
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}