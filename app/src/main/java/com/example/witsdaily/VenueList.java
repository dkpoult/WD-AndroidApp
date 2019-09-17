package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VenueList extends ToolbarActivity {

    ArrayList<String> buildings = new ArrayList<>();
    ArrayAdapter<String> adapter;
    JSONArray venues;


    String personNumber, user_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);
        setupAppBar();
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);



        adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, buildings);

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                try {
                    if(data.getString("responseCode").equals("successful")){
                        venues = data.getJSONArray("venues");
                        int size = venues.length();
                        for(int i = 0; i < size; i ++){
                            JSONObject venue = venues.getJSONObject(i);
                            if(!buildings.contains(venue.getString("buildingCode"))){
                                buildings.add(venue.getString("buildingCode").toUpperCase());
                            }
                        }
                        adapter = new ArrayAdapter<>
                                (VenueList.this, android.R.layout.select_dialog_item, buildings);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getVenues();
        AutoCompleteTextView venue = findViewById(R.id.eText);
        venue.setThreshold(1); //will start working from first character
        venue.setAdapter(adapter);
    }


    public void viewOnMap(View v){
        // Create a Uri from an intent string. Use the result to create an Intent.

        AutoCompleteTextView venue = findViewById(R.id.eText);
        String ven = venue.getText().toString();
        if(!buildings.contains(ven.toUpperCase())){
            String s = "No venue found, no building with that name was found";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder coordsBuilder = new StringBuilder();
        for(int i = 0; i < venues.length(); i++){
            try {
                JSONObject currVen = venues.getJSONObject(i);
                if(ven.equals(currVen.getString("buildingCode"))){
                    coordsBuilder.append(currVen.getString("coordinates"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String coords = coordsBuilder.toString();
        if(coords.isEmpty()){
            String s = "No venue found, no coordinates available";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + coords + "(" + ven + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }

}
