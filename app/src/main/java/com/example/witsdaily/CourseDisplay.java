package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.witsdaily.Forum.LectureCourseForum;
import com.example.witsdaily.Survey.SurveyCreator;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class CourseDisplay extends AppCompatActivity {
    int courseID;
    String courseCodeString;
    String user_token,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_display);
        Intent i  = getIntent();
        courseID = Integer.parseInt(i.getStringExtra("courseID"));
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        updateFields();
    }
    public void clickAnnouncement(View v){
        Intent i = new Intent(CourseDisplay.this, AnnouncementSender.class);
        i.putExtra("courseCode",courseCodeString);
        startActivity(i);
    }
    private void updateFields(){ // fill in values, take from local database
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        TextView courseName = (TextView)findViewById(R.id.tvCourseName);
        TextView courseCode = (TextView)findViewById(R.id.tvCourseCode);
        TextView courseDescription = (TextView)findViewById(R.id.tvDescription);

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
}
