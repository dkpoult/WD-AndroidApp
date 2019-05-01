package com.example.witsdaily;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class addSession extends AppCompatActivity {
    String forumCode, user_token, personNumber;
    final TimePickerFragment tFrag = new TimePickerFragment();
    final DatePickerFragment dFrag = new DatePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        LinearLayout mainLayout;
        View inflate;
        forumCode = getIntent().getStringExtra("forumCode");
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        mainLayout = findViewById(R.id.LLayout);
        inflate = getLayoutInflater().inflate(R.layout.session, mainLayout, false);
        mainLayout.setTag(forumCode);
        mainLayout.setPadding(10, 10, 10, 10);
        mainLayout.addView(inflate);
        TextView id = inflate.findViewById(R.id.label);
        EditText venue = inflate.findViewById(R.id.venue);
        Spinner spinner = inflate.findViewById(R.id.spinner);
        Spinner sType = inflate.findViewById(R.id.type);
        CheckBox cb = inflate.findViewById(R.id.delete);
        cb.setVisibility(View.INVISIBLE);
        String[] sessionTypes = new String[]{"Lecture", "Lab", "Tutorial", "Lab", "Other"};
        String[] items = new String[]{"Daily", "Weekly", "Monthly", "Once"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(addSession.this,
                android.R.layout.simple_spinner_item, items);

        ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(addSession.this,
                android.R.layout.simple_spinner_item, sessionTypes);
        spinner.setAdapter(adapter);
        sType.setAdapter(sessAdapter);
        EditText repeatFrequency = inflate.findViewById(R.id.repeat);
        TextView date = inflate.findViewById(R.id.date);
        TextView time = inflate.findViewById(R.id.time);
    }


    public void addSesh(View v) {
        LinearLayout LLayout = ((LinearLayout) v.getParent()).findViewById(R.id.LLayout);
//        TextView id = LLayout.findViewById(R.id.label);
        EditText venue = LLayout.findViewById(R.id.venue);
        Spinner spinner = LLayout.findViewById(R.id.spinner);
        Spinner sType = LLayout.findViewById(R.id.type);
        EditText repeatFrequency = LLayout.findViewById(R.id.repeat);
        TextView date = LLayout.findViewById(R.id.date);
        TextView time = LLayout.findViewById(R.id.time);
        EditText duration = LLayout.findViewById(R.id.duration);

        String type = spinner.getSelectedItem().toString().toUpperCase();
        int freq = Integer.parseInt(repeatFrequency.getText().toString());
        String pDate = date.getText().toString();
        String pTime = time.getText().toString();
        String pType = sType.getSelectedItem().toString().toUpperCase();
        String pVenue = venue.getText().toString();
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
        if(pDate.isEmpty()){
            t = findViewById(R.id.date);
            t.setTextColor(0xff0000);
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
        NA.addSession(forumCode, pVenue, type, freq, pDate + " " + pTime, pType, pDuration);
    }

    public void showTimePicker(View v){
        tFrag.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View v){
        dFrag.show(getSupportFragmentManager(), "datePicker");
    }
}
