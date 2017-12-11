package com.example.craig.finalyearproject.model;

import android.net.Uri;

/**
 * Created by craig on 26/11/2017.
 */

public class User {
    private String UserID;
    private String UserName;
    private String Image;
    private boolean profile;

    public User (){

    }

    public User(String UserID,String UserName,String Image){
        this.UserID = UserID;
        this.UserName = UserName;
        this.Image = Image;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public boolean isProfile() {return profile;}

    public void setProfile(boolean profile) {this.profile = profile;}



}
