package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

public class makeBooking extends AppCompatActivity {

    String user_token,personNumber, courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_booking);
        Intent i  = getIntent();
        courseID = i.getStringExtra("forumCode");
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        loadLecturersWithSessions();
    }

    public void loadLecturersWithSessions(){
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                try{
                    if (data.getString("responseCode").equals("successful")) {
                        JSONArray r = data.getJSONArray("courses");
                        System.out.println(r.toString());
                        JSONObject sessionTemp = r.getJSONObject(0);
                        JSONObject lecSessions = sessionTemp.getJSONObject("bookableSessions");
                        Iterator keys = lecSessions.keys();
                        LinearLayout mainLayout = findViewById(R.id.LLayout);
                        View LLayout;
                        TextView tv;
                        while(keys.hasNext()){
                            String next = (String)keys.next();
                            LLayout = getLayoutInflater().inflate(R.layout.leclayout, mainLayout, false);
                            Random random = new Random();
                            int color = Color.argb(255,random.nextInt(255),random.nextInt(255),random.nextInt(255));
                            tv = LLayout.findViewById(R.id.LecturerName);
                            tv.setTextColor(color);
                            tv.setText(next);
                            tv.setOnClickListener(view -> {
                                Intent i = new Intent(makeBooking.this, bookActivity.class);
                                i.putExtra("forumCode", courseID);
                                i.putExtra("lecPNumber", next);
                                try {
                                    i.putExtra("jsonArray", lecSessions.getJSONArray(next).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(i);
                                finish();
                            });
                            mainLayout.addView(LLayout);
                        }

                    }else{
                        System.out.println(data.getString("responseCode"));
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        };
        NA.getCourse(courseID);
    }

}
