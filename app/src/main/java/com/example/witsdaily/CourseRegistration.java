package com.example.witsdaily;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CourseRegistration extends AppCompatActivity {
    TextView tvState = (TextView) findViewById(R.id.tvState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);
    }

    public void clickRegistration(View v) {
        //when the user clicks the button this is the code that creates the course
        if (!validFields()) {

            return;
        } else {

        }
    }

    private boolean validFields() {
        // prevents sql injection, makes sure that passwords match etc
        tvState.setTextColor(Color.RED);
        tvState.setText("Failed:"); // this should specify what is wrong
        return true;
    }

    private void handleResponse(String response) { // handle response once the volley script is complete
        tvState.setTextColor(Color.RED); // it wont display if successful
        String stateText = "";
        switch (response){
            case "successful": /*do something*/;
                Toast.makeText(CourseRegistration.this, "Successfully created new course," +
                        ""      , Toast.LENGTH_LONG).show();;return;
            case "failed_already_exists": stateText = "Course already exists";break;
            case "failed_no_perm": stateText = "No permissions to add course";break;
            case "failed_missing_param": stateText = "Missing parameters";break;
            case "failed_invalid_param": stateText = "Invalid field";break;
            case "failed_unknown": stateText = "Unknown";break;
        }
        Toast.makeText(CourseRegistration.this, "Failed To Create Course" , Toast.LENGTH_LONG).show();
        tvState.setText("Failed: "+stateText);
    }
    }
