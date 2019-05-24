package com.example.witsdaily.Forum;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LectureCourseForum extends AppCompatActivity {
String user_token,personNumber,forumCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_course_forum);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        Intent i = getIntent();
        forumCode = ((Intent) i).getStringExtra("forumCode");
        getPosts();
    }

    private void getPosts(){
        LinearLayout forumPosts = (LinearLayout)findViewById(R.id.llForumPosts);

        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONArray posts = data.getJSONArray("posts");
                        for (int i =0;i<posts.length();i++){
                            JSONObject currentPost = posts.getJSONObject(i);
                            View newPost = getLayoutInflater().inflate(R.layout.content_forum_forum, null);
                            TextView tvResponse = (TextView)newPost.findViewById(R.id.tvResponse);
                            TextView tvPersonNumber = (TextView)newPost.findViewById(R.id.tvPersonNumber);
                            TextView tvForumTitle = (TextView)newPost.findViewById(R.id.tvForumTitle);

                            tvForumTitle.setText(currentPost.getString("title"));
                            tvResponse.setText(currentPost.getString("body"));
                            tvPersonNumber.setText(currentPost.getString("poster"));

                            newPost.setTag(currentPost.getString("code"));

                            forumPosts.addView(newPost);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getPosts(forumCode);
/*

        responseCode: successful, failed_unknown, failed_invalid_params,
                failed_missing_params, failed_missing_perms
        posts: A list of post objects in JSON format. Post format is {code
    : string, title: string, body: string, poster: string, time: string, up
        score: int, downscore: int, voted: int, locked: boolean, answer: Comment} */

    }
    public void clickGoToForum(View v){
        Intent i = new Intent(LectureCourseForum.this,ViewForum.class);
        i.putExtra("postCode",String.valueOf(v.getTag()));
        startActivity(i);
    }

    public void addPost(View v){
        Intent i = new Intent(LectureCourseForum.this,ForumAddPost.class);
        i.putExtra("forumCode",forumCode);
        startActivity(i);
    }
}
