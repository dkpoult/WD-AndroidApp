package com.example.witsdaily;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class viewEventDetails extends AppCompatActivity {
    String userToken, personNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_venue_details);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        StorageAccessor SA = new StorageAccessor(this, personNumber, userToken) {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getData(JSONObject data) {
                System.out.println(data);
                try {
                    JSONArray events = data.getJSONArray("events");
                    if(events.length() == 0){
                        LinearLayout mainLayout = findViewById(R.id.eventList);
                        View LLayout = getLayoutInflater().inflate(R.layout.event, mainLayout, false);
                        TextView t = LLayout.findViewById(R.id.eventName);
                        String s = "EventName";
                        t.setText(s);
                        t = LLayout.findViewById(R.id.eventCode);
                        s =  "EventCode";
                        t.setText(s);
                        t.setVisibility(View.GONE);
                        t = LLayout.findViewById(R.id.startDate);
                        s = "EventStartDate";
                        t.setText(s);
                        t = LLayout.findViewById(R.id.endDate);
                        s = "EventEndDate";
                        t.setText(s);
                        t = LLayout.findViewById(R.id.eDesc33);
                        s = "this is an event description";
                        t.setText(s);


                        // add on click to view venue image when available
                        LLayout.setOnClickListener(view -> {

                        });


                        mainLayout.addView(LLayout);
                    }
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        LinearLayout mainLayout = findViewById(R.id.eventList);
                        View LLayout = getLayoutInflater().inflate(R.layout.event, mainLayout, false);
                        TextView t = LLayout.findViewById(R.id.eventName);
                        String s = event.getString("eventName");
                        t.setText(s);
                        t = LLayout.findViewById(R.id.eventCode);
                        s = event.getString("eventCode");
                        t.setText(s);
                        t.setVisibility(View.GONE);

                        t = LLayout.findViewById(R.id.eDesc33);
                        s = event.getString("eventDescription");
                        System.out.println(s);
                        t.setText(s);

                        t = LLayout.findViewById(R.id.startDate);
                        s = event.getString("startDate");
                        t.setText(s);
                        t = LLayout.findViewById(R.id.endDate);
                        s = event.getString("endDate");
                        t.setText(s);


                        // add on click to view venue image when available
                        LLayout.setOnClickListener(view -> {

                        });


                        mainLayout.addView(LLayout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SA.getEvents();
    }
}
