package com.chasetech.pcount;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chasetech.pcount.Assortment.AssortmentActivity;
import com.chasetech.pcount.adapter.BranchListViewAdapter;
import com.chasetech.pcount.database.SQLLib;
import com.chasetech.pcount.database.SQLiteHelper;
import com.chasetech.pcount.library.Location;
import com.chasetech.pcount.library.MainLibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ULTRABOOK on 10/27/2015.
 */
public class DateLocPickerActivity extends Activity {

    private ProgressDialog pDL;
    private ArrayList<Location> myArrayListLocation = new ArrayList<>();
    private BranchListViewAdapter mBranchListViewAdapter;
    private int mCurrentBranchSelected;
    private String mCurrentBranchNameSelected;
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

 /*       HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                NavUtils.navigateUpFromSameTask(DateLocPickerActivity.this);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();*/

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
                                MainLibrary.gCurrentBranchSelected = loc.locationCode;
                                MainLibrary.gCurrentBranchNameSelected = loc.locationName;

                                if(loc.locationName.toUpperCase().contains("711")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.SEVEN_ELEVEN;
                                }
                                if(loc.locationName.toUpperCase().contains("MINISTOP")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MINISTOP;
                                }
                                if(loc.locationName.toUpperCase().contains("MERCURY")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.MERCURY_DRUG;
                                }
                                if(loc.locationName.toUpperCase().contains("FAMILY")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.FAMILY_MART;
                                }
                                if(loc.locationName.toUpperCase().contains("LAWSON")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.LAWSON;
                                }
                                if(loc.locationName.toUpperCase().contains("ALFAMART")){
                                    MainLibrary.eStore = MainLibrary.STORE_TYPES.ALFAMART;
                                }

                                startActivityForResult(intent, 999);
                                break;
                            case 1: // ASSORTMENT
                                MainLibrary.isAssortmentMode = true;
                                Location location = myArrayListLocation.get(nPosition);
                                Intent assortintent = new Intent(DateLocPickerActivity.this, AssortmentActivity.class);

                                MainLibrary.gStrCurrentDate = mStrCurrentDate;
                                MainLibrary.selectedMonth = strSelectedMonth;
                                MainLibrary.gCurrentBranchSelected = location.locationCode;
                                MainLibrary.gCurrentBranchNameSelected = location.locationName;

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
                            MainLibrary.gCurrentBranchSelected = location.locationCode;
                            MainLibrary.gCurrentBranchNameSelected = location.locationName;
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
            StringBuilder text = new StringBuilder();
            try{

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                Boolean inid = false;
                int type;

                while ((line = br.readLine()) != null) {
                    String[] itemRefs = line.split(",");

                    Log.e("TAG", line);

                    if (!inid) {
                        inid = true;
                        continue;
                    }

                    if (itemRefs[0].startsWith("\"")) {
                        continue;
                    }

                    db.insertToBranch(Integer.parseInt(itemRefs[0]), itemRefs[2].replace("\"", ""));

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
/*            if ((cursorTrans.getCount() != 0) || (cursorAssortTrans.getCount() != 0)) {
                cursorTrans.moveToFirst();
                cursorAssortTrans.moveToFirst();
                type = 1;

                if(cursorTrans.getCount() > 0) {
                    if (cursorTrans.getInt(cursorTrans.getColumnIndex(SQLiteHelper.COLUMN_TRANSACTION_LPOSTED)) != 0) {
                        type = 2;
                    }
                }

                if(cursorAssortTrans.getCount() > 0) {
                    if (cursorAssortTrans.getInt(cursorAssortTrans.getColumnIndex(SQLiteHelper.COLUMN_TRANSACTION_ASSORT_LPOSTED)) != 0) {
                        type = 2;
                    }
                }
            }*/

            if (cursorTrans.getCount() != 0) {
                cursorTrans.moveToFirst();
                type = 1;
                if(cursorTrans.getCount() > 0) {
                    if (cursorTrans.getInt(cursorTrans.getColumnIndex(SQLiteHelper.COLUMN_TRANSACTION_LPOSTED)) != 0) {
                        type = 2;
                    }
                }
            }

            myArrayListLocation.add(new Location(Integer.parseInt(locationCode), locationName, type, 5));
            cursorTrans.close();
            cursor.moveToNext();
        }

        mBranchListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder logoutdialog = new AlertDialog.Builder(DateLocPickerActivity.this);
        logoutdialog.setTitle("Log Out");
        logoutdialog.setMessage("Are you sure you want to log out?");
        logoutdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        logoutdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });

        logoutdialog.show();
    }

}
