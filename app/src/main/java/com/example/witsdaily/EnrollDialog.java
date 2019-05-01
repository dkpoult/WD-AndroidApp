package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class EnrollDialog extends AppCompatActivity {
    String courseCode,personNumber,user_token,courseID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_course);
        Intent i = getIntent();
        courseID = i.getStringExtra("courseCode");
        TextView courseCodeTV = (TextView)findViewById(R.id.tvCourseCode);
        getCourseCode();
        courseCodeTV.setText(courseCode);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);

    }
    public void clickEnroll(View v){
        EditText passEdit  = (EditText)findViewById(R.id.edtPassword);
        String password = passEdit.getText().toString();
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personNumber);
            params.put("password", password);
            params.put("userToken",user_token);
            params.put("courseCode",courseCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/course/enrol_in_course", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            Toast.makeText(getApplicationContext(),response.getString("responseCode") , Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                })
        {
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);

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
