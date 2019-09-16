package com.example.witsdaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class bookActivity extends AppCompatActivity {

    String user_token, personNumber, courseID, lecPNumber, jsonArray;
    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Intent i = getIntent();
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        courseID = i.getStringExtra("forumCode");
        lecPNumber = i.getStringExtra("lecPNumber");
        jsonArray = i.getStringExtra("jsonArray");
        JSONArray bookables;
        try {
            bookables = new JSONArray(jsonArray);
            System.out.println(bookables);
            loadBookables(bookables);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void loadBookables(JSONArray bookables) throws JSONException, ParseException {
        LinearLayout mainLayout = findViewById(R.id.bookLLayout);
        View LLayout;
        TextView temp;
        System.out.println(bookables.length());
        for (int i = 0; i < bookables.length(); i++) {
            JSONObject bookable = bookables.getJSONObject(i);
            LLayout = getLayoutInflater().inflate(R.layout.to_book, mainLayout, false);

            temp = LLayout.findViewById(R.id.label);
            temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            temp.setText(lecPNumber);
            temp.setTag(bookable.getInt("id"));

            int repeatGap = bookable.getInt("repeatGap");

            JSONObject building = bookable.getJSONObject("venue");
            temp = LLayout.findViewById(R.id.venue);
            temp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            String tempVenue = building.getString("buildingCode") + " " + building.getString("subCode");
            temp.setText(tempVenue);

            String repeatType = bookable.getString("repeatType");
            temp = LLayout.findViewById(R.id.repeatType);
            temp.setText(repeatType);

            String s = bookable.getString("startDate");
            temp = LLayout.findViewById(R.id.date);
            temp.setText(s);

            temp = LLayout.findViewById(R.id.eDate);
            if (bookable.has("endDate")) {
                temp.setText(bookable.getString("endDate"));
            } else {
                temp.setText("No end date selected");
            }

            temp = LLayout.findViewById(R.id.type);
            temp.setText(bookable.getString("sessionType"));

            temp = LLayout.findViewById(R.id.numSessions);
            temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            int numSlots = bookable.getInt("slotCount");
            temp.setText(Integer.toString(numSlots));

            temp = LLayout.findViewById(R.id.duration);
            temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            temp.setText(bookable.getString("duration"));

            temp = LLayout.findViewById(R.id.padding);
            temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            temp.setText(bookable.getString("slotGap"));

            JSONArray cans = bookable.getJSONArray("cancellations");
//            ArrayList<String> cancellations = new ArrayList<>();
            StringBuilder cancel = new StringBuilder();
            String tempString;
            for (int q = 0; q < cans.length(); q++) {
                tempString = cans.getString(i);
//                cancellations.add(tempString);
                cancel.append(tempString).append("\n");
            }
            temp = LLayout.findViewById(R.id.cancellations);
            temp.setText(cancel.toString());
            mainLayout.addView(LLayout);

            HashMap<String, Pair<Integer, ArrayList<Integer>>> dateMap = new HashMap<>();

            Date d = dateFormat.parse(s);
            Calendar c;
            c = Calendar.getInstance();
            int res = c.getActualMaximum(Calendar.DATE);
            c.set(Calendar.DATE, res);
            System.out.println(c.getTime());
            List<Pair<Integer, Date>> datesBetween = getDatesBetween(d, c.getTime(), repeatType, repeatGap);
            System.out.println(datesBetween.size());
            ArrayList<String> dateSelector = new ArrayList<>();
            dateSelector.add("None");
            for (Pair<Integer, Date> p : datesBetween) {
//                JSONArray
                String[] dateSplit;
                String inDate;
                if (p.second.after(Calendar.getInstance().getTime())) {
                    System.out.println(p.first + ": " + p.second.toString());
                    dateSplit = p.second.toString().split(" ");
                    inDate = dateSplit[0] + " " + dateSplit[1] + " " + dateSplit[2] + " " + p.first;
                    dateSelector.add(inDate);
                }

                boolean wasArray = false;
                JSONObject tTArray = bookable.getJSONObject("bookings");
                JSONArray tArray;
                if(tTArray.has(p.first+"")) {
                    wasArray = true;
                    tArray = tTArray.getJSONArray(p.first + "");
                }else{
                    tArray = new JSONArray();
                    for(int l = 0; l < numSlots; l++){
                        tArray.put(l);
                    }
                }
                ArrayList<Integer> tempAL = new ArrayList<>();
                for(int b = 0; b < tArray.length(); b++){
                    if(wasArray) {
                        JSONObject bookingSlot = tArray.getJSONObject(b);
                        boolean isBooked = bookingSlot.getBoolean("allocated");
                        if (!isBooked) {
                            tempAL.add(b);
                        }
                    }else{
                        tempAL.add(b);
                    }
                }
                dateSplit = p.second.toString().split(" ");
                inDate = dateSplit[0] + " " + dateSplit[1] + " " + dateSplit[2] + " " + p.first;
                dateMap.put(inDate, new Pair<>(p.first, tempAL));
            }

            Spinner spinner = LLayout.findViewById(R.id.daySelect);
            Spinner slotSelect = LLayout.findViewById(R.id.slotSelect);
            ArrayAdapter<String> sessAdapter = new ArrayAdapter<>(bookActivity.this,
                    android.R.layout.simple_spinner_item, dateSelector);
            spinner.setAdapter(sessAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!spinner.getSelectedItem().equals("None") && !dateSelector.isEmpty()) {
                        System.out.println(spinner.getSelectedItem());
                        slotSelect.setVisibility(View.VISIBLE);
                        ArrayList<Integer> newAdapter = Objects.requireNonNull(dateMap.get(spinner.getSelectedItem().toString())).second;
                        ArrayAdapter<Integer> sessAdapter = new ArrayAdapter<>(bookActivity.this,
                                android.R.layout.simple_spinner_item, newAdapter);
                        slotSelect.setAdapter(sessAdapter);
                    } else {
                        slotSelect.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    slotSelect.setVisibility(View.GONE);
                }
            });
        }
    }

    public static List<Pair<Integer, Date>> getDatesBetween(
            Date startDate, Date endDate, String type, int repeatGap) {
        List<Pair<Integer, Date>> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        int itt = 0;
        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            System.out.println(result.toString());
            datesInRange.add(new Pair<>(itt, result));
            if(!type.equals("ONCE")) {
                switch (type) {
                    case "WEEKLY":
                        calendar.add(Calendar.DATE, 7 * repeatGap);
                        break;
                    case "MONTHLY":
                        calendar.add(Calendar.MONTH, repeatGap);
                        break;
                    case "DAILY":
                        calendar.add(Calendar.DATE, repeatGap);
                        break;
                }
                itt++;
            }else
                break;
        }
        return datesInRange;
    }

    public void makeBookings(View v) {
        LinearLayout LLayout = findViewById(R.id.bookLLayout);
        NetworkAccessor NA = new NetworkAccessor(this, personNumber,user_token) {
            @Override
            void getResponse(JSONObject data) {
                try {
                    String response = data.getString("responseCode");
                    String s;
                    switch (response){
                        case "successful":
                            s = response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case "failed_slot_taken":
                            s = "Your slot was already booked by someone else";
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case "failed_unknown":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_no_user":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_invalid_token":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_invalid_params":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_params":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_perms":
                            s = "Failed to book: " + response;
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        for (int i = 0; i < LLayout.getChildCount(); i++) {
            View temp = LLayout.getChildAt(i);
            Spinner spinnerDay = temp.findViewById(R.id.daySelect);
            String dateSelected = (String)spinnerDay.getSelectedItem();
            String[] tempSA = dateSelected.split(" ");
            if (!dateSelected.equals("None")) {
                String repeatIndex = tempSA[3];
                Spinner spinnerSlot = temp.findViewById(R.id.slotSelect);
                int slot = (int)spinnerSlot.getSelectedItem();
                int id = (int)LLayout.findViewById(R.id.label).getTag();
                NA.makeBooking(id, repeatIndex, slot, courseID, lecPNumber);
                System.out.println(id + ", " + repeatIndex  + ", " + slot  + ", " + courseID  + ", " + lecPNumber);
            }
        }
    }
}
