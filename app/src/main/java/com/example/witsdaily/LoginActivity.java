package com.example.witsdaily;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    EditText sNum, pWord;
    String type;
    String response_code;
    String user_token;

    public void doSignIn(View v) {
        // When the user signs in this will execute
        sNum = (EditText)findViewById(R.id.sNumber);
        pWord =(EditText) findViewById(R.id.password);
        final String password = pWord.getText().toString();
        final String sNumber = sNum.getText().toString();

        final StringRequest request = new StringRequest(Request.Method.POST, "https://url.goes.here",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String output = jsonObject.getString("response_code");
                            doOutput(output);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If there's a network error.
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", sNumber);
                params.put("password", password);

                return params;
            }
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);




    }

    // called when a personclicks the register text
    public void doRegister(View v){
        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(i);
    }

    //this handles the response from the server API

    private void doOutput(String output){
//        type = Character.toString();
        switch (output) {
            case "successful":
                boolean canNext = true;
                Intent i = new Intent(LoginActivity.this, UserRegistration.class);
                startActivity(i);

                break;
            case "failed_no_user": {
                canNext = false;
                String s = "Login failed: Please register";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_no_perm": {
                canNext = false;
                String s = "Login failed: You do not have the required permissions";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_invalid_param": {
                canNext = false;
                String s = "Login failed: Please enter a valid username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_missing_param": {
                canNext = false;
                String s = "Login failed: Please enter a username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_unknown": {
                canNext = false;
                String s = "Login failed: Please try again";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            default: {
                canNext = false;
                String s = "Login failed: Check your connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            }
        }


    }
}

