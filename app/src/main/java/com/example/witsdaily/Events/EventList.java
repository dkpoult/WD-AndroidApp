package com.example.witsdaily.Events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.ToolbarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventList extends ToolbarActivity {
    final LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        params.topMargin = 10;
        loadEvents();
    }
    private void loadEvents(){
        StorageAccessor dataAccessor = new StorageAccessor(getApplicationContext(),personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONArray events = data.getJSONArray("events");

                        LinearLayout llEvents = findViewById(R.id.llEventView);
                        for (int i =0;i<events.length();i++){
                            View newEvent = getLayoutInflater().inflate(R.layout.content_event_list,null);
                            TextView eventName = newEvent.findViewById(R.id.tvEventName);
                            TextView eventDescription = newEvent.findViewById(R.id.tvEventDescription);
                            TextView eventStart = newEvent.findViewById(R.id.tvStartDate);
                            TextView eventEnd = newEvent.findViewById(R.id.tvEndDate);
                            JSONObject currentEvent = events.getJSONObject(i);
                            eventName.setText(currentEvent.getString("eventName"));
                            eventDescription.setText(currentEvent.getString("eventDescription"));
                            eventStart.setText("Starts: "+currentEvent.getString("startDate"));
                            String endDate;
                            try {
                                endDate = currentEvent.getString("endDate");
                            }catch (Exception e){
                                endDate = "Never";
                            }

                            eventEnd.setText("Ends: "+endDate);
                            newEvent.setTag(currentEvent.getString("eventCode"));
                            newEvent.setLayoutParams(params);
                            llEvents.addView(newEvent);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getEvents();
    }

    public void clickViewEvent(View v){
        //eventCode is v's tag
        String eventCode = String.valueOf(v.getTag());
        Intent i = new Intent(getApplicationContext(),EventViewer.class);
        i.putExtra("eventCode",eventCode);
        startActivity(i);
    }
}
