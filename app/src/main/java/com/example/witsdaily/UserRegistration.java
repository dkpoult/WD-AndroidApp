package com.example.witsdaily;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistration extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }
    public void clickRegister(View v){
        final EditText personID = findViewById(R.id.edtPersonID);
        EditText personPassword = findViewById(R.id.edtPassword);
        final String personIDValue = personID.getText().toString();
        final String passwordValue = personPassword.getText().toString();

        // When the user signs in this will execute

        StorageAccessor dataAccessor = new StorageAccessor(getApplicationContext(),"",""){
            @Override
            void getData(JSONObject data) {
                try {
                    doOutput(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.registerUser(personIDValue,passwordValue);
    }

    private void doOutput(JSONObject response) throws JSONException {

        String output = response.getString("responseCode");
//        type = Character.toString();
        switch (output) {
            case "successful":
                String user_token = response.getString("userToken");
                Intent i = new Intent(UserRegistration.this, HomeScreen.class);
                i.putExtra("user_token", user_token);
                startActivity(i);

                break;
            case "failed_already_exists": {
                String s = "Registration failed: Please sign in";
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
