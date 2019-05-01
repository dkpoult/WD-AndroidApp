package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class UnregisteredCourses extends AppCompatActivity {
String userToken,personNumber;
StorageAccessor syncAccessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unregistered_courses);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        syncAccessor  = new StorageAccessor(this, personNumber,userToken){
            @Override
            void getData(JSONObject data) {
                System.out.println("Successful sync task complete");
            }
        };
        addAvailableCourses();
    }

    private void addAvailableCourses(){
        View currentLayout = (LinearLayout)findViewById(R.id.llUnregister);
        ((LinearLayout) currentLayout).removeAllViews(); // clears this for when async is done
        JSONArray value = syncAccessor.getUCourses();
        for (int i =0;i<value.length();i++){
            try {
                View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
                TextView name = (TextView)(courseBrief.findViewById(R.id.tvName));
                TextView description = (TextView)(courseBrief.findViewById(R.id.tvDescription));
                ImageView colorBar = (ImageView)(courseBrief.findViewById(R.id.imgColor));

                String courseID = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_ID); // or whatever whatever
                String courseName = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_NAME);
                String courseDescription = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_DESCRIPTION);

                courseBrief.setTag(courseID);
                name.setText(courseName);
                description.setText(courseDescription);

                colorBar.setImageDrawable(getResources().getDrawable(R.color.colorAccent));
                ((LinearLayout) currentLayout).addView(courseBrief);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void courseClicked(View v){ // means they're already enrolled
        // go to that course
        Intent i = new Intent(UnregisteredCourses.this, EnrollDialog.class);
        i.putExtra("courseCode",v.getTag().toString()); // course code ?
        startActivity(i);
    }

}
