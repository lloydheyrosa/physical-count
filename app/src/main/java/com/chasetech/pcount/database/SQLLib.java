package com.chasetech.pcount.database;

/**
 * Created by INID on 10/14/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class SQLLib {

    public SQLiteDatabase DBASE;
    private SQLiteHelper dbHelper;

    public SQLLib(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        DBASE = dbHelper.getWritableDatabase();
    }

    public void insertToPcount(String barcode, String desc, int ig, int conversion, String categoryid, String subcategoryid,
                               String brandid, String divisionid){

        DBASE = dbHelper.getWritableDatabase();

        int id = SQLLib.this.getMaxPcountId() + 1;
        int defIntVal = 0;

        ContentValues insertValues = new ContentValues();
        insertValues.put(dbHelper.COLUMN_PCOUNT_ID, id);
        insertValues.put(dbHelper.COLUMN_PCOUNT_BARCODE, barcode);
        insertValues.put(dbHelper.COLUMN_PCOUNT_DESC, desc);
        insertValues.put(dbHelper.COLUMN_PCOUNT_IG, ig);
        insertValues.put(dbHelper.COLUMN_PCOUNT_SAPC, defIntVal);
        insertValues.put(dbHelper.COLUMN_PCOUNT_WHPC, defIntVal);
        insertValues.put(dbHelper.COLUMN_PCOUNT_WHCS, defIntVal);
        insertValues.put(dbHelper.COLUMN_PCOUNT_CONVERSION, conversion);
        insertValues.put(dbHelper.COLUMN_PCOUNT_SO, defIntVal);
        insertValues.put(dbHelper.COLUMN_PCOUNT_FSO, defIntVal);
        insertValues.put(dbHelper.COLUMN_PCOUNT_CATEGORYID, categoryid);
        insertValues.put(dbHelper.COLUMN_PCOUNT_BRANDID, brandid);
        insertValues.put(dbHelper.COLUMN_PCOUNT_DIVISIONID, divisionid);
        insertValues.put(dbHelper.COLUMN_PCOUNT_SUBCATEGORYID, subcategoryid);
        DBASE.insert(dbHelper.TABLE_PCOUNT,null,insertValues);

    }

    public String getStringBulkInsert(int fieldSize, String strTable){
        String qCount = "?";

        for (int i = 0;i < fieldSize; i++){
            qCount = qCount + ",?";
        }

        return "INSERT INTO " + strTable + " VALUES (" + qCount + ");";
    }

    public void insertBulktoPcount(String strBulkInsertCommand, BufferedReader br){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SQLiteStatement statement = db.compileStatement(strBulkInsertCommand);
        db.beginTransaction();
        int id = 0;
        Boolean fLine = false;
        String line;

        try {
            while ((line = br.readLine()) != null) {

                if (!fLine) {
                    fLine = true;
                    continue;
                }

                String[] itemRefs = line.split(",");

                id++;

                statement.clearBindings();
                statement.bindLong(1, id);
                statement.bindString(2, itemRefs[0].replace("\"", ""));
                statement.bindString(3, itemRefs[1].replace("\"",""));
                statement.bindLong(4, Integer.parseInt(itemRefs[2]));
                statement.bindLong(5, 0);
                statement.bindLong(6, 0);
                statement.bindLong(7, 0);
                statement.bindLong(8, Integer.parseInt(itemRefs[3]));
                statement.bindLong(9, 0);
                statement.bindLong(10, 0);
                statement.bindString(11, itemRefs[5].replace("\"", ""));
                statement.bindString(12, itemRefs[7].replace("\"",""));
                statement.bindString(13, itemRefs[8].replace("\"",""));
                statement.bindString(14, itemRefs[6].replace("\"",""));
                statement.bindLong(15, Integer.parseInt(itemRefs[9]));
                statement.bindDouble(16, Double.parseDouble(itemRefs[4]));
                statement.bindDouble(17,Integer.parseInt(itemRefs[10]));
                statement.bindDouble(18,Integer.parseInt(itemRefs[11]));
                statement.execute();
            }

            br.close();

        } catch (IOException e) {
            //LogThis.LogToFile("TaskProcessData_IOException : \n"+e.toString());
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }



    public void insertToUser(int uid, String desc, String hash){

        DBASE = dbHelper.getWritableDatabase();

        int id = SQLLib.this.getMaxUId() + 1;

        ContentValues insertValues = new ContentValues();
        insertValues.put(dbHelper.COLUMN_USER_ID, id);
        insertValues.put(dbHelper.COLUMN_USER_UID, uid);
        insertValues.put(dbHelper.COLUMN_USER_DESC, desc);
        insertValues.put(dbHelper.COLUMN_USER_HASH, hash);
        DBASE.insert(dbHelper.TABLE_USER, null, insertValues);

    }

    public void insertToBranch(String desc){

        DBASE = dbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        //insertValues.put(dbHelper.COLUMN_BRANCH_BID, uid);
        insertValues.put(dbHelper.COLUMN_BRANCH_DESC, desc);
        DBASE.insert(SQLiteHelper.TABLE_BRANCH,null,insertValues);

    }

    public void insertToBranch(int uid, String desc){

        DBASE = dbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(dbHelper.COLUMN_BRANCH_BID, uid);
        insertValues.put(dbHelper.COLUMN_BRANCH_DESC, desc);
        DBASE.insert(SQLiteHelper.TABLE_BRANCH,null,insertValues);

    }

    public int getMaxPcountId(){

        int ret;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor pcount = db.rawQuery("select max(id) from " + SQLiteHelper.TABLE_PCOUNT, null);
        if (pcount.getCount()>=1){
            pcount.moveToFirst();
            ret = pcount.getInt(0);
        }else{
            ret = 0;
        }

        return ret;
    }

    public int getMaxUId(){

        int ret;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor user = db.rawQuery("select max(id) from " + SQLiteHelper.TABLE_USER, null);
        if (user.getCount()>=1){
            user.moveToFirst();
            ret = user.getInt(0);
        }else{
            ret = 0;
        }
        return ret;
    }

    public Cursor GetDataCursor(String tableName, String strCondition) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where " + strCondition, null);
        return cursor;

    }

    public Cursor queryData(String statement) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(statement, null);
        return cursor;

    }

    public Cursor queryData(String statement, String[] args) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(statement, args);
        return cursor;

    }

    public int getProfilesCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + SQLiteHelper.TABLE_PCOUNT, null);
        cursor.moveToFirst();
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    public boolean TruncateTable(String tableName) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(tableName, null, null);
        db.close();

        return count==0;

    }

    public void DeleteRecord(String tableName, String where, String[] values){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(tableName, where, values);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Cursor GetGroupby(String fieldName, String tableName) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rowid _id,* FROM "
                + tableName + " GROUP BY " + fieldName, null);
        return cursor;

    }

    public boolean AddRecord(String tableName, String[] aFields, String[] aValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (aFields.length != aValues.length) {
            return false;
        }

        ContentValues values = new ContentValues();

        for (int i = 0; i < aFields.length ; i++)
        {
            values.put(aFields[i], aValues[i]);
        }

        db.insert(tableName, null, values);
        return true;

    }

    public String UpdateRecord(String tableName, String strWhere, String aColumn, int aValues) {
        //credits to the poging owner
        String strUpdate = "";
        try{

            DBASE = dbHelper.getWritableDatabase();

  /*          ContentValues newValues = new ContentValues();
            newValues.put(aColumn, aValues);

            db.update(tableName, newValues, strWhere, null);*/


            strUpdate = "Update " + tableName + " set " + aColumn +  "=" + aValues + " where " + strWhere;
            //strUpdate = "Update pcount set sapc = 5 where barcode = '20279003'";

            DBASE.execSQL(strUpdate);

        }catch(Exception e){
            e.printStackTrace();
        }
        return strUpdate;
    }


    public void UpdateRecord(String tableName, String strWhereField, String[] strWhereValue, String[] aFields, String[] aValues) {
        try{

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < aFields.length ; i++)
            {
                contentValues.put(aFields[i], aValues[i]);
            }

            db.update(tableName, contentValues, strWhereField, strWhereValue);

            db.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void UpdateRecord(String tableName, String strWhereField, String strWhereValue, String[] aFields, String[] aValues) {
        try{

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < aFields.length ; i++)
            {
                contentValues.put(aFields[i], aValues[i]);
            }

            db.update(tableName, contentValues, strWhereField + " = ?", new String[]{strWhereValue});

            db.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void UpdateRecord(String tableName, String strWhereField, String strWhereValue,
                             String strWhereField2, String strWhereValue2,
                             String[] aFields, String[] aValues) {
        try{

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < aFields.length ; i++)
            {
                contentValues.put(aFields[i], aValues[i]);
            }

            db.update(tableName, contentValues, strWhereField + " = ? and " + strWhereField2 + " = ?",
                    new String[] { strWhereValue, strWhereValue2 });

            db.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
