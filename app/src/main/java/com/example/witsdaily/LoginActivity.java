package com.example.witsdaily;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

    public void doSignIn(View v) {
        // When the user signs in this will execute
        sNum = findViewById(R.id.sNumber);
        pWord = findViewById(R.id.password);
        final String password = pWord.getText().toString();
        final String sNumber = sNum.getText().toString();

        final StringRequest request = new StringRequest(Request.Method.POST, "https://url.goes.here",
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
        Intent i = new Intent(LoginActivity.this, UserRegistration.class);
        startActivity(i);
    }

    //this handles the response from the server API

    private void doOutput(String response) throws JSONException {
//        type = Character.toString();
        JSONObject jsonObject = new JSONObject(response);
        String output = jsonObject.getString("response_code");
        switch (output) {
            case "successful":
                String user_token = jsonObject.getString("user_token");
                Intent i = new Intent(LoginActivity.this, HomeScreen.class);
                i.putExtra("user_token", user_token);
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

