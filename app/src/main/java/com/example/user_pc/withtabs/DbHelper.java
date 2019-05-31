package com.example.user_pc.withtabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Query.db";
    public static final String TABLE_NAME = "Query_history";
    private static final int DATABASE_VERSION = 1;
    public static final String  col_1 = "_id";
    public static final String col_2 = "Prob";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    //------------------------------------------------------------------------create a DB table if not exists in the device
    public void onCreate(SQLiteDatabase db) {
        Log.v("bundle","On dbhelper");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_2 + " TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    public boolean insertPerson( String query) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT "+ col_2+ " FROM " + TABLE_NAME, null );
        //__________________-///  add function for update query
        int count =0 ;
        if (res.getCount()>10){  // ------------------------------------------------------------check for query count within the table
            while (true) {
                if (count < 11) {
                    Log.v("bundle", "res count is " + String.valueOf(count));
                    ContentValues cv = new ContentValues();
                    cv.put(col_2,query);
                    db.update(TABLE_NAME,cv,"_id =" + count+1 ,null);
                    count++;
                    return true;


                } else {
                    count = 0;
                }
            }

        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(col_2, query);
            db.insert(TABLE_NAME, null, contentValues);
            return true;

        }



    }

    public Cursor getAllPersons() { //--------------------------------------------------get all query list from the DB table
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT "+ col_2+ " FROM " + TABLE_NAME, null );
        Log.v("bundle", String.valueOf(res.getCount()));
        return res;
    }

    public void deleteHistory(){ // ------------------------------------------------------deelte all query history
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, null, null);

    }
}
