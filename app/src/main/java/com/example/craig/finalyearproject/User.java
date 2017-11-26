package com.example.craig.finalyearproject;

import android.net.Uri;

/**
 * Created by craig on 26/11/2017.
 */

public class User {
    private String UserID;
    private String UserName;
    private String Image;

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


}
