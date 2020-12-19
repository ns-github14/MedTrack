package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.blackcoffer_neelanshi.Model.History;
import com.example.blackcoffer_neelanshi.Model.Pill;
import com.example.blackcoffer_neelanshi.Model.PillBox;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This activity handles the view and controller of the alert page, which contains
 * a dialog fragment AlertAlarm that shows the dialog box to let the user respond to an alarm.
 * This is the "notification" we are using right now. But it only contains a dialog box so it is
 * not a real notification. We can change this to a real notification that has a ringtone or a
 * vibrating function in the future.
 */

public class AlertActivity extends FragmentActivity {

    private AlarmManager alarmManager;
    private PendingIntent operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlertAlarm alert = new AlertAlarm();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getSupportFragmentManager(), "AlertAlarm");
    }

    // Snooze
    public void doNeutralClick(String pillName){
        final int _id = (int) System.currentTimeMillis();
        final long minute = 60000;
        long snoozeLength = 10;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;
                                                                  
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("notificationTime", "Snooze");
        intent.putExtra("notificationMedicationName", pillName);

        operation = PendingIntent.getBroadcast(getApplicationContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** Getting a reference to the System Service ALARM_SERVICE */
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
        Toast.makeText(getApplicationContext(), "Alarm for " + pillName + " was snoozed for 10 minutes", Toast.LENGTH_SHORT).show();

        finish();

    }

    // I took it
    public void doPositiveClick(String pillName){
        PillBox pillBox = new PillBox();
        Pill pill = pillBox.getPillByName(this, pillName);
        History history = new History();

        Calendar takeTime = Calendar.getInstance();
        Date date = takeTime.getTime();
        String dateString = new SimpleDateFormat("MMM d, yyyy").format(date);

        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";

        history.setHourTaken(hour);
        history.setMinuteTaken(minute);
        history.setDateString(dateString);
        history.setPillName(pillName);

        pillBox.addToHistory(this, history);

        String stringMinute;
        if (minute < 10)
            stringMinute = "0" + minute;
        else
            stringMinute = "" + minute;

        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;

        Toast.makeText(getBaseContext(),  pillName + " was taken at "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_SHORT).show();

        Intent returnHistory = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(returnHistory);
        finish();
    }

    // I won't take it
    public void doNegativeClick(){
        finish();
    }

    /**
     * Utilized the link below as a reference guide:
     * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
     *
     * This is a dialog box that AlertActivity called when it is triggered.
     * It contains three buttons to let the user respond to an alarm.
     */

    public static class AlertAlarm extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            /** Creating a alert dialog builder */
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            /** Setting title for the alert dialog */
            builder.setTitle("MedTrack");

            /** Making it so notification can only go away by pressing the buttons */
            setCancelable(false);

            final String pill_name = getActivity().getIntent().getStringExtra("pill_name");

            builder.setMessage("Did you take your "+ pill_name + " ?");

            builder.setPositiveButton("I took it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertActivity act = (AlertActivity)getActivity();
                    act.doPositiveClick(pill_name);
                    getActivity().finish();
                }
            });

            builder.setNeutralButton("Snooze", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /** Exit application on click OK */
                    AlertActivity act = (AlertActivity)getActivity();
                    act.doNeutralClick(pill_name);
                    getActivity().finish();
                }
            });

            builder.setNegativeButton("I won't take", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /** Exit application on click OK */
                    AlertActivity act = (AlertActivity)getActivity();
                    act.doNegativeClick();
                    getActivity().finish();
                }
            });

            return builder.create();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getActivity().finish();
        }
    }
}