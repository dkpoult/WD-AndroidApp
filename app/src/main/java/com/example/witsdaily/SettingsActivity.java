package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
String currentLanguage,userToken,personNumber;
int currentNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);

        getCurrentLanguage();
        getCurrentNotificationSettings();
        Spinner spinner = (Spinner) findViewById(R.id.spnrLanguages);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Xhosa");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentLanguage = String.valueOf(adapterView.getSelectedItem());
                System.out.println(currentLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Switch notificationSwitch = (Switch)findViewById(R.id.switchNotifications);
        if (currentNotification==1){
            notificationSwitch.setChecked(true);
        }
    }

    public void clickApplyChanges(View v){
        WitsDailySettings settings = new WitsDailySettings(personNumber,userToken,getBaseContext(),this);

        Switch notificationSwitch = (Switch)findViewById(R.id.switchNotifications);

        int currentChecked = 0;
        if  (notificationSwitch.isChecked()){
            currentChecked = 1;
        }
        settings.updateSettings(currentLanguage,currentChecked);
        settings.loadLanguage(personNumber);

        Toast.makeText(SettingsActivity.this, "Application restart required",
                Toast.LENGTH_SHORT).show();
    }

    private void getCurrentLanguage(){
        WitsDailySettings settings = new WitsDailySettings(personNumber,userToken,getBaseContext(),this);
        currentLanguage = settings.getCurrentLanguage();
    }

    private void getCurrentNotificationSettings(){
        WitsDailySettings settings = new WitsDailySettings(personNumber,userToken,getBaseContext(),this);
        currentNotification = settings.getCurrentNotifications();
    }


}
