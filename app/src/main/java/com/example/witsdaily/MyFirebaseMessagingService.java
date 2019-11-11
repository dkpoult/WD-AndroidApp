package com.example.witsdaily;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
            String channelID = "Default channel";
            System.out.println( "Message Notification Body: " + remoteMessage.getNotification().getBody());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(R.drawable.forum_upvote)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelID, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);

                manager.notify(0, builder.build());

            }else{
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(0, builder.build());

            }



        }
        else{
            if (remoteMessage.getData().containsValue("survey")){

                String courseCode;
                courseCode  = remoteMessage.getData().get("body").split(" ")[0];
                String type = remoteMessage.getData().get("body").split(" ")[1];
                if (type.equals("closed")){ // next sprint add this as an option

             //       Intent i = new Intent(this, SurveyViewer.class);// hope this works
             //       i.putExtra("courseCode",courseCode);
             //       startActivity(i);
                }else{

                    Intent i = new Intent(this, SurveyAnswer.class);// hope this works
                    i.putExtra("courseCode",courseCode);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

            }
        }


    }
}
