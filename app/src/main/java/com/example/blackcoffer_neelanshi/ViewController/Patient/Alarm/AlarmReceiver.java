package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.blackcoffer_neelanshi.R;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private static final String CHANNEL_ID = "reminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context, intent);
    }

    public void deliverNotification(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medication Reminder";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(notificationChannel);

        }
        Intent i = new Intent(context, AlertActivity.class);
        i.putExtra("pill_name", intent.getStringExtra("notificationMedicationName"));
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Time to take your pills!")
                .setContentText("Medication reminder for " + intent.getStringExtra("notificationMedicationName"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(1, builder.build());
    }
}

