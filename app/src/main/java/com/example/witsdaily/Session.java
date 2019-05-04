package com.example.witsdaily;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Session implements Comparable<Session>{
    String venue, repeatType, nextDate, sessionType;
    int repeatGap, duration;
    int dayVal = 0;
    Date time;

    public Session(String venue,String repeatType,int repeatGap,String nextDate,String sessionType,int duration) throws ParseException {
        this.venue = venue;
        this.repeatType = repeatType;
        this.repeatGap = repeatGap;
        this.nextDate = nextDate;
        this.sessionType = sessionType;
        this.duration = duration;
        Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nextDate);
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
        if(dayVal == session.dayVal){
            return (int)(this.time.getTime() - session.time.getTime());
        }else {
            return this.dayVal - session.dayVal;
        }
    }


}
