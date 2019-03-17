package com.example.witsdaily;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class addComments extends AppCompatActivity {
    CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatposts);
        LinearLayout mainLayout = findViewById(R.id.commentView);
        int var = 3;
        int numLikes=21;
        String text;
        TextView edit = findViewById(R.id.numComments);
        edit.setText("View " + var + " comments");
        edit = findViewById(R.id.numUp);
        edit.setText(Integer.toString(numLikes));
        edit = findViewById(R.id.addComment);
        cb = findViewById(R.id.checkBox1);
        if(cb.isChecked()){
            edit.setEnabled(false);
            cb.setBackground(getResources().getDrawable(R.drawable.red_background));
        }
        if(false /* personNumber != posterNumber */){
            cb.setClickable(false);
        }
        for(int i = 0; i < 10; i++) {
            View inflate = getLayoutInflater().inflate(R.layout.chatposts2, mainLayout,false);
            mainLayout.addView(inflate);
            var = 3;
            text = "View " + var + " comments";
            edit = inflate.findViewById(R.id.numComments);
            edit.setText(text);
            edit = inflate.findViewById(R.id.numUp);
            numLikes = 15;
            edit.setText(Integer.toString(numLikes));
            CheckBox cb2 = inflate.findViewById(R.id.checkBox);
            cb2.setClickable(false);
            if(cb.isChecked()){
                edit = findViewById(R.id.addComment);
                edit.setEnabled(false);
                cb2.setBackground(getResources().getDrawable(R.drawable.red_background));
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(addComments.this, LectureCourseForum.class);
        startActivity(i);
    }

    public void viewPost(View v){
        Intent i = new Intent(addComments.this, addComments.class);
        startActivity(i);
    }


    public void setLocked(View v){
        cb = findViewById(R.id.checkBox1);
        if(cb.isChecked()){
            cb.setBackground(getResources().getDrawable(R.drawable.clear_background));
        }else if(!cb.isChecked()){
            cb.setBackground(getResources().getDrawable(R.drawable.red_background));
        }
    }
}
