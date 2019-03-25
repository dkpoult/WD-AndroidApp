package com.example.witsdaily;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;

import com.example.witsdaily.PhoneDatabaseContract.TableCourse;

public class PhoneDatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PhoneDatabase.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+ TableCourse.TABLE_NAME +
            " (" +TableCourse.COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            TableCourse.COLUMN_NAME_CODE+" VARCHAR,"+
            TableCourse.COLUMN_NAME_NAME+" VARCHAR,"+
            TableCourse.COLUMN_NAME_DESCRIPTION+" VARCHAR,"+
            TableCourse.COLUMN_NAME_LECTURER+" VARCHAR,"+
            TableCourse.COLUMN_NAME_SYNCED+"DATETIME)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+TableCourse.TABLE_NAME;

    public PhoneDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}