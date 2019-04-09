package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.witsdaily.PhoneDatabaseContract.*;

interface storageListener{
    void getData(JSONObject data);
}

public class StorageAccessor{ // singleton class
    private static StorageAccessor single_instance = null;
    private List<storageListener> listeners = new ArrayList<storageListener>();
    private NetworkAccessor networkAccessor;
    private DatabaseAccessor databaseAccessor;
    private String personNumber;
    private String userToken;

    // private constructor restricted to this class itself
    private StorageAccessor(Context context,String pPersonNumber,String pUserToken)
    {
        personNumber = pPersonNumber;
        userToken = pUserToken;
        networkAccessor  = new NetworkAccessor(context, personNumber,userToken);
        databaseAccessor = new DatabaseAccessor(context);
    }

    public static StorageAccessor getInstance(Context context,String personNumber,String userToken)
    {
        if (single_instance == null)
            single_instance = new StorageAccessor(context,personNumber,userToken);

        return single_instance;
    }

    public static StorageAccessor getInstance()
    {
        if (single_instance == null)
            return null;

        return single_instance;
    }

    public void addListener(storageListener toAdd) {
        listeners.add(toAdd);
    }
    private void sendData(JSONObject data){
        storageListener rL = listeners.get(0);
        listeners.remove(0);
        rL.getData(data);
    }

    public void login(String password)
    {
        networkAccessor.addListener(new requestListener(){
            @Override
            public void getResponse(JSONObject data){
                // manipulate data in someway here
                sendData(data);
            }
        });
        networkAccessor.loginRequest(password);
    }

    public void updateServerFCMToken(String fcmToken){
        networkAccessor.addListener(new requestListener(){
            @Override
            public void getResponse(JSONObject data){
                // it is only a response, no need to call a method
                try {
                    System.out.println(data.getString("responseCode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        networkAccessor.updateServerFCMToken(fcmToken);
    }

    public void getEnrolledCourses()
    {
        networkAccessor.addListener(new requestListener(){
            @Override
            public void getResponse(JSONObject data){
                // manipulate data in someway here
                sendData(data);

            }
        });
        networkAccessor.getEnrolledCourses();
    }
    public void getUnenrolledCourses()
    {
        networkAccessor.addListener(new requestListener(){
            @Override
            public void getResponse(JSONObject data){
                // manipulate data in someway here
                sendData(data);
            }
        });
        networkAccessor.getUnenrolledCourses();
    }

    public JSONArray getCourses(){
        String sql = "Select * from "+ TableCourse.TABLE_NAME
                +" JOIN "+ TablePersonCourse.TABLE_NAME +" ON " + TableCourse.COLUMN_NAME_ID
                + " = " + TablePersonCourse.COLUMN_NAME_COURSEID
                + " WHERE " + TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                + " = \""+ personNumber+"\"";
        JSONArray values = null;
        try {
            values = databaseAccessor.selectRecords(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
//
    public JSONArray getUCourses(){
        String sql = "Select * From " +TableCourse.TABLE_NAME
                +" where "+ TableCourse.COLUMN_NAME_CODE+" NOT IN (Select " +
                TableCourse.COLUMN_NAME_CODE
                +" From "
                +TableCourse.TABLE_NAME
                +" Join "+ TablePersonCourse.TABLE_NAME+" on " +TableCourse.COLUMN_NAME_ID +" = "
        +TablePersonCourse.COLUMN_NAME_COURSEID+")";
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
        networkAccessor.addListener(new requestListener() {
            @Override
            public void getResponse(JSONObject data) {
                sendData(data);
            }
        });
        networkAccessor.enrollUser(password,courseCode);
    }
    /* template
    public void nameofprocess(extra paramaters, exclusive of usertoken and person number)
    {
        networkAccessor.addListener(new requestListener(){
            @Override
            public void getResponse(JSONObject data){
                // manipulate data in someway here
                sendData(data);
            }
        });
        networkAccessor.someprocessyouwroteInNetworkAccessor(password);
    }
    * */
}
