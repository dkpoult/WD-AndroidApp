package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

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
        addAvailableCourses();
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
        View currentLayout = (LinearLayout)findViewById(R.id.llHomeLayout);
        View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
    }
    public void courseClicked(View v){
     // go to that course
    }

}
