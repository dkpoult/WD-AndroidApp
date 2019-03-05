package com.example.witsdaily;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class courseLink extends AppCompatActivity {
    EditText edtCourseCode = (EditText)findViewById(R.id.edtCourseCode);
    TextView tvState = (TextView)findViewById(R.id.tvState);
    final String userID = ""; // get Dylan to send this to me through activity data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_link);
        tvState.setVisibility(View.INVISIBLE);
    }
    public void clickLinkCourse(View v){
        final String courseCode = edtCourseCode.getText().toString();
        if (!validFields()) {
            tvState.setVisibility(View.VISIBLE);
            return;
        } else {
            tvState.setVisibility(View.INVISIBLE);
        }
        final StringRequest request = new StringRequest(Request.Method.POST, "https://wd.dimensionalapps.com/linkCourse",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try {
                            handleResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String s = "Course linking failed";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject params = new JSONObject();
                try {
                    params.put("courseCode", courseCode);
                    params.put("userID",userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params.toString().getBytes();
            }
        };

        VolleyRequestManager.getManagerInstance(this.getApplicationContext()).addRequestToQueue(request);
    }

    private boolean validString(String currentString){ // consider adding this to a class to handle encryption/hashing/validating
        return true;
    }

    private boolean validFields() {
        // prevents sql injection, makes sure that passwords match etc
        tvState.setTextColor(Color.RED);
        if (edtCourseCode.getText().length()!=8){
            tvState.setText("Failed: Course code must be 8 characters long");
            return false;
        }
        return true;
    }

    private void handleResponse(String jResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jResponse);
        String response = jsonObject.getString("responseCode");
        tvState.setTextColor(Color.RED); // it wont display if successful
        String stateText = "";
        switch (response){
            case "successful": /*do something*/;
                Toast.makeText(courseLink.this, "Successfully created new course," +
                        ""      , Toast.LENGTH_LONG).show();;return;
            case "failed_already_exists": stateText = "Course already exists";break;
            case "failed_no_perm": stateText = "No permissions to add course";break;
            case "failed_missing_param": stateText = "Missing parameters";break;
            case "failed_invalid_param": stateText = "Invalid field";break;
            case "failed_unknown": stateText = "Unknown";break;
        }
        Toast.makeText(courseLink.this, "Failed To Create Course" , Toast.LENGTH_LONG).show();
        tvState.setText("Failed: "+stateText);
    }


}
