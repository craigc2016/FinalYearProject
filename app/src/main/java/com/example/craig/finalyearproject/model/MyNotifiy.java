package com.example.craig.finalyearproject.model;

/**
 * This is a model class which is used for the states
 * of the notification widgets to be set using the setter
 * methdos. The getter methods allow for the notification state
 * to be retrieved.
 */

public class MyNotifiy {
    boolean signUp;
    String companyName;

    public MyNotifiy(){
    }
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isSignUp() {
        return signUp;
    }

    public void setSignUp(boolean signUp) {
        this.signUp = signUp;
    }

    @Override
    public String toString() {
        return "MyNotifiy{" +
                "signUp=" + signUp +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
