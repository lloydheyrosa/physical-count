package com.chasetech.pcount.library;

import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.DatePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Vinid on 10/25/2015.
 */


public class MainLibrary extends Application {

    //public static final String API_URL = "http://pcoun2.chasetech.com";
    public static final String API_URL = "http://ulp-projectsos.com";

    public static final String API_URL_ASSORTMENT_IMAGE = MainLibrary.API_URL + "/api/uploadassortmentimage";
    public static final String API_URL_ASSORTMENT_POSTING = MainLibrary.API_URL + "/api/uploadassortment";

    public static final String API_URL_MKL_IMAGE = MainLibrary.API_URL + "/api/uploadimage";
    public static final String API_URL_MKL_POSTING = MainLibrary.API_URL + "/api/uploadpcount";

    public static final String API_GET_PRNFILENAMES = MainLibrary.API_URL + "/api/prnlist";
    public static final String API_GET_PRNFILES = MainLibrary.API_URL + "/api/downloadprn/";

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    public static final SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);

    public static DecimalFormat priceDec = new DecimalFormat("###,###,###.00");
    public static String CompanyCode = "";

    public static File ROOT_SDCARD = Environment.getExternalStorageDirectory();
    public static String DOWNLOAD_FOLDER = "Download";
    public static File PRN_FOLDER = null;

    public static String ShowDateReturn;

    public static Boolean gLUpdate = true;

    public static int gCurrentBranchSelected;

    public static String gCurrentBranchNameSelected;

    public static String gStrCurrentDate = "";

    public static String selectedMonth = "";

    public static int gStrCurrentUserID;

    public static String gStrCurrentUserName = "";

    public static boolean isAssortmentMode = false;

    private static final String[] PRN_FILES = new String[] {
            "DEFAULT.PRN",
            "FAMILY.PRN",
            "LAWSON.PRN",
            "ALFAMART.PRN",
            "MERCURY.PRN",
            "711.PRN",
            "MINISTOP.PRN"
    };

    public static final String[] aModules = new String[] {
            "NORMAL",
            "ASSORTMENT"
    };

    public static final String MKL_TEXTFILE = "mkl.txt";
    public static final String ASSORTMENT_TXTFILE = "assortment.txt";

    public enum STORE_TYPES {
        DEFAULT,
        SEVEN_ELEVEN,
        MINISTOP,
        FAMILY_MART,
        MERCURY_DRUG,
        LAWSON,
        ALFAMART,
    }

    public static STORE_TYPES eStore = STORE_TYPES.DEFAULT;

    public static void LoadFolders() {
        PRN_FOLDER = new File(ROOT_SDCARD, DOWNLOAD_FOLDER);
        if(!PRN_FOLDER.exists())
            PRN_FOLDER.mkdirs();
    }

    public static boolean CopyFile(File fileFrom, File fileTo) throws IOException {
        boolean bReturn = false;

        InputStream in = new FileInputStream(fileFrom);
        OutputStream out = new FileOutputStream(fileTo);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        return bReturn;
    }

    public void ShowCalendar() {

        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String date = MainLibrary.dateFormatter.format(newDate.getTime());

                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

}
