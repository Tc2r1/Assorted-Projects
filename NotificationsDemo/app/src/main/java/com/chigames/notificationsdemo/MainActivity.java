package com.chigames.notificationsdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We create a pending intent to be used when the notification is clicked on.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 001, intent, 0);

        // Channel IDs are used in Android Oreo so that users can decide
        // which category of notifications they want to receive from an app.
        String CHANNEL_ID = "Main Notifications Channel";

        // We create a notificationManager which manages the Notification_service.
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);

        Notification.Builder notificationBuilder;

        // Create the NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Basic Notifications";
            String description = "This Channel is for Notifications of the basic notification category. User sees this in the system settings.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.YELLOW);
            mChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            mChannel.enableVibration(true);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(this);
        }


            notificationBuilder
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("My Note 365")
                    .setContentTitle("Notification Title")
                    .setContentText("Notification Text going on...")
                    .setContentInfo("Info")
                    .setSmallIcon(android.R.drawable.btn_star)
                    .addAction(android.R.drawable.sym_action_chat, "Chat", pendingIntent)
                    .build();


        notificationManager.notify(001, notificationBuilder.build());

    }
}
