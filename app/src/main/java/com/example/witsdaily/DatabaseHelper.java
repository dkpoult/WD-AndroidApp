package com.example.witsdaily;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.witsdaily.post;

import static android.content.ContentValues.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	
	private String DB_PATH;
	private String DB_NAME;
	private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param dbname
	 */
	public DatabaseHelper(Context p,String dbname) {
		super(p, dbname + ".db", null, 1); 
		DB_NAME = dbname + ".db";
	}
	
	public String getNow(){
		return sdf.format(new java.util.Date());
	}

	public Cursor doQuery(String sql, String[] params) {
		try {
			Cursor mCur = getReadableDatabase().rawQuery(sql, params);
			return mCur;
		} catch (SQLException mSQLException) {
			System.err.println("-- doQuery --\n"+sql);
			mSQLException.printStackTrace(System.err);
			return null;
		}
	}
	
	public void doUpdate(String sql, String[] params) {
		try {
			 getWritableDatabase().execSQL(sql, params);
		} catch (SQLException mSQLException) {
			System.err.println("-- doUpdate --\n"+sql);
			mSQLException.printStackTrace(System.err);
		}
	}
	
	
	public Cursor doQuery(String sql) {
		try {
			Cursor mCur = getReadableDatabase().rawQuery(sql,null);
			return mCur;
		} catch (SQLException mSQLException) {
			System.err.println("-- doQuery --\n"+sql);
			mSQLException.printStackTrace();
			return null;
		}
	}
		
	public void doUpdate(String sql) {
		try {
			 this.getWritableDatabase().execSQL(sql);
		} catch (SQLException mSQLException) {
			System.err.println("-- doUpdate --\n"+sql);
			mSQLException.printStackTrace(System.err);
		}
	}

	public long getSize()
	{
		/* Open the database object in "read" mode. */
	    final SQLiteDatabase db = getReadableDatabase();

	    /* Get length of database file. */
        final String dbPath       = db.getPath();        
        final File   dbFile       = new File(dbPath);
        final long   dbFileLength = dbFile.length();
        
        return (dbFileLength);
	}

    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE VOTED(\n" +
                "    postID INT NOT NULL UNIQUE,\n" +
                "    TYPE TINYINT(1) NOT NULL\n" +
                ");");
	   	db.execSQL("CREATE TABLE POST(\n" +
                "    postID INT PRIMARY KEY,\n" +
                "    dovsID TEXT NOT NULL UNIQUE,\n" +
                "    courseID INT NOT NULL,\n" +
                "    title TEXT,\n" +
                "    body TEXT NOT NULL,\n" +
                "    isLocked TINYINT(1) NOT NULL,\n" +
                "    isComment TINYINT(1) NOT NULL,\n" +
                "    isAnswer TINYINT(1) NOT NULL,\n" +
                "    postDate DATETIME NOT NULL,\n" +
                "    parentID INT,\n" +
                "    sender TEXT NOT NULL,\n" +
                "    upVotes INT NOT NULL,\n" +
                "    downVotes INT NOT NULL\n" +
                ");");

	}

	public SQLiteDatabase getDB(){
		return getWritableDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
