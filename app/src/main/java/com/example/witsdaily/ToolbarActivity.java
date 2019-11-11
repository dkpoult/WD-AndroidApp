package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.witsdaily.Events.EventList;
import com.example.witsdaily.Events.EventViewer;
import com.example.witsdaily.Venue.VenueList;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ToolbarActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    public String userToken,personNumber;

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_on_primary));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        Objects.requireNonNull(actionbar).setHomeAsUpIndicator(R.drawable.ic_hamburger);

        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_layout);
        View header = navView.getHeaderView(0);
        TextView headerTitle = header.findViewById(R.id.header_title);
        TextView headerSubtitle = header.findViewById(R.id.header_subtitle);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        try {
            AsyncGettingBitmapFromUrl changeAvatar = new AsyncGettingBitmapFromUrl() {};
            changeAvatar.execute("https://api.adorable.io/avatars/128/"+personNumber+".png");
        }catch (Exception ignored){

        }
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        headerTitle.setText(personNumber);
        headerSubtitle.setText(date.toString());
        navView.setNavigationItemSelectedListener(menuItem -> {

            String currentIntent = Objects.requireNonNull(getIntent().getComponent()).getClassName();
            Intent destination;

            switch (menuItem.toString()){
                case "Home Screen": destination = new Intent(getApplicationContext(),HomeScreen.class);break;
                case "Events": destination = new Intent(getApplicationContext(),EventList.class);break;
                case "Settings": destination = new Intent(getApplicationContext(),SettingsActivity.class);break;
                case "Timetable":destination= new Intent(getApplicationContext(),timetable.class);break;
                case "Venue":destination= new Intent(getApplicationContext(), VenueList.class);break;
                case "Logout":logout();return true;
                    default:return false;
            }

            if (currentIntent.equals(Objects.requireNonNull(destination.getComponent()).getClassName())){

                return false; // don't do anything
            }
            startActivity(destination);
            return true;
        });
    }  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
    public void setupAppBar(){
        configureNavigationDrawer();
        configureToolbar();
    }
    public void logout(){
        SharedPreferences.Editor settings = getSharedPreferences("com.wd", Context.MODE_PRIVATE).edit();
        settings.clear().apply();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(i);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception

            return null;
        }
    }

    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap;
            bitmap = getBitmapFromURL(params[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            NavigationView navView = findViewById(R.id.nav_layout);
            View header = navView.getHeaderView(0);
            ImageView userImage = header.findViewById(R.id.avatar);
            userImage.setImageBitmap(bitmap);
        }
    }


}
