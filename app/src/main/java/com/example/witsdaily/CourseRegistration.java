package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseRegistration extends AppCompatActivity {
    EditText edtCourseCode;
    EditText edtCourseName;
    EditText edtCourseDescription;
    TextView tvState;

    String userID;
    String userToken;// get Dylan to send this to me through activity data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);
        tvState = findViewById(R.id.tvState);
        edtCourseCode = findViewById(R.id.edtCourseCode);
        edtCourseName = findViewById(R.id.edtCourseName);
        edtCourseDescription = findViewById(R.id.edtCourseDescription);
        tvState.setVisibility(View.INVISIBLE);
        userID = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
    }

    public void clickRegistration(View v) {
        //when the user clicks the button this is the code that creates the course
        if (!validFields()) {
            tvState.setVisibility(View.VISIBLE);
            return;
        } else {
            tvState.setVisibility(View.INVISIBLE);
        }

        // this is basically all sending the network request
        final String courseName = edtCourseName.getText().toString();
        final String courseDescription = edtCourseDescription.getText().toString();
        final String courseCode = edtCourseCode.getText().toString();
        JSONObject params = new JSONObject();
        try {

            System.out.println(userID);
            System.out.println(userToken);
            System.out.println(courseCode);
            System.out.println(courseName);
            System.out.println(courseDescription);
            params.put("personNumber", userID);
            params.put("userToken", userToken);
            params.put("courseCode", courseCode);
            params.put("courseName", courseName);
            params.put("courseDescription", courseDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!validFields()) {
            tvState.setVisibility(View.VISIBLE);
            return;
        } else {
            tvState.setVisibility(View.INVISIBLE);
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/course/create_course", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            handleResponse(response.toString());
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
    private boolean validString(String currentString){ // consider adding this to a class to handle encryption/hashing/validating
        return true;
    }

    private boolean validFields() {
        // prevents sql injection, makes sure that passwords match etc
        tvState.setTextColor(Color.RED);
        if (edtCourseCode.getText().length()!=8){
            tvState.setText("Failed: Course code must be 8 characters long");
            return false;
        }
        if (!validString(edtCourseName.getText().toString())){
            tvState.setText("Failed: Invalid characters entered");
            return false;
        }
        if (edtCourseName.getText().toString().equals("")){
            tvState.setText("Failed: Must be a course name");
            return false;
        }

        return true;
    }

    private void handleResponse(String jResponse) throws JSONException { // handle response once the volley script is complete
        JSONObject jsonObject = new JSONObject(jResponse);
        String response = jsonObject.getString("responseCode");
        tvState.setTextColor(Color.RED); // it wont display if successful
        String stateText = "";
        System.out.println(response);
        switch (response){
            case "successful":
                Toast.makeText(CourseRegistration.this, "Successfully created new course," +
                        ""      , Toast.LENGTH_LONG).show();
                Intent i = new Intent(CourseRegistration.this, HomeScreen.class);
                startActivity(i);
                return;
            case "failed_already_exists": stateText = "Course already exists";break;
            case "failed_no_perm": stateText = "No permissions to add course";break;
            case "failed_missing_params": stateText = "Missing parameters";break;
            case "failed_invalid_params": stateText = "Invalid field";break;
            case "failed_unknown": stateText = "Unknown";break;
        }
        Toast.makeText(CourseRegistration.this, "Failed To Create Course" , Toast.LENGTH_LONG).show();
        tvState.setText("Failed: "+stateText);
    }
    }
