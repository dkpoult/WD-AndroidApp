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

public class ViewForum extends AppCompatActivity {
String user_token,personNumber;
LinearLayout llPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_forum);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);

        Intent i = getIntent();
        String postCode = ((Intent) i).getStringExtra("postCode");
        setupForum(postCode);
    }

    private void setupForum(String postCode){
        llPosts = (LinearLayout)findViewById(R.id.llPosts);
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {

                    try {
                        if (data.getString("responseCode").equals("successful")) {
                            JSONArray posts = data.getJSONArray("posts"); // this has a title
                            View parent = addMainPost(posts.getJSONObject(0));
                            JSONArray comments = posts.getJSONObject(0).getJSONArray("comments");
                            addComments(parent,comments);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

  /*posts: JSON array containing a single Post object {code: string, tit
  le: string, body: string, upscore: int, downscore: int, poster: string, ti
  me: string, voted: int, locked: boolean, answer: Comment, comments: array}. Commen
  t format is {code: string, body: string
  , poster: string, time: string, upscore: int, downscore: int, voted:
   int, comments: array}*/
            }
        };
        dataAccessor.getPost(postCode);
    }
    private View addMainPost(JSONObject titlePost){
        View parent = getLayoutInflater().inflate(R.layout.content_forum_forum,null);
        parent.setClickable(false);
        TextView tvResponse = (TextView)parent.findViewById(R.id.tvResponse);
        TextView tvPersonNumber = (TextView)parent.findViewById(R.id.tvPersonNumber);
        TextView tvForumTitle = (TextView)parent.findViewById(R.id.tvForumTitle);

        try {
            tvForumTitle.setText(titlePost.getString("title"));
            tvResponse.setText(titlePost.getString("body"));
            tvPersonNumber.setText(titlePost.getString("poster"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        llPosts.addView(parent);
        return parent;
    }
    private void addComments(View parent,JSONArray comments){

        try {
                if (comments.length()==0){
                    return;
                }
                for (int i =0;i<comments.length();i++){
                    JSONObject currentPost = comments.getJSONObject(i);
                    View newPost = getLayoutInflater().inflate(R.layout.content_forum_post, null);
                    TextView tvResponse = (TextView)newPost.findViewById(R.id.tvResponse);
                    TextView tvPersonNumber = (TextView)newPost.findViewById(R.id.tvPersonNumber);

                    tvResponse.setText(currentPost.getString("body"));
                    tvPersonNumber.setText(currentPost.getString("poster"));

                    LinearLayout parentView = (LinearLayout)parent.findViewById(R.id.llChildren);
                    parentView.addView(newPost);
                    addComments(newPost,currentPost.getJSONArray("comments"));
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
