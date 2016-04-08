package com.chasetech.pcount;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chasetech.pcount.Assortment.AssortmentActivity;
import com.chasetech.pcount.MKL.PCountActivity;
import com.chasetech.pcount.adapter.BranchListViewAdapter;
import com.chasetech.pcount.database.SQLLib;
import com.chasetech.pcount.database.SQLiteHelper;
import com.chasetech.pcount.library.Location;
import com.chasetech.pcount.library.MainLibrary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ULTRABOOK on 10/27/2015.
 */
public class DateLocPickerActivity extends AppCompatActivity {

    private ProgressDialog pDL;
    private ArrayList<Location> myArrayListLocation = new ArrayList<>();
    private BranchListViewAdapter mBranchListViewAdapter;
    private String mStrCurrentDate = "";
    private String strSelectedMonth = "";
    private SQLLib db = new SQLLib(this);
    private Calendar mCurrentDate;
    private EditText editTextDate;
    private AlertDialog mAlertDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        MainLibrary.sprefUsers = getSharedPreferences(getString(R.string.pcount_sharedprefKey), Context.MODE_PRIVATE);

        boolean isLoggedIn = MainLibrary.sprefUsers.getBoolean(getString(R.string.logged_pref_key), false);
        if(isLoggedIn) {
            String username = MainLibrary.sprefUsers.getString(getString(R.string.pref_username), MainLibrary.gStrCurrentUserName);
            int userid = MainLibrary.sprefUsers.getInt(getString(R.string.pref_userid), MainLibrary.gStrCurrentUserID);

            MainLibrary.gStrCurrentUserID = userid;
            MainLibrary.gStrCurrentUserName = username;
        }
        else {
            Intent intent = new Intent(DateLocPickerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getSupportActionBar().setTitle("USER: " + MainLibrary.gStrCurrentUserName);

        db.open();

        mCurrentDate = Calendar.getInstance();
        mStrCurrentDate = MainLibrary.dateFormatter.format(mCurrentDate.getTime());
        strSelectedMonth = MainLibrary.monthFormatter.format(mCurrentDate.getTime());

        editTextDate = (EditText) findViewById(R.id.editTextDate);

        editTextDate.setText(mStrCurrentDate);

        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(DateLocPickerActivity.this, dateSetListener, mCurrentDate
                        .get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                        mCurrentDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listViewBranch);

        mBranchListViewAdapter = new BranchListViewAdapter(this, myArrayListLocation);
        listView.setAdapter(mBranchListViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(DateLocPickerActivity.this, "Please turn on the bluetooth to proceed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int nPosition = position;

                final AlertDialog.Builder mModuleDialog = new AlertDialog.Builder(DateLocPickerActivity.this);
                mModuleDialog.setTitle("Select a module");
                mModuleDialog.setCancelable(true);

                mModuleDialog.setItems(MainLibrary.aModules, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: // NORMAL
                                MainLibrary.isAssortmentMode = false;
                                Location loc = myArrayListLocation.get(nPosition);
                                Intent intent = new Intent(DateLocPickerActivity.this, PCountActivity.class);

                                MainLibrary.gStrCurrentDate = mStrCurrentDate;
                                MainLibrary.gSelectedLocation = loc;

                                if(loc.locationName.toUpperCase().contains("711")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.SEVEN_ELEVEN;
                                }
                                else if(loc.locationName.toUpperCase().contains("MINISTOP")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MINISTOP;
                                }
                                else if(loc.locationName.toUpperCase().contains("MERCURY") || loc.locationName.toUpperCase().contains("MDC")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MERCURY_DRUG;
                                }
                                else if(loc.locationName.toUpperCase().contains("FAMILY")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.FAMILY_MART;
                                }
                                else if(loc.locationName.toUpperCase().contains("LAWSON")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.LAWSON;
                                }
                                else if(loc.locationName.toUpperCase().contains("ALFAMART")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.ALFAMART;
                                }
                                else MainLibrary.eStore = MainLibrary.STORE_TYPES.DEFAULT;

                                startActivityForResult(intent, 999);
                                break;
                            case 1: // ASSORTMENT
                                MainLibrary.isAssortmentMode = true;
                                Location location = myArrayListLocation.get(nPosition);
                                Intent assortintent = new Intent(DateLocPickerActivity.this, AssortmentActivity.class);

                                MainLibrary.gStrCurrentDate = mStrCurrentDate;
                                MainLibrary.selectedMonth = strSelectedMonth;
                                MainLibrary.gSelectedLocation = location;

                                if(location.locationName.toUpperCase().contains("711")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.SEVEN_ELEVEN;
                                }
                                if(location.locationName.toUpperCase().contains("MINISTOP")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MINISTOP;
                                }
                                if(location.locationName.toUpperCase().contains("MERCURY")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MERCURY_DRUG;
                                }
                                if(location.locationName.toUpperCase().contains("FAMILY")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.FAMILY_MART;
                                }
                                if(location.locationName.toUpperCase().contains("LAWSON")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.LAWSON;
                                }
                                if(location.locationName.toUpperCase().contains("ALFAMART")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.ALFAMART;
                                }

                                startActivityForResult(assortintent, 999);
                                break;
                            default:
                                MainLibrary.isAssortmentMode = false;
                                break;
                        }
                    }
                });

                if(!CheckAssortmentOnSelectedMonth(myArrayListLocation.get(nPosition))) {
                    mAlertDialog = new AlertDialog.Builder(DateLocPickerActivity.this).create();
                    mAlertDialog.setTitle("Assortment");
                    mAlertDialog.setMessage("You didn't transact for assortment.\nDo you want to perform assortment transaction?");
                    mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                            mModuleDialog.show();
                        }
                    });
                    mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainLibrary.isAssortmentMode = true;
                            Location location = myArrayListLocation.get(nPosition);
                            Intent assortintent = new Intent(DateLocPickerActivity.this, AssortmentActivity.class);

                            MainLibrary.gStrCurrentDate = mStrCurrentDate;
                            MainLibrary.gSelectedLocation = location;
                            MainLibrary.selectedMonth = strSelectedMonth;

                            if(location.locationName.contains("711")){
                                MainLibrary.eStore = MainLibrary.STORE_TYPES.SEVEN_ELEVEN;
                            }
                            if(location.locationName.contains("MINISTOP")){
                                MainLibrary.eStore = MainLibrary.STORE_TYPES.MINISTOP;
                            }
                            if(location.locationName.contains("MERCURY")){
                                MainLibrary.eStore = MainLibrary.STORE_TYPES.MERCURY_DRUG;
                            }
                            if(location.locationName.contains("FAMILY")){
                                MainLibrary.eStore = MainLibrary.STORE_TYPES.FAMILY_MART;
                            }

                            startActivityForResult(assortintent, 999);
                        }
                    });
                    mAlertDialog.show();
                    return;
                }
                mModuleDialog.show();
            }
        });

        if (MainLibrary.gLUpdate) {
            new TaskProcessLocationData().execute();
        } else {
            GetLocationArrayList();
        }

    }

    //CHECK IF ASSORTMENT IS MADE ON CURRENT MONTH
    private boolean CheckAssortmentOnSelectedMonth(Location seletedLoc) {
        boolean bReturn = false;

/*        Cursor cursCheck1 = db.GetDataCursor(SQLiteHelper.TABLE_TRANSACTION_ASSORT, SQLiteHelper.COLUMN_TRANSACTION_ASSORT_MONTH + " = '" + strSelectedMonth
                        + "' AND " + SQLiteHelper.COLUMN_TRANSACTION_ASSORT_STOREID + " = '" + seletedLoc.locationCode + "' AND " + SQLiteHelper.COLUMN_TRANSACTION_ASSORT_LPOSTED + " = '0'");
        cursCheck1.moveToFirst();
        if(cursCheck1.getCount() > 0) return 1;*/

        Cursor cursCheck = db.GetDataCursor(SQLiteHelper.TABLE_TRANSACTION_ASSORT, SQLiteHelper.COLUMN_TRANSACTION_ASSORT_MONTH + " = '" + strSelectedMonth + "' AND " + SQLiteHelper.COLUMN_TRANSACTION_ASSORT_LPOSTED + " = '1' AND " + SQLiteHelper.COLUMN_TRANSACTION_ASSORT_STOREID + " = '" + seletedLoc.locationCode + "'");
        cursCheck.moveToFirst();
        if(cursCheck.getCount() > 0) {
            bReturn = true;
        }
        cursCheck.close();

        return bReturn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999 && resultCode == 888) {
            GetLocationArrayList();
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCurrentDate = Calendar.getInstance();
            mCurrentDate.set(year, monthOfYear, dayOfMonth);
            mStrCurrentDate = MainLibrary.dateFormatter.format(mCurrentDate.getTime());
            strSelectedMonth = MainLibrary.monthFormatter.format(mCurrentDate.getTime());
            editTextDate.setText(mStrCurrentDate);
            GetLocationArrayList();
        }

    };

    public class TaskProcessLocationData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            db.TruncateTable("branch");

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard,"stores.txt");
            try{

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                Boolean inid = false;
                int type;

                while ((line = br.readLine()) != null) {
                    String[] itemRefs = line.split(",");

                    if (!inid) {
                        inid = true;
                        continue;
                    }

                    if (itemRefs[0].startsWith("\"")) {
                        continue;
                    }

                    int branchid = Integer.parseInt(itemRefs[0].trim());
                    String storecode = itemRefs[1].trim();
                    String branchdesc = itemRefs[2].replace("\"", "");
                    int channelID = Integer.parseInt(itemRefs[3].trim());
                    String channelDesc = itemRefs[4].replace("\"", "");
                    String channelArea = itemRefs[5].replace("\"", "");

                    db.insertToBranch(branchid, storecode, branchdesc, channelID, channelDesc, channelArea );
                }

                br.close();

            }
            catch (IOException e) {
                Toast.makeText(DateLocPickerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDL.dismiss();
            GetLocationArrayList();
        }

        @Override
        protected void onPreExecute() {
            pDL = ProgressDialog.show(DateLocPickerActivity.this, "", "Updating Location Masterfile. Please wait...", true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    public void GetLocationArrayList() {

        myArrayListLocation.clear();

        Cursor cursor = db.queryData("select * from branch");

        cursor.moveToFirst();

        int type;

        while (!cursor.isAfterLast()) {

            String locationCode = cursor.getString(cursor.getColumnIndex("bid"));
            String locationName = cursor.getString(cursor.getColumnIndex("bdesc")).replace("\"", "");
            String storeCode = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_BRANCH_storecode)).replace("\"", "");
            int channelId = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_BRANCH_CHANNELID));
            String channelArea = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_BRANCH_CHANNELAREA)).replace("\"", "");
            String channelDesc = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_BRANCH_CHANNELDESC)).replace("\"", "");

/*
            Cursor cursorTrans = db.queryData("select lposted from trans where storeid = " + locationCode
                    + " and date = '" + mStrCurrentDate + "' limit 1");
*/

            Cursor cursorTrans = db.queryData("select lposted from " + SQLiteHelper.TABLE_TRANSACTION + " where storeid = " + locationCode
                    + " and date = '" + mStrCurrentDate + "' and userid = " + String.valueOf(MainLibrary.gStrCurrentUserID) + " limit 1");
            cursorTrans.moveToFirst();

            Cursor cursorAssortTrans = db.queryData("select lposted from " + SQLiteHelper.TABLE_TRANSACTION_ASSORT + " where storeid = " + locationCode
                    + " and date = '" + mStrCurrentDate + "' and userid = " + String.valueOf(MainLibrary.gStrCurrentUserID) + " limit 1");
            cursorAssortTrans.moveToFirst();

            type = 0;


            if (cursorTrans.getCount() != 0) {
                cursorTrans.moveToFirst();
                type = 1;
                if(cursorTrans.getCount() > 0) {
                    if (cursorTrans.getInt(cursorTrans.getColumnIndex(SQLiteHelper.COLUMN_TRANSACTION_LPOSTED)) != 0) {
                        type = 2;
                    }
                }
            }

            myArrayListLocation.add(new Location(Integer.parseInt(locationCode), storeCode, locationName, type, 5, channelId, channelDesc, channelArea));
            cursorTrans.close();
            cursor.moveToNext();
        }

        mBranchListViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        final int REQUEST_ENABLE_BT = 3;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(DateLocPickerActivity.this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_set_printer:
                Intent intentSettings = new Intent(DateLocPickerActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder logoutdialog = new AlertDialog.Builder(DateLocPickerActivity.this);
        logoutdialog.setTitle("Log Out");
        logoutdialog.setMessage("Are you sure you want to log out?");
        logoutdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        logoutdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor spEditor = MainLibrary.sprefUsers.edit();
                spEditor.putBoolean(getString(R.string.logged_pref_key), false);
                spEditor.apply();

                dialog.dismiss();

                new UserLogout().execute();
            }
        });

        logoutdialog.show();
    }

    public class UserLogout extends AsyncTask<Void, Void, Boolean> {

        String response;
        String errmsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDL = ProgressDialog.show(DateLocPickerActivity.this, "", "Logging out. Please Wait...", true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean bReturn = false;

            try{

                String urlfinal = MainLibrary.API_URL + "/api/logout?email=" + MainLibrary.gStrCurrentUserName + "&device_id=" + MainLibrary.gStrDeviceId;

                URL url = new URL(urlfinal);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    urlConnection.disconnect();
                    response = stringBuilder.toString();
                    bReturn = true;
                }
                catch (MalformedURLException mex) {
                    mex.printStackTrace();
                    Log.e("MalformedURLException", mex.getMessage());
                    errmsg += "\n" + mex.getMessage();
                }

            } catch(Exception e){
                e.printStackTrace();
                Log.e("Exception", e.getMessage(), e);
                errmsg += "\n" + e.getMessage();
            }
            return bReturn;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            pDL.dismiss();
            Intent intentMain = new Intent(DateLocPickerActivity.this, MainActivity.class);
            if(!success) {
                //Toast.makeText(DateLocPickerActivity.this, errmsg, Toast.LENGTH_LONG).show();
                startActivity(intentMain);
                finish();
                return;
            }

            try {

                JSONObject data = new JSONObject(response);
                String msg = data.getString("msg");

                MainLibrary.gStrCurrentUserID = 0;
                MainLibrary.gStrCurrentUserName = "";

                Toast.makeText(DateLocPickerActivity.this, msg, Toast.LENGTH_SHORT).show();
                startActivity(intentMain);
                finish();
            }
            catch (JSONException jex) {
                jex.printStackTrace();
                Log.e("JSONException", jex.getMessage());
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_printer, menu);
        return true;
    }
}
