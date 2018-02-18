package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 06/12/2017.
 */

public class MyGeoLocation {
    private String key;
    private double lat;
    private double lon;
    private String openingHours;
    private String website;
    private String companyName;
    private String phoneNum;
    private String address;

    public MyGeoLocation(){}

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String  phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
        return "Address: " + address + "\nLatitude: " + lat + "\nLongitude: " + lon +"\nPhoneNum : "+phoneNum ;
    }
}
