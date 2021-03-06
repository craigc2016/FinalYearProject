package com.example.craig.finalyearproject.model;

/**
 * This is a model class used to set the account settings
 * for the users using the email and username stored in the
 * database. The email acts as a primary key and username is
 * the information for which to retrieve.
 */

public class UsernameInfo {
    private String username;
    private String email;
    private String key;
    public UsernameInfo(){}

    public UsernameInfo(String username, String email,String key){
        this.username = username;
        this.email = email;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public String toString() {
        return "UsernameInfo{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
