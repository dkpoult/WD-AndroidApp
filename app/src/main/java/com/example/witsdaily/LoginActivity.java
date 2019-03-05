package com.example.witsdaily;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    String user_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

    }

    EditText sNum, pWord;
    String type;

    public void doSignIn(View v) {
        // When the user signs in this will execute
        sNum = findViewById(R.id.sNumber);
        pWord = findViewById(R.id.password);
        final String password = pWord.getText().toString();
        final String sNumber = sNum.getText().toString();

        /*final StringRequest request = new StringRequest(Request.Method.POST, "https://wd.dimensionalapps.com/login",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try {
                            doOutput(response);
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
            @Override
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
            params.put("personNumber", sNumber);
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
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);




    }

    // called when a personclicks the register text
    public void doRegister(View v){
        Intent i = new Intent(LoginActivity.this, UserRegistration.class);
        startActivity(i);
    }

    //this handles the response from the server API

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
            case "failed_invalid_param": {
                String s = "Login failed: Please enter a valid username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_missing_param": {
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
}

