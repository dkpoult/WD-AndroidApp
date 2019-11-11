package com.example.witsdaily.Events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.witsdaily.Building;
import com.example.witsdaily.Floor;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.Venue.RoomView;
import com.example.witsdaily.floorVenue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;


public class EventStage extends Fragment {
    JSONArray steps;
    int stepsComplete = 0;
    boolean optional = false;
    final LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
    EventStage(JSONArray pSteps,boolean pOptional){
        optional = pOptional;
        steps = pSteps;
    }

    String personNumber, user_token;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_stage, container, false);
        params.topMargin=10;

        personNumber = getContext().getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getContext().getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        addSteps(v);
        return v;
    }

    private void addSteps(View parent){
        try {
            for (int i =0;i<steps.length();i++){
                View newStep = getLayoutInflater().inflate(R.layout.content_stage_step,null);
                TextView stepInfo = newStep.findViewById(R.id.tvStep);
                stepInfo.setText(steps.getJSONObject(i).getString("text"));
                LinearLayout llSteps = parent.findViewById(R.id.llSteps);
                newStep.setLayoutParams(params);
                ImageButton select = newStep.findViewById(R.id.btnSelectStep);
                RelativeLayout rlLayout = parent.findViewById(R.id.rlEventNavigation);
                Button next = rlLayout.findViewById(R.id.btnNext);
                LinearLayout llVenue = newStep.findViewById(R.id.llVenue);
                if (optional){
                    next.setText("Next(Optional)");
                    next.setEnabled(true);
                }else{

                }
                try {
                    addVenue(steps.getJSONObject(i).getJSONObject("venue"),llVenue);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!String.valueOf(view.getTag()).equals("1")){
                            view.setTag("1");
                            stepsComplete++;
                            view.setBackground(getResources().getDrawable(R.color.color_secondary));
                        }else{
                            view.setTag("0");
                            stepsComplete--;
                            view.setBackground(getResources().getDrawable(R.color.tutor));
                        }


                        if (stepsComplete>=steps.length() || optional ){

                            next.setEnabled(true);
                        }else{
                            next.setEnabled(false);
                        }
                    }
                });
                llSteps.addView(newStep);
            }

        }catch (JSONException e){

        }
    }
    private void addVenue(final JSONObject venue,LinearLayout layout){
        View newVenue = getLayoutInflater().inflate(R.layout.content_event_building,null);
        TextView tvBuildingCode = newVenue.findViewById(R.id.tvBuildingCode);
        TextView tvBuildingName =  newVenue.findViewById(R.id.tvBuildingName);
        TextView floorNum = newVenue.findViewById(R.id.tvFloorNum);
        Button venueButton = newVenue.findViewById(R.id.btnVisitVenue);

        newVenue.setTag(venue);

        boolean setValues = true;

        try {
            if (venue.has("buildingCode") && venue.getString("buildingCode").length() > 0) {
                venueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StorageAccessor SA = new StorageAccessor(getContext(), personNumber, user_token) {
                            @Override
                            public void getData(JSONObject data) {
                                System.out.println(data);
                                try {
                                    if (data.getString("responseCode").equals("successful")) {
                                        JSONArray jBuildings = data.getJSONArray("venues");
                                        int numBuildings = jBuildings.length();
                                        int numFloors, numVens;
                                        for (int i = 0; i < numBuildings; i++) {
                                            JSONObject jBuilding = jBuildings.getJSONObject(i);
                                            if(!jBuilding.getString("buildingCode").equals(venue.getString("buildingCode"))) {
                                                continue;
                                            }

                                            String coordinates = null;
                                            if (jBuilding.has("coordinates")) {
                                                JSONObject coords = jBuilding.getJSONObject("coordinates");
                                                coordinates = coords.getDouble("lat") + "," + coords.getDouble("lng");
                                            }
                                            JSONArray jFloors = jBuilding.getJSONArray("floors");

                                            JSONObject floor = jFloors.getJSONObject(venue.getInt("floor"));

                                            JSONArray venues = floor.getJSONArray("venues");
                                            numVens = venues.length();
                                            for (int k = 0; k < numVens; k++) {
                                                JSONObject jVenue = venues.getJSONObject(k);

                                                if(!jVenue.getString("venueCode").equals(venue.getString("venueCode"))) {
                                                    continue;
                                                }

                                                JSONObject subCoords;
                                                String venueCoords = null;
                                                if (jVenue.has("coordinates")) {
                                                    subCoords = jVenue.getJSONObject("coordinates");
                                                    venueCoords = subCoords.getDouble("x") + "," + subCoords.getDouble("y");
                                                }

                                                Intent intent = new Intent(getContext(), RoomView.class);
                                                intent.putExtra("coords", coordinates);
                                                intent.putExtra("venueName", jBuilding.getString("buildingCode") + " " + venue.getInt("floor") + " "
                                                        + jVenue.getString("venueCode"));
                                                intent.putExtra("floorName", floor.getString("floorName"));

                                                startActivity(intent);
                                            }
                                        }
                                    }
                                } catch (JSONException jsex) {
                                    System.out.println("Get fuckt lol");
                                    jsex.printStackTrace();
                                }
                            }
                        };

                        SA.getBuildings();
                    }
                });
            } else {
                venueButton.setEnabled(false);
                tvBuildingCode.setText("This step has no associated location.");
                tvBuildingName.setVisibility(View.INVISIBLE);
                floorNum.setVisibility(View.INVISIBLE);
                setValues = false;
            }
        } catch (JSONException jsex) {
            venueButton.setEnabled(false);
            tvBuildingCode.setText("This step has no associated location.");
            tvBuildingName.setVisibility(View.INVISIBLE);
            floorNum.setVisibility(View.INVISIBLE);
            setValues = false;
        }
        // yes i know this looks awful

        if(setValues) {
            try {
                tvBuildingCode.setText("Building Code: " + venue.getString("buildingCode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                tvBuildingName.setText("Venue Code: " + venue.getString("venueCode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                floorNum.setText("Floor Number: " + venue.getInt("floor"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        layout.addView(newVenue);
    }

}
