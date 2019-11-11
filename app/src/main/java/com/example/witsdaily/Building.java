package com.example.witsdaily;

import android.util.Pair;

import java.util.HashMap;

public class Building {
    private String buildingCode;
    private String buildingName;
    private boolean hasImage;
    public Pair<Double, Double> coordinates = null;
    private HashMap<String, Floor> floors = new HashMap<>();

    public Building(String buildingCode, String buildingName, boolean hasImage) {
        this.buildingCode = buildingCode;
        this.buildingName = buildingName;
        this.hasImage = hasImage;
    }


    public String getBuildingCode() {
        return this.buildingCode;
    }

    public String getBuildingName() {
        return this.buildingName;
    }

    public boolean hasImage() {
        return this.hasImage;
    }

    public void addFloor(String key, Floor floor) {
        this.floors.put(key, floor);
    }

    public HashMap<String, Floor> getFloors() {
        return this.floors;
    }

    public void setCoordinates(double x, double y){
        this.coordinates = new Pair<>(x,y);
    }

    public Pair<Double, Double> getCoordinates() {
        return this.coordinates;
    }
}
