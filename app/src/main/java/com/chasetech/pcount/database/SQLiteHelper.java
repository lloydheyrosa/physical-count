package com.chasetech.pcount.database;

/**
 * Created by ULTRABOOK on 10/14/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME  = "UPcDb";
    private static final String TAG = "SettingsProvider";
    private static final int DATABASE_VERSION = 13;

    //Pcount MainTable
    public static final String TABLE_PCOUNT = "pcount";
    public static final String COLUMN_PCOUNT_ID = "id";
    public static final String COLUMN_PCOUNT_BARCODE = "barcode";
    public static final String COLUMN_PCOUNT_DESC = "desc";
    public static final String COLUMN_PCOUNT_IG = "ig";
    public static final String COLUMN_PCOUNT_SAPC = "sapc";
    public static final String COLUMN_PCOUNT_WHPC = "whpc";
    public static final String COLUMN_PCOUNT_WHCS = "whcs";
    public static final String COLUMN_PCOUNT_CONVERSION = "conversion";
    public static final String COLUMN_PCOUNT_SO = "so";
    public static final String COLUMN_PCOUNT_FSO = "fso";
    public static final String COLUMN_PCOUNT_CATEGORYID = "categoryid";
    public static final String COLUMN_PCOUNT_BRANDID = "brandid";
    public static final String COLUMN_PCOUNT_DIVISIONID = "divisionid";
    public static final String COLUMN_PCOUNT_SUBCATEGORYID = "subcategoryid";
    public static final String COLUMN_PCOUNT_STOREID = "storeid";
    public static final String COLUMN_PCOUNT_FSOVALUE = "fsovalue";
    public static final String COLUMN_PCOUNT_WEBID = "webid";
    public static final String COLUMN_PCOUNT_MULTI = "multi";
    public static final String COLUMN_PCOUNT_OTHERBARCODE = "otherbarcode"; // new column v2.4

    public static final String DATABASE_CREATE_TABLE_PCOUNT = "CREATE TABLE " + TABLE_PCOUNT + "("
            + COLUMN_PCOUNT_ID + " integer PRIMARY KEY, "
            + COLUMN_PCOUNT_BARCODE + " text, "
            + COLUMN_PCOUNT_DESC + " text, "
            + COLUMN_PCOUNT_IG + " integer, "
            + COLUMN_PCOUNT_SAPC + " integer, "
            + COLUMN_PCOUNT_WHPC + " integer, "
            + COLUMN_PCOUNT_WHCS + " integer, "
            + COLUMN_PCOUNT_CONVERSION + " integer, "
            + COLUMN_PCOUNT_SO + " integer, "
            + COLUMN_PCOUNT_FSO + " integer,"
            + COLUMN_PCOUNT_CATEGORYID + " text,"
            + COLUMN_PCOUNT_BRANDID + " text,"
            + COLUMN_PCOUNT_DIVISIONID + " text,"
            + COLUMN_PCOUNT_SUBCATEGORYID + " text, "
            + COLUMN_PCOUNT_STOREID + " integer, "
            + COLUMN_PCOUNT_FSOVALUE + " real, "
            + COLUMN_PCOUNT_WEBID + " text, "
            + COLUMN_PCOUNT_MULTI + " integer, "
            + COLUMN_PCOUNT_OTHERBARCODE + " text)";

    //Branch Table
    public static final String TABLE_BRANCH = "branch";
    public static final String COLUMN_BRANCH_ID = "id";
    public static final String COLUMN_BRANCH_BID = "bid";
    public static final String COLUMN_BRANCH_storecode = "storecode"; // new column v.2.6
    public static final String COLUMN_BRANCH_DESC = "bdesc";
    public static final String COLUMN_BRANCH_MULTIPLE = "multiple";
    public static final String COLUMN_BRANCH_CHANNELID = "channelid"; // new column v.2.6
    public static final String COLUMN_BRANCH_CHANNELDESC = "channeldesc"; // new column v.2.6
    public static final String COLUMN_BRANCH_CHANNELAREA = "area"; // new column v.2.6

    public static final String DATABASE_CREATE_TABLE_BRANCH = "CREATE TABLE " + TABLE_BRANCH + "("
            + COLUMN_BRANCH_ID + " integer PRIMARY KEY, "
            + COLUMN_BRANCH_BID + " integer, "
            + COLUMN_BRANCH_storecode + " text, "
            + COLUMN_BRANCH_DESC + " text, "
            + COLUMN_BRANCH_MULTIPLE + " integer, "
            + COLUMN_BRANCH_CHANNELID + " integer, "
            + COLUMN_BRANCH_CHANNELDESC + " text, "
            + COLUMN_BRANCH_CHANNELAREA + " text)";

    //User Table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_UID = "uid";
    public static final String COLUMN_USER_DESC = "udesc";
    public static final String COLUMN_USER_HASH = "hash";

    public static final String DATABASE_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " integer PRIMARY KEY, "
            + COLUMN_USER_UID + " integer, "
            + COLUMN_USER_DESC + " text, "
            + COLUMN_USER_HASH + " text) ";

    //PCOUNT Transaction
    public static final String TABLE_TRANSACTION = "TRANS";
    public static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_TRANSACTION_DATE = "date";
    public static final String COLUMN_TRANSACTION_STOREID = "storeid";
    public static final String COLUMN_TRANSACTION_BARCODE = "barcode";
    public static final String COLUMN_TRANSACTION_IG = "ig";
    public static final String COLUMN_TRANSACTION_SAPC = "sapc";
    public static final String COLUMN_TRANSACTION_WHPC = "whpc";
    public static final String COLUMN_TRANSACTION_WHCS = "whcs";
    public static final String COLUMN_TRANSACTION_CONVERSION = "conversion";
    public static final String COLUMN_TRANSACTION_SO = "so";
    public static final String COLUMN_TRANSACTION_FSO = "fso";
    public static final String COLUMN_TRANSACTION_FSOVALUE = "fsovalue";
    public static final String COLUMN_TRANSACTION_LPOSTED = "lposted";
    public static final String COLUMN_TRANSACTION_WEBID = "webid";
    public static final String COLUMN_TRANSACTION_USERID = "userid";
    public static final String COLUMN_TRANSACTION_MULTI = "multi";

    public static final String DATABASE_CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + "("
            + COLUMN_TRANSACTION_ID + " integer PRIMARY KEY autoincrement, "
            + COLUMN_TRANSACTION_DATE + " text, "
            + COLUMN_TRANSACTION_STOREID + " integer, "
            + COLUMN_TRANSACTION_BARCODE + " text, "
            + COLUMN_TRANSACTION_IG + " integer, "
            + COLUMN_TRANSACTION_SAPC + " integer, "
            + COLUMN_TRANSACTION_WHPC + " integer, "
            + COLUMN_TRANSACTION_WHCS + " integer, "
            + COLUMN_TRANSACTION_CONVERSION + " integer, "
            + COLUMN_TRANSACTION_SO + " integer, "
            + COLUMN_TRANSACTION_FSO + " integer, "
            + COLUMN_TRANSACTION_FSOVALUE + " real, "
            + COLUMN_TRANSACTION_LPOSTED + " integer, "
            + COLUMN_TRANSACTION_WEBID + " text, "
            + COLUMN_TRANSACTION_USERID + " integer, "
            + COLUMN_TRANSACTION_MULTI + " integer )";

    //ASSORTMENT MASTERFILE
    public static final String TABLE_ASSORTMENT = "tblAssortment";
    public static final String COLUMN_ASSORTMENT_ID = "id";
    public static final String COLUMN_ASSORTMENT_BARCODE = "barcode";
    public static final String COLUMN_ASSORTMENT_DESC = "desc";
    public static final String COLUMN_ASSORTMENT_IG = "ig";
    public static final String COLUMN_ASSORTMENT_SAPC = "sapc";
    public static final String COLUMN_ASSORTMENT_WHPC = "whpc";
    public static final String COLUMN_ASSORTMENT_WHCS = "whcs";
    public static final String COLUMN_ASSORTMENT_CONVERSION = "conversion";
    public static final String COLUMN_ASSORTMENT_SO = "so";
    public static final String COLUMN_ASSORTMENT_FSO = "fso";
    public static final String COLUMN_ASSORTMENT_CATEGORYID = "categoryid";
    public static final String COLUMN_ASSORTMENT_BRANDID = "brandid";
    public static final String COLUMN_ASSORTMENT_DIVISIONID = "divisionid";
    public static final String COLUMN_ASSORTMENT_SUBCATEGORYID = "subcategoryid";
    public static final String COLUMN_ASSORTMENT_STOREID = "storeid";
    public static final String COLUMN_ASSORTMENT_FSOVALUE = "fsovalue";
    public static final String COLUMN_ASSORTMENT_WEBID = "webid";
    public static final String COLUMN_ASSORTMENT_MULTI = "multi";
    public static final String COLUMN_ASSORTMENT_OTHERBARCODE = "otherbarcode"; // new column v2.4

    public static final String DATABASE_CREATE_TABLE_ASSORTMENT = "CREATE TABLE " + TABLE_ASSORTMENT + "("
            + COLUMN_ASSORTMENT_ID + " integer PRIMARY KEY, "
            + COLUMN_ASSORTMENT_BARCODE + " text, "
            + COLUMN_ASSORTMENT_DESC + " text, "
            + COLUMN_ASSORTMENT_IG + " integer, "
            + COLUMN_ASSORTMENT_SAPC + " integer, "
            + COLUMN_ASSORTMENT_WHPC + " integer, "
            + COLUMN_ASSORTMENT_WHCS + " integer, "
            + COLUMN_ASSORTMENT_CONVERSION + " integer, "
            + COLUMN_ASSORTMENT_SO + " integer, "
            + COLUMN_ASSORTMENT_FSO + " integer,"
            + COLUMN_ASSORTMENT_CATEGORYID + " text,"
            + COLUMN_ASSORTMENT_BRANDID + " text,"
            + COLUMN_ASSORTMENT_DIVISIONID + " text,"
            + COLUMN_ASSORTMENT_SUBCATEGORYID + " text, "
            + COLUMN_ASSORTMENT_STOREID + " integer, "
            + COLUMN_ASSORTMENT_FSOVALUE + " real, "
            + COLUMN_ASSORTMENT_WEBID + " text, "
            + COLUMN_ASSORTMENT_MULTI + " integer, "
            + COLUMN_ASSORTMENT_OTHERBARCODE + " text)";

    //ASSORTMENT TRANSACTIONS
    public static final String TABLE_TRANSACTION_ASSORT = "tblAssortTransaction";
    public static final String COLUMN_TRANSACTION_ASSORT_ID = "id";
    public static final String COLUMN_TRANSACTION_ASSORT_DATE = "date";
    public static final String COLUMN_TRANSACTION_ASSORT_STOREID = "storeid";
    public static final String COLUMN_TRANSACTION_ASSORT_BARCODE = "barcode";
    public static final String COLUMN_TRANSACTION_ASSORT_IG = "ig";
    public static final String COLUMN_TRANSACTION_ASSORT_SAPC = "sapc";
    public static final String COLUMN_TRANSACTION_ASSORT_WHPC = "whpc";
    public static final String COLUMN_TRANSACTION_ASSORT_WHCS = "whcs";
    public static final String COLUMN_TRANSACTION_ASSORT_CONVERSION = "conversion";
    public static final String COLUMN_TRANSACTION_ASSORT_SO = "so";
    public static final String COLUMN_TRANSACTION_ASSORT_FSO = "fso";
    public static final String COLUMN_TRANSACTION_ASSORT_FSOVALUE = "fsovalue";
    public static final String COLUMN_TRANSACTION_ASSORT_LPOSTED = "lposted";
    public static final String COLUMN_TRANSACTION_ASSORT_WEBID = "webid";
    public static final String COLUMN_TRANSACTION_ASSORT_USERID = "userid";
    public static final String COLUMN_TRANSACTION_ASSORT_MULTI = "multi";
    public static final String COLUMN_TRANSACTION_ASSORT_MONTH = "month";

    public static final String DATABASE_CREATE_TABLE_TRANSASSORT = "CREATE TABLE " + TABLE_TRANSACTION_ASSORT + "("
            + COLUMN_TRANSACTION_ASSORT_ID + " integer PRIMARY KEY autoincrement, "
            + COLUMN_TRANSACTION_ASSORT_DATE + " text, "
            + COLUMN_TRANSACTION_ASSORT_STOREID + " integer, "
            + COLUMN_TRANSACTION_ASSORT_BARCODE + " text, "
            + COLUMN_TRANSACTION_ASSORT_IG + " integer, "
            + COLUMN_TRANSACTION_ASSORT_SAPC + " integer, "
            + COLUMN_TRANSACTION_ASSORT_WHPC + " integer, "
            + COLUMN_TRANSACTION_ASSORT_WHCS + " integer, "
            + COLUMN_TRANSACTION_ASSORT_CONVERSION + " integer, "
            + COLUMN_TRANSACTION_ASSORT_SO + " integer, "
            + COLUMN_TRANSACTION_ASSORT_FSO + " integer, "
            + COLUMN_TRANSACTION_ASSORT_FSOVALUE + " real, "
            + COLUMN_TRANSACTION_ASSORT_LPOSTED + " integer, "
            + COLUMN_TRANSACTION_ASSORT_WEBID + " text, "
            + COLUMN_TRANSACTION_ASSORT_USERID + " integer, "
            + COLUMN_TRANSACTION_ASSORT_MULTI + " integer, "
            + COLUMN_TRANSACTION_ASSORT_MONTH + " text)";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_PCOUNT);
        database.execSQL(DATABASE_CREATE_TABLE_BRANCH);
        database.execSQL(DATABASE_CREATE_TABLE_USER);
        database.execSQL(DATABASE_CREATE_TABLE_TRANSACTION);
        database.execSQL(DATABASE_CREATE_TABLE_ASSORTMENT);
        database.execSQL(DATABASE_CREATE_TABLE_TRANSASSORT);

        database.execSQL("CREATE INDEX pcountDescIndex ON pcount (desc)");
        database.execSQL("CREATE INDEX pcountCategoryIndex ON pcount (categoryid)");
        database.execSQL("CREATE INDEX pcountBrandIndex ON pcount (brandid)");
        database.execSQL("CREATE INDEX pcountDivisionIndex ON pcount (divisionid)");
        database.execSQL("CREATE INDEX pcountSubCategoryIndex ON pcount (subcategoryid)");

        database.execSQL("CREATE INDEX assortDescIndex ON " + TABLE_ASSORTMENT + " (desc)");
        database.execSQL("CREATE INDEX assortCategoryIndex ON " + TABLE_ASSORTMENT + " (categoryid)");
        database.execSQL("CREATE INDEX assortBrandIndex ON " + TABLE_ASSORTMENT + " (brandid)");
        database.execSQL("CREATE INDEX assortDivisionIndex ON " + TABLE_ASSORTMENT + " (divisionid)");
        database.execSQL("CREATE INDEX assortSubCategoryIndex ON " + TABLE_ASSORTMENT + " (subcategoryid)");

        database.execSQL("CREATE INDEX transactionIndex ON TRANS (date,storeid)");
        database.execSQL("CREATE INDEX transactionBarcodeIndex ON TRANS (barcode)");

        database.execSQL("CREATE INDEX assorttransactionIndex ON " + TABLE_TRANSACTION_ASSORT + " (date,storeid)");
        database.execSQL("CREATE INDEX assorttransactionBarcodeIndex ON " + TABLE_TRANSACTION_ASSORT + " (barcode)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

        if(currentVersion > oldVersion) {
            // db version 13
            db.execSQL("ALTER TABLE " + TABLE_BRANCH + " ADD COLUMN " + COLUMN_BRANCH_CHANNELDESC + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BRANCH + " ADD COLUMN " + COLUMN_BRANCH_CHANNELAREA + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BRANCH + " ADD COLUMN " + COLUMN_BRANCH_storecode + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BRANCH + " ADD COLUMN " + COLUMN_BRANCH_CHANNELID + " INTEGER");
        }

        Log.w(TAG, "Upgrading settings database from version " + oldVersion + " to "
                + currentVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}