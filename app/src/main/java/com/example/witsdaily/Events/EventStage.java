package com.example.witsdaily.Events;

import android.content.Context;
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

import com.example.witsdaily.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_stage, container, false);
        params.topMargin=10;
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
    private void addVenue(JSONObject venue,LinearLayout layout){
        View newVenue = getLayoutInflater().inflate(R.layout.content_event_building,null);
        TextView tvBuildingCode = newVenue.findViewById(R.id.tvBuildingCode);
        TextView tvBuildingName =  newVenue.findViewById(R.id.tvBuildingName);
        TextView floorNum = newVenue.findViewById(R.id.tvFloorNum);
        newVenue.setTag(venue);
        // yes i know this looks awful
        try {
            tvBuildingCode.setText("Building Code: "+venue.getString("buildingCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            tvBuildingName.setText("Building Name: "+venue.getString("buildingName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            floorNum.setText("Floor Number: "+venue.getInt("floor"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //have a click event for new Venue

        layout.addView(newVenue);
    }

}
