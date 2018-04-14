package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 23/02/2018.
 */

public class PlaceInformation {
    private String key;
    private String openingHours;
    private String website;
    private String companyName;
    private String phoneNum;
    private String address;
    private double lat;
    private double lon;
    private String photo;
    private int position;
    private boolean isChecked;
    private String openNow;

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
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

    public PlaceInformation(){}
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String toString(){
        return "CompanyName : " + companyName + "\nAddress : " + address + "\nPhoneNumber : " + phoneNum + "\nWebsite : " + website + "\nOpen : " + openNow + "\nOpeningHours " + openingHours;
    }
}
