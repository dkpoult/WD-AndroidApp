package com.example.witsdaily;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class timetable extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        String [][] lables = new String[8][16];

        String[] days = {"", "Mo","Tu","We","Th","Fr","Sa","Su"};
        int[] periods = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        String[] timeSlots = {"", "08:00-\n08:45", "08:45-\n09:00","09:00-\n09:45", "09:45-\n10:15", "10:15-\n11:00", "11:00-\n11:15", "11:15-\n12:00", "12:00-\n12:30", "12:30-\n13:15", "13:15-\n14:15", "14:15-\n15:00", "15:00-\n15:15" , "15:15-\n16:00", " 16:00-\n16:15", "16:15-\n17:00"};

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 16; j++){
                if(i == 0 && j == 0){
                    lables[i][j] = days[i] + "         ";
                }
                else if(i == 0){
                    lables[i][j] = timeSlots[j];
                }else if(j == 0){
                    lables[i][j] = days[i];
                }else{
                    lables[i][j] = days[i] + " " + Integer.toString(periods[j])
                    + "\n" + timeSlots[j];
                }
            }
        }


        HorizontalScrollView HSV = findViewById(R.id.HSV);
        ScrollView VSV = findViewById(R.id.VSC);
        VSV.setPadding(10,10,10,10);
        HSV.setPadding(10,10,10,10);
        TableLayout t = findViewById(R.id.timeTable);
        for(int i = 0; i < 8; i++){
            TableRow tempRow = new TableRow(this);
            for(int j = 0; j < 16; j++){
                TableRow tempCol = new TableRow(this);
                if(i == 0 || j == 0){
                    tempCol.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                }
                TextView tmp = new TextView(this);
                if(i == 0 || j == 0){
                    tmp.setTextColor(this.getResources().getColor(R.color.colorWhite));
                }
                tmp.setHeight(400);
                tmp.setWidth(200);
                tmp.setBackground(getDrawable(R.drawable.border_black));
                tmp.setText(lables[i][j]);
                tmp.setPadding(5,5,5,5);
                tmp.setGravity(Gravity.CENTER);
                tmp.setBackground(getDrawable(R.drawable.border_black));
                tempCol.setTag(lables[i][j]);
                tempCol.addView(tmp);
                tempRow.addView(tempCol);
            }
            t.addView(tempRow);
        }

    }

}
