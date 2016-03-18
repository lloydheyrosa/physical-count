package com.chasetech.pcount;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import android.os.Environment;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.widget.SimpleCursorAdapter;

import com.chasetech.pcount.adapter.PCountListViewAdapter;
import com.chasetech.pcount.adapter.ReportListViewAdapter;
import com.chasetech.pcount.database.SQLLib;

import com.chasetech.pcount.database.SQLiteHelper;
import com.chasetech.pcount.Assortment.Assortment;
import com.chasetech.pcount.library.BPrinter;
import com.chasetech.pcount.library.HomeWatcher;
import com.chasetech.pcount.library.MainLibrary;
import com.chasetech.pcount.library.PCount;
import com.chasetech.pcount.library.ReportClass;
import com.chasetech.pcount.viewholder.PCountViewHolder;

import org.apache.commons.lang3.StringUtils;
/**
 * Created by ULTRABOOK on 9/28/2015.
 */
public class PCountActivity extends AppCompatActivity {

    private ProgressDialog pDL;
    private AlertDialog mAlertDialog;
    private SQLLib db;

    private final String TAG = "DEBUGGING";

    private ArrayList<PCount> mArrayListPcount = new ArrayList<>();
    private ArrayList<PCount> mArrayListPcountAll = new ArrayList<PCount>();
    private HashMap<String, PCount> hmPcount = new HashMap<>();

    private ListView mListViewPcount = null;

    private PCountListViewAdapter mPCountListViewAdapter;

    private Boolean mReupdatePCount = true;

    private EditText editTextSearch = null;

    private BPrinter Printer;
//    private Boolean lupdate;
    private Boolean lprintall = false;
    private Boolean lprintwithso = false;

    public int len  = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcount);

        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                NavUtils.navigateUpFromSameTask(PCountActivity.this);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

        final TextView lblfso = (TextView) findViewById(R.id.lblfso);
        String fsolbl = MainLibrary.gStrCurrentUserName.substring(3,6) + " Unit";
        lblfso.setText(fsolbl);

        db = new SQLLib(PCountActivity.this);
        db.open();

        Printer = new BPrinter(this);

/*        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setTitle(MainLibrary.gCurrentBranchNameSelected);*/
//        lupdate = getIntent().getExtras().getBoolean("lupdate");

        getSupportActionBar().setTitle(MainLibrary.gCurrentBranchNameSelected + " - MKL");
        new TaskProcessData().execute();

        editTextSearch = (EditText) findViewById(R.id.enter_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = editTextSearch.getText().toString();
                mPCountListViewAdapter.filter(99, search);
            }
        });

        mListViewPcount = (ListView) findViewById(R.id.listViewPcount);
        mPCountListViewAdapter = new PCountListViewAdapter(PCountActivity.this, mArrayListPcount);
        mListViewPcount.setAdapter(mPCountListViewAdapter);

        mListViewPcount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PCountViewHolder viewHolder = (PCountViewHolder) view.getTag();

                final PCount selectedPcount = viewHolder.pCount;

                final Dialog dialog = new Dialog(PCountActivity.this, R.style.Transparent);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_sku_details);

                final TextView textViewDesc = (TextView) dialog.findViewById(R.id.textViewDesc);
                final EditText editTextPcs = (EditText) dialog.findViewById(R.id.pcs);
                final EditText editTextWhPcs = (EditText) dialog.findViewById(R.id.whpcs);
                final EditText editTextWhCs = (EditText) dialog.findViewById(R.id.whcs);
                final Button btnQty = (Button) dialog.findViewById(R.id.btnQtyOk);

                textViewDesc.setText(selectedPcount.desc);
                editTextPcs.setText("");
                editTextWhPcs.setText("");
                editTextWhCs.setText("");

                if (selectedPcount.sapc != 0 || selectedPcount.whpc != 0 || selectedPcount.whcs !=0 ) {
                    editTextPcs.setText(String.valueOf(selectedPcount.sapc));
                    editTextWhPcs.setText(String.valueOf(selectedPcount.whpc));
                    editTextWhCs.setText(String.valueOf(selectedPcount.whcs));
                }

                btnQty.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        String inputPcs = editTextPcs.getText().toString();
                        String inputWhPcs = editTextWhPcs.getText().toString();
                        String inputWhcs = editTextWhCs.getText().toString();

/*                        if (inputPcs.isEmpty() && inputWhPcs.isEmpty() && inputWhcs.isEmpty()) {
                            return;
                        }*/

                        if (inputPcs.isEmpty()) {
                            inputPcs = "0";
                        }
                        if (inputWhPcs.isEmpty()) {
                            inputWhPcs = "0";
                        }
                        if (inputWhcs.isEmpty()) {
                            inputWhcs = "0";
                        }

                        int so = selectedPcount.ig - Integer.parseInt(inputPcs)
                                - Integer.parseInt(inputWhPcs) - (Integer.parseInt(inputWhcs) * selectedPcount.conversion);

                        int fso = 0;

                        if ((so % selectedPcount.multi) == 0) {
                            fso = so;
                        }
                        else{
                            fso = so - (so % selectedPcount.multi) + selectedPcount.multi;
                        }

                        if (so <= 0) {    //10/27 for negative values
                            so = 0;
                            fso = 0;
                        }

                        selectedPcount.sapc = Integer.parseInt(inputPcs);
                        selectedPcount.whpc = Integer.parseInt(inputWhPcs);
                        selectedPcount.whcs = Integer.parseInt(inputWhcs);
                        selectedPcount.so = so;
                        selectedPcount.fso = fso;

                        mPCountListViewAdapter.notifyDataSetChanged();

                        new TaskSaveData().execute();
                    }
                });
                dialog.show();
            }
        });
    }


    /** DATA PROCESSING *************************************************************/
    public class TaskProcessData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {

                if (MainLibrary.gLUpdate) {

                    // STORE MKL RECORDS
                    File fTextFile = new File (Environment.getExternalStorageDirectory(), MainLibrary.MKL_TEXTFILE);

                    if (fTextFile.exists() && mReupdatePCount) {
                        db.TruncateTable(SQLiteHelper.TABLE_PCOUNT);

                        BufferedReader br = new BufferedReader(new FileReader(fTextFile));

                        String rQuery = db.getStringBulkInsert(17, SQLiteHelper.TABLE_PCOUNT);
                        db.insertBulktoPcount(rQuery, br);

                        mReupdatePCount = false;
                    }
                }

                mArrayListPcount.clear();

                // SELECTING MKL MASTERFILE
                Cursor cursor = db.queryData("select * from " + SQLiteHelper.TABLE_PCOUNT + " where storeid = " + String.valueOf(MainLibrary.gCurrentBranchSelected));
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    mArrayListPcount.add(new PCount(
                            cursor.getString(cursor.getColumnIndex("barcode")).trim(),
                            cursor.getString(cursor.getColumnIndex("desc")).trim(),
                            cursor.getString(cursor.getColumnIndex("categoryid")).trim(),
                            cursor.getString(cursor.getColumnIndex("brandid")).trim(),
                            cursor.getString(cursor.getColumnIndex("divisionid")).trim(),
                            cursor.getString(cursor.getColumnIndex("subcategoryid")).trim(),
                            cursor.getInt(cursor.getColumnIndex("ig")),
                            cursor.getInt(cursor.getColumnIndex("conversion")),
                            cursor.getDouble(cursor.getColumnIndex("fsovalue")),
                            cursor.getInt(cursor.getColumnIndex("webid")),
                            cursor.getInt(cursor.getColumnIndex("multi"))
                    ));

                    cursor.moveToNext();
                }

                cursor.close();
                mArrayListPcountAll.clear();
                mArrayListPcountAll.addAll(mArrayListPcount);
            }
            catch (IOException e) {
                //LogThis.LogToFile("TaskProcessData_IOException : \n"+e.toString());
                e.printStackTrace();
                Log.e("IOException", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDL.dismiss();
//            Cursor cursor = db.queryData("select * from trans where storeid = " + String.valueOf(MainLibrary.gCurrentBranchSelected) + " and date = '" + MainLibrary.gStrCurrentDate + "'");
            Cursor cursor = db.queryData("select * from " + SQLiteHelper.TABLE_TRANSACTION + " where storeid = " + String.valueOf(MainLibrary.gCurrentBranchSelected) + " and date = '" + MainLibrary.gStrCurrentDate +
                    "' and [userid] = " + MainLibrary.gStrCurrentUserID);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String barcode = cursor.getString(cursor.getColumnIndex("barcode")).trim();

                for (PCount pCount : mArrayListPcount) {
                    if (pCount.barcode.contains(barcode)) {
                        pCount.ig = cursor.getInt(cursor.getColumnIndex("ig"));
                        pCount.conversion = cursor.getInt(cursor.getColumnIndex("conversion"));
                        pCount.sapc = cursor.getInt(cursor.getColumnIndex("sapc"));
                        pCount.whpc = cursor.getInt(cursor.getColumnIndex("whpc"));
                        pCount.whcs = cursor.getInt(cursor.getColumnIndex("whcs"));
                        pCount.so = cursor.getInt(cursor.getColumnIndex("so"));
                        pCount.fso = cursor.getInt(cursor.getColumnIndex("fso"));
                        pCount.multi = cursor.getInt(cursor.getColumnIndex("multi"));
                        hmPcount.put(pCount.barcode, pCount);
                        break;
                    }
                }

                cursor.moveToNext();

            }

            cursor.close();

            mPCountListViewAdapter = new PCountListViewAdapter(PCountActivity.this, mArrayListPcount);
            mListViewPcount.setAdapter(mPCountListViewAdapter);
            mPCountListViewAdapter.notifyDataSetChanged();

//            final int mYear = mCurrentDate.get(Calendar.YEAR);
//            final int mMonth = mCurrentDate.get(Calendar.MONTH);
//            final int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog dpd = new DatePickerDialog(PCountActivity.this,
//                    new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker view, int year,
//                                              int monthOfYear, int dayOfMonth) {
//                            mCurrentDate = Calendar.getInstance();
//                            mCurrentDate.set(year, monthOfYear, dayOfMonth);
//                            mStrCurrentDate = MainLibrary.dateFormatter.format(mCurrentDate.getTime());
//                            Toast.makeText(PCountActivity.this, mStrCurrentDate, Toast.LENGTH_SHORT).show();
//
//
//                            mPCountListViewAdapter = new PCountListViewAdapter(PCountActivity.this, mArrayListPcount);
//                            mListViewPcount.setAdapter(mPCountListViewAdapter);
//                            mPCountListViewAdapter.notifyDataSetChanged();
//
//                        }
//                    }, mYear, mMonth, mDay);
//            dpd.setCancelable(false);
//            dpd.show();
//            Toast.makeText(PCountActivity.this, "Total Number of Record(s) " + String.valueOf(mArrayListPcount.size()), Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(PCountActivity.this, "Current Location Selected " + String.valueOf(MainLibrary.gCurrentBranchSelected), Toast.LENGTH_SHORT).show();
            pDL = ProgressDialog.show(PCountActivity.this, "", "Updating Masterfile. Please Wait...", true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    /** DATA PROCESSING *************************************************************/
    public class TaskSaveData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

//          db.DeleteRecord("trans","date = ? and storeid = ?", new String[] { MainLibrary.gStrCurrentDate, String.valueOf(MainLibrary.gCurrentBranchSelected) });

            db.DeleteRecord(SQLiteHelper.TABLE_TRANSACTION,"date = ? and storeid = ? and userid = ?", new String[] { MainLibrary.gStrCurrentDate, String.valueOf(MainLibrary.gCurrentBranchSelected), String.valueOf(MainLibrary.gStrCurrentUserID) });

            for (PCount pCount : mArrayListPcountAll) {

                if (pCount.sapc != 0 || pCount.whpc != 0 || pCount.whcs != 0 || pCount.fso != 0) {

                    String[] afields = {
                            "date",
                            "storeid",
                            "barcode",
                            "ig",
                            "sapc",
                            "whpc",
                            "whcs",
                            "conversion",
                            "so",
                            "fso",
                            "fsovalue",
                            "webid",
                            "userid",
                            "multi",
                            "lposted"
                    };

                    String[] avalues = { MainLibrary.gStrCurrentDate
                            , String.valueOf(MainLibrary.gCurrentBranchSelected)
                            , pCount.barcode
                            , String.valueOf(pCount.ig)
                            , String.valueOf(pCount.sapc)
                            , String.valueOf(pCount.whpc)
                            , String.valueOf(pCount.whcs)
                            , String.valueOf(pCount.conversion)
                            , String.valueOf(pCount.so)
                            , String.valueOf(pCount.fso)
                            , String.valueOf(pCount.fsovalue)
                            , String.valueOf(pCount.webid)
                            , String.valueOf(MainLibrary.gStrCurrentUserID)
                            , String.valueOf(pCount.multi)
                            , "0"
                    };

                    hmPcount.put(pCount.barcode, pCount);
                    db.AddRecord(SQLiteHelper.TABLE_TRANSACTION, afields, avalues );
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDL.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDL = ProgressDialog.show(PCountActivity.this, "", "Saving Transaction dated " + MainLibrary.gStrCurrentDate + ". Please Wait...", true);
        }

    }

    /** DATA PROCESSING *************************************************************/
    public class TaskPrintData extends AsyncTask<String, Void, Void> {

        String print = "";
        Boolean lsuccess = false;
        @Override
        protected Void doInBackground(String... params) {

            print = Printer.GenerateStringTSCPrint(PrintFormat(), len, 1);

            if(Printer.Open()) {

                String basfile = "DEFAULT.PRN";
                switch (MainLibrary.eStore) {
                    case SEVEN_ELEVEN:
                        basfile = "711.PRN";
                        break;
                    case MERCURY_DRUG:
                        basfile = "MERCURY.PRN";
                        break;
                    case MINISTOP:
                        basfile = "MINISTOP.PRN";
                        break;
                    case FAMILY_MART:
                        basfile = "FAMILY.PRN";
                        break;
                    case LAWSON:
                        basfile = "LAWSON.PRN";
                        break;
                    case ALFAMART:
                        basfile = "ALFAMART.PRN";
                        break;
                    default:
                        break;
                };

                Printer.sendcommand("SIZE 4,1\n");
                Printer.sendcommand("GAP 0,0\n");
                Printer.sendcommand("DIRECTION 1\n");
                Printer.sendcommand("SET TEAR ON\n");
                Printer.sendcommand("CLS\n");

                Printer.sendfile(basfile);

                Printer.clearbuffer();
                Printer.PrintString(print);
                lsuccess = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDL.dismiss();

            if (lsuccess) {

                AlertDialog printdialog = new AlertDialog.Builder(PCountActivity.this).create();
                printdialog.setTitle("Print");
                printdialog.setMessage("Sent to Printer.");
                printdialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Printer.Close();
                    }
                });
                printdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Printer.Close();
                    }
                });

                printdialog.show();

            }else{
                Toast.makeText(PCountActivity.this, "Error Printing. Please Check Connection with the Printer.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            pDL = ProgressDialog.show(PCountActivity.this, "", "Printing. Please Wait...", true);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(888);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
//                NavUtils.navigateUpFromSameTask(this);
                AlertDialog.Builder logoutdialog = new AlertDialog.Builder(PCountActivity.this);
                logoutdialog.setTitle("Log Out");
                logoutdialog.setMessage("Are you sure you want to log out?");
                logoutdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                logoutdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PCountActivity.this, MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                logoutdialog.show();
                break;
            case R.id.action_submenu_category:
                FilterChanged(0);
                break;
            case R.id.action_submenu_subcateg:
                FilterChanged(1);
                break;
            case R.id.action_submenu_brand:
                FilterChanged(2);
                break;
            case R.id.action_submenu_division:
                FilterChanged(3);
                break;
            case R.id.action_submenu_withso:
                FilterChanged(4);
                break;
            case R.id.action_submenu_woso:
                FilterChanged(5);
                break;
            case R.id.action_submenu_all:
                mPCountListViewAdapter.filter(0, "");
                break;
            case R.id.action_detail_summary:
                ViewReports(-1);
                break;
            case R.id.action_category_report:
                ViewReports(0);
                break;
            case R.id.action_subcate_report:
                ViewReports(1);
                break;
            case R.id.action_brand_report:
                ViewReports(2);
                break;
            case R.id.action_division_report:
                ViewReports(3);
                break;
            case R.id.action_withso_report:
                ViewReports(4);
                break;
            case R.id.action_post:
                if (!BuildConfig.DEBUG) {
                    Boolean linvalid = false;
                    for (PCount pCount : mArrayListPcountAll) {
                        if (pCount.sapc == 0 && pCount.whpc == 0 && pCount.whcs == 0) {
                            linvalid = true;
                            break;
                        }
                    }
                    if (linvalid) {
                        Toast.makeText(PCountActivity.this, "Cannot Post Transaction.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                new CheckRequiredItems(true).execute();

/*                Intent intent = new Intent(PCountActivity.this, CaptureSignatureActivity.class);
                intent.putExtra("location", MainLibrary.gCurrentBranchSelected);
                intent.putExtra("datepick", MainLibrary.gStrCurrentDate);
                startActivity(intent);*/

                break;
            case R.id.action_save_current:
                new TaskSaveData().execute();
                break;
            case R.id.action_print_all:
                new CheckRequiredItems(false, true).execute();
                break;
            case R.id.action_print_withso:
                //lprintwithso = true;
                //new TaskPrintData().execute(); // PRINT DATA
                new CheckRequiredItems(false, false).execute();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class CheckRequiredItems extends AsyncTask<Void, Void, Boolean> {

        String strError;
        int nAllItems;
        boolean bPostMode = false;
        boolean bPrintAll = false;

        public CheckRequiredItems(boolean isPostMode) {
            this.bPostMode = isPostMode;
        }

        public CheckRequiredItems(boolean isPostMode, boolean isPrintAll) {
            this.bPostMode = isPostMode;
            this.bPrintAll = isPrintAll;
        }

        @Override
        protected void onPreExecute() {
            pDL = ProgressDialog.show(PCountActivity.this, "", "Checking required items.", true);
            strError = "";
            nAllItems = mArrayListPcountAll.size();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean bReturn = true;

            if(hmPcount.size() == 0) {
                bReturn = false;
                strError = "No transactions found.";
            }
            else {
                for (PCount pcount : mArrayListPcountAll) {
                    if (!hmPcount.containsKey(pcount.barcode)) {
                        bReturn = false;
                        strError = hmPcount.size() + " / " + nAllItems + ". Some required items not transacted.";
                        break;
                    }
                }
            }

            return bReturn;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pDL.dismiss();
            if(!aBoolean) {
                Toast.makeText(PCountActivity.this, strError, Toast.LENGTH_SHORT).show();
                return;
            }

            // FOR POSTING
            Intent intentpost = new Intent(PCountActivity.this, CaptureSignatureActivity.class);
            intentpost.putExtra("location", MainLibrary.gCurrentBranchSelected);
            intentpost.putExtra("datepick", MainLibrary.gStrCurrentDate);

            // FOR PRINTING
            mAlertDialog = new AlertDialog.Builder(PCountActivity.this).create();
            mAlertDialog.setTitle("Print all items");
            mAlertDialog.setMessage("Do you want to print all items?");
            mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Print", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAlertDialog.dismiss();
                    lprintall = true;
                    new TaskPrintData().execute();
                }
            });

            if(bPostMode) startActivity(intentpost);
            else {
                if (bPrintAll) mAlertDialog.show();
                else {
                    lprintwithso = true;
                    new TaskPrintData().execute(); // PRINT DATA
                }
            }
        }
    }

    private String PrintFormat() {

        String toPrint = "";

        toPrint += "\n";
        toPrint += "Store: " + MainLibrary.gCurrentBranchNameSelected + "\n";

        int nSkuWithStocks = 0;
        int nTotSku = 0;
        double nOsaScore;
        String osascore = "";
        for (PCount pcount : mArrayListPcountAll) {
            nTotSku++;
            if(pcount.sapc != 0 || pcount.whpc != 0 || pcount.whcs != 0) {
                nSkuWithStocks++;
            }
        }
        nOsaScore = (Double.valueOf(nSkuWithStocks) / Double.valueOf(nTotSku)) * 100;
        osascore = String.format("%.2f", nOsaScore) + " %";

        toPrint += "OSA Score: " + osascore + "\n";
        toPrint += "Date: " + MainLibrary.gStrCurrentDate +"\n" + "\n" ;
        toPrint += StringUtils.rightPad("SKU",45,"") +
                StringUtils.rightPad("IG",14,"") +
                StringUtils.rightPad("Invty",14,"") +
                StringUtils.rightPad("Order qty", 14,"") +
                StringUtils.rightPad("Order amt", 14, "") + "\n";
        toPrint += Printer.tsclines;

        int totsku = 0, totfso = 0;
        double totfsoval = 0;
        len = 0;

        for (PCount pCount : mArrayListPcountAll) {

            if (lprintwithso) {
                if (pCount.so == 0) {
                    continue;
                }
            }

            int lensku = 50 - pCount.barcode.length();
            int lenig = 18 - String.valueOf(pCount.ig).length();
            int totig = pCount.sapc + pCount.whpc + (pCount.whcs * pCount.conversion);
            int lenei = 14 - String.valueOf(totig).length();
            int lenfso = 12 - String.valueOf(pCount.fso).length(); // 18
            int lenfsoval = 12 - String.valueOf(pCount.fsovalue * pCount.fso).length(); // 18

            toPrint += StringUtils.rightPad(pCount.desc, 20, "") + "\n"
                    + "BARCODE ;\"128\",50,2,0,2,2,\"" + pCount.barcode + "\"" + "\n"
                    + "\nINFO ;"
                    + StringUtils.rightPad(" ", 47,"")
                    + StringUtils.rightPad(String.valueOf(pCount.ig), lenig)
                    + StringUtils.rightPad(String.valueOf(totig),lenei)
                    + "*"
                    + StringUtils.rightPad(String.valueOf(pCount.fso), lenfso, "")
                    + StringUtils.rightPad(MainLibrary.priceDec.format(pCount.fsovalue * pCount.fso), lenfsoval, "")
                    + "*"
                    + StringUtils.rightPad("       ", lensku,"")
                    + "\n\n";

            if (pCount.so > 0) {
                totsku = totsku + 1;
            }

            totfso = totfso + pCount.fso;
            totfsoval = totfsoval + (pCount.fsovalue * pCount.fso) ;
            len = len + 2;
        }

        toPrint += Printer.tsclines;
        toPrint += "Total: " + StringUtils.rightPad(String.valueOf(totsku),32/*76*/) + StringUtils.rightPad(String.valueOf(totfso),11)
                + StringUtils.rightPad(String.format("%.2f", totfsoval),12) + "\n";
        toPrint += "\n" + "\n" + "\n" + "\n" + "\n";
        toPrint += StringUtils.center(Printer.tsclines2, 80);
        toPrint += StringUtils.center("Acknowledge by", 80);

        lprintall = false;
        lprintwithso = false;

        return toPrint;
    }

    public void FilterChanged(final int filterCode) {

        final Dialog dialog = new Dialog(PCountActivity.this, R.style.Transparent);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_branch2);

        TextView lvCaption = (TextView) dialog.findViewById(R.id.textViewBranchName);

        if (filterCode > 3) {

            mPCountListViewAdapter.filter(filterCode, "xxx");

        } else {

            final String filterId, filterTitle;

            switch (filterCode) {

                case 0:
                    filterId = "categoryid";
                    filterTitle = "Category";
                    break;
                case 1:
                    filterId = "subcategoryid";
                    filterTitle = "Subcategory";
                    break;
                case 2:
                    filterId = "brandid";
                    filterTitle = "Brand";
                    break;
                case 3:
                    filterId = "divisionid";
                    filterTitle = "Division";
                    break;
                default:
                    filterId = "";
                    filterTitle = "";
            }

            Cursor tmpCategory = db.GetGroupby(filterId,"pcount");

            String[] from = new String[] {
                    filterId
            };
            int[] to = new int[] { R.id.itemTextView };

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(PCountActivity.this, R.layout.activity_items_filtering,
                    tmpCategory, from, to, 0);
            final ListView lv = (ListView) dialog.findViewById(R.id.listViewBranch);
            lv.setAdapter(adapter);
            lvCaption.setText("Select " + filterTitle);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();

                    TextView c = (TextView) view.findViewById(R.id.itemTextView);
                    String name = c.getText().toString();
                    Toast.makeText(PCountActivity.this, name, Toast.LENGTH_SHORT).show();
                    mPCountListViewAdapter.filter(filterCode, name);

                }
            });

            dialog.show();

        }

    }

    public void ViewReports(int reportType) {

        Boolean lvalid = false;
        Boolean lwso = false;
        Cursor cursorGroup = null;

        final Dialog dialog = new Dialog(PCountActivity.this, R.style.Transparent);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_report);

        final TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
        final TextView textViewColumnTitle = (TextView) dialog.findViewById(R.id.textViewColumnTitle);
        final ListView listViewReport = (ListView) dialog.findViewById(R.id.listViewReport);

        final String reportTitle, columnTitle, filterId;

        switch (reportType) {

            case 0:
                reportTitle = "Per Category Report";
                columnTitle = "Category";
                filterId = "categoryid";
                break;
            case 1:
                reportTitle = "Per Subcategory Report";
                columnTitle = "Subcategory";
                filterId = "subcategoryid";
                break;
            case 2:
                reportTitle = "Per Brand Report";
                columnTitle = "Brand";
                filterId = "brandid";
                break;
            case 3:
                reportTitle = "Per Division Report";
                columnTitle = "Division";
                filterId = "divisionid";
                break;
            case 4:
                reportTitle = "With SO Report";
                columnTitle = "With SO";
                filterId = "[desc]";
                lwso = true;
                break;
            default:
                reportTitle = "Items Summary Report";
                columnTitle = "Items";
                filterId = "[desc]";
        }

        textViewTitle.setText(reportTitle);
        textViewColumnTitle.setText(columnTitle);

        ArrayList<ReportClass> arrayListReport = new ArrayList<>();

        cursorGroup = db.queryData("select " + filterId + " as name from pcount where storeid = " + String.valueOf(MainLibrary.gCurrentBranchSelected) +
                    " group by " + filterId);


        cursorGroup.moveToFirst();

        while (!cursorGroup.isAfterLast()) {

            arrayListReport.add(new ReportClass(cursorGroup.getString(cursorGroup.getColumnIndex("name")).trim()));

            cursorGroup.moveToNext();
        }

        for (PCount pCount : mArrayListPcountAll) {

 /*           if (reportType >= 4){
                switch (reportType){
                    case 4:
                        lvalid = pCount.sapc != 0 || pCount.whpc != 0 || pCount.whcs != 0;
                        if (!lvalid) {
                            continue;
                        }
                        break;
                }
            }*/

            for (ReportClass reportClass : arrayListReport) {

                switch (reportType) {

                    case 0:
                        if (!reportClass.name.contains(pCount.category)) {
                            continue;
                        }
                        break;
                    case 1:
                        if (!reportClass.name.contains(pCount.subcate)) {
                            continue;
                        }
                        break;
                    case 2:
                        if (!reportClass.name.contains(pCount.brand)) {
                            continue;
                        }
                        break;
                    case 3:
                        if (!reportClass.name.contains(pCount.division)) {
                            continue;
                        }
                        break;
                    case 4:
 /*                       lvalid = pCount.sapc != 0 || pCount.whpc != 0 || pCount.whcs != 0;
                        if (!lvalid) {
                            continue;
                        }*/
                        if (!reportClass.name.contains(pCount.desc)) {
                            continue;
                        }
                        break;
                    default:
                        if (!reportClass.name.contains(pCount.desc)) {
                            continue;
                        }
                }

                reportClass.ig = reportClass.ig + pCount.ig;
                reportClass.so = reportClass.so + pCount.so;
                reportClass.endinv = reportClass.endinv + (pCount.sapc + pCount.whpc + (pCount.whcs * pCount.conversion));
                reportClass.finalso = reportClass.finalso + pCount.fso;
                reportClass.multi = pCount.multi;

            }

        }

        ArrayList<ReportClass> arrayListReport2 = new ArrayList<>();

        if (reportType == 4){
            Iterator i = arrayListReport.iterator();
            while(i.hasNext()){
                ReportClass reportClass = (ReportClass) i.next();
                if (reportClass.so > 0){
//                    reportClass.finalso = reportClass.finalso; //- (reportClass.so % reportClass.multi) + reportClass.multi;
                    arrayListReport2.add(reportClass);
                }else{
                    i.remove();
                }
            }
        }else{
           /* for (ReportClass reportClass : arrayListReport) {
                if (reportClass.so > 0) reportClass.finalso = reportClass.so - (reportClass.so % reportClass.multi) + reportClass.multi;
            }*/
        }

        /*if (reportType >= 4) {
            ArrayList<ReportClass> arrayListReport2 = new ArrayList<>();
            for (ReportClass reportClass : arrayListReport) {
                switch (reportType) {
                    case 4:
                        if (reportClass.so != 0) arrayListReport2.add(reportClass);
                        break;
                }
            }
            listViewReport.setAdapter(new ReportListViewAdapter(PCountActivity.this, arrayListReport2));
        } else {
            listViewReport.setAdapter(new ReportListViewAdapter(PCountActivity.this, arrayListReport));
        }

        dialog.show();*/


        if ((reportType == 4)) {
            listViewReport.setAdapter(new ReportListViewAdapter(PCountActivity.this, arrayListReport2));
        }else{
            listViewReport.setAdapter(new ReportListViewAdapter(PCountActivity.this, arrayListReport));
        }

        dialog.show();

    }

    public void ViewReports2(int reportType) {

        final Dialog dialog = new Dialog(PCountActivity.this, R.style.Transparent);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_report);

        final ListView listViewReport = (ListView) dialog.findViewById(R.id.listViewReport);

        final PCountListViewAdapter reportAdapter = new PCountListViewAdapter(PCountActivity.this, mArrayListPcountAll);
        listViewReport.setAdapter(reportAdapter);

        if (reportType == 0) {
            reportAdapter.filter(4, "xxx");
        } else {
            reportAdapter.filter(5, "xxx");
        }

        dialog.show();

    }

}

