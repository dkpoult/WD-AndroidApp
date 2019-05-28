package com.example.witsdaily;

import android.content.Context;
import android.content.res.Configuration;

import com.example.witsdaily.PhoneDatabaseContract;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WitsDailySettings {
    String personNumber,user_token;
    Context baseContext,currentContext;
    WitsDailySettings(String pPersonNumber, String pUser_token, Context pBaseContext, Context pCurrentContext){
        personNumber = pPersonNumber;
        user_token = pUser_token;
        baseContext = pBaseContext;
        currentContext = pCurrentContext;
    }
    public void loadLanguage(String personNumber){
        StorageAccessor dataAccessor = new StorageAccessor(currentContext,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {

            }
        };
        try {

            JSONObject settings = dataAccessor.getSettings(personNumber).getJSONObject(0);
            String language =  settings.getString(PhoneDatabaseContract.TableSettings.COLUMN_NAME_LANGUAGE);
            String languageCode = "en";
            switch (language){
                case "English" : languageCode = "en";break;
                case "French" : languageCode = "fr";break;
                case "Portoguese" : languageCode = "pt";break;
                case "Xhosa" : languageCode = "xh";break;
                case "Zulu" : languageCode = "zu";break;
            }
            Configuration config = baseContext.getResources().getConfiguration();
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            config.locale = locale;
            baseContext.getResources().updateConfiguration(config,
                    baseContext.getResources().getDisplayMetrics());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String getCurrentLanguage(){
        StorageAccessor dataAccessor = new StorageAccessor(currentContext,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {

            }
        };
        try {
            JSONObject settings = dataAccessor.getSettings(personNumber).getJSONObject(0);
            String language = settings.getString(PhoneDatabaseContract.TableSettings.COLUMN_NAME_LANGUAGE);
            return language;
        }catch (Exception e){

        }
        return "English";
    }
    public void updateSettings(String pLanguage,int currentChecked){
        StorageAccessor dataAccessor = new StorageAccessor(currentContext,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {

            }
        };
        dataAccessor.updateSettings(pLanguage,currentChecked,personNumber);
    }
    public int getCurrentNotifications(){
        StorageAccessor dataAccessor = new StorageAccessor(currentContext,personNumber,user_token) {
            @Override
            public void getData(JSONObject data) {

            }
        };
        try {
            JSONObject settings = dataAccessor.getSettings(personNumber).getJSONObject(0);
            int notification = settings.getInt(PhoneDatabaseContract.TableSettings.COLUMN_NAME_NOTIFICATIONS);
            return notification;
        }catch (Exception e){

        }
        return 0;
    }
}
