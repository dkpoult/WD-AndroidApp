package com.example.witsdaily;

<<<<<<< HEAD
=======
import android.content.Context;
import android.content.Intent;
>>>>>>> courseregistration
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD
import android.support.v7.widget.Toolbar;
=======
>>>>>>> courseregistration
import android.view.View;

public class HomeScreen extends AppCompatActivity {

<<<<<<< HEAD
=======
    String user_token;
    String personNumber;

>>>>>>> courseregistration
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
<<<<<<< HEAD
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
=======
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
>>>>>>> courseregistration

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

<<<<<<< HEAD
=======
    public void Link(View v){
        Intent i = new Intent(HomeScreen.this, courseLink.class);
        startActivity(i);
    }
    public void Create(View v){
        Intent i = new Intent(HomeScreen.this, CourseRegistration.class);
        startActivity(i);
    }

>>>>>>> courseregistration
}
