package com.chasetech.pcount;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Environment;

import com.chasetech.pcount.database.SQLLib;
import com.chasetech.pcount.database.SQLiteHelper;
import com.chasetech.pcount.library.HomeWatcher;
import com.chasetech.pcount.library.MainLibrary;
import com.simplify.ink.InkView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static com.chasetech.pcount.R.color.white;
import android.content.Context;
import android.content.ContextWrapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by ULTRABOOK on 10/21/2015.
 */
public class CaptureSignatureActivity extends AppCompatActivity{

    private static final String SAMPLE_DB_NAME = "UPcDb";
    private static final String SUCCESS_RESPONSE = "Success";
    private SQLLib db;
    String urlConnect;
    private ProgressDialog progressDL;
    File dlPath;
    File appFolder;
    String filetoSend1 = "";
    String filetoSend2 = "";
    String[] filetoSend = {filetoSend1 , filetoSend2};

    String mImageName = "";

    private int currentBranchSelected;
    private String currentDateSelected;
    private String DcurrentDateSelected;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_signature);

        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                NavUtils.navigateUpFromSameTask(CaptureSignatureActivity.this);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

/*        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);*/

        getSupportActionBar().setTitle(MainLibrary.gCurrentBranchNameSelected + " - SIGN CAPTURE");

        currentBranchSelected = getIntent().getExtras().getInt("location");
        currentDateSelected = getIntent().getExtras().getString("datepick");
        DcurrentDateSelected = currentDateSelected.replace("-", "");

        db = new SQLLib(CaptureSignatureActivity.this);
        db.open();

//        Cursor dbSum = db.queryData("select count(*) as ctr, sum(so) as so, sum(fso) as fso from trans where so > 0 and storeid = "
//                + String.valueOf(MainLibrary.gCurrentBranchSelected) + " and date = '" + MainLibrary.gStrCurrentDate + "'");
        Cursor dbSum = null;
        if(MainLibrary.isAssortmentMode) {
            dbSum = db.queryData("select * from " + SQLiteHelper.TABLE_TRANSACTION_ASSORT + " where storeid = "
                    + String.valueOf(MainLibrary.gCurrentBranchSelected) + " and date = '" + MainLibrary.gStrCurrentDate + "'");
        }
        else {
            dbSum = db.queryData("select * from " + SQLiteHelper.TABLE_TRANSACTION + " where storeid = "
                    + String.valueOf(MainLibrary.gCurrentBranchSelected) + " and date = '" + MainLibrary.gStrCurrentDate + "'");
        }
        dbSum.moveToFirst();

        int totso = 0,totfso = 0, totig = 0, totei = 0;

        while (!dbSum.isAfterLast()) {

            int sapc = dbSum.getInt(dbSum.getColumnIndex("sapc"));
            int whpc = dbSum.getInt(dbSum.getColumnIndex("whpc"));
            int whcs = dbSum.getInt(dbSum.getColumnIndex("whcs"));
            int ig = dbSum.getInt(dbSum.getColumnIndex("ig"));
            int conversion = dbSum.getInt(dbSum.getColumnIndex("conversion"));

            int so = dbSum.getInt(dbSum.getColumnIndex("so"));
            int fso = dbSum.getInt(dbSum.getColumnIndex("fso"));

            if (so > 0) {
                totso = totso + 1; //so; &&former SO Count, now Sku count with SO
            }
            //if (sapc == 0 && whpc == 0 && whcs == 0) continue;

            totei = totei + sapc + whpc + (whcs * conversion);
            totig = totig + ig;
            totfso = totfso + fso;

            dbSum.moveToNext();
        }

        final String stotsku = String.valueOf(dbSum.getCount());
        final String stotso = String.valueOf(totso);
        final String stotfso = String.valueOf(totfso);
        final String stotei = String.valueOf(totei);
        final String stotig = String.valueOf(totig);


        urlConnect = MainLibrary.API_URL;
        appFolder = Environment.getExternalStorageDirectory() ; //new File(getExternalFilesDir(null),"");
        dlPath = new File(appFolder, ""); //

        final InkView inkSign = (InkView) findViewById(R.id.inkSignature);
        inkSign.setColor(getResources().getColor(android.R.color.black));
        inkSign.setMinStrokeWidth(1.5f);
        inkSign.setMaxStrokeWidth(6f);

        final Button btnClear = (Button) findViewById(R.id.btnClear);
        final Button btnSubmitSign = (Button) findViewById(R.id.btnSubmitSign);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkSign.clear();
            }
        });

        btnSubmitSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(CaptureSignatureActivity.this)
                        .setTitle("Transaction Summary")
                        .setMessage("Are you sure you want to post?\nTotal SKUs for Posting = " + stotsku + "\nTotal EI (PC)= " + stotei +
                                "\nTotal SKU with SO = " + stotso + "\nTotal FSO Qty (PC)= " + stotfso)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bitmap inkSignature = inkSign.getBitmap(getResources().getColor(white));

                                Cursor tmp = db.queryData("select uid from user");
                                tmp.moveToLast();
                                String uid = tmp.getString(tmp.getColumnIndex("uid"));

                                mImageName = "IM_" + String.valueOf(currentBranchSelected) + "-" + uid + "-" + currentDateSelected + ".jpg";
                                filetoSend1 = mImageName;
                                File mediastorage = new File(Environment.getExternalStorageDirectory(), "");
                                File pictureFile = new File(mediastorage.getPath() + File.separator + mImageName);

                                try {
                                    FileOutputStream fos = new FileOutputStream(pictureFile);
                                    inkSignature.compress(Bitmap.CompressFormat.PNG, 90, fos);
                                    fos.close();
                                    ExportTabletoCSV();
                                    new AsyncPostFiles().execute();
                                } catch (FileNotFoundException a) {
                                    Toast.makeText(CaptureSignatureActivity.this, a.getMessage(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    Toast.makeText(CaptureSignatureActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }

    private void exportDb(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        File dataBaseFile = getDatabasePath("UPcDb");
        String currentDBPath = dataBaseFile.toString();
        String backupDBPath = SAMPLE_DB_NAME;
        File currentDB = dataBaseFile; //new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void ExportTabletoCSV(){
        File myFile;

        try {
            String datetimeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

            Cursor tmp = db.queryData("select uid from user");
            tmp.moveToLast();
            String uid = tmp.getString(tmp.getColumnIndex("uid"));
            String mFileName = String.valueOf(currentBranchSelected) + "-" + uid + "-" + currentDateSelected +".csv";
            filetoSend2 = mFileName;
            myFile = new File (Environment.getExternalStorageDirectory() + File.separator + mFileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
/*            myOutWriter.append("WEBID;SAPC;WHPC;WHCS;SO;FSO;FSOVALUE;OTHERCODE;MULTIPLIER;IG;CONVERSION"); //remove posting date, insert multiplier after othercode
            myOutWriter.append("\n");*/

//            Cursor c = db.queryData("SELECT webid,sapc,whpc,whcs,so,fso,barcode FROM trans where " +
//                    "storeid = " + String.valueOf(currentBranchSelected) + " and date = '" + currentDateSelected + "'" , null);

            Cursor cursExport = null;
            if(MainLibrary.isAssortmentMode) {
                cursExport = db.queryData("SELECT webid,sapc,whpc,whcs,so,fso,barcode,fsovalue,ig,conversion,multi FROM " + SQLiteHelper.TABLE_TRANSACTION_ASSORT + " where storeid = " + String.valueOf(currentBranchSelected) + " and date = '" + currentDateSelected +
                        "' and userid = " + MainLibrary.gStrCurrentUserID , null);
            }
            else {
                cursExport = db.queryData("SELECT webid,sapc,whpc,whcs,so,fso,barcode,fsovalue,ig,conversion,multi FROM " + SQLiteHelper.TABLE_TRANSACTION + " where storeid = " + String.valueOf(currentBranchSelected) + " and date = '" + currentDateSelected +
                        "' and userid = " + MainLibrary.gStrCurrentUserID , null);
            }

            if (cursExport != null) {
                if (cursExport.moveToFirst()) {
                    do {
                        String webid = cursExport.getString(cursExport.getColumnIndex("webid"));
                        String sapc = cursExport.getString(cursExport.getColumnIndex("sapc"));
                        String whpc = cursExport.getString(cursExport.getColumnIndex("whpc"));
                        String whcs = cursExport.getString(cursExport.getColumnIndex("whcs"));
                        String so = cursExport.getString(cursExport.getColumnIndex("so"));
                        String fso = cursExport.getString(cursExport.getColumnIndex("fso"));
                        String fsovalue = cursExport.getString(cursExport.getColumnIndex("fsovalue"));
                        String fsovalue2 = String.valueOf(Integer.parseInt(fso) * Double.parseDouble(fsovalue));
                        String barcode = cursExport.getString(cursExport.getColumnIndex("barcode"));
                        String ig = cursExport.getString(cursExport.getColumnIndex("ig"));
                        String conv = cursExport.getString(cursExport.getColumnIndex("conversion"));
                        String multi = cursExport.getString(cursExport.getColumnIndex("multi"));

                        myOutWriter.append(webid+";"+sapc+";"+whpc+";"+whcs+";"+so+";"+fso+";"+fsovalue2+";"+barcode+";"+multi
                                +";"+ig+";"+conv); //elapse_Time
                        myOutWriter.append("\n");
                    }
                    while (cursExport.moveToNext());
                }

                cursExport.close();
                myOutWriter.close();
                fOut.close();
            }
        } catch (SQLiteException se)
        {
            Toast.makeText(this, "1" + se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"2" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "3" + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {

        }

    }

    public class AsyncPostFiles extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(CaptureSignatureActivity.this, "", "Sending file....", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            if (!uploadFile(filetoSend2).equals("")) {
                return uploadFile(filetoSend1);
            }

            return "";

 /*           String attachmentName = "data";
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            String response = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;

            String attachmentFileName = filetoSend2 ;//+ String.valueOf(i);
            try {

                FileInputStream fileInputStream = new FileInputStream(new File(dlPath, filetoSend2)); // text file to upload

                HttpURLConnection httpUrlConnection = null;
                URL url = new URL("http://pcount.chasetech.com/api/uploadpcount");
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);

                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(
                        httpUrlConnection.getOutputStream());

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                request.writeBytes(crlf);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    request.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                //I want to send only 8 bit black & white bitmaps
*//*                byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }

            request.write(pixels);*//*

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                request.flush();
                request.close();

                InputStream responseStream = new
                        BufferedInputStream(httpUrlConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                response = stringBuilder.toString();
                responseStream.close();
                httpUrlConnection.disconnect();

                return response;
            }
            catch (final MalformedURLException ex) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaptureSignatureActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                });
            }
            catch (final ProtocolException pex) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaptureSignatureActivity.this,pex.getMessage(),Toast.LENGTH_SHORT).show();
                        pex.printStackTrace();
                    }
                });
            }
            catch (final IOException ioex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CaptureSignatureActivity.this,ioex.getMessage(),Toast.LENGTH_SHORT).show();
                        ioex.printStackTrace();
                    }
                });
            }
            finally {
                return response;
            }*/

        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("")) {
                s = "Error! Please try again.";
                progressDL.dismiss();
                Toast.makeText(CaptureSignatureActivity.this, s, Toast.LENGTH_SHORT).show();
            }else {
                s = "Posting Successful";
                Toast.makeText(CaptureSignatureActivity.this, s, Toast.LENGTH_SHORT).show();
                if(MainLibrary.isAssortmentMode) {
                    db.UpdateRecord(SQLiteHelper.TABLE_TRANSACTION_ASSORT, "date = ? and storeid = ? and userid = ?", new String[]{MainLibrary.gStrCurrentDate, String.valueOf(MainLibrary.gCurrentBranchSelected), String.valueOf(MainLibrary.gStrCurrentUserID)},
                            new String[]{"lposted"}, new String[]{"1"});
                }
                else {
                    db.UpdateRecord(SQLiteHelper.TABLE_TRANSACTION, "date = ? and storeid = ? and userid = ?", new String[]{MainLibrary.gStrCurrentDate, String.valueOf(MainLibrary.gCurrentBranchSelected), String.valueOf(MainLibrary.gStrCurrentUserID)},
                            new String[]{"lposted"}, new String[]{"1"});
                }

                File filenew = new File(dlPath, filetoSend1);
                if (filenew.exists()) {
                    filenew.delete();
                }
                File filenew2 = new File(dlPath, filetoSend2);
                if (filenew2.exists()) {
                    filenew2.delete();
                }
                finish();
                progressDL.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public String uploadFile(String cfile) {

        String attachmentName = "data";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        String strSuccess = "success";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024*1024;

        String attachmentFileName = cfile ;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(dlPath, cfile)); // text file to upload

            HttpURLConnection httpUrlConnection = null;
            if(MainLibrary.isAssortmentMode) {
                if (cfile == mImageName) {
                    URL url = new URL(MainLibrary.API_URL_ASSORTMENT_IMAGE);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                } else {
                    URL url = new URL(MainLibrary.API_URL_ASSORTMENT_POSTING);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                }
            }
            else {
                if (cfile == mImageName) {
                    URL url = new URL(MainLibrary.API_URL_MKL_IMAGE);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                } else {
                    URL url = new URL(MainLibrary.API_URL_MKL_POSTING);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                }
            }

            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                request.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();

            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            responseStream.close();
            httpUrlConnection.disconnect();

        } catch (Exception ex) {

            Log.d("TAG", ex.getMessage());
            strSuccess = "";
        }

        return strSuccess;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
