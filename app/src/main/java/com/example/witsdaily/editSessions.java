package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class editSessions extends AppCompatActivity {

    String personNumber, user_token, forumCode;
    final TimePickerFragment tFrag = new TimePickerFragment();
    final DatePickerFragment dFrag = new DatePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sessions);

        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        forumCode = getIntent().getStringExtra("forumCode");

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                try {
                    if(data.getString("responseCode").equals("successful")) {
                        JSONArray r = data.getJSONArray("courses");
                        JSONObject sessionTemp = r.getJSONObject(0);
                        JSONArray sessions = sessionTemp.getJSONArray("sessions");
                        System.out.println(sessions.toString());
                        LinearLayout mainLayout = findViewById(R.id.LLayout);
                        View LLayout;
                        Session[] sesh = new Session[sessions.length()];
                        for(int i = 0; i < sessions.length(); i++){
                            LLayout = getLayoutInflater().inflate(R.layout.session, mainLayout, false);
                            JSONObject session = sessions.getJSONObject(i);
                            EditText venue = LLayout.findViewById(R.id.venue);
                            Spinner spinner = LLayout.findViewById(R.id.spinner);
                            Spinner sType = LLayout.findViewById(R.id.type);
                            String[] sessionTypes = new String[]{"lecture", "lab", "tutorial", "lab", "other"};
                            String[] items = new String[]{"daily", "weekly", "monthly", "once"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(editSessions.this,
                                    android.R.layout.simple_spinner_item, items);

                            ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(editSessions.this,
                                    android.R.layout.simple_spinner_item, sessionTypes);
                            spinner.setAdapter(adapter);
                            sType.setAdapter(sessAdapter);
                            EditText repeatFrequency = LLayout.findViewById(R.id.repeat);
                            TextView date = LLayout.findViewById(R.id.date);
                            TextView time = LLayout.findViewById(R.id.time);
                            EditText duration = LLayout.findViewById(R.id.duration);

                            int freq = session.getInt("repeatGap");
                            String type = session.getString("repeatType");
                            String pDate = session.getString("nextDate");
                            String pType = session.getString("sessionType");
                            String pVenue = session.getString("venue");
                            int pDuration = session.getInt("duration");

                            sesh[i] = new Session(pVenue, type, freq, pDate, pType, pDuration);
                            String[] dateTime = pDate.split(" ");

                            venue.setText(pVenue);

                            String temp = type.toLowerCase();
                            List<String> tList = Arrays.asList(items);
                            int ind = tList.indexOf(temp);
                            spinner.setSelection(ind);

                            temp = pType.toLowerCase();
                            tList = Arrays.asList(sessionTypes);
                            ind = tList.indexOf(temp);
                            sType.setSelection(ind);

                            repeatFrequency.setText(Integer.toString(freq));
                            date.setText(dateTime[0]);
                            time.setText(dateTime[1].substring(0, dateTime[1].length()-3));
                            duration.setText(Integer.toString(pDuration));
                            mainLayout.addView(LLayout);
                        }
                    }else{
                        String s = data.getString("responseCode");
                        switch (s){
                            case "failed_unknown":
                                s = "Failed to get course: ";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        NA.getSessions(forumCode);
    }

    public void addSession(View v){
        Intent i = new Intent(editSessions.this, addSession.class);
        i.putExtra("forumCode", forumCode);
        startActivity(i);
    }

    public void updateSessions(View v) {
        LinearLayout LLayout = ((LinearLayout) v.getParent()).findViewById(R.id.LLayout);
        JSONArray sessions = new JSONArray();
        for (int i = 0; i < LLayout.getChildCount(); i++) {
            JSONObject jo = new JSONObject();
            View temp = LLayout.getChildAt(i);
            EditText venue = temp.findViewById(R.id.venue);
            Spinner spinner = temp.findViewById(R.id.spinner);
            Spinner sType = temp.findViewById(R.id.type);
            EditText repeatFrequency = temp.findViewById(R.id.repeat);
            TextView date = temp.findViewById(R.id.date);
            TextView time = temp.findViewById(R.id.time);
            EditText duration = temp.findViewById(R.id.duration);
            CheckBox cb = temp.findViewById(R.id.delete);

            String type = spinner.getSelectedItem().toString().toUpperCase();
            int freq = Integer.parseInt(repeatFrequency.getText().toString());
            String pDate = date.getText().toString();
            String pTime = time.getText().toString();
            String pType = sType.getSelectedItem().toString().toUpperCase();
            String pVenue = venue.getText().toString();
            int pDuration = Integer.parseInt(duration.getText().toString());

            if(pTime.length() == 5) {
                pTime += ":00";
            }

            try {
                jo.put("duration", pDuration);
                jo.put("venue", pVenue);
                jo.put("repeatType", type);
                jo.put("sessionType", pType);
                jo.put("repeatGap", freq);
                jo.put("nextDate", pDate + " " + pTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!cb.isChecked()) {
                sessions.put(jo);
            }
        }

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                try {
                    String s = data.getString("responseCode");
                    if (s.equals("successful")) {
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        switch (s) {
                            case "failed_unknown":
                                s = "Failed to edit sessions: ";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to edit sessions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to edit sessions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to edit sessions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.editSessions(forumCode, sessions);
    }

    public void showTimePicker(View v){
        tFrag.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View v){
        dFrag.show(getSupportFragmentManager(), "datePicker");
    }

}