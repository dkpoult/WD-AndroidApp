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
    public abstract void getResponse(JSONObject data);

    public NetworkAccessor(Context pContext, String pPersonNumber, String pUserToken){
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext.getApplicationContext());

        userToken = pUserToken;
        personNumber = pPersonNumber;

    }

    private void makeRequest(JSONObject params, final String APIUrl, final String errorMessage){ // the bread and butter of all requests
        // check errors for params through specific requests
        final ProgressDialog progressBar = new ProgressDialog(context);

        try{
            progressBar.setMessage("Loading");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setIndeterminate(true);
            progressBar.show();
        }
        catch(Exception e){
            System.out.println("Doesn't work with fragments for now");
        }
        final JsonObjectRequest request = new JsonObjectRequest(APIUrl, params,
                response -> {
                    System.out.println("successfull "+APIUrl); // possible return, make function instead
                    try {
                        progressBar.dismiss();
                    }catch (Exception e){
                        //progressBar probably doesnt exist
                    }
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
    public void registerUser(String personIDValue, String passwordValue){
        JSONObject params = new JSONObject();
        try {
            params.put("personNumber", personIDValue);
            params.put("password", passwordValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/auth/register","Register user failed");
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
    public void enrollUser(String password, String courseCode){
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
    public void getSurvey(String courseCode){
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
    public void makeSurvey(String courseCode, String title, JSONArray options, String responseType){
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

    public void closeSurvey(String courseCode){
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
    public void getSurveyResults(String courseCode){
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

    public void sendAnswer(String answer, String courseCode, String surveyType){
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




    public void getSessions(String courseCode){
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

    public void addSession(String courseCode, JSONObject venue, String type, int Freq, String Occurence, String sType, int duration, JSONArray cancels){
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
            session.put("startDate", Occurence);
            session.put("duration", duration);
            session.put("cancellations", cancels);
            params.put("session", session);
        }catch (JSONException e){
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/add_session","add_session error");
    }

public void getCourse(String courseCode){
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

public void linkCourse (String courseCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseId", courseCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/link_course","Link course failed");
    }

public void updateCourse(String courseCode, String couseDesc, String courseName, String newKey){
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

    public void editSessions(String courseCode, JSONArray sessions){
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


    public void getVenues(){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/venue/get_venues ","Get venues failed");
    }

    public void getChatTypeMessages(String chatroomCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("chatroomCode", chatroomCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/chat/get_messages ","Get messages failed");
    }

    public void getPermissionCodes(){
        JSONObject params = new JSONObject();

        makeRequest(params,"https://wd.dimensionalapps.com/permission/get_permission_codes ","get permissions failed");
    }

    public void serPermissions(JSONObject j){
        try {
            j.put("userToken", userToken);
            j.put("personNumber", personNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeRequest(j,"https://wd.dimensionalapps.com/permission/set_permissions ","Set permissions failed");
    }

    public void resync(String courseCode){
        JSONObject params = new JSONObject();
        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/resync_course ","Get Posts failed");

    }

    public void editBookables(String courseCode, JSONArray sessions){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
            params.put("bookableSessions", sessions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/update_bookable_sessions  ","Update bookable sessions failed");
    }

    public void makeBooking(int sessionId, String  repeatIndex, int slotIndex, String courseCode, String lecturerPersonNumber){JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("courseCode", courseCode);
            params.put("bookableSessionId", sessionId);
            params.put("slotIndex", slotIndex);
            params.put("repeatIndex", repeatIndex);
            params.put("lecturerPersonNumber", lecturerPersonNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/course/make_booking  ","Booking failed");
    }
  
    public void getVenueImage(String buildingCode, String subCode){
        JSONObject params = new JSONObject();

        try {
            params.put("userToken", userToken);
            params.put("personNumber", personNumber);
            params.put("buildingCode", buildingCode);
            params.put("subCode", subCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest(params,"https://wd.dimensionalapps.com/venue/get_venue_image  ","floorVenue image failed");
    }

    public void getEvents(){
        JSONObject params = new JSONObject();
        makeRequest(params,"https://wd.dimensionalapps.com/event/get_events","Get events failed");
    }
  
    public void getEvent(String eventCode){
        JSONObject params = new JSONObject();
        try {
            params.put("eventCode",eventCode);
        }catch (JSONException e){
            e.printStackTrace();
        }
        makeRequest(params,"https://wd.dimensionalapps.com/event/get_event","Get event failed");
    }

    public void getBuildings(){
        JSONObject params = new JSONObject();
        makeRequest(params, "https://wd.dimensionalapps.com/venue/get_buildings ","Get buildings failed");
    }
}
/*For MC, answer should be the zero-
based index of the selected option. For text and numeric the answer should simply be the user given answer.*/