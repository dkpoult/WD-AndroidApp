package com.example.witsdaily;

import android.content.Context;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class timetable extends AppCompatActivity {

    String personNumber, user_token;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        String[][] lables = new String[8][16];
        String[] days = {"", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        int[] periods = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        String[] timeSlots = {"", "08:00-\n08:45", "08:45-\n09:00", "09:00-\n09:45", "09:45-\n10:15", "10:15-\n11:00", "11:00-\n11:15", "11:15-\n12:00", "12:00-\n12:30", "12:30-\n13:15", "13:15-\n14:15", "14:15-\n15:00", "15:00-\n15:15", "15:15-\n16:00", " 16:00-\n16:15", "16:15-\n17:00"};

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date[] times = new Date[15];
        final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");


        for (int j = 0; j < 16; j++) {
            if (j > 0) {
                try {
                    times[j - 1] = sdfTime.parse(timeSlots[j].split("-")[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(times[j - 1].getHours() + ":" + times[j - 1].getMinutes());
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                if (i == 0 && j == 0) {
                    lables[i][j] = days[i] + "         ";
                } else if (i == 0) {
                    lables[i][j] = timeSlots[j];
                } else if (j == 0) {
                    lables[i][j] = days[i];
                } else {
                    lables[i][j] = days[i] + " " + Integer.toString(periods[j])
                            + "\n" + timeSlots[j];
                }
            }
        }

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                JSONArray sessions;
                try {
                    ArrayList<Session> seshs = new ArrayList<>();
                    JSONArray e = data.getJSONArray("courses");
                    for (int i = 0; i < e.length(); i++) {
                        JSONObject temp = (JSONObject) e.get(i);
                        sessions = temp.getJSONArray("sessions");
                        System.out.println(sessions.toString());
                        JSONObject session;
                        for (int j = 0; j < sessions.length(); j++) {
                            session = (JSONObject) sessions.get(j);
                            System.out.println(session.toString());
                            String venue, type, sType, startDate;
                            int duration, gap;
                            venue = session.getString("venue");
                            type = session.getString("repeatType");
                            sType = session.getString("sessionType");
                            System.out.println(sType);
                            startDate = session.getString("nextDate");
                            duration = session.getInt("duration");
                            gap = session.getInt("repeatGap");
                            seshs.add(new Session(venue, type, gap, startDate, sType, duration));

                        }
                    }

                    for(Session sesh: seshs){
                        System.out.println("test " + sesh.nextDate);
                    }
                    ArrayList<Session> removable = new ArrayList<>();
                    for(Session sesh :seshs){
                        Date d = dateFormat.parse(sesh.nextDate);
                        Calendar c = Calendar.getInstance();
                        Calendar k = Calendar.getInstance();
                        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        c.setFirstDayOfWeek(Calendar.MONDAY);
                        c.add(Calendar.DAY_OF_MONTH, -7);
                        System.out.println(c.getTime());
                        while(d.compareTo(c.getTime())<0){
                            k.setTime(d);
                            if(sesh.repeatType.equals("WEEKLY")) {
                                k.add(Calendar.DAY_OF_MONTH, sesh.repeatGap * 7);
                            }else if(sesh.repeatType.equals("MONTHLY")){
                                k.add(Calendar.MONTH, sesh.repeatGap);
                            }else if(sesh.repeatType.equals("DAILY")){
                                k.add(Calendar.DAY_OF_MONTH, sesh.repeatGap);
                            }
                            d=k.getTime();
                        }
                        sesh.nextDate = d.toString();
                        System.out.println(d.toString() + " this is a test");
                        if(!isDateInCurrentWeek(d)){
                            System.out.println("Removed sesh: " + sesh.venue);
                            removable.add(sesh);
                        }
                    }
                    Collections.sort(seshs);
                    for(Session sesh: seshs){
                        System.out.println(sesh.sessionType);
                        if(!removable.contains(sesh)) {
                            LinearLayout mainLayout;
                            View inflate;
                            mainLayout = findViewById(R.id.llayout);
                            inflate = getLayoutInflater().inflate(R.layout.ttsesh, mainLayout, false);
                            TextView type = inflate.findViewById(R.id.Type);
                            TextView venue = inflate.findViewById(R.id.venue);
                            TextView startTime = inflate.findViewById(R.id.startTime);
                            TextView endTime = inflate.findViewById(R.id.endTime);
                            TextView dat = inflate.findViewById(R.id.date);
                            String[] tt = sesh.nextDate.split(" ");
                            String start = tt[3];
                            System.out.println(sesh.nextDate);
                            String temp = start;
                            startTime.setText(temp.substring(0, temp.length() - 3));
                            temp = tt[0]+ "-" + tt[1] + "-" + tt[2];
                            dat.setText(temp);
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
                            type.setText(sesh.sessionType);
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


//        HorizontalScrollView HSV = findViewById(R.id.HSV);
//        ScrollView VSV = findViewById(R.id.VSC);
//        VSV.setPadding(10,10,10,10);
//        HSV.setPadding(10,10,10,10);
//        TableLayout t = findViewById(R.id.timeTable);
//        for(int i = 0; i < 8; i++){
//            TableRow tempRow = new TableRow(this);
//            for(int j = 0; j < 16; j++){
//                TableRow tempCol = new TableRow(this);
//                if(i == 0 || j == 0){
//                    tempCol.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
//                }
//                TextView tmp = new TextView(this);
//                if(i == 0 || j == 0){
//                    tmp.setTextColor(this.getResources().getColor(R.color.colorWhite));
//                    tmp.setText(lables[i][j]);
//                }else{
//
//                }
//                tmp.setHeight(400);
//                if(j%2 != 0) {
//                    tmp.setWidth(200);
//                }else{
//                    tmp.setWidth(100);
//                }
//                tmp.setBackground(getDrawable(R.drawable.border_black));
//                tmp.setTag(lables[i][j]);
//                tmp.setPadding(5,5,5,5);
//                tmp.setGravity(Gravity.CENTER);
//                tmp.setBackground(getDrawable(R.drawable.border_black));
//                tempCol.setTag(lables[i][j]);
//                tempCol.addView(tmp);
//                tempRow.addView(tempCol);
//            }
//            t.addView(tempRow);
//        }
//
//    }
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

}
