package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public abstract class StorageAccessor{ // singleton class

    private DatabaseAccessor databaseAccessor;
    private String personNumber;
    private String userToken;
    private Context appContext;

    public StorageAccessor(Context context,String pPersonNumber,String pUserToken)
    {
        personNumber = pPersonNumber;
        userToken = pUserToken;
        appContext = context;
        databaseAccessor = new DatabaseAccessor(context);
    }

    public abstract void getData(JSONObject data);


    public void login(String password)
    {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.loginRequest(password);
    }

    public void updateServerFCMToken(String fcmToken){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.updateServerFCMToken(fcmToken);
    }
    public void registerUser(String personIDNumber,String personPassword){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.registerUser(personIDNumber,personPassword);
    }
    public void getEnrolledCourses()
    {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getEnrolledCourses();
    }
    public void getUnenrolledCourses()
    {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getUnenrolledCourses();
    }
    public boolean userLinked(String courseID){
        return databaseAccessor.userLinked(courseID,personNumber);

    }
    public String courseCodeToID(String courseCode){
        return databaseAccessor.courseCodeToID(courseCode);
    }

    public JSONArray getLocalCourses(){
       return databaseAccessor.getLocalCourses(personNumber);
    }
//
    public JSONArray getUCourses(){

        String sql = "Select * From " +TableCourse.TABLE_NAME
                +" where "+ TableCourse.COLUMN_NAME_CODE+" NOT IN (Select " +
                TableCourse.COLUMN_NAME_CODE
                +" From "
                +TableCourse.TABLE_NAME
                +" Join "+ TablePersonCourse.TABLE_NAME+" on " +TableCourse.COLUMN_NAME_ID +" = "
        +TablePersonCourse.COLUMN_NAME_COURSEID+" where "+TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                +" = \""+personNumber+"\")";
        JSONArray values = null;
        try {
            values = databaseAccessor.selectRecords(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public void linkUserToCourse(String courseID){
        ContentValues params = new ContentValues();
        params.put(TablePersonCourse.COLUMN_NAME_PERSONNUMBER,personNumber);
        params.put(TablePersonCourse.COLUMN_NAME_COURSEID,courseID);
        databaseAccessor.insertValues(params,TablePersonCourse.TABLE_NAME);
    }

    public void enrollUser(String password,String courseCode){

        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.enrollUser(password,courseCode);
    }

    public boolean containsCourseCode(String courseCode){
        return databaseAccessor.containsCourseCode(courseCode);
    }

    public void firebaseAuthenticate()
    {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.firebaseAutenticate();
    }

    public void getSurvey(String courseCode){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber,userToken) {
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getSurvey(courseCode);
    }
    public void makeSurvey(String courseCode,String title,JSONArray options,String surveyType){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext,personNumber,userToken) {
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.makeSurvey(courseCode,title,options,surveyType);
    }
    public void setSurveyAnswer(String courseCode, String answer){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext,personNumber,userToken) {
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.setSurveyAnswer(courseCode,answer);
    }

    public void closeSurvey(String courseCode){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext,personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.closeSurvey(courseCode);
    }
    public void getSurveyResults(String courseCode){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext,personNumber,userToken){
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getSurveyResults(courseCode);
    }

    public void sendAnswer(String answer, String courseCode){
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext,personNumber,userToken) {
            @Override
            void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.sendAnswer(answer,courseCode);
    }

}
