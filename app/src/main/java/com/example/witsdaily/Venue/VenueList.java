package com.example.witsdaily.Venue;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.witsdaily.Course.EnrolledCourses;
import com.example.witsdaily.Course.UnenrolledCourses;
import com.example.witsdaily.Course.courseLink;
import com.example.witsdaily.HomeScreen;
import com.example.witsdaily.NetworkAccessor;
import com.example.witsdaily.R;
import com.example.witsdaily.TabAdapter;
import com.example.witsdaily.ToolbarActivity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VenueList extends ToolbarActivity {

    ArrayList<String> buildings = new ArrayList<>();

    JSONArray venues;
    ArrayAdapter buildingAdapter;
    ArrayList<String> coordinates = new ArrayList<>();


    String personNumber, user_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);
        setupAppBar();
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        buildingAdapter = new ArrayAdapter(VenueList.this,android.R.layout.select_dialog_item);

        NetworkAccessor NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            public void getResponse(JSONObject data) {
                try {
                    if(data.getString("responseCode").equals("successful")){

                        venues = data.getJSONArray("venues");
                        int size = venues.length();
                        for(int i = 0; i < size; i ++){
                            JSONObject venue = venues.getJSONObject(i);
                            String buildingCode = venue.getString("buildingCode").toUpperCase();
                            String subCode = venue.getString("subCode").toUpperCase();
                            if(!buildings.contains(venue.getString("buildingCode"))){
                                buildings.add(buildingCode);

                            }
                            coordinates.add(venue.getString("coordinates"));
                            buildingAdapter.add(buildingCode+" "+subCode);
                        }


                        ListView buildings = findViewById(R.id.lvBuildings);
                        buildings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String venueName = adapterView.getItemAtPosition(i).toString();

                                Intent j = new Intent(VenueList.this, RoomView.class);
                                j.putExtra("coords",coordinates.get(i));
                                j.putExtra("venueName",venueName);
                                startActivity(j);
                            }
                        });
                        buildings.setAdapter(buildingAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        NA.getVenues();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater test = getMenuInflater();

        test.inflate(R.menu.venue_menu,menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                buildingAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }




}
