package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 06/12/2017.
 */

public class MyGeoLocation {
    private String key;
    private double lat;
    private double lon;

    public MyGeoLocation(){}



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String toString(){
        return "Address: " + key + "\nLatitude: " + lat + "\nLongitude: " + lon ;
    }
}
