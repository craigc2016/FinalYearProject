package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 23/02/2018.
 */

public class PlaceInformation {
    private String openingHours;
    private String website;
    private String companyName;
    private String phoneNum;
    private String address;
    private double lat;
    private double lon;
    private String photo;
    private boolean isChecked;
    private String openNow;


    public PlaceInformation(String openingHours, String website, String companyName, String phoneNum, String address, double lat, double lon, String photo, boolean isChecked, String openNow) {
        this.openingHours = openingHours;
        this.website = website;
        this.companyName = companyName;
        this.phoneNum = phoneNum;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.photo = photo;
        this.isChecked = isChecked;
        this.openNow = openNow;
    }

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


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String toString(){
        return "CompanyName : " + companyName + "\nAddress : " + address + "\nPhoneNumber : " + phoneNum + "\nWebsite : " + website + "\nOpen : " + openNow + " OpeningHours " + openingHours;
    }
}
