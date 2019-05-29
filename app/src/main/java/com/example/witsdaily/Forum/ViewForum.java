package com.example.witsdaily.Forum;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewForum extends AppCompatActivity {
String user_token,personNumber;
LinearLayout llPosts;
String answerCode = "";
String mainPostCode;
View currentAnswer = null;
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
        TextView tvLikeCount = (TextView)parent.findViewById(R.id.tvLikeCount);
        try {
            mainPostCode = titlePost.getString("code");
            parent.setTag(mainPostCode);
            RadioGroup rgLikes = parent.findViewById(R.id.rgLikes);
            setLikeButtons(rgLikes);
            String upscore = titlePost.getString("upscore");
            String downscore = titlePost.getString("downscore");
            String likes = String.valueOf(Integer.parseInt(upscore)-Integer.parseInt(downscore));
            tvLikeCount.setText("("+likes+")");
            tvForumTitle.setText(titlePost.getString("title"));
            tvResponse.setText(titlePost.getString("body"));
            tvPersonNumber.setText(titlePost.getString("poster"));
            try {
                JSONObject answer = titlePost.getJSONObject("answer");
                Button btnMarked = parent.findViewById(R.id.btnMarked);
                btnMarked.setVisibility(View.VISIBLE);
                answerCode = answer.getString("code");
            }catch(Exception e){

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        llPosts.addView(parent);
        return parent;
    }
    public void clickCancelComment(View v){
        LinearLayout llCommentView = ((LinearLayout)(v.getParent().getParent()));
        EditText edtComment = (EditText)llCommentView.findViewById(R.id.edtComment);
        edtComment.setText("");
        llCommentView.setVisibility(View.GONE);

    }
    private void addComments(View parent,JSONArray comments){

        try {
                if (comments.length()==0){
                    return;
                }
                for (int i =0;i<comments.length();i++){
                    JSONObject currentPost = comments.getJSONObject(i);
                    TextView tvLikeCount = (TextView)parent.findViewById(R.id.tvLikeCount);
                    View newPost = getLayoutInflater().inflate(R.layout.content_forum_post, null);
                    TextView tvResponse = (TextView)newPost.findViewById(R.id.tvResponse);
                    TextView tvPersonNumber = (TextView)newPost.findViewById(R.id.tvPersonNumber);
                    Button btnMarked = (Button)newPost.findViewById(R.id.btnMarked);
                    String upscore = currentPost.getString("upscore");
                    String downscore = currentPost.getString("downscore");
                    String likes = String.valueOf(Integer.parseInt(upscore)-Integer.parseInt(downscore));
                    tvLikeCount.setText("("+likes+")");
                    String code = currentPost.getString("code");
                    newPost.setTag(code);
                    RadioGroup rgLikes = newPost.findViewById(R.id.rgLikes);
                    setLikeButtons(rgLikes);
                    tvResponse.setText(currentPost.getString("body"));
                    tvPersonNumber.setText(currentPost.getString("poster"));
                    if (code.equals(answerCode)){
                        btnMarked.setBackground(getResources().getDrawable(R.drawable.forum_answered));
                        currentAnswer = newPost;
                    }
                    LinearLayout parentView = (LinearLayout)parent.findViewById(R.id.llChildren);
                    parentView.addView(newPost);
                    addComments(newPost,currentPost.getJSONArray("comments"));
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void clickShowReply(View v){
        LinearLayout llMainView = ((LinearLayout)(v.getParent().getParent()));
        llMainView.findViewById(R.id.llCommentView).setVisibility(View.VISIBLE);
    }

    public void clickComment(View v){
        LinearLayout commentLayout = (LinearLayout)v.getParent().getParent();
        LinearLayout mainView = ((LinearLayout)commentLayout.getParent());
        String comment = ((EditText)commentLayout.findViewById(R.id.edtComment)).getText().toString();
        if (comment.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {
                try {
                    String responseCode = data.getString("responseCode");
                    Toast.makeText(getApplicationContext(), responseCode, Toast.LENGTH_SHORT).show();
                    if (responseCode.equals("successful")){
                        JSONArray comments = new JSONArray();
                        JSONObject comment = data.getJSONObject("comment");
                        comments.put(comment);
                        addComments((View)mainView,comments);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String postCode = String.valueOf(mainView.getTag());
        dataAccessor.makeComment(postCode,comment);
    }
    private void setLikeButtons(RadioGroup rgLikes){
        ForumAccessor fa = new ForumAccessor(this,personNumber,user_token);
        fa.setLikeButtons(rgLikes);
    }
    public void clickForumMarked(View v){
        if (currentAnswer != null) {
            Button btnMarkedOld = currentAnswer.findViewById(R.id.btnMarked);
            btnMarkedOld.setBackground(getResources().getDrawable(R.drawable.forum_mark_answer));
        }
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        v.setBackground(getResources().getDrawable(R.drawable.forum_answered));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LinearLayout postView = ((LinearLayout)(v.getParent().getParent()));
        String commentCode = String.valueOf(postView.getTag());
        dataAccessor.setAnswer(mainPostCode,commentCode);

    }
}
