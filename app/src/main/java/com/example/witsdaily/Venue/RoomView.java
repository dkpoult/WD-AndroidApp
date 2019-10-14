package com.example.witsdaily.Venue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.ToolbarActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoomView extends AppCompatActivity {
    String coords,venueName,userToken,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);
        Intent i = getIntent();
        coords = i.getStringExtra("coords");
        venueName = i.getStringExtra("venueName");
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        setTitle(venueName);
        String[] roomInfo = venueName.split(" ");
        String buildingName = roomInfo[0];
        String roomNumber = roomInfo[1];

        String imageUrl = "https://wd.dimensionalapps.com/venue/get_venue_image?buildingCode="+buildingName+"&subCode="+roomNumber;
        try {
            AsyncGettingBitmapFromUrl updateImage = new AsyncGettingBitmapFromUrl() {};
            updateImage.execute(imageUrl);
        }catch (Exception ignored){

        }


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
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception

            return null;
        }
    }

    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            bitmap = getBitmapFromURL(params[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView buildingImage = findViewById(R.id.imgVenue);
            if (bitmap!=null) {
                buildingImage.setImageBitmap(bitmap);
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buildingImage.setImageDrawable(getDrawable(R.drawable.no_image));
                }
            }
        }
    }

}
