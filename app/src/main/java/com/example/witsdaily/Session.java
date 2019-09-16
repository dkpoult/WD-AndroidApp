package com.example.witsdaily;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Session implements Comparable<Session>{
    String venue, repeatType, nextDate, sessionType, course;
    int repeatGap, duration;
    int dayVal = 0;
    Date time;
    ArrayList<String> cancells = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean isBookable;
    JSONArray slotBookings;
    int key = 0;
    int numSlots = 0;

    public Session(String venue,String repeatType,int repeatGap,String nextDate,String sessionType,int duration, String course) throws ParseException {
        this.venue = venue;
        this.course = course;
        this.repeatType = repeatType;
        this.repeatGap = repeatGap;
        this.nextDate = nextDate;
        this.sessionType = sessionType;
        this.duration = duration;
        Date date1=sdf.parse(nextDate);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // the day of the week abbreviated
        String day1 = simpleDateformat.format(date1);
        if(day1.equals("Mon")){
            dayVal = 0;
        }else if(day1.equals("Tue")){
            dayVal = 1;
        }else if(day1.equals("Wed")){
            dayVal = 2;
        }else if(day1.equals("Thu")){
            dayVal = 3;
        }else if(day1.equals("Fri")){
            dayVal = 4;
        }else if(day1.equals("Sat")){
            dayVal = 5;
        }else if(day1.equals("Sun")){
            dayVal = 6;
        }
        time = sdf.parse(nextDate.split(" ")[1]);
    }

    @Override
    public int compareTo(Session session) {
        Date d1 = new Date(this.nextDate);
        Date d2 = new Date(session.nextDate);
        Calendar c = Calendar.getInstance();
        c.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if(c.before(c2)){
            return -1;
        }else{
            return 1;
        }
    }


}
