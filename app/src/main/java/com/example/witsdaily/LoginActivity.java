package com.example.witsdaily;
<<<<<<< HEAD
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
=======
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
>>>>>>> courseregistration
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

<<<<<<< HEAD
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

=======
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
//Test
public class LoginActivity extends AppCompatActivity {
    String user_token;
    String personNumber;
>>>>>>> courseregistration
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
<<<<<<< HEAD
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
=======
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        System.out.println(personNumber);
        System.out.println(user_token);

        if(user_token != null && personNumber != null){
            doValidate(user_token, personNumber);
        }

    }

    EditText sNum, pWord;

    public void doValidate(String user_token, String personNumber){
        JSONObject params = new JSONObject();
        System.out.println("Test");
        try {
            params.put("userToken", user_token);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/validate_token", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println(response.toString());
                        doValidateMessage(response);
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

    public void doValidateMessage(JSONObject response){
        String output = null;
        try {
            output = response.getString("responseCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert output != null;
        switch (output) {
            case "successful":
                SharedPreferences sharedPreferences = getSharedPreferences("com.wd", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("userToken", user_token).apply();
                System.out.println(personNumber);
                sharedPreferences.edit().putString("personNumber", personNumber).apply();
                Intent i = new Intent(LoginActivity.this, HomeScreen.class);
                startActivity(i);

                break;
            case "failed_no_user": {
                String s = "Login failed: Please register";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_no_perm": {
                String s = "Login failed: You do not have the required permissions";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_invalid_token": {
                String s = "Login failed: Please enter a valid username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_missing_param": {
                System.out.println(output);
                String s = "Login failed: Please enter a username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_unknown": {
                String s = "Login failed: Please try again";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            default: {
                String s = "Login failed: Check your connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            }
        }


    }

    public void doSignIn(View v) {
        // When the user signs in this will execute
        sNum = findViewById(R.id.sNumber);
        pWord = findViewById(R.id.password);
        final String password = pWord.getText().toString();
        personNumber = sNum.getText().toString();

        /*final StringRequest request = new StringRequest(Request.Method.POST, "https://wd.dimensionalapps.com/login",
>>>>>>> courseregistration
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try {
<<<<<<< HEAD
                            JSONObject jsonObject = new JSONObject(response);
                            String output = jsonObject.getString("response_code");
                            doOutput(output);
=======
                            doOutput(response);
>>>>>>> courseregistration
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
<<<<<<< HEAD
                        //If there's a network error.
=======
                        String s = error.getLocalizedMessage();
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
>>>>>>> courseregistration
                    }
                })
        {
            @Override
<<<<<<< HEAD
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", sNumber);
                params.put("password", password);

                return params;
            }
=======
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject params = new JSONObject();
                try {
                    params.put("personNumber", sNumber);
                    params.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(params.toString());

                return params.toString().getBytes();
            }
        };*/


        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personNumber);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/login", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            doOutput(response.toString());
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
>>>>>>> courseregistration
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);




    }

    // called when a personclicks the register text
    public void doRegister(View v){
<<<<<<< HEAD
        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
=======
        Intent i = new Intent(LoginActivity.this, UserRegistration.class);
>>>>>>> courseregistration
        startActivity(i);
    }

    //this handles the response from the server API

<<<<<<< HEAD
    private void doOutput(String output){
//        type = Character.toString();
        switch (output) {
            case "successful":
                boolean canNext = true;
=======
    private void doOutput(String response) throws JSONException {
//        type = Character.toString();
        JSONObject jsonObject = new JSONObject(response);
        String output = jsonObject.getString("responseCode");
        System.out.println(output);
        switch (output) {
            case "successful":
                String user_token = jsonObject.getString("userToken");
                SharedPreferences sharedPreferences = getSharedPreferences("com.wd", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("userToken", user_token).apply();
                sharedPreferences.edit().putString("personNumber", personNumber).apply();
>>>>>>> courseregistration
                Intent i = new Intent(LoginActivity.this, HomeScreen.class);
                startActivity(i);

                break;
            case "failed_no_user": {
<<<<<<< HEAD
                canNext = false;
=======
>>>>>>> courseregistration
                String s = "Login failed: Please register";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_no_perm": {
<<<<<<< HEAD
                canNext = false;
=======
>>>>>>> courseregistration
                String s = "Login failed: You do not have the required permissions";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_invalid_param": {
<<<<<<< HEAD
                canNext = false;
=======
>>>>>>> courseregistration
                String s = "Login failed: Please enter a valid username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
<<<<<<< HEAD
            case "failed_missing_param": {
                canNext = false;
=======
            case "failed_missing_params": {
>>>>>>> courseregistration
                String s = "Login failed: Please enter a username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_unknown": {
<<<<<<< HEAD
                canNext = false;
=======
>>>>>>> courseregistration
                String s = "Login failed: Please try again";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            default: {
<<<<<<< HEAD
                canNext = false;
=======
>>>>>>> courseregistration
                String s = "Login failed: Check your connection";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                break;
            }
        }


    }
}

