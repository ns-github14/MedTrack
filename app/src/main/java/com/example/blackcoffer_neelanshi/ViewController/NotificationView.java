package com.example.blackcoffer_neelanshi.ViewController;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.AddActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.AlertActivity;

public class NotificationView extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pill_name = intent.getStringExtra("pill_name");
        Intent i = new Intent(getApplicationContext(), AlertActivity.class);
        i.putExtra("pill_name", pill_name);
        PendingIntent pIntent = PendingIntent.getActivity(NotificationView.this, (int) System.currentTimeMillis(), intent, 0);
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(NotificationView.this)
                .setContentTitle("Time to take your meds!")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_user_foreground)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
        startActivity(new Intent(getApplicationContext(), AlertActivity.class));
    }
}