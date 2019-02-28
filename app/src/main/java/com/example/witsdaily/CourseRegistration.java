package com.example.witsdaily;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CourseRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);
    }


    /*Give: 'course_code' - 8 character code. 'course_name' - Any name.
    'course_description' - Arbitrary description.
    Returns: 'response_code' - Can be:

    "successful"
    "failed_already_xists"
    "failed_no_perm"
    "failed_missing_param"
    "failed_invalid_param"
    "failed_unknown"

    */
}
