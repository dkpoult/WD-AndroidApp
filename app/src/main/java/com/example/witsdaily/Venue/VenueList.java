package com.example.witsdaily.Venue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.witsdaily.Building;
import com.example.witsdaily.Floor;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.ToolbarActivity;
import com.example.witsdaily.floorVenue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class VenueList extends ToolbarActivity {

//    ArrayList<String> buildings = new ArrayList<>();

    JSONArray jBuildings;
    ArrayAdapter buildingAdapter, floorAdapter, venueAdapter;

    HashMap<String, Building> buildings = new HashMap<>();


    String personNumber, user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);

        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);

        buildingAdapter = new ArrayAdapter(VenueList.this, android.R.layout.select_dialog_item);
        floorAdapter = new ArrayAdapter(VenueList.this, android.R.layout.select_dialog_item);
        venueAdapter = new ArrayAdapter(VenueList.this, android.R.layout.select_dialog_item);
        final boolean[] flag = new boolean[1];


        StorageAccessor SA = new StorageAccessor(this, personNumber, user_token) {
            @Override
            public void getData(JSONObject data) {
                System.out.println(data);
                try {
                    if (data.getString("responseCode").equals("successful")) {
                        jBuildings = data.getJSONArray("venues");
                        int numBuildings = jBuildings.length();
                        int numFloors, numVens;
                        for (int i = 0; i < numBuildings; i++) {
                            JSONObject jBuilding = jBuildings.getJSONObject(i);
                            Building building = new Building(jBuilding.getString("buildingCode"), jBuilding.getString("buildingName"), false);
                            JSONObject coordinates;
                            if (jBuilding.has("coordinates")) {
                                coordinates = jBuilding.getJSONObject("coordinates");
                                building.setCoordinates((coordinates.has("x") ? coordinates.getDouble("x") : coordinates.getDouble("lat")), (coordinates.has("y") ? coordinates.getDouble("y") : coordinates.getDouble("lng")));
                            }
                            JSONArray jFloors = jBuilding.getJSONArray("floors");
                            numFloors = jFloors.length();
                            Floor floor;
                            for (int j = 0; j < numFloors; j++) {
                                JSONObject jFloor = jFloors.getJSONObject(j);
                                floor = new Floor((jFloor.has("floorName") ? jFloor.getString("floorName") : j + ""), jFloor.getBoolean("hasImage"), j);
                                JSONArray venues = jFloor.getJSONArray("venues");
                                numVens = venues.length();
                                floorVenue venue;
                                for (int k = 0; k < numVens; k++) {
                                    JSONObject jVenue = venues.getJSONObject(k);
                                    venue = new floorVenue(jVenue.getString("venueCode"), jVenue.getString("venueName"), jVenue.getBoolean("hasImage"));
                                    JSONObject subCoords;
                                    if (jVenue.has("coordinates")) {
                                        subCoords = jVenue.getJSONObject("coordinates");
                                        venue.setCoordinates((subCoords.has("x") ? subCoords.getDouble("x") : subCoords.getDouble("lat")), (subCoords.has("y") ? subCoords.getDouble("y") : subCoords.getDouble("lng")));
                                    }
                                    if (jVenue.has("attributes")) {
                                        JSONObject jAttributes = jVenue.getJSONObject("attributes");
                                        for (Iterator<String> it = jAttributes.keys(); it.hasNext(); ) {
                                            String key = it.next();
                                            venue.addAttribute(key, jAttributes.getString(key));
                                        }
                                    }
                                    floor.addVenue(venue.getVenueCode(), venue);
                                }
                                building.addFloor(floor.getFloorCode(), floor);
                            }
                            buildings.put(building.getBuildingCode(), building);
                        }


//                        int size = jBuildings.length();
//                        for(int i = 0; i < size; i ++){
//                            JSONObject venue = jBuildings.getJSONObject(i);
//                            String buildingCode = venue.getString("buildingCode").toUpperCase();
//                            String subCode = venue.getString("subCode").toUpperCase();
//                            if(!buildings.contains(venue.getString("buildingCode"))){
//                                buildings.add(buildingCode);
//                                coordinates.add(venue.getString("coordinates"));
//                                buildingAdapter.add(buildingCode);
//
//                            }
//                            coordinates.add(venue.getString("coordinates"));
//                            buildingAdapter.add(buildingCode+" "+subCode);
//                        }

                        Spinner buildingSpinner = findViewById(R.id.buildingCode);
                        buildingSpinner.setClickable(true);
                        Spinner floorSpinner = findViewById(R.id.floorCode);
                        floorSpinner.setClickable(false);
                        Spinner venueSpinner = findViewById(R.id.venueCode);
                        venueSpinner.setClickable(false);

                        ArrayList<String> adapt = new ArrayList<>(buildings.keySet());
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(VenueList.this,
                                android.R.layout.simple_spinner_item, adapt);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        buildingSpinner.setAdapter(dataAdapter);

                        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                      @Override
                                                                      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                          ArrayList<String> floorData = new ArrayList<>(Objects.requireNonNull(buildings.get(buildingSpinner.getSelectedItem().toString())).getFloors().keySet());
                                                                          ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(VenueList.this, android.R.layout.simple_spinner_item, floorData);
                                                                          floorAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                                                          floorSpinner.setAdapter(floorAdapter);
                                                                      }

                                                                      @Override
                                                                      public void onNothingSelected(AdapterView<?> adapterView) {
                                                                          flag[0] = false;
                                                                          floorSpinner.setClickable(false);
                                                                          ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(VenueList.this, android.R.layout.simple_spinner_item, new ArrayList<>());
                                                                          floorAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                                                      }
                                                                  }

                        );

                        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (!floorSpinner.getSelectedItem().equals("None")) {
                                    flag[0] = true;
                                    System.out.println("THE ITEM THAT WAS SELECTED EXISTS AND CAUSED THIS TO HAPPEN -------------------------------- " + floorSpinner.getSelectedItem().toString() + " :" + buildings.get(buildingSpinner.getSelectedItem().toString()).getFloors().get(floorSpinner.getSelectedItem().toString()).getVenues().size());
                                    venueSpinner.setClickable(true);
                                    ArrayList<String> venueData = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(buildings.get(buildingSpinner.getSelectedItem().toString())).getFloors().get(floorSpinner.getSelectedItem().toString())).getVenues().keySet());
                                    venueData.add(0, "None");
                                    for (String key : venueData) {
                                        System.out.println("_________________________________________________________________________" + key);
                                    }
                                    ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(VenueList.this, android.R.layout.simple_spinner_item, venueData);
                                    venueAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                    venueSpinner.setAdapter(venueAdapter);

                                    floorSpinner.setClickable(true);
                                } else {
                                    venueSpinner.setClickable(false);
                                    ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(VenueList.this, android.R.layout.simple_spinner_item, new ArrayList<>());
                                    venueAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                }
                            }


                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                venueSpinner.setClickable(false);
                                ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(VenueList.this, android.R.layout.simple_spinner_item, new ArrayList<>());
                                venueAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            }
                        });

                        venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(!venueSpinner.getSelectedItem().equals("None")) {
                                    TextView text = findViewById(R.id.attributes);
                                    StringBuilder t = new StringBuilder();
                                    HashMap<String, String> attributes = buildings.get(buildingSpinner.getSelectedItem().toString()).getFloors().get(floorSpinner.getSelectedItem().toString()).getVenues().get(venueSpinner.getSelectedItem().toString()).getAttributes();
                                    for (String key : attributes.keySet()) {
                                        t.append(key).append(" : ").append(attributes.get(key)).append("\n");
                                    }
                                    text.setText(t.toString());
                                }else{
                                    TextView text = findViewById(R.id.attributes);
                                    text.setText("");

                                }
                            }


                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                TextView text = findViewById(R.id.attributes);
                                text.setText("");

                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SA.getBuildings();

    }

    public void viewImage(View view) {
        Spinner floor, venue, building;
        building = findViewById(R.id.buildingCode);
        floor = findViewById(R.id.floorCode);
        venue = findViewById(R.id.venueCode);
        if (!building.getSelectedItem().toString().isEmpty() && Objects.requireNonNull(buildings.get(building.getSelectedItem().toString())).coordinates != null) {
            Intent i = new Intent(VenueList.this, RoomView.class);
            String extra = "";
            extra += building.getSelectedItem() + " " + buildings.get(building.getSelectedItem().toString()).getFloors().get(floor.getSelectedItem().toString()).index + " " + (!venue.getSelectedItem().equals("None") ? venue.getSelectedItem() : "");
            String coords = Objects.requireNonNull(buildings.get(building.getSelectedItem().toString())).getCoordinates().first + "," + Objects.requireNonNull(buildings.get(building.getSelectedItem().toString())).getCoordinates().second;
            i.putExtra("coords", coords);
            i.putExtra("venueName", extra);
            i.putExtra("floorName", floor.getSelectedItem().toString());
            if (!venue.getSelectedItem().equals("None") && buildings.get(building.getSelectedItem().toString()).getFloors().get(floor.getSelectedItem().toString()).getVenues().get(venue.getSelectedItem().toString()).hasCoords()) {
                i.putExtra("venCoords", buildings.get(building.getSelectedItem().toString()).getFloors().get(floor.getSelectedItem().toString()).getVenues().get(venue.getSelectedItem().toString()).getCoordinates().first + "," + buildings.get(building.getSelectedItem().toString()).getFloors().get(floor.getSelectedItem().toString()).getVenues().get(venue.getSelectedItem().toString()).getCoordinates().second);
            } startActivity(i);
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater test = getMenuInflater();
//
//        test.inflate(R.menu.venue_menu, menu);
//        SearchManager searchManager = (SearchManager)
//                getSystemService(Context.SEARCH_SERVICE);
//        MenuItem searchMenuItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) searchMenuItem.getActionView();
//
//        searchView.setSearchableInfo(searchManager.
//                getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                buildingAdapter.getFilter().filter(s);
//                return false;
//            }
//        });
//        return true;
//    }


}
