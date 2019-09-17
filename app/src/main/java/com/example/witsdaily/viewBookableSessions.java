package com.example.witsdaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class viewBookableSessions extends AppCompatActivity {

    String personNumber, user_token, forumCode;
    TimePickerFragment tFrag;
    DatePickerFragment dFrag;
    DateSelector dfrag;
    DateChooser dFfrag;
    ArrayList<String> buildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookable_sessions);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        forumCode = getIntent().getStringExtra("forumCode");
        System.out.println(personNumber);
        System.out.println(user_token);
        System.out.println(forumCode);
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
                System.out.println(data);
                try {
                    if (data.getString("responseCode").equals("successful")) {
                        JSONArray r = data.getJSONArray("courses");
                        System.out.println(r.toString());
                        JSONObject sessionTemp = r.getJSONObject(0);
                        JSONObject lecSessions = sessionTemp.getJSONObject("bookableSessions");
                        JSONArray sessions = lecSessions.getJSONArray(personNumber);
                        System.out.println("TESTING SESSIONS: " + sessions.toString());

                        LinearLayout mainLayout = findViewById(R.id.bookableLayout);
                        View LLayout;
                        for (int i = 0; i < sessions.length(); i++) {
                            LLayout = getLayoutInflater().inflate(R.layout.booking, mainLayout, false);
                            JSONObject session = sessions.getJSONObject(i);
                            System.out.println(session);

                            AutoCompleteTextView venue = LLayout.findViewById(R.id.venue);
                            EditText room = LLayout.findViewById(R.id.room);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>
                                    (viewBookableSessions.this, android.R.layout.select_dialog_item, buildings);
                            venue.setThreshold(1); //will start working from first character
                            venue.setAdapter(adapter);

                            Spinner spinner = LLayout.findViewById(R.id.spinner);
                            Spinner sType = LLayout.findViewById(R.id.type);
                            String[] sessionTypes = new String[]{"MEETING", "CONSULTATION"};
                            String[] items = new String[]{"DAILY", "WEEKLY", "MONTHLY", "ONCE"};
                            ArrayAdapter<String> ad = new ArrayAdapter<>(viewBookableSessions.this,
                                    android.R.layout.simple_spinner_item, items);

                            ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(viewBookableSessions.this,
                                    android.R.layout.simple_spinner_item, sessionTypes);
                            spinner.setAdapter(ad);
                            sType.setAdapter(sessAdapter);
                            EditText repeatFrequency = LLayout.findViewById(R.id.repeat);
                            TextView cencels = LLayout.findViewById(R.id.cancellations);
                            TextView date = LLayout.findViewById(R.id.date);
                            dFrag = new DatePickerFragment();
                            date.setOnClickListener(view -> {
                                dFrag.setView(view);
                                dFrag.show(getSupportFragmentManager(), "datePicker");
                            });
                            TextView time = LLayout.findViewById(R.id.time);
                            tFrag = new TimePickerFragment();
                            time.setOnClickListener(view -> {
                                tFrag.setView(view);
                                tFrag.show(getSupportFragmentManager(), "timePicker");
                            });
                            TextView eDate = LLayout.findViewById(R.id.eDate);
                            dFfrag = new DateChooser();
                            eDate.setOnClickListener(view -> {
                                dFfrag.setView(view);
                                dFfrag.show(getSupportFragmentManager(), "datePicker");
                            });
                            EditText duration = LLayout.findViewById(R.id.duration);
                            EditText slotCount = LLayout.findViewById(R.id.numSessions);
                            EditText slotGap = LLayout.findViewById(R.id.padding);

                            int freq = session.getInt("repeatGap");
                            String type = session.getString("repeatType");
                            String pType = session.getString("sessionType");
                            ArrayList<String> tempTypeArray = new ArrayList<>(Arrays.asList(sessionTypes));
                            int ind = tempTypeArray.indexOf(pType);
                            System.out.println(ind + ": " + pType + " in tempTypeArray");
                            sType.setSelection(ind);
                            ArrayList<String> tempTArray = new ArrayList<>(Arrays.asList(items));
                            spinner.setSelection(tempTArray.indexOf(type));
                            String pDate = session.getString("startDate");
                            JSONArray cancel = session.getJSONArray("cancellations");
                            JSONObject ven = session.getJSONObject("venue");
                            String pVenue = ven.getString("buildingCode");
                            String pRoom = ven.getString("subCode");
                            String endDate;
                            if (session.has("endDate")) {
                                endDate = session.getString("endDate");
                            } else {
                                endDate = "Not set";
                            }
                            int pDuration = session.getInt("duration");
                            int pSlotCount = session.getInt("slotCount");
                            int pSlotGap = session.getInt("slotGap");


                            TextView cancelDate = LLayout.findViewById(R.id.cancelDate);
                            dfrag = new DateSelector();
                            cancelDate.setOnClickListener(view -> {
                                dfrag.setView(view);
                                dfrag.show(getSupportFragmentManager(), "datePicker");
                            });
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
                            addCancel.setOnClickListener(view -> {
                                String s = ((TextView) findViewById(R.id.cancelDate)).getText().toString();
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


                            String[] dateTime = pDate.split(" ");
                            String[] eDateTime = endDate.split(" ");

                            venue.setText(pVenue);
                            room.setText(pRoom);
                            System.out.println(freq);
                            repeatFrequency.setText(Integer.toString(freq));

                            date.setText(dateTime[0]);
                            eDate.setText(eDateTime[0]);

                            time.setText(dateTime[1].substring(0, dateTime[1].length() - 3));

                            duration.setText(Integer.toString(pDuration));
                            slotCount.setText(Integer.toString(pSlotCount));
                            slotGap.setText(Integer.toString(pSlotGap));

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
                                break;
                            case "failed_invalid_token":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_no_user":
                                s = "Failed to get course: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getCourse(forumCode);
    }

    public void updateBookables(View v) {
        LinearLayout LLayout = ((LinearLayout) v.getParent()).findViewById(R.id.bookableLayout);
        JSONArray sessions = new JSONArray();
        for (int i = 0; i < LLayout.getChildCount(); i++) {
            JSONObject jo = new JSONObject();
            View temp = LLayout.getChildAt(i);
            AutoCompleteTextView venue = temp.findViewById(R.id.venue);
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


            EditText slotGap = temp.findViewById(R.id.padding);
            EditText slotCount = temp.findViewById(R.id.numSessions);
            TextView eDate = LLayout.findViewById(R.id.eDate);


            CheckBox cb = temp.findViewById(R.id.delete);
            String type = spinner.getSelectedItem().toString().toUpperCase();
            TextView t;
            boolean issue = false;
            if (repeatFrequency.getText().toString().isEmpty()) {
                t = temp.findViewById(R.id.textView3);
                t.setTextColor(Color.RED);
                issue = true;
            }
            if (slotGap.getText().toString().isEmpty()) {
                t = temp.findViewById(R.id.textView13);
                t.setTextColor(Color.RED);
                issue = true;

            }
            if (slotCount.getText().toString().isEmpty()) {
                t = temp.findViewById(R.id.textView11);
                t.setTextColor(Color.RED);
                issue = true;

            }
            String endDate;
            boolean isEnd = true;
            if (eDate.getText().toString().equals("Date:") || eDate.getText().toString().equals("Not")) {
                endDate = eDate.getText().toString();
                isEnd = false;

            } else {
                endDate = eDate.getText().toString();

            }
            if (date.getText().toString().equals("Date:")) {
                t = temp.findViewById(R.id.textView4);
                t.setTextColor(Color.RED);
                issue = true;
            }
            if (duration.getText().toString().isEmpty()) {
                t = temp.findViewById(R.id.textView6);
                t.setTextColor(Color.RED);
                issue = true;
            }
            if (time.getText().toString().equals("Time:")) {
                t = temp.findViewById(R.id.textView4);
                t.setTextColor(Color.RED);
                issue = true;

            }
            if (venue.getText().toString().isEmpty()) {
                t = temp.findViewById(R.id.textView);
                t.setTextColor(Color.RED);
                issue = true;

            }
            if (issue) {
                return;
            }
            int freq = Integer.parseInt(repeatFrequency.getText().toString());
            int pSlotGap = Integer.parseInt(slotGap.getText().toString());
            int pSlotCount = Integer.parseInt(slotCount.getText().toString());
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
                jo.put("slotGap", pSlotGap);
                jo.put("slotCount", pSlotCount);
                jo.put("venue", ven);
//                System.out.println(ven.getString("buildingCode"));
                jo.put("repeatType", type);
                jo.put("sessionType", pType);
                jo.put("repeatGap", freq);
                jo.put("startDate", pDate + " " + pTime);
                if (isEnd) {
                    jo.put("endDate", endDate);
                }
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
        System.out.println(personNumber);
        System.out.println(user_token);
        System.out.println(forumCode);
        System.out.println(sessions.toString());
        NA.editBookables(forumCode, sessions);
    }

    public void addBookableSession(View v) {
        LinearLayout mainLayout = findViewById(R.id.bookableLayout);
        View LLayout = getLayoutInflater().inflate(R.layout.booking, mainLayout, false);
        AutoCompleteTextView venue = LLayout.findViewById(R.id.venue);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (viewBookableSessions.this, android.R.layout.select_dialog_item, buildings);
        venue.setThreshold(1); //will start working from first character
        venue.setAdapter(adapter);


        TextView cencels = LLayout.findViewById(R.id.cancellations);
        Spinner spinner = LLayout.findViewById(R.id.spinner);
        Spinner sType = LLayout.findViewById(R.id.type);
        String[] sessionTypes = new String[]{"MEETING", "CONSULTATION"};
        String[] items = new String[]{"DAILY", "WEEKLY", "MONTHLY", "ONCE"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(viewBookableSessions.this,
                android.R.layout.simple_spinner_item, items);

        ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(viewBookableSessions.this,
                android.R.layout.simple_spinner_item, sessionTypes);
        spinner.setAdapter(ad);
        sType.setAdapter(sessAdapter);

        TextView date = LLayout.findViewById(R.id.date);
        dFrag = new DatePickerFragment();
        date.setOnClickListener(view -> {
            dFrag.setView(view);
            dFrag.show(getSupportFragmentManager(), "datePicker");
        });
        TextView time = LLayout.findViewById(R.id.time);
        tFrag = new TimePickerFragment();
        time.setOnClickListener(view -> {
            tFrag.setView(view);
            tFrag.show(getSupportFragmentManager(), "timePicker");
        });
        TextView eDate = LLayout.findViewById(R.id.eDate);
        dFfrag = new DateChooser();
        eDate.setOnClickListener(view -> {
            dFfrag.show(getSupportFragmentManager(), "datePicker");
            dFfrag.setView(LLayout);
        });
        TextView cancelDate = LLayout.findViewById(R.id.cancelDate);
        cancelDate.setOnClickListener(view -> {
            dfrag.setView(view);
            dfrag.show(getSupportFragmentManager(), "datePicker");
        });

        final StringBuilder[] cancells = {new StringBuilder()};
        ArrayList<String> cans = new ArrayList<>();
        Button addCancel = LLayout.findViewById(R.id.addCancellation);
        addCancel.setOnClickListener(view -> {
            String s = ((TextView) findViewById(R.id.cancelDate)).getText().toString();
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

        mainLayout.addView(LLayout);
    }

    public void showTimePicker(View v) {
        tFrag.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View v) {
        dFrag.show(getSupportFragmentManager(), "datePicker");
    }

    public void viewDatePicker(View v) {
        dFfrag.show(getSupportFragmentManager(), "datePicker");
    }

    public void showDateSelector(View v) {
        dfrag.show(getSupportFragmentManager(), "datePicker");
    }

}
