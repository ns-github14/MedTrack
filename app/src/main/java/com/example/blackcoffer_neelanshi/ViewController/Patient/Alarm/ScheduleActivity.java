package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.example.blackcoffer_neelanshi.Model.Alarm;
import com.example.blackcoffer_neelanshi.Model.PillBox;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.BookAppointmentActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.ProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class ScheduleActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    ImageView imageView;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

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
                FirebaseFirestore.getInstance().collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                        i.putExtra("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        i.putExtra("Location", documentSnapshot.getString("Location"));
                        startActivity(i);
                    }
                });
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

                    FirebaseFirestore.getInstance().collection("Messages")
                            .document(FirebaseInstanceId.getInstance().getToken()).delete();
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update("TokenId", "");
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        TableLayout stk = (TableLayout) findViewById(R.id.table_calendar);

        PillBox pillBox = new PillBox();

        List<Alarm> alarms = null;

        List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        int color = getResources().getColor(R.color.Cyan);

        for (int i = 1; i < 8; i++) {

            String day = days.get(i-1);
            TableRow headerRow = new TableRow(this);
            TextView headerText = new TextView(this);

            headerText.setText(day);
            headerText.setTextSize(20);
            headerText.setTextColor(Color.WHITE);
            headerText.setPadding(0, 20, 0, 20);
            headerText.setGravity(Gravity.CENTER);

            headerRow.addView(headerText);
            headerRow.setBackgroundColor(color);
            stk.addView(headerRow);

            //Let headerText span two columns
            TableRow.LayoutParams params = (TableRow.LayoutParams)headerText.getLayoutParams();
            params.span = 2;
            headerText.setLayoutParams(params);

            try {
                alarms = pillBox.getAlarms(this, i);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if(alarms.size() != 0) {
                for(Alarm alarm: alarms) {
                    TableRow tbrow = new TableRow(this);

                    TextView t1v = new TextView(this);
                    t1v.setText(alarm.getPillName());
                    t1v.setMaxEms(6);
                    t1v.setTextSize(20);
                    t1v.setPadding(0, 10, 0, 50);
                    t1v.setTextColor(getResources().getColor(android.R.color.black));
                    t1v.setGravity(Gravity.CENTER);
                    tbrow.addView(t1v);

                    TextView t2v = new TextView(this);

                    String time = alarm.getStringTime();
                    t2v.setText(time);
                    t2v.setTextSize(20);
                    t2v.setPadding(0, 10, 0, 50);
                    t2v.setTextColor(getResources().getColor(android.R.color.black));
                    t2v.setGravity(Gravity.CENTER);
                    tbrow.addView(t2v);

                    stk.addView(tbrow);
                }
            } else {
                TableRow tbrow = new TableRow(this);

                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(15);
                tv.setPadding(0, 10, 0, 50);
                tv.setText("You don't have any alarms for " + day + ".");

                tbrow.addView(tv);
                stk.addView(tbrow);

                //Let tv span two columns
                TableRow.LayoutParams params2 = (TableRow.LayoutParams)tv.getLayoutParams();
                params2.span = 2;
                tv.setLayoutParams(params2);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(returnHome);
        finish();
    }
}