package com.example.witsdaily.Events;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.Course.EnrolledCourses;
import com.example.witsdaily.Course.UnenrolledCourses;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.TabAdapter;
import com.example.witsdaily.ToolbarActivity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventViewer extends ToolbarActivity {
String eventCode;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAdapter adapter;
    JSONArray stages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);
        Intent i = getIntent();
        eventCode = i.getStringExtra("eventCode");
        viewPager = findViewById(R.id.courseViewPager);
        viewPager.setOffscreenPageLimit(10);
        tabLayout = findViewById(R.id.courseTabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventHome(),"Home");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        loadEventInfo();
    }

    private void loadEventInfo(){
        StorageAccessor storageAccessor = new StorageAccessor(getApplicationContext(),personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if(data.getString("responseCode").equals("successful")) {
                        stages = data.getJSONObject("event").getJSONArray("stages");
                        loadHome(data.getJSONObject("event"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        storageAccessor.getEvent(eventCode);
    }

    private void loadHome(JSONObject currentEvent){

        LinearLayout llEvents = findViewById(R.id.eventDescription);

            TextView eventName = llEvents.findViewById(R.id.tvEventName);
            TextView eventDescription = llEvents.findViewById(R.id.tvEventDescription);
            TextView eventStart = llEvents.findViewById(R.id.tvStartDate);
            TextView eventEnd = llEvents.findViewById(R.id.tvEndDate);
            Button btnContinue = llEvents.findViewById(R.id.btnContinue);

        try {
            eventName.setText(currentEvent.getString("eventName"));
            eventDescription.setText(currentEvent.getString("eventDescription"));
            eventStart.setText("Starts: "+currentEvent.getString("startDate"));
            String endDate;
            try {
                endDate = currentEvent.getString("endDate");
            }catch (Exception e){
                endDate = "Never";
            }
            try {
                JSONArray temporaryVenues = currentEvent.getJSONArray("temporaryVenues");
                addVenues(temporaryVenues);

            }catch (JSONException e){

            }

            eventEnd.setText("Ends: "+endDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventContinueHome(view);
            }
        });

    }
    private void addVenues(JSONArray tempVenues){
        System.out.println("no temp venues to test");
    }

    public void eventContinueHome(View v){
            if (adapter.getCount()>1){
                viewPager.setCurrentItem(1,true);
            }else{
                addEvent(0);  // adds stage 0
                viewPager.setCurrentItem(1,true);

            }


    }
    private void addEvent(int stageIndex){
        String title = "Stage "+stageIndex;

        try {
            JSONObject currentStage = stages.getJSONObject(stageIndex);
            title = currentStage.getString("title");
            boolean optional = currentStage.getBoolean("optional");
            Fragment newScreen = new EventStage(currentStage.getJSONArray("steps"),optional);
            adapter.addFragment(newScreen,title);

        } catch (JSONException e) {
            // its probably because it is complete
            Toast.makeText(this, "Event Completed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }
    public void clickEventPrevious(View v){

        viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
    }

    public void clickEventNext(View v){

        if (adapter.getCount()-1<= viewPager.getCurrentItem())
            addEvent(viewPager.getCurrentItem());  //  stage 0 is home
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
    }


}
