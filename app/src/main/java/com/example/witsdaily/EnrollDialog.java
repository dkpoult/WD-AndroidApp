package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class EnrollDialog extends AppCompatActivity {
    String courseCode,personNumber,userToken,courseID;
    StorageAccessor syncAccessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_course);
        Intent i = getIntent();
        courseID = i.getStringExtra("courseCode");
        TextView courseCodeTV = (TextView)findViewById(R.id.tvCourseCode);
        getCourseCode();
        courseCodeTV.setText(courseCode);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        syncAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            void getData(JSONObject data) {

            }
        };

    }
    public void clickEnroll(View v){
        EditText passEdit  = (EditText)findViewById(R.id.edtPassword);
        String password = passEdit.getText().toString();
        StorageAccessor dataAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        linkUser();
                    }

                    Toast.makeText(EnrollDialog.this, data.getString("responseCode"),
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        dataAccessor.enrollUser(password,courseCode);
    }
    private void linkUser(){

        syncAccessor.linkUserToCourse(courseID);
    }
    private void getCourseCode(){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "Select * From "+ TableCourse.TABLE_NAME+" where "+TableCourse.COLUMN_NAME_ID +" = "+courseID;
        Cursor cursor = db.rawQuery(sql,null); //have to do a better table name here

        while(cursor.moveToNext()) {
            courseCode = cursor.getString(
                    cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_CODE));
            }
        cursor.close();
    }
}
