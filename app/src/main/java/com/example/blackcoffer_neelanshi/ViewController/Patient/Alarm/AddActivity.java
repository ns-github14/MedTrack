package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.example.blackcoffer_neelanshi.Model.Alarm;
import com.example.blackcoffer_neelanshi.Model.Pill;
import com.example.blackcoffer_neelanshi.Model.PillBox;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.BookAppointmentActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AddActivity extends AppCompatActivity {
    private AlarmManager alarmManager;
    private PendingIntent operation;
    private boolean dayOfWeekList[] = new boolean[7];
    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    ImageView imageView;
    View header;

    int hour, minute;
    TextView timeLabel;
    PillBox pillBox = new PillBox();

    // Time picker dialog that pops up when the user presses the time string
    // This method specifies the hour and minute of the time picker before the user
    // does anything
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minuteOfHour) {
            hour = hourOfDay;
            minute = minuteOfHour;
            timeLabel.setText(setTime(hour, minute));
        }
    };

    /*public void showNotifications( String pill_name ) {
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
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else if(id == R.id.nav_app) {
                    startActivity(new Intent(getApplicationContext(), BookAppointmentActivity.class));
                } else if(id == R.id.nav_add_med) {
                    startActivity(new Intent(getApplicationContext(), AddActivity.class));
                } else if(id == R.id.nav_pillbox) {
                    startActivity(new Intent(getApplicationContext(), PillBoxActivity.class));
                } else if(id == R.id.nav_week) {
                    startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                } else if(id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        // Set up the time string on the page
        timeLabel = (TextView)findViewById(R.id.reminder_time);

        // Get the time right now and set it to be the time string
        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);
        timeLabel.setText(setTime(hour, minute));

        timeLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(AddActivity.this, t, hour, minute, false).show();
            }
        });
        timeLabel.setText(setTime(hour, minute));

        OnClickListener setClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkBoxCounter = 0;

                EditText editText = (EditText) findViewById(R.id.pill_name);
                String pill_name = editText.getText().toString();

                /** Updating model */
                Alarm alarm = new Alarm();

                /** If Pill does not already exist */
                if (!pillBox.pillExist(getApplicationContext(), pill_name)) {
                    Pill pill = new Pill();
                    pill.setPillName(pill_name);
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setPillName(pill_name);
                    alarm.setDayOfWeek(dayOfWeekList);
                    pill.addAlarm(alarm);
                    long pillId = pillBox.addPill(getApplicationContext() ,pill);
                    pill.setPillId(pillId);
                    pillBox.addAlarm(getApplicationContext(), alarm, pill);
                }
                else { // If Pill already exists
                    Pill pill = pillBox.getPillByName(getApplicationContext(), pill_name);
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setPillName(pill_name);
                    alarm.setDayOfWeek(dayOfWeekList);
                    pill.addAlarm(alarm);
                    pillBox.addAlarm(getApplicationContext(), alarm, pill);
                }
                List<Long> ids = new LinkedList<Long>();
                try {
                    List<Alarm> alarms = pillBox.getAlarmByPill(getApplicationContext(), pill_name);
                    for(Alarm tempAlarm: alarms) {
                        if(tempAlarm.getHour() == hour && tempAlarm.getMinute() == minute) {
                            ids = tempAlarm.getIds();
                            break;
                        }
                    }
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                for(int i=0; i<7; i++) {
                    if (dayOfWeekList[i] && pill_name.length() != 0) {

                        int dayOfWeek = i+1;

                        long _id = ids.get(checkBoxCounter);
                        int id = (int) _id;
                        checkBoxCounter++;

                        //showNotifications(pill_name);

                        /** This intent invokes the activity AlertActivity, which in turn opens the AlertAlarm window */
                        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                        intent.putExtra("pill_name", pill_name);
                        operation = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        /** Getting a reference to the System Service ALARM_SERVICE */
                        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                        /** Creating a calendar object corresponding to the date and time set by the user */
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                        /** Converting the date and time in to milliseconds elapsed since epoch */
                        long alarm_time = calendar.getTimeInMillis();

                        if (calendar.before(Calendar.getInstance()))
                            alarm_time += AlarmManager.INTERVAL_DAY * 7;

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                                alarmManager.INTERVAL_DAY * 7, operation);
                    }
                }
                /** Input form is not completely filled out */
                if(checkBoxCounter == 0 || pill_name.length() == 0)
                    Toast.makeText(getBaseContext(), "Please input a pill name or check at least one day!", Toast.LENGTH_SHORT).show();
                else { // Input form is completely filled out
                    Toast.makeText(getBaseContext(), "Alarm for " + pill_name + " is set successfully", Toast.LENGTH_SHORT).show();
                    Intent returnHome = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(returnHome);
                    finish();
                }
            }
        };

        OnClickListener cancelClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnHome = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(returnHome);
                finish();
            }
        };

        Button btnSetAlarm = (Button) findViewById(R.id.btn_set_alarm);
        btnSetAlarm.setOnClickListener(setClickListener);

        Button btnQuitAlarm = (Button) findViewById(R.id.btn_cancel_alarm);
        btnQuitAlarm.setOnClickListener(cancelClickListener);
    }

    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        /** Checking which checkbox was clicked */
        switch(view.getId()) {
            case R.id.checkbox_monday:
                if (checked)
                    dayOfWeekList[1] = true;
                else
                    dayOfWeekList[1] = false;
                break;
            case R.id.checkbox_tuesday:
                if (checked)
                    dayOfWeekList[2] = true;
                else
                    dayOfWeekList[2] = false;
                break;
            case R.id.checkbox_wednesday:
                if (checked)
                    dayOfWeekList[3] = true;
                else
                    dayOfWeekList[3] = false;
                break;
            case R.id.checkbox_thursday:
                if (checked)
                    dayOfWeekList[4] = true;
                else
                    dayOfWeekList[4] = false;
                break;
            case R.id.checkbox_friday:
                if (checked)
                    dayOfWeekList[5] = true;
                else
                    dayOfWeekList[5] = false;
                break;
            case R.id.checkbox_saturday:
                if (checked)
                    dayOfWeekList[6] = true;
                else
                    dayOfWeekList[6] = false;
                break;
            case R.id.checkbox_sunday:
                if (checked)
                    dayOfWeekList[0] = true;
                else
                    dayOfWeekList[0] = false;
                break;
        }
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        Intent returnHome = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method takes hours and minute as input and returns
     * a string that is like "12:01pm"
     */
    public String setTime(int hour, int minute) {
        String am_pm = (hour < 12) ? "am" : "pm";
        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;
        String minuteWithZero;
        if (minute < 10)
            minuteWithZero = "0" + minute;
        else
            minuteWithZero = "" + minute;
        return nonMilitaryHour + ":" + minuteWithZero + am_pm;
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(returnHome);
        finish();
        }
}