package com.example.witsdaily;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class timetable extends ToolbarActivity {

    String personNumber, user_token;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        //setupAppBar();
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final JSONArray[] ja = {new JSONArray()};
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                JSONArray sessions;
                try {
                    ArrayList<Session> seshs = new ArrayList<>();
                    JSONArray e = data.getJSONArray("courses");
                    for (int i = 0; i < e.length(); i++) {
//                        System.out.println(e);
                        String course;
                        JSONObject temp = (JSONObject) e.get(i);
                        course = temp.getString("courseCode");
                        sessions = temp.getJSONArray("sessions");
//                        System.out.println(sessions.toString());
                        JSONObject session;
                        for (int j = 0; j < sessions.length(); j++) {
                            session = (JSONObject) sessions.get(j);
//                            System.out.println(session.toString());
                            String venue, type, sType, startDate;
                            int duration, gap;
                            JSONObject ven = session.getJSONObject("venue");
                            venue = ven.getString("buildingCode") + ven.getString("subCode");
                            type = session.getString("repeatType");
                            sType = session.getString("sessionType");
                            System.out.println(sType);
                            startDate = session.getString("nextDate");
                            JSONArray cancellations;
                            if(session.has("cancellations")) {
                                ja[0] = session.getJSONArray("cancellations");
                                cancellations = session.getJSONArray("cancellations");
                            }else{
                                ja[0] = new JSONArray();
                                cancellations =new JSONArray();
                            }
                            duration = session.getInt("duration");
                            gap = session.getInt("repeatGap");
                            Session s = new Session(venue, type, gap, startDate, sType, duration, course);
                            for(int p = 0; p < cancellations.length(); p++){
                                s.cancells.add(cancellations.getString(p));
                            }
                            seshs.add(s);

                        }
                    }
                    ArrayList<Session> removable = new ArrayList<>();
                    System.out.println(seshs.size());
                    Date d;
                    Calendar c, k;
                    for(Session sesh :seshs){
                        System.out.println("test " + sesh.sessionType);
                        d = dateFormat.parse(sesh.nextDate);
                        c = Calendar.getInstance();
                        k = Calendar.getInstance();
                        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        c.setFirstDayOfWeek(Calendar.MONDAY);
                        c.add(Calendar.DAY_OF_MONTH, -7);
//                        System.out.println(c.getTime());
                        while(d.compareTo(c.getTime())<0){
                            k.setTime(d);
                            switch (sesh.repeatType) {
                                case "WEEKLY":
                                    k.add(Calendar.DAY_OF_MONTH, sesh.repeatGap * 7);
                                    break;
                                case "MONTHLY":
                                    k.add(Calendar.MONTH, sesh.repeatGap);
                                    break;
                                case "DAILY":
                                    k.add(Calendar.DAY_OF_MONTH, sesh.repeatGap);
                                    break;
                            }
                            d=k.getTime();
                        }
                        sesh.nextDate = d.toString();
                        System.out.println(d.toString() + " this is a test");
                        if(!isDateInCurrentWeek(d)){
//                            System.out.println("Removed sesh: " + sesh.venue);
                            removable.add(sesh);
                        }
                    }
                    Collections.sort(seshs);

                    TextView type, venue, startTime, endTime, dat;
                    String[] tt;
                    for(Session sesh: seshs){
//                        System.out.println(sesh.sessionType);
                        if(!removable.contains(sesh)) {
                            LinearLayout mainLayout;
                            View inflate;
                            mainLayout = findViewById(R.id.llayout);
                            inflate = getLayoutInflater().inflate(R.layout.ttsesh, mainLayout, false);
                            type = inflate.findViewById(R.id.Type);
                            String[] p = sesh.nextDate.split(" ");
                            String kpop = p[5] + "-" +getNumMonth(p[1])+ "-" + p[2];
                            if(sesh.sessionType.equals("TEST")){
                                type.setTextColor(Color.RED);
                            }
                            venue = inflate.findViewById(R.id.venue);
                            startTime = inflate.findViewById(R.id.startTime);
                            endTime = inflate.findViewById(R.id.endTime);
                            dat = inflate.findViewById(R.id.date);
                            tt = sesh.nextDate.split(" ");
                            String start = tt[3];
//                            System.out.println(sesh.nextDate);
                            String temp = start;
                            startTime.setText(temp.substring(0, temp.length() - 3));
                            temp = tt[0]+ "-" + tt[1] + "-" + tt[2];
                            dat.setText(temp);
                            if(sesh.cancells.contains(kpop)){
                                dat.setText(dat.getText().toString() + " cancelled");
                                dat.setTextColor(Color.RED);
                            }
                            long ONE_MINUTE_IN_MILLIS=60000;//millisecs
                            Calendar date = Calendar.getInstance();
                            date.setTime(format.parse(start));
                            long t= date.getTimeInMillis();
                            Date afterAddingTenMins=new Date(t + (sesh.duration * ONE_MINUTE_IN_MILLIS));
                            String tempHours =afterAddingTenMins.getHours() + "";
                            String tempMins = afterAddingTenMins.getMinutes() + "";
                            if(tempHours.length()<2){
                                tempHours += "0";
                            }
                            if(tempMins.length()<2){
                                tempMins += "0";
                            }
                            temp = tempHours + ":" + tempMins;
                            endTime.setText(temp);
                            type.setText(MessageFormat.format("{0} : {1}", sesh.sessionType, sesh.course));
                            venue.setText(sesh.venue);
                            mainLayout.addView(inflate);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        NA.getEnrolledCourses();
    }

    public static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public String getNumMonth(String mnth){
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("MMM").parse(mnth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int m = cal.get(Calendar.MONTH) + 1;
        if(m <10){
            return "0" + m;
        }else{
            return "" + m;
        }
    }

}