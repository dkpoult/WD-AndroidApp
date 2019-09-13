package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class courseLink extends AppCompatActivity {
    EditText edtCourseCode;
    String userID;
    TextView tvState;
    String userToken;// get Dylan to send this to me through activity data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_link);

        userID = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        tvState = findViewById(R.id.tvState);
        tvState.setVisibility(View.INVISIBLE);
    }
    public void clickLinkCourse(View v){
        edtCourseCode = findViewById(R.id.edtCourseCode);
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", userID);
            params.put("userToken", userToken);
            params.put("courseId", edtCourseCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!validFields()) {
            tvState.setVisibility(View.VISIBLE);
            return;
        } else {
            tvState.setVisibility(View.INVISIBLE);
        }

        final JsonObjectRequest request = new JsonObjectRequest("https://wd.dimensionalapps.com/course/link_course", params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            handleResponse(response.toString());
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

    private boolean validString(String currentString){ // consider adding this to a class to handle encryption/hashing/validating
        return true;
    }

    private boolean validFields() {
        // prevents sql injection, makes sure that passwords match etc
        tvState.setTextColor(Color.RED);
        return true;
    }

    private void handleResponse(String jResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jResponse);
        String response = jsonObject.getString("responseCode");
        tvState.setTextColor(Color.RED); // it wont display if successful
        System.out.println(response);
        String stateText = "";
        switch (response){
            case "successful":
                Intent i = new Intent(courseLink.this, HomeScreen.class);
                startActivity(i);
                Toast.makeText(courseLink.this, "Successfully linked course," +
                        ""      , Toast.LENGTH_LONG).show();;return;
            case "failed_already_exists": stateText = "Course already exists";break;
            case "failed_no_perm": stateText = "No permissions to add course";break;
            case "failed_missing_param": stateText = "Missing parameters";break;
            case "failed_invalid_param": stateText = "Invalid field";break;
            case "failed_unknown": stateText = "Unknown";break;
        }
        Toast.makeText(courseLink.this, "Failed To Link Course" , Toast.LENGTH_LONG).show();
        tvState.setText("Failed: "+stateText);
    }


}
