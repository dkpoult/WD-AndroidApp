package com.example.witsdaily;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token){
        // do something with token
        System.out.println("wadup");
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
       // message, here is where that should be initiated. See sendNotification method below.
        System.out.println("message received");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            System.out.println( "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                System.out.println("somethiing idk");
            } else {
                // Handle message within 10 seconds
                System.out.println("somethiing idk");
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println( "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }
}
