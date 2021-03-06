package com.example.witsdaily;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.witsdaily.PhoneDatabaseContract.*;

public class PhoneDatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PhoneDatabase.db";
    public static final String SQL_CREATE_COURSES = "CREATE TABLE "+ TableCourse.TABLE_NAME +
            " (" +TableCourse.COLUMN_NAME_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            TableCourse.COLUMN_NAME_CODE+" VARCHAR,"+
            TableCourse.COLUMN_NAME_NAME+" VARCHAR,"+
            TableCourse.COLUMN_NAME_DESCRIPTION+" VARCHAR,"+
            TableCourse.COLUMN_NAME_LECTURER+" VARCHAR,"+
            TableCourse.COLUMN_NAME_SYNCED+" DATETIME)";

    public static final String SQL_CREATE_PERSON = "CREATE TABLE "+ TablePerson.TABLE_NAME +
            " (" +TablePerson.COLUMN_NAME_NUMBER+" VARCHAR PRIMARY KEY,"+
            TablePerson.COLUMN_NAME_NAME+" VARCHAR)";

    public static final String SQL_CREATE_PERSONCOURSE = "CREATE TABLE "+ TablePersonCourse.TABLE_NAME +
            " (" +TablePersonCourse.COLUMN_NAME_PERSONNUMBER+" VARCHAR,"+
            TablePersonCourse.COLUMN_NAME_COURSEID+" INTEGER)";

    public static final String SQL_CREATE_SETTINGS = "CREATE TABLE "+ TableSettings.TABLE_NAME +
            " (" +TableSettings.COLUMN_NAME_LANGUAGE+" VARCHAR,"+
            TableSettings.COLUMN_NAME_NOTIFICATIONS+" BOOLEAN, "+TableSettings.COLUMN_NAME_PERSONNUMBER+" VARCHAR)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+TableCourse.TABLE_NAME;

    public PhoneDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COURSES);
        db.execSQL(SQL_CREATE_PERSON);
        db.execSQL(SQL_CREATE_PERSONCOURSE);
        db.execSQL(SQL_CREATE_SETTINGS);
        db.execSQL("Insert into "+TableSettings.TABLE_NAME+" ("+TableSettings.COLUMN_NAME_NOTIFICATIONS
        +","+TableSettings.COLUMN_NAME_LANGUAGE+","+TableSettings.COLUMN_NAME_PERSONNUMBER+") values (1,'English',-1)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES); // have to delete a lot more than one table
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}