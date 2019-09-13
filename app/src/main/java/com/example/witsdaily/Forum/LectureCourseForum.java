package com.example.witsdaily.Forum;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    }
    public void clickCancelComment(View v){
        LinearLayout llCommentView = ((LinearLayout)(v.getParent().getParent()));
        EditText edtComment = (EditText)llCommentView.findViewById(R.id.edtComment);
        edtComment.setText("");
        llCommentView.setVisibility(View.GONE);

    }
    private void getPosts(){

        LinearLayout forumPosts = (LinearLayout)findViewById(R.id.llForumPosts);
        forumPosts.removeAllViews();
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONArray posts = data.getJSONArray("posts");
                        for (int i =0;i<posts.length();i++){
                            JSONObject currentPost = posts.getJSONObject(i);
                            View newPost = getLayoutInflater().inflate(R.layout.content_forum_forum, null);
                            newPost.setTag(currentPost.getString("code"));
                            RadioGroup rgLikes = newPost.findViewById(R.id.rgLikes);
                            setLikeButtons(rgLikes);
                            TextView tvLikeCount = (TextView)newPost.findViewById(R.id.tvLikeCount);
                            String upscore = currentPost.getString("upscore");
                            String downscore = currentPost.getString("downscore");
                            try {
                                JSONObject answer = currentPost.getJSONObject("answer");
                                Button btnMarked = newPost.findViewById(R.id.btnMarked);
                                btnMarked.setVisibility(View.VISIBLE);
                            }
                            catch (Exception e){
                                   // Then there is no answer
                            }

                            String likes = String.valueOf(Integer.parseInt(upscore)-Integer.parseInt(downscore));
                            tvLikeCount.setText("("+likes+")");
                            tvLikeCount.setTag(likes);

                            TextView tvResponse = (TextView)newPost.findViewById(R.id.tvResponse);
                            TextView tvPersonNumber = (TextView)newPost.findViewById(R.id.tvPersonNumber);
                            TextView tvForumTitle = (TextView)newPost.findViewById(R.id.tvForumTitle);
                            Button btnReply = (Button)newPost.findViewById(R.id.btnReply);
                            btnReply.setVisibility(View.GONE);
                            tvForumTitle.setText(currentPost.getString("title"));
                            tvResponse.setText(currentPost.getString("body"));
                            tvPersonNumber.setText(currentPost.getString("poster"));


                            forumPosts.addView(newPost);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getPosts(forumCode);

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

    @Override
    protected void onResume() {
        super.onResume();
        getPosts();
    }
    public void clickShowReply(View v){
        LinearLayout llMainView = ((LinearLayout)(v.getParent().getParent()));
        llMainView.findViewById(R.id.llCommentView).setVisibility(View.VISIBLE);
    }
    private void setLikeButtons(RadioGroup rgLikes){
        ForumAccessor fa = new ForumAccessor(this,personNumber,user_token);
        fa.setLikeButtons(rgLikes);
    }
    public void clickForumMarked(View v){

    }
}
