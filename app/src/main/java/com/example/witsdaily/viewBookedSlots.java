package com.example.witsdaily;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class viewBookedSlots extends AppCompatActivity {

    String jStrin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booked_slots);
        Intent i = getIntent();
        jStrin = i.getStringExtra("session");
        try {
            JSONArray ja = new JSONArray(jStrin);
            String pNum;
            int key = Integer.parseInt( i.getStringExtra("itt"));
            System.err.println(key);
//            int loopBound = Integer.parseInt(key);
            TextView t = findViewById(R.id.tViewSlots);
            JSONObject temp;
            String s;
            if(ja.length()>0) {
                for (int l = 0; l < ja.length(); l++) {
                    temp = (JSONObject) ja.get(l);
                    if(temp.getBoolean("allocated")) {
                        pNum = temp.getString("personNumber");
                    }else{
                        pNum = "Empty";
                    }
                    s = l + ": " + pNum;
                    t.setText(s);
                }
            }else{
                for (int l = 0; l < key; l++) {
                    s = l + ": Empty";
                    t.setText(s);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
