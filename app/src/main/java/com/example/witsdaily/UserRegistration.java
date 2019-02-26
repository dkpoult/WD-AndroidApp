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

public class UserRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }
    public void clickRegister(View v){
        final EditText personID = (EditText)findViewById(R.id.edtPersonID);
        EditText personPassword = (EditText)findViewById(R.id.edtPassword);

        final String personIDValue = personID.getText().toString();
        final String passwordValue = personPassword.getText().toString();

        // When the user signs in this will execute

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

                params.put("username", personIDValue);
                params.put("password", passwordValue);

                return params;
            }
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);
    }

    private void doOutput(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String output = jsonObject.getString("response_code");
//        type = Character.toString();
        switch (output) {
            case "successful":
                String user_token = jsonObject.getString("user_token");
                Intent i = new Intent(UserRegistration.this, HomeScreen.class);
                i.putExtra("user_token", user_token);
                startActivity(i);

                break;
            case "failed_already_exists": {
                String s = "Registration failed: Please register";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_no_perm": {
                String s = "Registration failed: You do not have the required permissions";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_invalid_param": {
                String s = "Registration failed: Please enter a valid username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_missing_param": {
                String s = "Registration failed: Please enter a username and password";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                break;
            }
            case "failed_unknown": {
                String s = "Registration failed: Please try again";
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
