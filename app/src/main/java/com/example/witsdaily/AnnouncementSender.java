package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AnnouncementSender extends AppCompatActivity {

    String personNumber,user_token,courseCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_sender);
        Intent i = getIntent();
        courseCode = (i.getStringExtra("courseCode"));
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);

    }
    public void clickMakeAnnouncement(View v){
        EditText edtTitle = (EditText)findViewById(R.id.edtTitle);
        EditText edtBody = (EditText)findViewById(R.id.edtBody);
        //personNumber, userToken, courseCode, title, body
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);
            params.put("title", edtTitle.getText().toString());
            params.put("body", edtBody.getText().toString());
            params.put("courseCode", courseCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/push/make_announcement", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            processRequest(response.getString("responseCode"));
                            Toast.makeText(getApplicationContext(), response.getString("responseCode"), Toast.LENGTH_LONG).show();
                            // sets a listener to determine the change

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                })
        {
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);

    }


    private void processRequest(String responseCode){
        System.out.println(responseCode);
    }
}
