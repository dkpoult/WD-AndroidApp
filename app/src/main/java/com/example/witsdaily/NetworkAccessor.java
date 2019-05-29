package com.example.witsdaily;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class NetworkAccessor {
    private RequestQueue requestQueue;
    private String personNumber;
    private Context context;
    private String userToken;
    abstract void getResponse(JSONObject data);

    NetworkAccessor(Context pContext, String pPersonNumber, String pUserToken){
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
                response -> {
                    System.out.println("successfull "+APIUrl); // possible return, make function instead
                    progressBar.dismiss();
                    getResponse(response); // get for outer class
                },
                error -> {
                    String s = error.getLocalizedMessage();
                    System.out.println(errorMessage+" "+s);
                })
        {
        };

        requestQueue.add(request);

    }
//personNumber, userToken, fcmToken
void updateServerFCMToken(String fcmToken){
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

    void firebaseAutenticate(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/push/notification_token","firebase authenticate error");
    }

    void loginRequest(String password){
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personNumber);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/auth/login","Login failed");

    }
    void registerUser(String personIDValue, String passwordValue){
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personIDValue);
            params.put("password", passwordValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/auth/register","Register user failed");
    }
    void getEnrolledCourses(){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/get_courses","Get enrolled failed");
    }
    void getUnenrolledCourses(){
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
    void enrollUser(String password, String courseCode){
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
    void getSurvey(String courseCode){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
        }catch (JSONException e){
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/survey/get_survey","Error survey");
    }
    void makeSurvey(String courseCode, String title, JSONArray options, String responseType){
        JSONObject params = new JSONObject();
        try{ //personNumber, userToken, title, options, courseCode
            params.put("personNumber", personNumber);
            params.put("title", title);
            params.put("options", options);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
            params.put("responseType",responseType);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/survey/make_survey","Error survey creation");
    }

    void closeSurvey(String courseCode){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/survey/close_survey","Error closing survey");
    }
    void getSurveyResults(String courseCode){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/survey/get_results","Error getting survey results");
    }

    void sendAnswer(String answer, String courseCode, String surveyType){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);
            params.put("answer",answer);
            params.put("responseType",surveyType);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/survey/send_answer","Error setting survey answer");
    }




    void getSessions(String courseCode){
        JSONObject params = new JSONObject();
        try{
            params.put("personNumber", personNumber);
            params.put("userToken", userToken);
            params.put("courseCode", courseCode);
        } catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/get_course","get_Session error");
    }

    void addSession(String courseCode, JSONObject venue, String type, int Freq, String Occurence, String sType, int duration, JSONArray cancels){
        JSONObject params = new JSONObject();

        try{
            params.put("personNumber", personNumber);
            params.put("userToken",userToken);
            params.put("courseCode",courseCode);

            JSONObject session = new JSONObject();

            session.put("venue", venue);
            session.put("repeatType", type);
            session.put("sessionType", sType);
            session.put("repeatGap", Freq);
            session.put("nextDate", Occurence);
            session.put("duration", duration);
            session.put("cancellations", cancels);
            params.put("session", session);
        }catch (JSONException e){
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/add_session","add_session error");
    }

void getCourse(String courseCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/get_course","Get course failed");
    }

void updateCourse(String courseCode, String couseDesc, String courseName, String newKey){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
            params.put("courseName", courseName);
            params.put("courseDescription", couseDesc);
            params.put("password", newKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/course/update_course","Update course failed");
    }

    void editSessions(String courseCode, JSONArray sessions){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
            params.put("sessions", sessions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/update_sessions ","Update sessions failed");
    }
    public void getPosts(String forumCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("forumCode", forumCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/get_posts ","Get Posts failed");
    }

    public void getPost(String postCode){ // detailed ones
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("postCode", postCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/get_post ","Get Post failed");
    }
    public void makePost(String forumCode,String title, String body){ // detailed ones
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("forumCode", forumCode);
            params.put("title",title);
            params.put("body",body);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/make_post ","Make Post failed");
    }
    public void makeComment(String postCode, String body){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("postCode", postCode);
            params.put("body",body);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/make_comment ","Make Comment failed");
    }
    public void makeVote(String postCode, String vote){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("postCode", postCode);
            params.put("vote",vote);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/make_vote ","Make vote failed");
    }

    public void setAnswer(String postCode, String commentCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("postCode", postCode);
            params.put("commentCode",commentCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/forum/set_answer ","Set answer failed");
    }


    void getVenues(){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/venue/get_venues ","Get venues failed");
    }

}
/*For MC, answer should be the zero-
based index of the selected option. For text and numeric the answer should simply be the user given answer.*/