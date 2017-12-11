package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 11/12/2017.
 */

public class Username {
    private String Uid;
    private String username;
    private String email;

    public Username(){}

    public Username(String username,String email){
        this.username = username;
        this.email = email;
    }
    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
