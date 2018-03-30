package com.example.craig.finalyearproject.model;

/**
 * Created by craig on 30/03/2018.
 */

public class MyNotifiy {
    @Override
    public String toString() {
        return "MyNotifiy{" +
                "signUp=" + signUp +
                ", companyName='" + companyName + '\'' +
                '}';
    }

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
}
