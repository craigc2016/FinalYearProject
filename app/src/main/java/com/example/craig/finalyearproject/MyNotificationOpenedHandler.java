package com.example.craig.finalyearproject;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by craig on 12/04/2018.
 * This class is needed for to handle the user action for when they click
 * on the One Signal notification.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    //Declase the variables needed for this class
    private Application application;
    private String companyName;
    private String username;
    /*
    Constructor which takes an argument of application and sets it to a local
    variable of application.
     */
    public MyNotificationOpenedHandler(Application application) {
        this.application = application;
    }
    /*
    Implemented method which checks for the users input of clicking t
     */
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        JSONObject data = result.notification.payload.additionalData;
        companyName = data.optString("tag");
        username = data.optString("tag1");
        if (data != null) {
            startApp();
        }

    }
    private void startApp() {
        Intent intent = new Intent(application, MessageActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CompanyName",companyName);
        intent.putExtra("UserName",username);
        application.startActivity(intent);
    }
}
