package com.example.witsdaily;

import android.util.Pair;

import java.util.HashMap;

public class floorVenue {
    private String venueCode, venueName;
    private boolean hasImage;
    private Pair<Double, Double> coordinates = null;
    private HashMap<String, String> attributes = new HashMap<>();

    public floorVenue(String venueCode, String venueName, boolean hasImage){
        this.venueCode = venueCode;
        this.venueName = venueName;
        this.hasImage = hasImage;
    }

    public String getVenueCode() {
        return this.venueCode;
    }

    public String getVenueName() {
        return this.venueName;
    }

    public boolean hasImage() {
        return this.hasImage;
    }

    public void setCoordinates(double x, double y){
        this.coordinates = new Pair<>(x,y);
    }

    public Pair<Double, Double> getCoordinates() {
        return this.coordinates;
    }

    public void addAttribute(String key, String value){
        this.attributes.put(key, value);
    }

    public String getAttribute(String key){
        return this.attributes.get(key);
    }

    public HashMap<String, String> getAttributes(){
        return this.attributes;
    }

    public boolean hasCoords(){
       return  this.coordinates != null;
    }
}
