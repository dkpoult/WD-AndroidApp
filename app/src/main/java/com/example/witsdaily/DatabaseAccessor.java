package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseAccessor {
    PhoneDatabaseHelper dbHelper;
    public DatabaseAccessor(Context context){
        dbHelper = new PhoneDatabaseHelper(context);
    }
    public void insertValues(ContentValues values,String tableName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            long result = db.insertOrThrow(tableName, null, values);
            if (result <= 0) {
                return;
            }
            System.out.println("Successfull insert");
            values = new ContentValues();
    }

    public JSONArray selectRecords(String sqlStatement)throws Exception{
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery(sqlStatement,null); //have to do a better table name here


        while(cursor.moveToNext()) {
            jsonObject = new JSONObject();
            for (int i =0;i<cursor.getColumnCount();i++){
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                jsonObject.put(columnName,value);
            }
            jsonArray.put(jsonObject);
        }
        cursor.close();
        return jsonArray;
    }
}
