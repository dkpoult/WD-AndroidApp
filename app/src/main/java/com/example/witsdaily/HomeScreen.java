package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.Toast;

import com.example.witsdaily.Course.CourseRegistration;
import com.example.witsdaily.Course.EnrolledCourses;
import com.example.witsdaily.Course.UnenrolledCourses;
import com.example.witsdaily.Course.courseLink;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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

        ViewPager viewPager = findViewById(R.id.courseViewPager);
        TabLayout tabLayout = findViewById(R.id.courseTabLayout);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new EnrolledCourses(),"Enrolled Courses");
        adapter.addFragment(new UnenrolledCourses(),"Not Courses");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


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



}
