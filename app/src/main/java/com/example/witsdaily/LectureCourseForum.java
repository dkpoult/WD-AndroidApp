package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class LectureCourseForum extends AppCompatActivity  {
    String resp = "";

    DatabaseHelper myDB;
    String user_token;
    String personNumber;
    String forumCode;
    HashMap<String, post> testPosts = new HashMap<>();
    ArrayList<Pair<String, String>> voted = new ArrayList<>();
    TextView titleBody;
    ImageButton up;
    ImageButton down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_course_forum);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Coursecode-CourseName");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        forumCode = intent.getStringExtra("forumCode");

        myDB=new DatabaseHelper(this, "PhoneDatabase");
        doSync();


    }

// in onCreate method


    public void doSync(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("forumCode", forumCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/get_posts", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        sync(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                })
        {
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);

    }

    public void sync(JSONObject r){
        String output = null;
        String code = null;
        String title = null;
        String body = null;
        String poster = null;
        String time = null;
        boolean locked = false;
        String parentID = null;
        int upscore = 0, downscore=0, vote=0;
        JSONObject answer = null;
        myDB=new DatabaseHelper(this, "PhoneDatabase");
        SQLiteDatabase db = myDB.getDB();
        try {
            output = r.getString("responseCode");
            if (output.equals("successful")) {
                JSONArray r2 = r.getJSONArray("posts");
                for (int j = 0; j < r2.length(); j++) {
                    JSONObject response = r2.getJSONObject(j);
                    code = response.getString("code");
                    parentID = response.getString("code");
                    title = response.getString("title");
                    body = response.getString("body");
                    poster = response.getString("poster");
                    locked = response.getBoolean("locked");
                    time = response.getString("time");
                    if (response.has("answer")) {
                        answer = response.getJSONObject("answer");
                    }
                    upscore = response.getInt("upscore");
                    downscore = response.getInt("downscore");
                    vote = response.getInt("voted");
                    post post = new post(Integer.toString(j), title, body, time, poster);
                    post.DovsID = code;
                    post.setVotes(upscore, downscore);
                    if (vote > 0) {
                        post.isVoted = true;
                        post.voteType = 1;
                    } else if (vote < 0) {
                        post.isVoted = true;
                        post.voteType = 0;
                    } else {
                        post.isVoted = false;
                    }
                    if(locked){
                        post.lock(this);
                    }
                    testPosts.put(post.getPostID(), post);

                    if (response.has("answer")) {
                        code = answer.getString("code");
                        body = answer.getString("body");
                        poster = answer.getString("poster");
                        time = answer.getString("time");
                        answer = answer.getJSONObject("answer");
                        locked = answer.getBoolean("locked");
                        upscore = answer.getInt("upscore");
                        downscore = answer.getInt("downscore");
                        vote = answer.getInt("voted");
                        post post2 = new post(Integer.toString(j+1), false, body, time, poster, parentID);
                        if (vote > 0) {
                            post2.isVoted = true;
                            post2.voteType = 1;
                        } else if (vote < 0) {
                            post2.isVoted = true;
                            post2.voteType = 0;
                        } else {
                            post2.isVoted = false;
                        }
                        if(locked){
                            post2.lock(this);
                        }
                        post2.isComment = true;
                        post2.isAnswer = true;
                        post.addComment(post2);
                        testPosts.put(Integer.toString(j+1), post2);
                        j++;
                    }
                    for(String i : testPosts.keySet()){
                        ContentValues vals = new ContentValues();
                        vals.put("postID", i);
                        vals.put("dovsID", Objects.requireNonNull(testPosts.get(i)).DovsID);
                        System.out.println(forumCode);
                        vals.put("courseID", forumCode);
                        vals.put("title", title);
                        vals.put("body", body);
                        if(locked) {
                            vals.put("isLocked", 1);
                        }else{
                            vals.put("isLocked", 0);
                        }
                        if(Objects.requireNonNull(testPosts.get(i)).isComment) {
                            vals.put("isComment", 1);
                        }else{
                            vals.put("isComment", 0);
                        }
                        if(Objects.requireNonNull(testPosts.get(i)).isAnswer) {
                            vals.put("isAnswer", 1);
                        }else{
                            vals.put("isAnswer", 0);
                        }
                        if(Objects.requireNonNull(testPosts.get(i)).isVoted){
                            ContentValues temp =new ContentValues();
                            temp.put("postID", i);
                            temp.put("TYPE", Objects.requireNonNull(testPosts.get(i)).voteType);

                            db.insert("VOTED",null, temp);
                        }
                        vals.put("postDate", time);
                        vals.put("parentID", forumCode);
                        vals.put("sender", poster);
                        vals.put("upVotes", upscore);
                        vals.put("downVotes", downscore);
                        db.insert("POST", null, vals);

                    }

                }
            }
            else{
                String s;
                System.out.print(output);
                switch (output){

                    case "failed_unknown":
                        s = "failed to get posts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_invalid_params":
                        s = "failed to get posts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_params":
                        s = "failed to get posts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_perms":
                        s = "failed to get posts, you do not have permission to get posts";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout mainLayout = findViewById(R.id.posts);
        Random random = new Random();
        post tempPost;

        try(Cursor c = myDB.doQuery("SELECT * FROM VOTED")){
            while(c.moveToNext()){
                voted.add(new Pair<>(c.getString(c.getColumnIndex("postID")), c.getString(c.getColumnIndex("TYPE"))));
            }
        }
//        try (Cursor c = myDB.doQuery("SELECT * FROM POST;"   /*WHERE courseID = " + forumCode + ";" )*/)) {
//            while (c.moveToNext()) {
////            String currentTime;
//                if (c.getString(c.getColumnIndex("isComment")).equals("0")) {
//                    tempPost = new post(
//                            c.getString(c.getColumnIndex("postID")),
//                            c.getString(c.getColumnIndex("title")),
//                            c.getString(c.getColumnIndex("body")),
//                            c.getString(c.getColumnIndex("postDate")),
//                            c.getString(c.getColumnIndex("sender"))
//                    );
//                } else {
//                    tempPost = new post(
//                            c.getString(c.getColumnIndex("postID")),
//                            false,
//                            c.getString(c.getColumnIndex("body")),
//                            c.getString(c.getColumnIndex("postDate")),
//                            c.getString(c.getColumnIndex("sender")),
//                            c.getString(c.getColumnIndex("parentID"))
//                    );
//                }
//                tempPost.setVotes(Integer.parseInt(c.getString(c.getColumnIndex("upVotes"))),
//                        Integer.parseInt(c.getString(c.getColumnIndex("downVotes"))));
////                if
//                if (c.getString(c.getColumnIndex("isAnswer")).equals("1")) {
//                    tempPost.setAnswer();
//                }
//                if (c.getString(c.getColumnIndex("isLocked")).equals("1")) {
//                    tempPost.lock(this);
//                }
//                if (tempPost.isComment) {
//                    testPosts.get(tempPost.getParentId()).addComment(tempPost);
//                }
//                testPosts.put(tempPost.getPostID(), tempPost);
//            }
//        }
        for(Pair x: voted) {
            if (testPosts.containsKey(x.first)) {
                Objects.requireNonNull(testPosts.get(x.first)).isVoted = true;
                Objects.requireNonNull(testPosts.get(x.first)).voteType = Integer.parseInt((String) x.second);
            }
        }
        int var;
        int numLikes;
        String text;
        View inflate;
        for(String i:testPosts.keySet()) {
            if(!testPosts.get(i).isComment) {
                inflate = getLayoutInflater().inflate(R.layout.chatposts, mainLayout, false);
                if(testPosts.get(i).isVoted){
                    if(testPosts.get(i).voteType == 1) {
                        up = inflate.findViewById(R.id.upvote);
                        up.setClickable(false);
                        up.setBackground(getResources().getDrawable(R.drawable.green_background));
                    }else if(testPosts.get(i).voteType == 0) {
                        down = inflate.findViewById(R.id.downvote);
                        down.setClickable(false);
                        down.setBackground(getResources().getDrawable(R.drawable.red_background));
                    }
                }
                mainLayout.addView(inflate);
                CheckBox cb = inflate.findViewById(R.id.checkBox1);
                cb.setClickable(false);
                if (Objects.requireNonNull(testPosts.get(i)).isLocked) {
                    cb.setChecked(true);
                }

                TextView edit = inflate.findViewById(R.id.addComment);

                if (testPosts.get(i).isLocked) {
                    testPosts.get(i).lock(this);
                    up = inflate.findViewById(R.id.upvote);
                    up.setClickable(false);
                    down = inflate.findViewById(R.id.downvote);
                    down.setClickable(false);
                    edit.setEnabled(false);
                    cb.setBackground(getResources().getDrawable(R.drawable.red_background));
                }
                Button b = inflate.findViewById(R.id.sendComment);
                b.setTag("send");
                b.setId(Integer.parseInt(testPosts.get(i).getPostID()));
                edit = inflate.findViewById(R.id.addComment);
                edit.setTag("text");
                edit.setId(Integer.parseInt(testPosts.get(i).getPostID()));
                titleBody = inflate.findViewById(R.id.title);
                titleBody.setId(Integer.parseInt(testPosts.get(i).getPostID()));
                titleBody.setTag("title");
                up = inflate.findViewById(R.id.upvote);
                up.setTag("Up");
                up.setId(Integer.parseInt(testPosts.get(i).getPostID()));
                down = inflate.findViewById(R.id.downvote);
                down.setTag("Down");
                down.setId(Integer.parseInt(testPosts.get(i).getPostID()));
                titleBody.setText(Objects.requireNonNull(testPosts.get(i)).getTitle());
                titleBody = inflate.findViewById(R.id.text);
                titleBody.setText(Objects.requireNonNull(testPosts.get(i)).getBody());
                titleBody = inflate.findViewById(R.id.numUp);
                titleBody.setTag("text");
                var = Objects.requireNonNull(testPosts.get(i)).getNumComments();
                numLikes = Objects.requireNonNull(testPosts.get(i)).getUpvotes();

                text = "View " + var + " more comments";
                edit = inflate.findViewById(R.id.numComments);
                edit.setText(text);
                edit = inflate.findViewById(R.id.numUp);
                edit.setText(Integer.toString(numLikes));
                LinearLayout change = inflate.findViewById(R.id.commentView);
                View secondInflator = getLayoutInflater().inflate(R.layout.chatposts2, change, false);
                int size = Objects.requireNonNull(testPosts.get(i)).getComments().size();
                if (size > 0) {
                    if(testPosts.get(i).getComments().get(0).isVoted){
                        if(testPosts.get(i).getComments().get(0).voteType == 1) {
                            up = secondInflator.findViewById(R.id.upvote);
                            up.setClickable(false);
                            up.setBackground(getResources().getDrawable(R.drawable.green_background));
                        }else if(testPosts.get(i).getComments().get(0).voteType == 0) {
                            down = secondInflator.findViewById(R.id.downvote);
                            down.setClickable(false);
                            down.setBackground(getResources().getDrawable(R.drawable.red_background));
                        }
                    }
                    change.addView(secondInflator);
                    b = secondInflator.findViewById(R.id.sendComment);
                    b.setTag("send");
                    b.setId(Integer.parseInt(testPosts.get(i).getComment(0).getPostID()));
                    edit = secondInflator.findViewById(R.id.addComment);
                    edit.setTag("text");
                    edit.setId(Integer.parseInt(testPosts.get(i).getComment(0).getPostID()));
                    titleBody = secondInflator.findViewById(R.id.title);
                    titleBody.setId(Integer.parseInt(testPosts.get(i).getComments().get(0).getPostID()));
                    titleBody.setTag("title");
                    up = secondInflator.findViewById(R.id.upvote);
//                    up.setOnClickListener(this);
                    up.setTag("Up");
                    up.setId(Integer.parseInt(testPosts.get(i).getComments().get(0).getPostID()));
                    down = secondInflator.findViewById(R.id.downvote);
//                    down.setOnClickListener(this);
                    down.setTag("Down");
                    down.setId(Integer.parseInt(testPosts.get(i).getComments().get(0).getPostID()));
                    titleBody.setText(Objects.requireNonNull(testPosts.get(i)).getComments().get(0).getTitle());
                    titleBody = secondInflator.findViewById(R.id.text);
                    titleBody.setText(Objects.requireNonNull(testPosts.get(i)).getComments().get(0).getBody());
                    titleBody = secondInflator.findViewById(R.id.numUp);
                    titleBody.setTag("text");
                    edit = secondInflator.findViewById(R.id.numUp);
                    numLikes = Objects.requireNonNull(testPosts.get(i)).getComments().get(0).getUpvotes();
                    edit.setText(Integer.toString(numLikes));
                    edit = secondInflator.findViewById(R.id.numComments);
                    var = Objects.requireNonNull(testPosts.get(i)).getComments().get(0).getNumComments();
                    text = "View " + var + " more comments";
                    CheckBox cb2 = secondInflator.findViewById(R.id.checkBox1);
                    edit.setText(text);
                    cb2.setClickable(false);
                    edit = secondInflator.findViewById(R.id.addComment);
                    if (Objects.requireNonNull(testPosts.get(i)).isLocked) {
                        cb2.setChecked(true);
                        testPosts.get(i).lock(this);
                        up = secondInflator.findViewWithTag("Up");
                        up.setClickable(false);
                        down = secondInflator.findViewWithTag("Down");
                        down.setClickable(false);
                        cb2.setBackground(getResources().getDrawable(R.drawable.red_background));
                        edit.setEnabled(false);
                    }
                    if (Objects.requireNonNull(testPosts.get(i)).getComments().get(0).isAnswer) {
                        cb2.setBackground(getResources().getDrawable(R.drawable.green_background));
                    }
                }
            }
        }

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
            i.putExtra("forumCode", forumCode);
            finish();
            startActivity(i);
        }
    }

    public void onBackPressed(){
        Intent i = new Intent(LectureCourseForum.this, HomeScreen.class);
        finish();
        startActivity(i);
    }

    public void viewPost(View v){
        Intent i = new Intent(LectureCourseForum.this, addComments.class);
        i.putExtra("postID", Integer.toString(v.getId()));
        i.putExtra("isAdmin", "true");
        i.putExtra("forumCode", forumCode);
        startActivity(i);
    }
//
//
//
//

public void doUpvote(View v) {
    String dovsID;
    ImageButton b;
    TextView t;
    String temp;
    temp = Integer.toString(v.getId());
    try(Cursor c = myDB.doQuery("SELECT (dovsID) FROM POST WHERE postID =" + temp + ";")){
        c.moveToNext();
        dovsID = c.getString(c.getColumnIndex("dovsID"));
        System.out.println(dovsID);
    }
    vote(dovsID, 1);
    if (resp.equals("successful")) {
        b = v.findViewWithTag(v.getTag());
        t = ((View) v.getParent()).findViewWithTag("text");
        b.setClickable(false);
        b.setBackground(getResources().getDrawable(R.drawable.green_background));
        b = ((View) v.getParent()).findViewWithTag("Down");
        b.setClickable(true);
        b.setBackground(getResources().getDrawable(R.drawable.clear_background));
        System.out.println(temp);
        Objects.requireNonNull(testPosts.get(temp)).addUp(this);
        t.setText(Integer.toString(testPosts.get(temp).getUpvotes()));
    } else {
        String s;
        switch (resp) {
            case "failed_unknown":
                s = "failed to get posts, check your internet connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            case "failed_invalid_params":
                s = "failed to get posts, check your internet connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            case "failed_missing_params":
                s = "failed to get posts, check your internet connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            case "failed_missing_perms":
                s = "failed to get posts, you do not have permission to get posts";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;


        }
    }
    resp = "";
}

public void tempFunction(JSONObject r){
    System.out.println(r.toString());
    try {
    String s = r.getString("responseCode");
    System.out.println(s);
        if(s.equals("successful")){
            System.out.println(resp);
            resp += "successful";
            System.out.println(resp);
        }else{
            resp += s;
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

}

public void vote(String postCode, int vote){
    JSONObject params = new JSONObject();
    try {
        params.put("userToken", user_token);
        System.out.println(user_token);
        System.out.println(personNumber);
        System.out.println(postCode);
        System.out.println(vote);
        params.put("personNumber", personNumber);
        params.put("postCode", postCode);
        params.put("vote", vote);

    } catch (JSONException e) {
        e.printStackTrace();
    }

    final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/make_vote", params,
            new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response){
                    System.out.println(response.toString());
                    tempFunction(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String s = error.getLocalizedMessage();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }
            })
    {
    };
    VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);
}

    public void doDownvote(View v){
        ImageButton b;
        String dovsID;

        TextView t;
        String temp;
        temp = Integer.toString(v.getId());
        try(Cursor c = myDB.doQuery("SELECT (dovsID) FROM POST WHERE postID =" + temp + ";")){
            c.moveToNext();
            dovsID = c.getString(c.getColumnIndex("dovsID"));
            System.out.println(dovsID);
        }
        System.out.println(resp);
        vote(dovsID, -1);
        System.out.println(resp);
        if(resp.equals( "successful")) {
            b = v.findViewWithTag(v.getTag());
            t = ((View) v.getParent()).findViewWithTag("text");
            b.setClickable(false);
            b.setBackground(getResources().getDrawable(R.drawable.red_background));
            b = ((View) v.getParent()).findViewWithTag("Up");
            b.setClickable(true);
            b.setBackground(getResources().getDrawable(R.drawable.clear_background));
            System.out.println(temp);
            Objects.requireNonNull(testPosts.get(temp)).addDown(this);
            t.setText(Integer.toString(testPosts.get(temp).getUpvotes()));
        } else {
            String s;
            switch (resp) {
                case "failed_unknown":
                    s = "failed to get posts, check your internet connection";
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    break;
                case "failed_invalid_params":
                    s = "failed to get posts, check your internet connection";
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    break;
                case "failed_missing_params":
                    s = "failed to get posts, check your internet connection";
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    break;
                case "failed_missing_perms":
                    s = "failed to get posts, you do not have permission to get posts";
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    resp = "";
    }

    public void makeComment(View v){
        TextView text = ((View)v.getParent()).findViewWithTag("text");
        String body = text.getText().toString();
        Button b = v.findViewWithTag("send");
        String dovsID;
        int id = b.getId();
        try(Cursor c = myDB.doQuery("SELECT (dovsID) FROM POST WHERE postID =" + id + ";")){
            c.moveToNext();
            dovsID = c.getString(c.getColumnIndex("dovsID"));
            System.out.println(dovsID);
        }
        doComment(dovsID, body);
        if(resp.equals("successful")){
            text.setText("");
        }
        Intent i = getIntent();
        i.putExtra("forumCode", forumCode);
        finish();
        startActivity(i);
    }

    public void doComment(String code, String body){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            System.out.println(user_token);
            System.out.println(personNumber);
            System.out.println(code);
            params.put("personNumber", personNumber);
            params.put("postCode", code);
            params.put("body", body);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/make_comment", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        tempFunction(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                })
        {
        };
        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);

    }
}
