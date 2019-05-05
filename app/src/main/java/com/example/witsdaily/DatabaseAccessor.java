package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

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
    public String courseCodeToID(String courseCode){
        JSONArray records;
        try {
            String sql ="Select "+ TableCourse.COLUMN_NAME_ID+" From "+
                    TableCourse.TABLE_NAME+" where "+ TableCourse.COLUMN_NAME_CODE+" = \""+courseCode+"\"";
            records = selectRecords(sql);
            if (records.length()>0)
               return records.getJSONObject(0).getString(TableCourse.COLUMN_NAME_ID);
            else
                return "-1";
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    public JSONArray getLocalCourses(String personNumber){
        String sql = "Select * from "+ TableCourse.TABLE_NAME
                +" JOIN "+ TablePersonCourse.TABLE_NAME +" ON " + TableCourse.COLUMN_NAME_ID
                + " = " + TablePersonCourse.COLUMN_NAME_COURSEID
                + " WHERE " + TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                + " = \""+ personNumber+"\"";
        JSONArray values = null;
        try {
            values = selectRecords(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public boolean userLinked(String courseID,String personNumber){
        JSONArray records;
        try {
            String sql = "Select * From "+
                    TablePersonCourse.TABLE_NAME+ " where "+TablePersonCourse.COLUMN_NAME_COURSEID+" =" +
                    " "+courseID+" and "+ TablePersonCourse.COLUMN_NAME_PERSONNUMBER+" = \""+personNumber+"" +
                    "\"";
            records = selectRecords(sql);
            if (records.length()>=1)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public boolean containsCourseCode(String courseCode){
        String sql = "Select * From "+TableCourse.TABLE_NAME + " where "+
                TableCourse.COLUMN_NAME_CODE+" = \""+
                courseCode+"\"";
        JSONArray records;
        try {
            records = selectRecords(sql);
            if (records.length()>0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
