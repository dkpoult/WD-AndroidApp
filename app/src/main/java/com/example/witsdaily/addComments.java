package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.zip.Inflater;

public class addComments extends AppCompatActivity implements View.OnClickListener {
    CheckBox cb;
    DatabaseHelper myDB;
    String postID;
    String personNumber;
    String user_token;
    String forumCode;
    String isAdmin;
    String dovsID;
    HashMap<String, post> testPosts;
    String resp = "";

    TextView titleBody;
    ImageButton up;
    ImageButton down;
    ArrayList<Pair<String, String>> voted = new ArrayList<>();
    View l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        System.out.println(forumCode);
        isAdmin = intent.getStringExtra("isAdmin");
        forumCode = intent.getStringExtra("forumCode");
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        testPosts = new HashMap<>();
        super.onCreate(savedInstanceState);
        myDB = new DatabaseHelper(this, "PhoneDatabase");
        try (Cursor c = myDB.doQuery("SELECT (dovsID) FROM POST WHERE postID =" + postID + ";")) {
            c.moveToNext();
            dovsID = c.getString(c.getColumnIndex("dovsID"));
            System.out.println(dovsID);
        }


        Random random = new Random();
        post tempPost;
        int var = 0;
        int numLikes = 0;
        String text;
        TextView edit;
        ImageButton button1;
        ImageButton button2;
        try (Cursor c = myDB.doQuery("SELECT * FROM VOTED WHERE postID =" + postID + ";")) {
            while (c.moveToNext()) {
                voted.add(new Pair<>(c.getString(c.getColumnIndex("postID")), c.getString(c.getColumnIndex("TYPE"))));
            }
        }

        doSync();
        try (Cursor c = myDB.doQuery("SELECT * FROM POST WHERE postID =" + postID + ";")) {
            while (c.moveToNext()) {
                if (c.getString(c.getColumnIndex("isComment")).equals("0")) {
                    setContentView(R.layout.chatposts);
                    tempPost = new post(
                            c.getString(c.getColumnIndex("postID")),
                            c.getString(c.getColumnIndex("title")),
                            c.getString(c.getColumnIndex("body")),
                            c.getString(c.getColumnIndex("postDate")),
                            c.getString(c.getColumnIndex("sender"))
                    );
                    tempPost.isComment = false;

                } else {
                    setContentView(R.layout.chatposts2);
                    tempPost = new post(
                            c.getString(c.getColumnIndex("postID")),
                            false,
                            c.getString(c.getColumnIndex("body")),
                            c.getString(c.getColumnIndex("postDate")),
                            c.getString(c.getColumnIndex("sender")),
                            c.getString(c.getColumnIndex("parentID"))
                    );
                    tempPost.isComment = true;

                }
                tempPost.DovsID = c.getString(c.getColumnIndex("dovsID"));
                tempPost.setVotes(Integer.parseInt(c.getString(c.getColumnIndex("upVotes"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("downVotes"))));
                if (c.getString(c.getColumnIndex("isAnswer")).equals("1")) {
                    tempPost.setAnswer();
                }
                if (c.getString(c.getColumnIndex("isLocked")).equals("1")) {
                    tempPost.lock(this);
                }
                testPosts.put(tempPost.getPostID(), tempPost);
            }
        }
        try (Cursor c = myDB.doQuery("SELECT * FROM POST WHERE parentID =" + postID + ";")) {
            while (c.moveToNext()) {
                tempPost = new post(
                        c.getString(c.getColumnIndex("postID")),
                        false,
                        c.getString(c.getColumnIndex("body")),
                        c.getString(c.getColumnIndex("postDate")),
                        c.getString(c.getColumnIndex("sender")),
                        c.getString(c.getColumnIndex("parentID"))
                );

                tempPost.setVotes(Integer.parseInt(c.getString(c.getColumnIndex("upVotes"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("downVotes"))));
                if (c.getString(c.getColumnIndex("isAnswer")).equals("1")) {
                    tempPost.setAnswer();
                }
                if (c.getString(c.getColumnIndex("isLocked")).equals("1")) {
                    tempPost.lock(this);
                }
                if (tempPost.isComment) {
                    System.out.println(tempPost.getParentId());
                    Objects.requireNonNull(testPosts.get(tempPost.getParentId())).addComment(tempPost);
                }
                testPosts.put(tempPost.getPostID(), tempPost);
            }
        }

        for (Pair x : voted) {
            if (testPosts.containsKey(x.first)) {
                Objects.requireNonNull(testPosts.get(x.first)).isVoted = true;
                Objects.requireNonNull(testPosts.get(x.first)).voteType = Integer.parseInt((String) x.second);
            }
        }
        for (String i : testPosts.keySet()) {
            System.out.println("test");
            System.out.println(i + " " + testPosts.get(i).getTitle());
        }
        if (testPosts.get(postID).isVoted) {
            if (testPosts.get(postID).voteType == 1) {
                up = findViewById(R.id.upvote);
                up.setClickable(false);
                up.setBackground(getResources().getDrawable(R.drawable.green_background));
            } else if (testPosts.get(postID).voteType == 0) {
                down = findViewById(R.id.downvote);
                down.setClickable(false);
                down.setBackground(getResources().getDrawable(R.drawable.red_background));
            }
        }
        if (testPosts.get(postID).isLocked) {
            testPosts.get(postID).lock(this);
        }
        button1 = findViewById(R.id.upvote);
        button2 = findViewById(R.id.downvote);
        LinearLayout mainLayout = findViewById(R.id.commentView);
        Button b = findViewById(R.id.sendComment);
        b.setTag("send");
        b.setId(Integer.parseInt(testPosts.get(postID).getPostID()));

        titleBody = findViewById(R.id.title);
        titleBody.setId(Integer.parseInt(Objects.requireNonNull(testPosts.get(postID)).getPostID()));
        titleBody.setTag(testPosts.get(postID).getPostID());
        titleBody.setText(Objects.requireNonNull(testPosts.get(postID)).getTitle());
        titleBody = findViewById(R.id.text);
        titleBody.setText(Objects.requireNonNull(testPosts.get(postID)).getBody());
        titleBody = findViewById(R.id.numUp);
        titleBody.setTag("title");
        titleBody = findViewById(R.id.numUp);
        titleBody.setTag("text");
        up = findViewById(R.id.upvote);
//        up.setOnClickListener(this);
        up.setTag("Up");
        up.setId(Integer.parseInt(Objects.requireNonNull(testPosts.get(postID)).getPostID()));
        down = findViewById(R.id.downvote);
//        down.setOnClickListener(this);
        down.setTag("Down");
        down.setId(Integer.parseInt(Objects.requireNonNull(testPosts.get(postID)).getPostID()));
        var = Objects.requireNonNull(testPosts.get(postID)).getNumComments();
        edit = findViewById(R.id.numComments);
        edit.setText("View " + var + " comments");
        edit = findViewById(R.id.numUp);
        numLikes = Objects.requireNonNull(testPosts.get(postID)).getUpvotes();
        edit.setText(Integer.toString(numLikes));
        edit = findViewById(R.id.addComment);
        cb = findViewById(R.id.checkBox1);
        cb.setTag(testPosts.get(postID).getPostID());
        if (Objects.requireNonNull(testPosts.get(postID)).isLocked) {
            cb.setChecked(true);
            edit.setEnabled(false);
            cb.setBackground(getResources().getDrawable(R.drawable.red_background));
            button1.setClickable(false);
            button2.setClickable(false);
        }
        edit = findViewById(R.id.addComment);
        edit.setTag("text");
        edit.setId(Integer.parseInt(testPosts.get(postID).getPostID()));
        if (isAdmin.equals("true")) {
            cb.setClickable(true);
        } else {
            cb.setClickable(false);
        }
        for (post i : Objects.requireNonNull(testPosts.get(postID)).getComments()) {
            View inflate = getLayoutInflater().inflate(R.layout.chatposts2, mainLayout, false);
            mainLayout.addView(inflate);
            if (i.isVoted) {
                if (i.voteType == 1) {
                    up = inflate.findViewById(R.id.upvote);
                    up.setClickable(false);
                    up.setBackground(getResources().getDrawable(R.drawable.green_background));
                } else if (i.voteType == 0) {
                    down = inflate.findViewById(R.id.downvote);
                    down.setClickable(false);
                    down.setBackground(getResources().getDrawable(R.drawable.red_background));
                }
            }
            if (i.isLocked) {
                cb.setChecked(true);
                i.lock(this);
            }
            button1 = inflate.findViewById(R.id.upvote);
            button2 = inflate.findViewById(R.id.downvote);
            b = inflate.findViewById(R.id.sendComment);
            b.setTag("send");
            b.setId(Integer.parseInt(i.getPostID()));

            titleBody = inflate.findViewById(R.id.title);
            titleBody.setId(Integer.parseInt(i.getPostID()));
            titleBody.setTag("title");
            System.out.println(titleBody.getId());
            titleBody.setText(Objects.requireNonNull(i.getTitle()));
            titleBody = inflate.findViewById(R.id.text);
            titleBody.setText(Objects.requireNonNull(i.getBody()));
            titleBody = inflate.findViewById(R.id.numUp);
            titleBody.setTag("text");
            up = inflate.findViewById(R.id.upvote);
//            up.setOnClickListener(this);
            up.setTag("Up");
            up.setId(Integer.parseInt(i.getPostID()));
            down = inflate.findViewById(R.id.downvote);
//            down.setOnClickListener(this);
            down.setTag("Down");
            down.setId(Integer.parseInt(i.getPostID()));
            var = i.getNumComments();
            text = "View " + var + " comments";
            edit = inflate.findViewById(R.id.numComments);
            edit.setText(text);
            edit = inflate.findViewById(R.id.numUp);
            numLikes = i.getUpvotes();
            edit.setText(Integer.toString(numLikes));
            CheckBox cb2 = inflate.findViewById(R.id.checkBox1);
            cb2.setClickable(false);
            cb2.setTag(i.getPostID());
            if (i.isLocked) {
                edit = findViewById(R.id.addComment);
                edit.setEnabled(false);
                cb2.setBackground(getResources().getDrawable(R.drawable.red_background));
                button1.setClickable(false);
                button2.setClickable(false);
            }
            edit = inflate.findViewById(R.id.addComment);
            edit.setTag("text");
            edit.setId(Integer.parseInt(i.getPostID()));
            l = inflate;
            comRequest(dovsID);

        }
    }


    public void getComments(JSONObject r) {
        System.out.println(r.toString());
        String code = null;
        String title = null;
        String body = null;
        String poster = null;
        String time = null;
        boolean locked = false;
        String parentID = null;
        int upscore = 0, downscore = 0, voted = 0;
        JSONArray comments = null;
        JSONObject answer = null;
        try {
            String responseCode = r.getString("responseCode");
            if (responseCode.equals("successful")) {
                JSONObject e = r.getJSONArray("posts").getJSONObject(0);
                code = e.getString("code");
                title = e.getString("title");
                body = e.getString("body");
                poster = e.getString("poster");
                time = e.getString("time");
                locked = e.getBoolean("locked");
                upscore = e.getInt("upscore");
                downscore = e.getInt("downscore");
                comments = e.getJSONArray("comments");
                voted = e.getInt("voted");
                myDB = new DatabaseHelper(this, "PhoneDatabase");
                SQLiteDatabase db = myDB.getDB();
                post post = new post(postID, false, body, time, poster, forumCode);
                if(comments.length()>0){
                    recDrawComs(comments, l);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void comRequest(String dovsCode){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("postCode", dovsCode);

            System.out.println(user_token + " : " + personNumber + " : " +  dovsID /*+ " : " + body*/);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/get_post", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        getComments(response);
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


    public void recDrawComs(JSONArray r, View v){
        LinearLayout secondLayout;
        System.out.println("recDrawing");

        if(r.length() == 0){
            return;
        }
        for(int i = 0; i < r.length(); i++) {
            try {
                JSONObject e = r.getJSONObject(i);
                String code = e.getString("code");
                String body = e.getString("body");
                String poster = e.getString("poster");
                String time = e.getString("time");
                Boolean locked;
                int upscore = e.getInt("upscore");
                int downscore = e.getInt("downscore");
                JSONArray comments = e.getJSONArray("comments");
                int voted = e.getInt("voted");
                myDB = new DatabaseHelper(this, "PhoneDatabase");
                SQLiteDatabase db = myDB.getDB();
                secondLayout = v.findViewById(R.id.commentView);
                View inflator2 = getLayoutInflater().inflate(R.layout.chatposts2, secondLayout, false);
                if (voted != 0) {
                    if (voted == 1) {
                        up = inflator2.findViewById(R.id.upvote);
                        up.setClickable(false);
                        up.setBackground(getResources().getDrawable(R.drawable.green_background));
                    } else if (voted == -1) {
                        down = inflator2.findViewById(R.id.downvote);
                        down.setClickable(false);
                        down.setBackground(getResources().getDrawable(R.drawable.red_background));
                    }
                }
                boolean isNew = true;
                String key = null;
                for(String l: testPosts.keySet()){
                     if(testPosts.get(l).DovsID.equals(dovsID)){
                        isNew = false;
                        key = l;
                    }
                }
                post k;
                if(isNew){
                    k = new post(Integer.toString(testPosts.size()), false, body, time, poster, postID);
                }else{
                    k = new post(testPosts.get(key).getPostID(), false, body, time, poster, postID);
                }
                if(testPosts.get(postID).isLocked){
                    locked = true;
                }
                secondLayout.addView(inflator2);
                Button button1 = inflator2.findViewById(R.id.upvote);
                Button button2 = inflator2.findViewById(R.id.downvote);
                Button b = inflator2.findViewById(R.id.sendComment);
                b.setTag("send");
                b.setId(Integer.parseInt(k.getPostID()));
                EditText edit = inflator2.findViewById(R.id.addComment);
                edit.setTag("text");
                edit.setId(Integer.parseInt(k.getPostID()));
                titleBody = inflator2.findViewById(R.id.title);
                titleBody.setId(Integer.parseInt(k.getPostID()));
                titleBody.setTag("title");
                titleBody.setText(Objects.requireNonNull(k.getTitle()));
                titleBody = inflator2.findViewById(R.id.text);
                titleBody.setText(Objects.requireNonNull(k.getBody()));
                titleBody = inflator2.findViewById(R.id.numUp);
                titleBody.setTag("text");
                up = inflator2.findViewById(R.id.upvote);
//                up.setOnClickListener(this);
                up.setTag("Up");
                up.setId(Integer.parseInt(k.getPostID()));
                down = inflator2.findViewById(R.id.downvote);
//                down.setOnClickListener(this);
                down.setTag("Down");
                down.setId(Integer.parseInt(k.getPostID()));
                int var = k.getNumComments();
                int numLikes = k.getUpvotes();
                String text = "View " + var + " comments";
                edit = inflator2.findViewById(R.id.numComments);
                edit.setText(text);
                edit = inflator2.findViewById(R.id.numUp);
                edit.setText(Integer.toString(numLikes));
                CheckBox cb3 = inflator2.findViewById(R.id.checkBox1);
                cb3.setTag(k.getPostID());
                cb3.setClickable(false);
                if (k.isLocked) {
                    edit = inflator2.findViewById(R.id.addComment);
                    edit.setEnabled(false);
                    k.lock(this);
                    cb3.setBackground(getResources().getDrawable(R.drawable.red_background));
                    button1.setClickable(false);
                    button2.setClickable(false);
                }
                if(k.getNumComments()>0){
                    recDrawComs(comments, inflator2);
                }else{
                    return;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }


            }



    }


    public void doSync(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("postCode", dovsID);

            System.out.println(user_token + " : " + personNumber + " : " +  dovsID /*+ " : " + body*/);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/get_post", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
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
        System.out.println(r.toString());
        String code = null;
        String title = null;
        String body = null;
        String poster = null;
        String time = null;
        boolean locked = false;
        String parentID = null;
        int upscore = 0, downscore=0, voted=0;
        JSONArray comments = null;
        JSONObject answer = null;
        try {
            String responseCode = r.getString("responseCode");
            if(responseCode.equals("successful")){
                JSONObject e = r.getJSONArray("posts").getJSONObject(0);
                code = e.getString("code");
                title = e.getString("title");
                body = e.getString("body");
                poster = e.getString("poster");
                time = e.getString("time");
                locked = e.getBoolean("locked");
                upscore = e.getInt("upscore");
                downscore = e.getInt("downscore");
                comments = e.getJSONArray("comments");
                voted = e.getInt("voted");
                myDB=new DatabaseHelper(this, "PhoneDatabase");
                SQLiteDatabase db = myDB.getDB();
                post post = new post(postID, false, body, time, poster, forumCode);
                if (e.has("answer")) {
                    answer = e.getJSONObject("answer");
                }
                if (voted > 0) {
                    post.isVoted = true;
                    post.voteType = 1;
                } else if (voted < 0) {
                    post.isVoted = true;
                    post.voteType = 0;
                } else {
                    post.isVoted = false;
                }
                post.isComment = false;
                post.setVotes(upscore, downscore);
                if (e.has("answer")) {
                    code = answer.getString("code");
                    body = answer.getString("body");
                    poster = answer.getString("poster");
                    time = answer.getString("time");
                    answer = answer.getJSONObject("answer");
                    upscore = answer.getInt("upscore");
                    downscore = answer.getInt("downscore");
                    voted = answer.getInt("voted");
                    post post2 = new post(Integer.toString(Integer.parseInt(postID) +1), false, body, time, poster, postID);
                    if (voted > 0) {
                        post2.isVoted = true;
                        post2.voteType = 1;
                    } else if (voted < 0) {
                        post2.isVoted = true;
                        post2.voteType = 0;
                    } else {
                        post2.isVoted = false;
                    }
                    post2.isComment = true;
                    post2.isAnswer = true;
                    post.addComment(post2);
                    testPosts.put(Integer.toString(Integer.parseInt(postID) +1), post2);
                }
                testPosts.put(postID, post);
                if(comments.length() > 0) {
                    System.out.println("syncing comments ");
                    recSync(comments, postID);
                }
                for(String i : testPosts.keySet()){
                    ContentValues vals = new ContentValues();
                    vals.put("postID", i);
                    vals.put("courseID", forumCode);
                    vals.put("title", title);
                    vals.put("body", body);
                    if(locked) {
                        vals.put("isLocked", 1);
                    }else{
                        vals.put("isLocked", 0);
                    }
                    if(testPosts.get(i).isComment) {
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
                        temp.put("postID", code);
                        temp.put("TYPE", Objects.requireNonNull(testPosts.get(i)).voteType);

                        db.insert("VOTED",null, temp);
                    }
                    vals.put("postDate", time);
                    vals.put("parentID", postID);
                    vals.put("sender", poster);
                    vals.put("upVotes", upscore);
                    vals.put("downVotes", downscore);
                    db.insert("POST", null, vals);
                }
            }else{
                String s = null;
                switch (responseCode){
                    case "failed_unknown":
                        s = "failed to get post, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_invalid_params":
                        s = "failed to get post, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_params":
                        s = "failed to get post, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_perms":
                        s = "failed to get post, you do not have permission to get posts";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void recSync(JSONArray r, String postID){
        System.out.println("syncing comments ");
        System.out.println(r.toString());
        if(r == null || postID == null || r.length() <= 0){
            return;
        }
        myDB = new DatabaseHelper(this, "PhoneDatabase");
        SQLiteDatabase db = myDB.getDB();
        String code = null;
        String title = null;
        String body = null;
        String poster = null;
        String time = null;
        boolean locked = false;
        String parentID = null;
        int upscore = 0, downscore=0, voted=0;
        JSONArray comments = null;
        JSONObject answer = null;
        post post = testPosts.get(postID);
        int k=0;
        try (Cursor c = myDB.doQuery("SELECT postID FROM POST")) {
            while (c.moveToNext()) {
                System.out.println(c.getString(c.getColumnIndex("postID")));
                k++;
            }
        }

        for(int j = 0; j < r.length(); j++){

            boolean ifTrue = false;
            String ifCode = "";
            try {
                JSONObject e = r.getJSONObject(j);
                code = e.getString("code");
                body = e.getString("body");
                poster = e.getString("poster");
                time = e.getString("time");
                upscore = e.getInt("upscore");
                downscore = e.getInt("downscore");
                comments = e.getJSONArray("comments");
                voted = e.getInt("voted");
                System.out.println(code);
                post post2 = null;
                try (Cursor c = myDB.doQuery("SELECT postID FROM POST where dovsID = " + code + ";")) {
                    if(c != null){
                        while(c.moveToNext()) {
                            ifCode = c.getString(c.getColumnIndex("postID"));
                            post2 = new post(ifCode, false, body, time, poster, postID);
                            ifTrue = true;
                        }
                    }else{
                        post2 = new post(Integer.toString(Integer.parseInt(postID) + k + j), false, body, time, poster, postID);
                    }
                }
                post2.DovsID = code;
                if (e.has("answer")) {
                    answer = e.getJSONObject("answer");
                }
                if (voted > 0) {
                    post2.isVoted = true;
                    post2.voteType = 1;
                } else if (voted < 0) {
                    post2.isVoted = true;
                    post2.voteType = 0;
                } else {
                    post2.isVoted = false;
                }
                post2.isComment = true;
                System.out.println("added a comment to post");
                post.addComment(post2);
                post2.setVotes(upscore, downscore);
                testPosts.get(postID).addComment(post2);
                System.out.println(testPosts.get(postID).getComments().get(0).getTitle());
                testPosts.put(post2.getPostID(), post2);
                ContentValues vals = new ContentValues();
                vals.put("postID", j+k+Integer.parseInt(postID));
                vals.put("courseID", forumCode);
                vals.put("title", title);
                vals.put("body", body);
                if(locked) {
                    vals.put("isLocked", 1);
                }else{
                    vals.put("isLocked", 0);
                }
                    vals.put("isComment", 1);
                    vals.put("isAnswer", 0);
                vals.put("postDate", time);
                vals.put("dovsID", code);
                vals.put("parentID", postID);
                vals.put("sender", poster);
                vals.put("upVotes", upscore);
                vals.put("downVotes", downscore);
                db.insert("POST", null, vals);
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(comments.length() > 0) {
                recSync(comments, Integer.toString(j + k + Integer.parseInt(postID)));
            }else {
                recSync(comments, ifCode);
            }
        }
    }



boolean canLock = false;
    @Override
    public void onBackPressed(){
        Intent i = new Intent(addComments.this, LectureCourseForum.class);
        i.putExtra("forumCode", forumCode);
        finish();
        startActivity(i);
    }
    String temp;
    public void setLocked(View v){
        cb = findViewById(R.id.checkBox1);
        int isLocked = 0;
        temp = v.getTag().toString();
        try(Cursor c = myDB.doQuery("SELECT * FROM POST WHERE postID =" + temp + ";")){
            c.moveToNext();
            dovsID = c.getString(c.getColumnIndex("dovsID"));
            isLocked = c.getInt(c.getColumnIndex("isLocked"));
            System.out.println(dovsID);
        }

        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("postCode", dovsID);
            if(isLocked == 1){
                System.out.println("false");
//                cb.setBackground(getResources().getDrawable(R.drawable.clear_background));
//                Objects.requireNonNull(testPosts.get(temp)).unlock(this);
                params.put("locked", false);
            }else if(isLocked == 0){
                System.out.println("true");
//                cb.setBackground(getResources().getDrawable(R.drawable.red_background));
//                Objects.requireNonNull(testPosts.get(temp)).lock(this);
                params.put("locked", true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/forum/set_locked", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        doLock(response);
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


    public void doLock(JSONObject postCode){
        try {
            if(postCode.getString("responseCode").equals("successful")){
                if(cb.isChecked()){
                cb.setBackground(getResources().getDrawable(R.drawable.clear_background));
                Objects.requireNonNull(testPosts.get(temp)).unlock(this);
                }else if(!cb.isChecked()){
                cb.setBackground(getResources().getDrawable(R.drawable.red_background));
                Objects.requireNonNull(testPosts.get(temp)).lock(this);
                }
            } else {
                String s;
                switch (resp) {
                    case "failed_unknown":
                        s = "failed to lock posts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_invalid_params":
                        s = "failed to lock posts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_params":
                        s = "failed to lockposts, check your internet connection";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;
                    case "failed_missing_perms":
                        s = "failed to lock posts, you do not have permission to lock posts";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void viewPost(View v){
        Intent i = new Intent(addComments.this, addComments.class);
        i.putExtra("postID", Integer.toString(v.getId()));
        i.putExtra("isAdmin", "true");
        i.putExtra("forumCode", forumCode);
        finish();
        startActivity(i);
    }
//
//
//
//


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
        System.out.println(forumCode + " forumcode");
        i.putExtra("forumCode", forumCode);
        finish();
        startActivity(i);
    }

    public void doComment(String code, String body){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            System.out.println(user_token + " : " + personNumber + " : " +  code + " : " + body);
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

    @Override
    public void onClick(View v) {
//        ImageButton b;
//        TextView t;
//        String temp;
//        switch (v.getTag()) {
//            case R.id.title:
//                Intent i = new Intent(addComments.this, addComments.class);
//                i.putExtra("postID", Integer.toString(v.getId()));
//                i.putExtra("isAdmin", "true");
//                System.out.println(v.getId());
//                startActivity(i);
//                break;
//            case :
//                b = v.findViewWithTag(v.getTag());
//                t = v.findViewWithTag("text");
//                b.setClickable(false);
//                b.setBackground(getResources().getDrawable(R.drawable.green_background));
//                b = ((View)v.getParent()).findViewWithTag("Down");
//                b.setClickable(true);
//                b.setBackground(getResources().getDrawable(R.drawable.clear_background));
//                temp = Integer.toString(((View)v.getParent().getParent()).findViewWithTag("title").getId());
//                Objects.requireNonNull(testPosts.get(temp)).addUp();
//                t.setText(Integer.toString(testPosts.get(temp).getUpvotes()));
//                break;
//            case "Down":
//                b = v.findViewWithTag(v.getTag());
//                t = ((View)v.getParent()).findViewWithTag("text");
//                b.setClickable(false);
//                b.setBackground(getResources().getDrawable(R.drawable.red_background));
//                b = ((View)v.getParent()).findViewWithTag("Up");
//                b.setClickable(true);
//                b.setBackground(getResources().getDrawable(R.drawable.clear_background));
//                temp = Integer.toString(((View)v.getParent()).findViewWithTag("title").getId());
//                System.out.println(temp);
//                Objects.requireNonNull(testPosts.get(temp)).addDown();
//                t.setText(Integer.toString(testPosts.get(temp).getUpvotes()));
//                break;
//
//        }
    }
}
