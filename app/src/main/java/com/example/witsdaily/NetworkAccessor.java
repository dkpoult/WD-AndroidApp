package com.example.witsdaily;

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

interface requestListener{
    // the do something that happens
    void getResponse(JSONObject response);
}

public class NetworkAccessor {
    private RequestQueue requestQueue;
    private List<requestListener> listeners = new ArrayList<requestListener>();
    private String personNumber;
    private String userToken;

    public NetworkAccessor(Context context,String pPersonNumber,String pUserToken){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        userToken = pUserToken;
        personNumber = pPersonNumber;

    }

    public void addListener(requestListener toAdd) {
        listeners.add(toAdd);
    }

    void processRequest(JSONObject data){
        requestListener rL = listeners.get(0);
        listeners.remove(0);
        rL.getResponse(data);
    }

    private void makeRequest(JSONObject params, final String APIUrl, final String errorMessage){ // the bread and butter of all requests
        // check errors for params through specific requests

        final JsonObjectRequest request = new JsonObjectRequest(APIUrl, params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        System.out.println("successfull "+APIUrl); // possible return, make function instead
                        processRequest(response);
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
        makeRequest(params,"https://wd.dimensionalapps.com/set_fcm_token","FCM Token");
    }

    public void loginRequest(String password){
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personNumber);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/login","Login failed");

    }
    public void getEnrolledCourses(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/get_courses","Get enrolled failed");
    }
    public void getUnenrolledCourses(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/get_available_courses",
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
        makeRequest(params,"https://wd.dimensionalapps.com/enrol_in_course","Error enroll");
    }
}
