package com.example.witsdaily;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class addPost extends Activity {
    String user_token;
    String personNumber;
    String forumCode;
    String title;
    String body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        TextView t = findViewById(R.id.titleValue);
        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        forumCode = intent.getStringExtra("forumCode");
        t.setText(title);
    }


    public void doPost(View v){
        System.out.print("test4");
        EditText e = findViewById(R.id.postContent);
        body = e.getText().toString();
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("forumCode", forumCode);
            params.put("body", body);
            params.put("title", title);

        } catch (JSONException t) {
            t.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/make_post", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        doValidateMessage(response);
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


    @SuppressLint("SetTextI18n")
    public void doValidateMessage(JSONObject r) {
        String response = null;
        try {
            response = r.getString("responseCode");
            TextView out = findViewById(R.id.response);
            System.out.print(response);
            switch (response){
                case "successful":
                    Intent i = new Intent(addPost.this, LectureCourseForum.class);
                    i.putExtra("forumCode", forumCode);
                    finish();
                    startActivity(i);
                    break;
                case "failed_unknown":
                    out.setText("Failed to send your post, please try again.");
                    break;
                case "failed_invalid_params":
                    out.setText("Failed to send your post, please try again.");
                    break;
                case "failed_missing_params":
                    out.setText("Failed to send your post, please try again.");
                    break;
                case "failed_missing_perms":
                    out.setText("Failed to send your post, you do not currently have permission to send a post.");
                    break;
                case "failed_forum_locked":
                    out.setText("Failed to send your post, this forum is locked.");
                    break;
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
