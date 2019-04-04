package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class HomeScreen extends AppCompatActivity {
 // FirebaseInstanceId.getInstance().deleteInstanceId(); good for logout button
    String user_token;
    String personNumber;
    String firebaseToken;
    private FirebaseAuth mAuth;
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

        FirebaseApp.initializeApp(getApplicationContext());
        firebaseAuthenticate();
        getCourses();
        addAvailableCourses();

        //syncCourses();
        // addRow();// assuming it worked

            //testDisplay();
            addUserToDB();
       // sendRequest();

    }
    private void updateServerFCMToken(String newToken){
        JSONObject params = new JSONObject();
        System.out.println("Test");
        try {
            params.put("fcmToken",newToken);
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/set_fcm_token", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        Toast.makeText(getApplicationContext(),response.toString() , Toast.LENGTH_LONG).show();
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
    private void loginCustomTokenFirebase(final String mCustomToken){
        mAuth = FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>(){
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newFCM = instanceIdResult.getToken();
                        updateServerFCMToken(newFCM);
                    }
                });
        mAuth.signInWithCustomToken(mCustomToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseToken = mCustomToken;
                            System.out.println("signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCustomToken:failure :"+ task.getException());
                            Toast.makeText(HomeScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
    private void firebaseAuthenticate(){

        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/notification_token", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){

                        try {
                            System.out.println(response.getString("customToken"));
                            loginCustomTokenFirebase(response.getString("customToken"));

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
    private void processGetCourses(JSONObject response) throws Exception{
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!response.getString("responseCode").equals("successful")){
            return;
        }
        JSONArray coursesList = response.getJSONArray("courses");
        for (int i =0;i<coursesList.length();i++) {
            response = coursesList.getJSONObject(i);
            if (containsCourseCode(response.getString("courseCode"))){
                continue;
            }
            ContentValues values = new ContentValues();
            values.put(TableCourse.COLUMN_NAME_CODE, response.getString("courseCode"));
            values.put(TableCourse.COLUMN_NAME_DESCRIPTION, response.getString("courseDescription"));
            values.put(TableCourse.COLUMN_NAME_LECTURER, "lecturer");
            values.put(TableCourse.COLUMN_NAME_NAME, "courseName");
            // other values to add to actual db

            long result = db.insertOrThrow(TableCourse.TABLE_NAME, null, values);
            if (result <= 0) {
                return;
            }
            values = new ContentValues();
            values.put(TablePersonCourse.COLUMN_NAME_PERSONNUMBER, personNumber);
            values.put(TablePersonCourse.COLUMN_NAME_COURSEID, getLatestCourseInsertID());
            long result2 = db.insertOrThrow(TablePersonCourse.TABLE_NAME, null, values);
        }
    }
    private boolean containsCourseCode(String courseCode){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * From "+TableCourse.TABLE_NAME + " where "+TableCourse.COLUMN_NAME_CODE+" = \""+
                courseCode+"\"",null);
        if (!cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    private int getLatestCourseInsertID(){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * From "+TableCourse.TABLE_NAME + " order by "+TableCourse.COLUMN_NAME_ID+" desc",null); //have to do a better table name here
        int latestID = 0;
        if (cursor.moveToFirst()){
            //add user
            latestID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(TableCourse.COLUMN_NAME_ID));
        }
        cursor.close();
        return latestID;
    }
    private void getCourses(){
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
                        System.out.println(response);
                        try {
                            processGetCourses(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), "Error getting courses", Toast.LENGTH_SHORT).show();
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
