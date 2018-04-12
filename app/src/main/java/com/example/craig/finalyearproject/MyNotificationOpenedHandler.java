package com.example.craig.finalyearproject;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by craig on 12/04/2018.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private Application application;
    private String companyName;
    public MyNotificationOpenedHandler(Application application) {
        this.application = application;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        // Get custom datas from notification
        JSONObject data = result.notification.payload.additionalData;
        companyName = data.optString("tag");

        if (data != null) {
            Log.i("DATA","" + data);
            startApp();
        }

    }

    private void startApp() {
        Intent intent = new Intent(application, MessageActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i("TESTTAG",companyName);
        intent.putExtra("CompanyName",companyName);
        application.startActivity(intent);
    }
}
