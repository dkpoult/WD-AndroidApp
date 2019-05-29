package com.example.witsdaily;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class addSession extends AppCompatActivity {
    String forumCode, user_token, personNumber;
    final TimePickerFragment tFrag = new TimePickerFragment();
    final DatePickerFragment dFrag = new DatePickerFragment();
    final DateSelector dfrag = new DateSelector();
    ArrayList<String> buildings = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, buildings);

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                System.out.println(data);
                try {
                    if(data.getString("responseCode").equals("successful")){
                        JSONArray venues = data.getJSONArray("venues");
                        int size = venues.length();
                        for(int i = 0; i < size; i ++){
                            JSONObject venue = venues.getJSONObject(i);
                            if(!buildings.contains(venue.getString("buildingCode"))){
                                System.out.println(venue.getString("buildingCode"));
                                buildings.add(venue.getString("buildingCode"));
                            }
                        }
                        for(String i:buildings){
                            System.out.print(i + " t ");
                        }
                        adapter = new ArrayAdapter<>
                                (addSession.this, android.R.layout.select_dialog_item, buildings);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getVenues();
        LinearLayout mainLayout;
        View inflate;
        forumCode = getIntent().getStringExtra("forumCode");
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        mainLayout = findViewById(R.id.LLayout);
        inflate = getLayoutInflater().inflate(R.layout.session, mainLayout, false);
        AppCompatAutoCompleteTextView venue = inflate.findViewById(R.id.venue);
        venue.setThreshold(1); //will start working from first character
        venue.setAdapter(adapter);
        mainLayout.setTag(forumCode);
        mainLayout.setPadding(10, 10, 10, 10);
        mainLayout.addView(inflate);
        Spinner spinner = inflate.findViewById(R.id.spinner);
        Spinner sType = inflate.findViewById(R.id.type);
        CheckBox cb = inflate.findViewById(R.id.delete);
        cb.setVisibility(View.INVISIBLE);
        ArrayList<String> cans = new ArrayList<>();
        TextView cancells = inflate.findViewById(R.id.cancellations);
        final String[] c = {""};
        Button addCancel = findViewById(R.id.addCancellation);
        addCancel.setOnClickListener(view -> {
            String s = ((TextView)findViewById(R.id.cancelDate)).getText().toString();
            if(!cans.contains(s) && s.charAt(0) != 'c') {
                c[0] += "\n" + s;
                cancells.setText(c[0]);
                cans.add(s);
            }
        });


        Button remove = findViewById(R.id.delCancels);
        remove.setOnClickListener(view ->{
                    cancells.setText("");
                    c[0] = "";
                    cans.clear();
        });
        String[] sessionTypes = new String[]{"Lecture", "Lab", "Tutorial", "Test", "Other"};
        String[] items = new String[]{"Daily", "Weekly", "Monthly", "Once"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(addSession.this,
                android.R.layout.simple_spinner_item, items);

        ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(addSession.this,
                android.R.layout.simple_spinner_item, sessionTypes);
        spinner.setAdapter(ad);
        sType.setAdapter(sessAdapter);
    }

    public void addSesh(View v) {
        LinearLayout LLayout = ((LinearLayout) v.getParent()).findViewById(R.id.LLayout);
//        TextView id = LLayout.findViewById(R.id.label);
//        Object[] builds = buildings.toArray();


        AppCompatAutoCompleteTextView venue = LLayout.findViewById(R.id.venue);

        Spinner spinner = LLayout.findViewById(R.id.spinner);
        Spinner sType = LLayout.findViewById(R.id.type);
        EditText repeatFrequency = LLayout.findViewById(R.id.repeat);
        TextView date = LLayout.findViewById(R.id.date);
        TextView time = LLayout.findViewById(R.id.time);
        EditText duration = LLayout.findViewById(R.id.duration);

        TextView cancells = LLayout.findViewById(R.id.cancellations);
        String[] cls = cancells.getText().toString().split("\n");
        JSONArray pCancel = new JSONArray();
        for(String i: cls){
            if(!i.isEmpty()) {
                pCancel.put(i);
            }
        }
        String type = spinner.getSelectedItem().toString().toUpperCase();
        int freq = Integer.parseInt(repeatFrequency.getText().toString());
        String pDate = date.getText().toString();
        String pTime = time.getText().toString();
        String pType = sType.getSelectedItem().toString().toUpperCase();
        String pVenue = venue.getText().toString();
        EditText room = LLayout.findViewById(R.id.room);
        String pRoom = room.getText().toString();
        JSONObject ven = new JSONObject();
        try {
            ven.put("buildingCode", pVenue);
            ven.put("subCode", pRoom);
            ven.put("coordinated", "0.0000,0.0000");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int pDuration = Integer.parseInt(duration.getText().toString());

        TextView t;

        if(type.isEmpty()){
            t = findViewById(R.id.textView2);
            t.setTextColor(0xff0000);
            return;
        }
        if(Integer.toString(freq).isEmpty()){
            t = findViewById(R.id.textView3);
            t.setTextColor(0xff0000);
            return;
        }
        if(pDate.isEmpty() || pDate.charAt(0) == 'd'){
            t = findViewById(R.id.date);
            t.setTextColor(0xff0000);
            t.setText("date");
            return;
        }
        if(pType.isEmpty()){
            t = findViewById(R.id.textView5);
            t.setTextColor(0xff0000);
            return;
        }
        if(pVenue.isEmpty()){
            t = findViewById(R.id.textView);
            t.setTextColor(0xff0000);
            return;
        }
        if(pTime.isEmpty()){
            t = findViewById(R.id.time);
            t.setTextColor(0xff0000);
            return;
        }
        if(Integer.toString(pDuration).isEmpty()){
            t = findViewById(R.id.textView6);
            t.setTextColor(0xff0000);
            return;
        }

        if(pTime.length() == 5) {
            pTime += ":00";
        }


        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                try {
                    String s;
                    if(data.getString("responseCode").equals("successful")){
                        s = "successfull";
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        s = data.getString("responseCode");
                        switch (s){
                            case "failed_unknown":
                                s = "Failed to add session: ";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to add session: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to add session: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to add session: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.addSession(forumCode, ven, type, freq, pDate + " " + pTime, pType, pDuration, pCancel);
    }

    public void showTimePicker(View v){
        tFrag.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View v){
        dFrag.show(getSupportFragmentManager(), "datePicker");
    }
    public void showDateSelector(View v){
        dfrag.show(getSupportFragmentManager(), "datePicker");
    }
}