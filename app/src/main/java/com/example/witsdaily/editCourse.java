package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class editCourse extends AppCompatActivity {
    String userToken, forumCode, personNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        forumCode = getIntent().getStringExtra("forumCode");

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, userToken) {
            @Override
            void getResponse(JSONObject data) {
                System.out.println(data.toString());
                try {
                    if(data.getString("responseCode").equals("successful")){
                        JSONObject courses = (JSONObject)data.getJSONArray("courses").get(0);
                        EditText name = findViewById(R.id.edtCourseName);
                        EditText courseDesc = findViewById(R.id.edtCourseDescription);
                        EditText CC = findViewById(R.id.edtCourseCode);
                        name.setText(courses.getString("courseName"));
                        courseDesc.setText(courses.getString("courseDescription"));
                        if(!courses.getBoolean("hasPassword")){
                            CC.setVisibility(View.INVISIBLE);
                        }else{
                            CC.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getCourse(forumCode);
    }

    public void updateCourse(View v) {
        EditText name = findViewById(R.id.edtCourseName);
        EditText courseDesc = findViewById(R.id.edtCourseDescription);
        EditText CC = findViewById(R.id.edtCourseCode);
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, userToken) {
            @Override
            void getResponse(JSONObject data) {
                String s;
                try {
                    if(data.getString("responseCode").equals("successful")){
                        s = "successful";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        switch (data.getString("responseCode")){
                            case "failed_unknown":
                                s = "Failed to update course information";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to update course information: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to update course information: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to update course information: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.updateCourse(forumCode, courseDesc.getText().toString(), name.getText().toString(), CC.getText().toString());
    }
}
