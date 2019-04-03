package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class HomeScreen extends AppCompatActivity {

    String user_token;
    String personNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //getApplicationContext().deleteDatabase("PhoneDatabase.db");
        firebaseAuthenticate();
        addAvailableCourses();
        //syncCourses();
        // addRow();// assuming it worked

            //testDisplay();
            addUserToDB();
       // sendRequest();

        //String customToken = FirebaseAuth.getInstance().createCustomToken(uid);

    }
    private void firebaseAuthenticate(){
        
    }
    private void addUserToDB(){ // possibly encrypt student number
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * From "+TablePerson.TABLE_NAME+" Where "+ TablePerson.COLUMN_NAME_NUMBER+" = " +
                "?",new String[]{personNumber}); //have to do a better table name here

        if (!cursor.moveToFirst()){
            //add user
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TablePerson.COLUMN_NAME_NUMBER,personNumber);
            db.insertOrThrow(TablePerson.TABLE_NAME,null,values);
        }

        cursor.close();

        // add all the known courses
    }

    private void addRow(){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
      //  values.put(TableCourse.COLUMN_NAME_CODE, "COMS1018");
       // values.put(TableCourse.COLUMN_NAME_DESCRIPTION, "This is a cool course where you programme stuff and its a firrst year thing");
       // values.put(TableCourse.COLUMN_NAME_ID, "");
       // values.put(TableCourse.COLUMN_NAME_LECTURER, "Steve");
        //values.put(TableCourse.COLUMN_NAME_NAME, "Introduction to algorithms and programming");
        //values.put(TableCourse.COLUMN_NAME_SYNCED, "2016-03-04 11:30");

        values.put(TablePersonCourse.COLUMN_NAME_COURSEID,"COMS1018");
        values.put(TablePersonCourse.COLUMN_NAME_PERSONNUMBER,personNumber);
// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insertOrThrow(TablePersonCourse.TABLE_NAME, null, values);
        if (newRowId>0)
            System.out.println("viva");
        else
            System.out.println("not cool man");

    }
    private void testDisplay(){
        Intent i = new Intent(HomeScreen.this, CourseDisplay.class);
        i.putExtra("courseID","COMS1018");
        startActivity(i);
    }
    private void syncCourses(){  // discuss with rest how to handle the syncing

        JSONObject params = new JSONObject();
        System.out.println("Test");
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/sync_courses", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        processRequest(response);
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

    public void Link(View v){
        Intent i = new Intent(HomeScreen.this, courseLink.class);
        startActivity(i);
    }
    public void Create(View v){
        Intent i = new Intent(HomeScreen.this, CourseRegistration.class);
        startActivity(i);
    }
    private void addAvailableCourses(){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        View currentLayout = (LinearLayout)findViewById(R.id.llHomeLayout);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "Select * from "+ TableCourse.TABLE_NAME
                +" JOIN "+ TablePersonCourse.TABLE_NAME +" ON " + TableCourse.COLUMN_NAME_ID
                + " = " + TablePersonCourse.COLUMN_NAME_COURSEID
                + " WHERE " + TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                + " = \""+ personNumber+"\"";
        Cursor cursor = db.rawQuery(sql,null); //have to do a better table name here

        while(cursor.moveToNext()) {
            String courseID = cursor.getString(
                    cursor.getColumnIndexOrThrow(TablePersonCourse.COLUMN_NAME_COURSEID));
            View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
            courseBrief.setTag(courseID);
            TextView name = (TextView)(courseBrief.findViewById(R.id.tvName));
            TextView description = (TextView)(courseBrief.findViewById(R.id.tvDescription));
            name.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_NAME)));
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_DESCRIPTION)));
            ((LinearLayout) currentLayout).addView(courseBrief);
        }
        cursor.close();

    }
    public void courseClicked(View v){
     // go to that course
        Intent i = new Intent(HomeScreen.this, CourseDisplay.class);
        i.putExtra("courseID",String.valueOf(v.getTag()));
        startActivity(i);
    }

    private void sendRequest(){
        JSONObject params = new JSONObject();
        System.out.println("Test");
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/get_courses", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        processRequest(response);
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
    public void clickViewAllCourses(View v){

    }
    private void processRequest(JSONObject response){
        String output = null;
        try {
            output = response.getString("responseCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
