package com.example.witsdaily;

import java.util.HashMap;

public class Floor {
    private String floorCode;
    private boolean hasImage;
    private HashMap<String, floorVenue> venues = new HashMap<>();
    public int index;

    public Floor(String floorCode, boolean hasImage, int index){
        this.floorCode = floorCode;
        this.hasImage = hasImage;
        this.index = index;
    }

    public String getFloorCode() {
        return this.floorCode;
    }

    public boolean hasImage() {
        return this.hasImage;
    }

    public void addVenue(String key, floorVenue venue){
        this.venues.put(key, venue);
    }

    public HashMap<String, floorVenue> getVenues(){
        return this.venues;
    }
}
