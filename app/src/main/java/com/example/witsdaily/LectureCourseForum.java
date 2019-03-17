package com.example.witsdaily;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

public class LectureCourseForum extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_course_forum);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Coursecode-CourseName");
        setSupportActionBar(toolbar);
        LinearLayout mainLayout = findViewById(R.id.posts);
        int var;
        int numLikes;
        String text;
        for(int i = 0; i < 10; i++) {
            View inflate = getLayoutInflater().inflate(R.layout.chatposts, mainLayout, false);
            mainLayout.addView(inflate);
            CheckBox cb = inflate.findViewById(R.id.checkBox1);
            cb.setClickable(false);
            TextView edit = inflate.findViewById(R.id.addComment);
            if(cb.isChecked()){
                edit.setEnabled(false);
                cb.setBackground(getResources().getDrawable(R.drawable.red_background));
            }

            var = 3-1;
            numLikes = 30;
            text = "View " + var + " more comments";
            edit = inflate.findViewById(R.id.numComments);
            edit.setText(text);
            edit = inflate.findViewById(R.id.numUp);
            edit.setText(Integer.toString(numLikes));
            LinearLayout change = inflate.findViewById(R.id.commentView);
            View secondInflator = getLayoutInflater().inflate(R.layout.chatposts2, change,false);
            change.addView(secondInflator);
            numLikes = 12;
            edit = secondInflator.findViewById(R.id.numUp);
            edit.setText(Integer.toString(numLikes));
            edit = secondInflator.findViewById(R.id.numComments);
            text = "View " + var + " more comments";
            CheckBox cb2 = secondInflator.findViewById(R.id.checkBox);
            edit.setText(text);
            cb2.setClickable(false);
            edit = secondInflator.findViewById(R.id.addComment);
            if(cb.isChecked()){
                cb2.setBackground(getResources().getDrawable(R.drawable.red_background));
                edit.setEnabled(false);
            }
        }
    }
    private boolean isReached = false;

// in onCreate method

    public void viewPost(View v){
        Intent i = new Intent(LectureCourseForum.this, addComments.class);
        startActivity(i);
    }

    public void doPost(View v){
        Intent i = new Intent(LectureCourseForum.this, addPost.class);
        EditText title = findViewById(R.id.postTitle);
        String val = title.getText().toString();
        if(val.isEmpty()){
            int greenColorValue = Color.parseColor("#ff0000");
            title.setHintTextColor(greenColorValue);
            title.setHint("Please title your post");
        }else {
            i.putExtra("title", val);
            startActivity(i);
        }
    }



}
