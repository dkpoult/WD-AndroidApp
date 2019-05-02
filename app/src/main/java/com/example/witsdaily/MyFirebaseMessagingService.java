package com.example.witsdaily;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.witsdaily.Survey.SurveyAnswer;
import com.example.witsdaily.Survey.SurveyViewer;
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

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("From: " + remoteMessage.getFrom());



        if (remoteMessage.getNotification() != null) {
            String channelID = "";
            System.out.println( "Message Notification Body: " + remoteMessage.getNotification().getBody());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(R.drawable.arrow_up)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelID, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
                manager.notify(0, builder.build());
            }

            builder.build();

        }
        else{
            if (remoteMessage.getData().containsValue("survey")){
                String courseCode = "";
                Intent i = new Intent(this, SurveyAnswer.class);// hope this works
                i.putExtra("courseCode",courseCode);
                startActivity(i);
            }
        }


    }
}
