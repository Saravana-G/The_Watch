package com.example.user.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DataManager";

    private static final String TABLE_Heart = "heartable";
    private static final String H_ID = "id";
    private static final String H_val = "val";
    private static final String H_date = "hdate";
    private static final String H_time = "time";

    private static final String TABLE_Spo = "spotable";
    private static final String S_ID = "id";
    private static final String S_val = "val";
    private static final String S_date = "sdate";
    private static final String S_time = "time";

    private static final String TABLE_Acc = "acctable";
    private static final String A_ID = "id";
    private static final String A_val = "val";
    private static final String A_date = "adate";
    private static final String A_time = "time";

    private static final String TABLE_Temp = "temptable";
    private static final String T_ID = "id";
    private static final String T_val = "val";
    private static final String T_date = "tdate";
    private static final String T_time = "time";

    public boolean push = false;


    String CREATE_HEART_TABLE = "CREATE TABLE " + TABLE_Heart + "("
            + H_ID + " INTEGER PRIMARY KEY," + H_val + " TEXT,"
            + H_date + " TEXT,"
            + H_time + " TEXT)";

    String CREATE_SPO_TABLE = "CREATE TABLE " + TABLE_Spo + "("
            + S_ID + " INTEGER PRIMARY KEY," + S_val + " TEXT,"
            + S_date + " TEXT,"
            + S_time + " TEXT)";

    String CREATE_ACC_TABLE = "CREATE TABLE " + TABLE_Acc + "("
            + A_ID + " INTEGER PRIMARY KEY," + A_val + " TEXT,"
            + A_date + " TEXT,"
            + A_time + " TEXT)";

    String CREATE_TEMP_TABLE = "CREATE TABLE " + TABLE_Temp + "("
            + T_ID + " INTEGER PRIMARY KEY," + T_val + " TEXT,"
            + T_date + " TEXT,"
            + T_time + " TEXT)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance  
    }

    // Creating Tables  
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_HEART_TABLE);
        db.execSQL(CREATE_SPO_TABLE);
        db.execSQL(CREATE_ACC_TABLE);
        db.execSQL(CREATE_TEMP_TABLE);

    }

    // Upgrading database  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed  
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_HEART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SPO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_ACC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TEMP_TABLE);
        // Create tables again  
        onCreate(db);
    }

    // code to add the heart data both in cloud and local db
    public void addHeartdata(Heartrate h) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(H_val, h.getVal());
        values.put(H_date, h.getdate());
        values.put(H_time, h.getime());
        db.insert(TABLE_Heart, null, values);
        db.close();
        ParseObject testObject = new ParseObject(ParseUser.getCurrentUser().getUsername() + "_Heart");
        testObject.put("Value", h.getVal());
        testObject.put("Date", h.getdate());
        testObject.put("Time", h.getime());
        testObject.saveEventually();
        // Inserting Row

    }
    // code to add the heart data both in local db
    public void addHeartdatafromcloud(Heartrate h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(H_val, h.getVal());
        values.put(H_date, h.getdate());
        values.put(H_time, h.getime());
        db.insert(TABLE_Heart, null, values);
        db.close();
        // Inserting Row

    }

    // code to add the spo data both in cloud and local db
    public void addSpodata(Sporate s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(S_val, s.getVal());
        values.put(S_date, s.getdate());
        values.put(S_time, s.getime());
        ParseObject testObject = new ParseObject(ParseUser.getCurrentUser().getUsername() + "_Spo");
        testObject.put("Value", s.getVal());
        testObject.put("Date", s.getdate());
        testObject.put("Time", s.getime());
        testObject.pinInBackground();
        testObject.saveEventually();
        // Inserting Row
        db.insert(TABLE_Spo, null, values);

        db.close();
    }

    // code to add the spo data in local db
    public void addSpodatafromcloud(Sporate s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(S_val, s.getVal());
        values.put(S_date, s.getdate());
        values.put(S_time, s.getime());
        // Inserting Row
        db.insert(TABLE_Spo, null, values);
        db.close();
    }

    // code to add the Acc data both in cloud and local db
    public void addAccdata(Accrate a) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(A_val, a.getVal());
        values.put(A_date, a.getdate());
        values.put(A_time, a.getime());
        ParseObject testObject = new ParseObject(ParseUser.getCurrentUser().getUsername() + "_Steps");
        testObject.put("Value", a.getVal());
        testObject.put("Date", a.getdate());
        testObject.put("Time", a.getime());
        testObject.pinInBackground();
        testObject.saveEventually();
        // Inserting Row
        db.insert(TABLE_Acc, null, values);

        db.close();
    }

    // code to add the Acc data in local db
    public void addAccdatafromcloud(Accrate a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(A_val, a.getVal());
        values.put(A_date, a.getdate());
        values.put(A_time, a.getime());
        // Inserting Row
        db.insert(TABLE_Acc, null, values);

        db.close();
    }

    // code to add the temperature data both in cloud and local db
    public void addTempdata(Temprate t) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(T_val, t.getVal());
        values.put(T_date, t.getdate());
        values.put(T_time, t.getime());
        ParseObject testObject = new ParseObject(ParseUser.getCurrentUser().getUsername() + "_Temp");
        testObject.put("Value", t.getVal());
        testObject.put("Date", t.getdate());
        testObject.put("Time", t.getime());
        testObject.pinInBackground();
        testObject.saveEventually();

        // Inserting Row
        db.insert(TABLE_Temp, null, values);

        db.close();
    }
    // code to add the temperature data only in local db
    public void addTempdatafromcloud(Temprate t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T_val, t.getVal());
        values.put(T_date, t.getdate());
        values.put(T_time, t.getime());
        // Inserting Row
        db.insert(TABLE_Temp, null, values);

        db.close();
    }
    //code to get all rows from tables
    public List<Heartrate> getAllHeartdata() {

        List<Heartrate> hearlist = new ArrayList<Heartrate>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Heart, new String[]{H_ID,
                        H_val, H_date, H_time},
                null, null, null, null, null);

        // looping through all rows and adding to list  
        if (cursor.moveToFirst()) {
            do {
                Heartrate h = new Heartrate();
                h.setID(Integer.parseInt(cursor.getString(0)));
                h.setVal(cursor.getString(1));
                h.setdate(cursor.getString(2));
                h.setime(cursor.getString(3));
                // Adding contact to list
                hearlist.add(h);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list  
        return hearlist;
    }

    //code to get selected rows from heart table
    public List<Heartrate> getselectedheartdata(String hdate) {

        List<Heartrate> hearlist = new ArrayList<Heartrate>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_Heart, new String[]{H_ID,
                        H_val, H_date, H_time}, H_date + "=?",
                new String[]{hdate}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Heartrate h = new Heartrate();
                h.setID(Integer.parseInt(cursor.getString(0)));
                h.setVal(cursor.getString(1));
                h.setdate(cursor.getString(2));
                h.setime(cursor.getString(3));
                // Adding contact to list
                hearlist.add(h);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return  list
        return hearlist;
    }

    //code to get selected rows from spo table
    public List<Sporate> getselectedspodata(String sdate) {

        List<Sporate> spolist = new ArrayList<Sporate>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_Spo, new String[]{S_ID,
                        S_val, S_date, S_time}, S_date + "=?",
                new String[]{sdate}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sporate s = new Sporate();
                s.setID(Integer.parseInt(cursor.getString(0)));
                s.setVal(cursor.getString(1));
                s.setdate(cursor.getString(2));
                s.setime(cursor.getString(3));
                // Adding contact to list
                spolist.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return  list
        return spolist;
    }

    //code to get selected rows from acc table
    public List<Accrate> getselectedaccdata(String adate) {

        List<Accrate> acclist = new ArrayList<Accrate>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_Acc, new String[]{A_ID,
                        A_val, A_date, A_time}, A_date + "=?",
                new String[]{adate}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Accrate a = new Accrate();
                a.setID(Integer.parseInt(cursor.getString(0)));
                a.setVal(cursor.getString(1));
                a.setdate(cursor.getString(2));
                a.setime(cursor.getString(3));
                // Adding contact to list
                acclist.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return  list
        return acclist;
    }

    //code to get selected rows from temp table
    public List<Temprate> getselectedtempdata(String tdate) {

        final List<Temprate> templist = new ArrayList<Temprate>();

   SQLiteDatabase db = this.getReadableDatabase();
  Cursor cursor = db.query(TABLE_Temp, new String[]{T_ID,
                   T_val, T_date, T_time}, T_date + "=?",
           new String[]{tdate}, null, null, null, null);

  // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Temprate t = new Temprate();
                t.setID(Integer.parseInt(cursor.getString(0)));
                t.setVal(cursor.getString(1));
                t.setdate(cursor.getString(2));
                t.setime(cursor.getString(3));
                // Adding contact to list
                templist.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return  list
        return templist;
    }

    // code to get the last row from heart table
    Heartrate getlastheartdata(int id) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Heart, new String[]{H_ID,
                        H_val, H_date, H_time}, H_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Heartrate h = new Heartrate(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        // return row
        return h;
    }

    // code to get the last row from spo table
    Sporate getlastspodata(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Spo, new String[]{S_ID,
                        S_val, S_date, S_time}, S_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Sporate s = new Sporate(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        // return row

        return s;
    }

    // code to get the last row from Acc table
    Accrate getlastaccdata(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Acc, new String[]{A_ID,
                        A_val, A_date, A_time}, A_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Accrate a = new Accrate(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        // return row
        return a;
    }

    // code to get the last row from Temp table
    Temprate getlastempdata(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_Temp, new String[]{T_ID,
                        T_val, T_date, T_time}, T_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Temprate t = new Temprate(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        // return row
        return t;
    }

    // Deleting single row in heart table  
    public void deleteRow(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Heart, H_date + " = ?",
                new String[]{date});
        db.close();
    }

    // Deleting ALL row in heart table  
    public int deleteAllheart() {
        SQLiteDatabase db = this.getWritableDatabase();
        int no = db.delete(TABLE_Heart, "1", null);
        db.close();
        return no;
    }

    // Deleting ALL row in spo table  
    public int deleteAllspo() {
        SQLiteDatabase db = this.getWritableDatabase();
        int no = db.delete(TABLE_Spo, "1", null);
        db.close();
        return no;
    }

    // Deleting ALL row in acc table
    public int deleteAllacc() {
        SQLiteDatabase db = this.getWritableDatabase();
        int no = db.delete(TABLE_Acc, "1", null);
        db.close();
        return no;
    }

    // Deleting ALL row in temp table  
    public int deleteAlltemp() {
        SQLiteDatabase db = this.getWritableDatabase();
        int no = db.delete(TABLE_Temp, "1", null);
        db.close();
        return no;
    }

    // Getting row Count in heat table  
    public int getheartableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Heart;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    // Getting row Count in Spo table  
    public int getspotableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Spo;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    // Getting row Count in Acc table
    public int getacctableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Acc;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    // Getting row Count in Temp table
    public int getemptableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Temp;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }
    //fetching the data from cloud and storing it in local database
    public void fetchalldata() {
        //finding today's date
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        int month1 = calendar.get(Calendar.MONTH) + 1;
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        DecimalFormat format = new DecimalFormat("00");
        year = year.substring(2);
        String month = format.format(month1);

        //creating a query object for each database in cloud
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Heart");
        query.whereEqualTo("Date", day + month + year);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Spo");
        query1.whereContains("Date", day + month + year);
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Temp");
        query2.whereContains("Date", day + month + year);
        ParseQuery<ParseObject> query3 = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername() + "_Steps");
        query3.whereContains("Date", day + month + year);

        //deleting the previous local database values
        int a = deleteAllheart();
        Log.v("rows",""+a);
        int b = deleteAllspo();
        Log.v("rows",""+b);
        int c = deleteAllacc();
        Log.v("rows",""+c);
        int d = deleteAlltemp();
        Log.v("rows",""+d);

        //getting the value form cloud and storing the value, date and time
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null) {
                    for(int i=0;i<list.size();i++)
                    addHeartdatafromcloud(new Heartrate(list.get(i).getString("Value"), list.get(i).getString("Date"), list.get(i).getString("Time")));
                }
            }
        });

        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null)
                    for(int i=0;i<list.size();i++)
                        addSpodatafromcloud(new Sporate(list.get(i).getString("Value"), list.get(i).getString("Date"), list.get(i).getString("Time")));
            }
        });

        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null)
                    for(int i=0;i<list.size();i++)
                        addTempdatafromcloud(new Temprate(list.get(i).getString("Value"), list.get(i).getString("Date"), list.get(i).getString("Time")));
            }
        });

        query3.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null)
                    for(int i=0;i<list.size();i++)
                        addAccdatafromcloud(new Accrate(list.get(i).getString("Value"), list.get(i).getString("Date"), list.get(i).getString("Time")));
            }
        });
    }

}
