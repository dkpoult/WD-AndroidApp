package com.example.witsdaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class editSessions extends AppCompatActivity {

    String personNumber, user_token, forumCode;
    TimePickerFragment tFrag;
    DatePickerFragment dFrag;
    DateSelector dfrag;
    ArrayList<String> buildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sessions);

        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        forumCode = getIntent().getStringExtra("forumCode");


        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")) {
                        JSONArray venues = data.getJSONArray("venues");
                        int size = venues.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject venue = venues.getJSONObject(i);
                            if (!buildings.contains(venue.getString("buildingCode"))) {
                                buildings.add(venue.getString("buildingCode"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getVenues();

        NA = new NetworkAccessor(this, personNumber, user_token) {
            @SuppressLint("SetTextI18n")
            @Override
            public void getResponse(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")) {
                        JSONArray r = data.getJSONArray("courses");
                        JSONObject sessionTemp = r.getJSONObject(0);
                        JSONArray sessions = sessionTemp.getJSONArray("sessions");
                        System.out.println(sessions.toString());
                        LinearLayout mainLayout = findViewById(R.id.LLayout);
                        View LLayout;
//                        Session[] sSession = new Session[sessions.length()];
                        for (int i = 0; i < sessions.length(); i++) {
                            LLayout = getLayoutInflater().inflate(R.layout.session, mainLayout, false);
                            JSONObject session = sessions.getJSONObject(i);
                            AppCompatAutoCompleteTextView venue = LLayout.findViewById(R.id.venue);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>
                                    (editSessions.this, android.R.layout.select_dialog_item, buildings);
                            venue.setThreshold(1); //will start working from first character
                            venue.setAdapter(adapter);
                            EditText room = LLayout.findViewById(R.id.room);
                            Spinner spinner = LLayout.findViewById(R.id.spinner);
                            Spinner sType = LLayout.findViewById(R.id.type);
                            String[] sessionTypes = new String[]{"LECTURE", "LAB", "TUTORIAL", "TEST", "OTHER"};
                            String[] items = new String[]{"DAILY", "WEEKLY", "MONTHLY", "ONCE"};
                            ArrayAdapter<String> ad = new ArrayAdapter<>(editSessions.this,
                                    android.R.layout.simple_spinner_item, items);

                            ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(editSessions.this,
                                    android.R.layout.simple_spinner_item, sessionTypes);
                            spinner.setAdapter(ad);
                            sType.setAdapter(sessAdapter);
                            EditText repeatFrequency = LLayout.findViewById(R.id.repeat);
                            TextView cencels = LLayout.findViewById(R.id.cancellations);
                            dFrag = new DatePickerFragment();
                            tFrag = new TimePickerFragment();
                            dfrag = new DateSelector();
                            TextView date = LLayout.findViewById(R.id.date);
                            date.setOnClickListener(view -> {
                                dFrag.setView(view);
                                dFrag.show(getSupportFragmentManager(), "datePicker");
                            });
                            TextView time = LLayout.findViewById(R.id.time);
                            time.setOnClickListener(view -> {
                                tFrag.setView(view);
                                tFrag.show(getSupportFragmentManager(), "timePicker");
                            });
                            EditText duration = LLayout.findViewById(R.id.duration);

                            int freq = session.getInt("repeatGap");
                            String type = session.getString("repeatType");
                            String pDate = session.getString("startDate");
                            String pType = session.getString("sessionType");
                            JSONArray cancel = session.getJSONArray("cancellations");
                            final StringBuilder[] cancells = {new StringBuilder()};
                            ArrayList<String> cans = new ArrayList<>();
                            for (int j = 0; j < cancel.length(); j++) {
                                String s = cancel.getString(j);
                                if (!cans.contains(s) && s.charAt(0) != 'c') {
                                    cans.add(s);
                                    cancells[0].append(s).append("\n");
                                }
                            }
                            cencels.setText(cancells[0].toString());
                            Button addCancel = LLayout.findViewById(R.id.addCancellation);
                            TextView cancelview = LLayout.findViewById(R.id.cancelDate);
                            cancelview.setOnClickListener(view -> {
                                dfrag.setView(view);
                                dfrag.show(getSupportFragmentManager(), "datePicker");
                            });
                            addCancel.setOnClickListener(view -> {
                                String s = cancelview.getText().toString();
                                if (!cans.contains(s) && s.charAt(0) != 'c') {
                                    cancells[0].append("\n").append(s);
                                    cencels.setText(cancells[0]);
                                    cans.add(s);
                                }
                            });

                            Button remove = LLayout.findViewById(R.id.delCancels);
                            remove.setOnClickListener(view -> {
                                        cencels.setText("");
                                        cancells[0] = new StringBuilder();
                                        cans.clear();
                                    }
                            );

                            JSONObject ven = session.getJSONObject("venue");
                            String pVenue = ven.getString("buildingCode");
                            String pRoom = ven.getString("subCode");
//                            JSONArray cancellations = session.getJSONArray("cancellations");
//                            System.out.println(cancellations);
                            int pDuration = session.getInt("duration");
//                            System.out.println(pDate);
//                            sSession[i] = new Session(pVenue + ": " + pRoom, type, freq, pDate, pType, pDuration, "pTest100");
                            String[] dateTime = pDate.split(" ");

                            venue.setText(pVenue);
                            room.setText(pRoom);
                            ArrayList<String> tempTypeArray = new ArrayList<>(Arrays.asList(sessionTypes));
                            int ind = tempTypeArray.indexOf(pType);
                            System.out.println(ind + ": " + pType + " in tempTypeArray");
                            sType.setSelection(ind);
                            ArrayList<String> tempTArray = new ArrayList<>(Arrays.asList(items));
                            spinner.setSelection(tempTArray.indexOf(type));

                            repeatFrequency.setText(Integer.toString(freq));
                            date.setText(dateTime[0]);
                            time.setText(dateTime[1].substring(0, dateTime[1].length() - 3));
                            duration.setText(Integer.toString(pDuration));
                            mainLayout.addView(LLayout);
                        }
                    } else {
                        String s = data.getString("responseCode");
                        switch (s) {
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
                }
            }
        };

        NA.getSessions(forumCode);
    }

    public void addSession(View v) {
        Intent i = new Intent(editSessions.this, addSession.class);
        i.putExtra("forumCode", forumCode);
        startActivity(i);
        finish();
    }

    public void updateSessions(View v) {
        LinearLayout LLayout = ((LinearLayout) v.getParent()).findViewById(R.id.LLayout);
        JSONArray sessions = new JSONArray();
        for (int i = 0; i < LLayout.getChildCount(); i++) {
            JSONObject jo = new JSONObject();
            View temp = LLayout.getChildAt(i);
            AppCompatAutoCompleteTextView venue = temp.findViewById(R.id.venue);
            Spinner spinner = temp.findViewById(R.id.spinner);
            Spinner sType = temp.findViewById(R.id.type);
            EditText repeatFrequency = temp.findViewById(R.id.repeat);
            TextView Cancellations = temp.findViewById(R.id.cancellations);
            String[] c = Cancellations.getText().toString().split("\n");
            JSONArray cancels = new JSONArray();
            for (String j : c) {
                if (!j.isEmpty()) {
                    cancels.put(j);
                }
            }

            TextView date = temp.findViewById(R.id.date);
            TextView time = temp.findViewById(R.id.time);
            EditText duration = temp.findViewById(R.id.duration);
            CheckBox cb = temp.findViewById(R.id.delete);
            String type = spinner.getSelectedItem().toString().toUpperCase();
            int freq = Integer.parseInt(repeatFrequency.getText().toString());
            String pDate = date.getText().toString();
            System.out.println(pDate);
            String pTime = time.getText().toString();
            String pType = sType.getSelectedItem().toString().toUpperCase();
            String pVenue = venue.getText().toString();
            EditText room = temp.findViewById(R.id.room);
            String pRoom = room.getText().toString();
            JSONObject ven = new JSONObject();
            try {
                ven.put("buildingCode", pVenue);
                ven.put("subCode", pRoom);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int pDuration = Integer.parseInt(duration.getText().toString());
            if (pTime.length() == 5) {
                pTime += ":00";
            }

            try {
                jo.put("duration", pDuration);
                jo.put("venue", ven);
//                System.out.println(ven.getString("buildingCode"));
                jo.put("repeatType", type);
                jo.put("sessionType", pType);
                jo.put("repeatGap", freq);
                jo.put("startDate", pDate + " " + pTime);
                jo.put("cancellations", cancels);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!cb.isChecked()) {
                sessions.put(jo);
            }
        }
        System.out.println(sessions.toString());

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
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

    public void showTimePicker(View v) {
        tFrag.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View v) {
        dFrag.show(getSupportFragmentManager(), "datePicker");
    }

    public void showDateSelector(View v) {
        dfrag.show(getSupportFragmentManager(), "datePicker");
    }

}