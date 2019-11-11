package com.example.witsdaily.Course;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.AnnouncementSender;
import com.example.witsdaily.ChatActivity;
import com.example.witsdaily.Forum.LectureCourseForum;
import com.example.witsdaily.LiveQuestions;
import com.example.witsdaily.NetworkAccessor;
import com.example.witsdaily.PhoneDatabaseHelper;
import com.example.witsdaily.R;
import com.example.witsdaily.Survey.SurveyCreator;
import com.example.witsdaily.ToolbarActivity;
import com.example.witsdaily.addTutors;
import com.example.witsdaily.editCourse;
import com.example.witsdaily.editSessions;
import com.example.witsdaily.makeBooking;
import com.example.witsdaily.viewBookableSessions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class CourseDisplay extends ToolbarActivity {
    int courseID;
    String courseCodeString;
    private static final long  lect = 128|64|32|16|8|4|2|1;
    private static final long  tutor = 128|64|1;
    String user_token,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_display);
        setupAppBar();
        Intent i  = getIntent();
        courseID = Integer.parseInt(i.getStringExtra("courseID"));
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        updateFields();
        hideRelevantButtons();

    }

    public void hideRelevantButtons(){
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                try {
                    String s = data.getString("responseCode");
                    if (s.equals("successful")) {
                        JSONArray t = data.getJSONArray("courses");
                        System.out.println(t.toString());
                        JSONObject course = t.getJSONObject(0);
                        Button b = findViewById(R.id.editSession);
                        Button b1 = findViewById(R.id.editCourse);
                        Button b2 = findViewById(R.id.addTutors);
                        Button b3 = findViewById(R.id.btnCreateSurvey);
                        Button b4 = findViewById(R.id.btnTutorChat);
                        Button b5 = findViewById(R.id.editBookings);
                        Button b6 = findViewById(R.id.btnAnnouncement);
                        b1.setVisibility(View.GONE);
                        b2.setVisibility(View.GONE);
                        b3.setVisibility(View.GONE);
                        b4.setVisibility(View.GONE);
                        b5.setVisibility(View.GONE);
                        b6.setVisibility(View.GONE);
                        b.setVisibility(View.GONE);
//                        System.out.println(personNumber);
                        long lecturer = course.getLong("permissions");
                        ImageButton IB = findViewById(R.id.imageButton);
                        IB.setVisibility(View.GONE);
                        System.out.println(t.toString());
//                        System.out.println(lecturer.getString("personNumber"));
                        if (lecturer == lect) {
                            b.setVisibility(View.VISIBLE);
                            b1.setVisibility(View.VISIBLE);
                            b2.setVisibility(View.VISIBLE);
                            b3.setVisibility(View.VISIBLE);
                            b4.setVisibility(View.VISIBLE);
                            b5.setVisibility(View.VISIBLE);
                            b6.setVisibility(View.VISIBLE);
                        } else if (lecturer == tutor) {
                            b4.setVisibility(View.VISIBLE);
                        }
                        if(course.has("moodleId") && lecturer == lect){
                            IB.setVisibility(View.VISIBLE);
                        }
                    }else{
                        switch (s){
                            case "failed_unknown":
                                s = "Failed to get course: ";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }   ;
        NA.getCourse(courseCodeString);
    }

    public void clickAnnouncement(View v){
        Intent i = new Intent(CourseDisplay.this, AnnouncementSender.class);
        i.putExtra("courseCode",courseCodeString);
        startActivity(i);
    }
    private void updateFields(){ // fill in values, take from local database
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        TextView courseName = findViewById(R.id.tvCourseName);
        TextView courseCode = findViewById(R.id.tvCourseCode);
        TextView courseDescription = findViewById(R.id.tvDescription);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlString  = "Select * from "+ TableCourse.TABLE_NAME
                +" where "+ TableCourse.COLUMN_NAME_ID+ " = " + courseID;
        //String sqlString  = "Select * from "+TableCourse.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlString,null); //have to do a better table name here

        while(cursor.moveToNext()) {
            courseCode.setText(
                    cursor.getString(cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_CODE)));
            courseCodeString = courseCode.getText().toString();
            courseName.setText(
                    cursor.getString(cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_NAME)));
            courseDescription.setText(
                    cursor.getString(cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_DESCRIPTION)));
        }
        cursor.close();
    }
    public void clickChat(View v){
        Intent i = new Intent(CourseDisplay.this, ChatActivity.class);
        i.putExtra("courseCode",courseCodeString);
        i.putExtra("audience","normal");
        startActivity(i);
    }
    public void clickTutorChat(View v){
        Intent i = new Intent(CourseDisplay.this, ChatActivity.class);
        i.putExtra("courseCode",courseCodeString);
        i.putExtra("audience","tutor");
        startActivity(i);
    }
    public void clickQuestions(View v){
        Intent i = new Intent(CourseDisplay.this, LiveQuestions.class);
        i.putExtra("courseCode",courseCodeString);
        i.putExtra("audience","normal");
        startActivity(i);
    }
    public void clickSurvey(View v){
        Intent i = new Intent(CourseDisplay.this, SurveyCreator.class);
        i.putExtra("courseCode",courseCodeString);
        startActivity(i);
    }

    public void doForum(View view){
        Intent i = new Intent(CourseDisplay.this, LectureCourseForum.class);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void editSessions(View v) {
        Intent i = new Intent(CourseDisplay.this, editSessions.class);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void editCourse(View v) {
        Intent i = new Intent(CourseDisplay.this, editCourse.class);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void editBookings(View v) {
        Intent i = new Intent(CourseDisplay.this, viewBookableSessions.class);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void makeBooking(View v) {
        Intent i = new Intent(CourseDisplay.this, makeBooking.class);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void addTutors(View v) {
        Intent i = new Intent(CourseDisplay.this, addTutors.class);
        i.putExtra("courseCode", courseCodeString);
        i.putExtra("forumCode", courseCodeString);
        startActivity(i);
    }

    public void resyncData(View v){
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                try {
                    String s = data.getString("responseCode");
                    switch (s){
                        case "successful":
                            s = "successful";
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_unknown":
                            s = "Failed to sync course: ";
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_invalid_params":
                            s = "Failed to sync course: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_params":
                            s = "Failed to sync course: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_perms":
                            s = "Failed to sync course: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_not_linked":
                            s = "Failed to sync course since this course is not a Moodle course: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.resync(courseCodeString);
    }
}
