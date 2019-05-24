package com.example.witsdaily.Forum;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONException;
import org.json.JSONObject;

public class ForumAddPost extends AppCompatActivity {
String forumCodeValue,user_token,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_add_post);
        Intent i = getIntent();
        forumCodeValue = i.getStringExtra("forumCode");
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        TextView forumCode = (TextView)(findViewById(R.id.tvForumCode));
        forumCode.setText(forumCodeValue);
    }

    public void clickCreatePost(View v){
       EditText edtTitle = (EditText)findViewById(R.id.edtTitle);
       EditText edtBody = (EditText)findViewById(R.id.edtBody);
       String title = edtTitle.getText().toString();
       String body = edtBody.getText().toString();
       if (title.equals("")||body.equals("")){
           Toast.makeText(getApplicationContext(), "Please enter valid post details", Toast.LENGTH_SHORT).show();
           return;
       }
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {
                try {
                    Toast.makeText(getApplicationContext(), data.getString("responseCode"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
       dataAccessor.makePost(forumCodeValue,title,body);
    }
}
