package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.Course.CourseDisplay;
import com.example.witsdaily.Course.CourseRegistration;
import com.example.witsdaily.Course.UnregisteredCourses;
import com.example.witsdaily.Course.courseLink;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public class HomeScreen extends ToolbarActivity {
 // FirebaseInstanceId.getInstance().deleteInstanceId(); good for logout button
    String userToken;
    String personNumber;
    String firebaseToken;
    StorageAccessor syncAccessor;
    boolean enableNotifications = true;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    try {
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
    }catch (Exception e){
        Intent currentIntent = getIntent();
        userToken = currentIntent.getStringExtra("user_token");
        personNumber = currentIntent.getStringExtra("person_number");
    }

        loadSettings();
        setContentView(R.layout.activity_home_screen);
        setupAppBar();
        syncAccessor  = new StorageAccessor(this, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                System.out.println("Successful sync task complete");
            }
        };

        addUserToDB();

        if (enableNotifications) {
            FirebaseApp.initializeApp(getApplicationContext());
            firebaseAuthenticate();
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }



        getCourses();
        getUnenrolledCourses();
        addAvailableCourses();


    }

    @Override
    protected void onResume() {
        super.onResume();
        addAvailableCourses();
    }



    private void updateServerFCMToken(String newToken){
        StorageAccessor dataAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                System.out.println("Successful fcm change");
            }
        };
        dataAccessor.updateServerFCMToken(newToken);
    }
    private void loginCustomTokenFirebase(final String mCustomToken){
        try{
            mAuth.signOut();
        }
        catch (Exception e){

        }
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
        StorageAccessor dataAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {

                try {
                    System.out.println(data.getString("customToken"));
                    loginCustomTokenFirebase(data.getString("customToken"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        dataAccessor.firebaseAuthenticate();
    }
    private void loadSettings(){
        WitsDailySettings settings = new WitsDailySettings(personNumber,userToken,getBaseContext(),this);
        settings.loadLanguage(personNumber);
        if (settings.getCurrentNotifications()==1){
            enableNotifications = false;
        }
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
            values = new ContentValues();
            values.put(TableSettings.COLUMN_NAME_NOTIFICATIONS,0);
            values.put(TableSettings.COLUMN_NAME_PERSONNUMBER,personNumber);
            values.put(TableSettings.COLUMN_NAME_LANGUAGE,"English");
            db.insertOrThrow(TableSettings.TABLE_NAME,null,values);
        }

        cursor.close();

        // add all the known courses
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

        View currentLayout = (LinearLayout)findViewById(R.id.llCourseLayout);
        ((LinearLayout) currentLayout).removeAllViews(); // clears this for when async is done
            JSONArray value = syncAccessor.getLocalCourses();
            for (int i =0;i<value.length();i++){
                try {
                    View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
                    TextView name = (TextView)(courseBrief.findViewById(R.id.tvName));
                    TextView description = (TextView)(courseBrief.findViewById(R.id.tvDescription));

                    String courseID = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_ID); // or whatever whatever
                    String courseName = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_NAME);
                    String courseDescription = value.getJSONObject(i).getString(TableCourse.COLUMN_NAME_DESCRIPTION);

                    courseBrief.setTag(courseID);
                    name.setText(courseName);
                    description.setText(courseDescription);
                    ((LinearLayout) currentLayout).addView(courseBrief);

                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }
    public void courseClicked(View v){ // means they're already enrolled
     // go to that course
        Intent i = new Intent(HomeScreen.this, CourseDisplay.class);
        i.putExtra("courseID",String.valueOf(v.getTag()));
        startActivity(i);
    }
    private void processGetCourses(JSONObject response,boolean enrolled) throws Exception{
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!response.getString("responseCode").equals("successful")){
            return;
        }
        JSONArray coursesList = response.getJSONArray("courses");
        for (int i =0;i<coursesList.length();i++) {
            response = coursesList.getJSONObject(i);

            ContentValues values = new ContentValues();
            if (!syncAccessor.containsCourseCode(response.getString("courseCode"))){
                values.put(TableCourse.COLUMN_NAME_CODE, response.getString("courseCode"));
                values.put(TableCourse.COLUMN_NAME_DESCRIPTION, response.getString("courseDescription"));
                values.put(TableCourse.COLUMN_NAME_LECTURER, response.getString("lecturer"));
                values.put(TableCourse.COLUMN_NAME_NAME, response.getString("courseName"));
                long result = db.insertOrThrow(TableCourse.TABLE_NAME, null, values);
                if (result <= 0) {
                    return;
                }
            }
            String courseID = syncAccessor.courseCodeToID(response.getString("courseCode"));
            if (enrolled && !syncAccessor.userLinked(courseID)){
                values = new ContentValues();
                values.put(TablePersonCourse.COLUMN_NAME_PERSONNUMBER, personNumber);
                values.put(TablePersonCourse.COLUMN_NAME_COURSEID, courseID);
                long result2 = db.insertOrThrow(TablePersonCourse.TABLE_NAME, null, values);

            }
        }
        addAvailableCourses(); // just to refresh the ui
    }


    private void getCourses(){
        StorageAccessor dataAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                try {
                    processGetCourses(data,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getEnrolledCourses();
    }
    private void getUnenrolledCourses(){
        StorageAccessor dataAccessor = new StorageAccessor(this, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                try {
                    processGetCourses(data,false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getUnenrolledCourses();
    }
    public void clickViewAllCourses(View v){
        Intent i = new Intent(HomeScreen.this, UnregisteredCourses.class);
        startActivity(i);
    }    private void processRequest(JSONObject response){
        String output = null;
        try {
            output = response.getString("responseCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
