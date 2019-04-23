package com.example.witsdaily;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public abstract class NetworkAccessor {
    private RequestQueue requestQueue;
    private String personNumber;
    private Context context;
    private String userToken;
    abstract void getResponse(JSONObject data);

    public NetworkAccessor(Context pContext,String pPersonNumber,String pUserToken){
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext.getApplicationContext());
        userToken = pUserToken;
        personNumber = pPersonNumber;

    }

    private void makeRequest(JSONObject params, final String APIUrl, final String errorMessage){ // the bread and butter of all requests
        // check errors for params through specific requests
        final ProgressDialog progressBar = new ProgressDialog(context);
        progressBar.setMessage("Loading");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
        progressBar.show();
        final JsonObjectRequest request = new JsonObjectRequest(APIUrl, params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println("successfull "+APIUrl); // possible return, make function instead
                        progressBar.dismiss();
                        getResponse(response); // get for outer class
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String s = error.getLocalizedMessage();
                        System.out.println(errorMessage+" "+s);
                    }
                })
        {
        };

        requestQueue.add(request);

    }
//personNumber, userToken, fcmToken
    public void updateServerFCMToken(String fcmToken){
        JSONObject params = new JSONObject();

        try {
            params.put("fcmToken",fcmToken);
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/push/set_fcm_token","FCM Token");
    }

    public void firebaseAutenticate(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/push/notification_token","firebase authenticate error");
    }

    public void loginRequest(String password){
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personNumber);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/auth/login","Login failed");

    }
    public void getEnrolledCourses(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/get_courses","Get enrolled failed");
    }
    public void getUnenrolledCourses(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/get_available_courses",
                "Get Unenrolled failed");
    }
    public void enrollUser(String password,String courseCode){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("password", password);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/enrol_in_course","Error enroll");
    }
}
