package com.example.craig.finalyearproject.model;

/**
 *This class is used for the model of the data retrieved from the
 * Google places Web service. It is used to populate the listview
 * in the map page. It will use the getter and setter methods to set
 * or get the instance variables values.
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


    public PlaceInformation(){}

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
