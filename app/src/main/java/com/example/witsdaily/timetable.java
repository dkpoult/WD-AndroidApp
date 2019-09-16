package com.example.witsdaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.GregorianCalendar;
import java.util.List;

public class timetable extends AppCompatActivity {

    String personNumber, user_token;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//        final JSONArray[] ja = {new JSONArray()};
        Context context = this;
        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @SuppressLint("SetTextI18n")
            @Override
            void getResponse(JSONObject data) {
                JSONArray sessions;
                JSONObject tempBook;
                try {
                    ArrayList<Session> seshs = new ArrayList<>();
                    JSONArray e = data.getJSONArray("courses");
                    for (int i = 0; i < e.length(); i++) {
                        String course;
                        JSONObject temp = (JSONObject) e.get(i);
                        System.out.println(temp.toString());
                        course = temp.getString("courseCode");
                        sessions = temp.getJSONArray("sessions");
                        tempBook = temp.getJSONObject("bookableSessions");
                        JSONArray bookables;
                        if (tempBook.has(personNumber)) {
                            bookables = tempBook.getJSONArray(personNumber);
                        } else {
                            bookables = new JSONArray();
                        }
                        JSONObject session;
                        for (int j = 0; j < sessions.length(); j++) {
                            session = (JSONObject) sessions.get(j);
                            String venue, type, sType, startDate;
                            int duration, gap;
                            JSONObject ven = session.getJSONObject("venue");
                            venue = ven.getString("buildingCode") + ven.getString("subCode");
                            type = session.getString("repeatType");
                            sType = session.getString("sessionType");
//                            System.out.println(sType);
                            startDate = session.getString("startDate");
                            JSONArray cancellations;
                            if (session.has("cancellations")) {
//                                ja[0] = session.getJSONArray("cancellations");
                                cancellations = session.getJSONArray("cancellations");
                            } else {
//                                ja[0] = new JSONArray();
                                cancellations = new JSONArray();
                            }
                            duration = session.getInt("duration");
                            gap = session.getInt("repeatGap");
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                            c.add(Calendar.DATE, 6);
                            for (Date date : getDatesBetween(dateFormat.parse(startDate), c.getTime(), type, gap)) {
                                System.out.println("Test out: " + date.toString());
                                Session s = new Session(venue, type, gap, dateFormat.format(date), sType, duration, course);
                                for (int p = 0; p < cancellations.length(); p++) {
                                    s.cancells.add(cancellations.getString(p));
                                }
                                seshs.add(s);
                            }

                        }
                        System.out.println("There are " + bookables.length() + " items in bookables");
                        for (int k = 0; k < bookables.length(); k++) {
                            session = (JSONObject) bookables.get(k);
                            JSONObject bookSessions = session.getJSONObject("bookings");
                            String venue, type, sType, startDate;
                            int duration, gap;
                            JSONObject ven = session.getJSONObject("venue");
                            venue = ven.getString("buildingCode") + " " + ven.getString("subCode");
                            type = session.getString("repeatType");
                            sType = session.getString("sessionType");
                            System.out.println(sType);
                            startDate = session.getString("startDate");
                            JSONArray cancellations;
                            if (session.has("cancellations")) {
//                                ja[0] = session.getJSONArray("cancellations");
                                cancellations = session.getJSONArray("cancellations");
                            } else {
//                                ja[0] = new JSONArray();
                                cancellations = new JSONArray();
                            }
                            duration = session.getInt("slotCount") * (session.getInt("slotGap") + session.getInt("duration"));
                            gap = session.getInt("repeatGap");
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                            c.add(Calendar.DATE, 6);
                            int itt = 0;
                            System.out.println("This is the bookable sessions array: " + bookSessions.toString());
                            for (Date date : getDatesBetween(dateFormat.parse(startDate), c.getTime(), type, gap)) {
                                System.out.println(date);
                                Session s = new Session(venue, type, gap, dateFormat.format(date), sType, duration, course);
                                for (int p = 0; p < cancellations.length(); p++) {
                                    s.cancells.add(cancellations.getString(p));
                                }
                                System.out.println("Adding bookable: " + s.sessionType);
                                s.isBookable = true;
//                                JSONObject bookings =
                                if (bookSessions.has(Integer.toString(itt))) {
                                    s.slotBookings = bookSessions.getJSONArray(Integer.toString(itt));
                                } else {
                                    int len = session.getInt("slotCount");
                                    s.slotBookings = new JSONArray();
                                }

                                s.key = itt;
                                System.out.println("THIS IS THE CURRENT DEBUG LINE" + session.getInt("slotCount"));
                                s.numSlots = session.getInt("slotCount");
                                seshs.add(s);
                                itt++;
                            }
                        }
                    }
                    ArrayList<Session> removable = new ArrayList<>();
                    System.out.println(seshs.size());
                    Date d;
                    Calendar c, k;
                    for (Session sesh : seshs) {
                        if (sesh.isBookable) {
                            System.out.print("This sesh is bookable: ");
                            System.out.println(sesh.key + " : " + sesh.slotBookings);
                        }
                        d = dateFormat.parse(sesh.nextDate);
                        c = Calendar.getInstance();
                        k = Calendar.getInstance();
                        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        c.setFirstDayOfWeek(Calendar.MONDAY);
                        c.add(Calendar.DAY_OF_MONTH, -7);
                        System.out.println("Gap: " + sesh.repeatGap + "\n repeatType: " + sesh.repeatType);
                        System.out.println(c.getTime() + ": " + d.toString());
                        while (d.compareTo(c.getTime()) < 0) {
                            System.out.print("Changing nextDate from: " + d);
                            k.setTime(d);
                            if (!sesh.repeatType.equals("ONCE")) {
                                switch (sesh.repeatType) {
                                    case "WEEKLY":
                                        k.add(Calendar.DAY_OF_WEEK, sesh.repeatGap * 7);
                                        break;
                                    case "MONTHLY":
                                        k.add(Calendar.MONTH, sesh.repeatGap);
                                        break;
                                    case "DAILY":
                                        k.add(Calendar.DAY_OF_WEEK, sesh.repeatGap);
                                        break;
                                }
                                d = k.getTime();
                            } else
                                break;
                            System.out.println(" to: " + d);
                        }
                        sesh.nextDate = d.toString();
                        System.out.println(d.toString() + " this is a test");
                        if (!isDateInCurrentWeek(d)) {
                            System.out.println("Removed: " + sesh.sessionType + " " + sesh.dayVal + ": " + sesh.nextDate);
                            removable.add(sesh);
                        }
                    }
                    Collections.sort(seshs);

                    for (Session sesh : seshs) {
                        System.out.println("Current sesh: " + sesh.nextDate + " of type: " + sesh.sessionType);
                    }

                    TextView type, venue, startTime, endTime, dat;
                    String[] tt;
                    LinearLayout mainLayout;
                    View inflate;
                    mainLayout = findViewById(R.id.llayout);
                    for (Session sesh : seshs) {
                        System.out.println("This is a: " + sesh.sessionType);
                        if (!removable.contains(sesh)) {
                            inflate = getLayoutInflater().inflate(R.layout.ttsesh, mainLayout, false);
                            type = inflate.findViewById(R.id.Type);
                            String[] p = sesh.nextDate.split(" ");
                            String kpop = p[5] + "-" + getNumMonth(p[1]) + "-" + p[2];
                            if (sesh.sessionType.equals("TEST")) {
                                type.setTextColor(Color.RED);
                            }
                            venue = inflate.findViewById(R.id.venue);
                            startTime = inflate.findViewById(R.id.startTime);
                            endTime = inflate.findViewById(R.id.endTime);
                            dat = inflate.findViewById(R.id.date);
                            tt = sesh.nextDate.split(" ");
                            String start = tt[3];
                            System.out.println(sesh.nextDate);
                            String temp = start;
                            startTime.setText(temp.substring(0, temp.length() - 3));
                            temp = tt[0] + "-" + tt[1] + "-" + tt[2];
                            dat.setText(temp);
                            if (sesh.cancells.contains(kpop)) {
                                dat.setText(dat.getText().toString() + " cancelled");
                                dat.setTextColor(Color.RED);
                            }
                            long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
                            Calendar date = Calendar.getInstance();
                            date.setTime(format.parse(start));
                            long t = date.getTimeInMillis();
                            Date afterAddingTenMins = new Date(t + (sesh.duration * ONE_MINUTE_IN_MILLIS));
                            date.setTime(afterAddingTenMins);
                            String tempHours = date.get(Calendar.HOUR_OF_DAY) + "";
                            String tempMins = date.get(Calendar.MINUTE) + "";
                            if (tempHours.length() < 2) {
                                tempHours += "0";
                            }
                            if (tempMins.length() < 2) {
                                tempMins += "0";
                            }
                            temp = tempHours + ":" + tempMins;
                            endTime.setText(temp);
                            type.setText(MessageFormat.format("{0} : {1}", sesh.sessionType, sesh.course));
                            venue.setText(sesh.venue);
                            if (sesh.isBookable) {
                                inflate.setOnClickListener(view -> {
                                    Intent i = new Intent(timetable.this, viewBookedSlots.class);
                                    i.putExtra("session", sesh.slotBookings.toString());
                                    System.out.println("THis is a debug line " + sesh.numSlots);
                                    i.putExtra("itt", "" + sesh.numSlots);
                                    startActivity(i);
                                });
                            }
                            mainLayout.addView(inflate);
                        }
                    }

                    LinearLayout l = mainLayout;
                    l.setPadding(10, 10, 10, 10);
                    if (l.getChildCount() < 1) {
                        TextView t = new TextView(context);
                        t.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        t.setTextColor(Color.BLACK);
                        t.setText("No sessions available");
                        l.addView(t);
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

    @SuppressLint("SimpleDateFormat")
    public String getNumMonth(String mnth) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("MMM").parse(mnth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int m = cal.get(Calendar.MONTH) + 1;
        if (m < 10) {
            return "0" + m;
        } else {
            return "" + m;
        }
    }

    public static List<Date> getDatesBetween(
            Date startDate, Date endDate, String type, int repeatGap) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
//            System.out.println(result.toString());
            datesInRange.add(result);
            System.out.println(type);
            if (!type.equals("ONCE")) {
                switch (type) {
                    case "WEEKLY":
                        calendar.add(Calendar.DATE, 7 * repeatGap);
                        break;
                    case "MONTHLY":
                        calendar.add(Calendar.MONTH, repeatGap);
                        break;
                    case "DAILY":
                        System.out.println("Was daily");
                        calendar.add(Calendar.DATE, repeatGap);
                        System.out.println(calendar.getTime() + " < " + endDate + "?");
                        break;
                }
            } else
                break;
        }
        for (Date i : datesInRange) {
            System.out.println(i + " is a date in range");
        }
        return datesInRange;
    }

}