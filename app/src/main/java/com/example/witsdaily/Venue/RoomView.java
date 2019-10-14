package com.example.witsdaily.Venue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.witsdaily.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomView extends AppCompatActivity {
    String coords,venueName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);
        Intent i = getIntent();
        coords = i.getStringExtra("coords");
        venueName = i.getStringExtra("venueName");
    }
    public void viewOnMap(View v){

        if(coords.isEmpty()){
            String s = "No venue found, no coordinates available";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + coords + "(" + venueName + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }
}
