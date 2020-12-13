package com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.AddActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.PillBoxActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.ScheduleActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.adapter.RVAdapter_Doctor_Class;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

public class BookAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RVAdapter_Doctor_Class adapter;
    Query base;
    private ProgressBar progressBar;
    private int mYear, mMonth, mDay, mHour, mMinute;
    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    ImageView imageView;
    Toolbar toolbar;
    View header;
    long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        base = FirebaseFirestore.getInstance().collection("Doctors");
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Doctor_Class> options = new FirestoreRecyclerOptions.Builder<Doctor_Class>().setQuery(base, Doctor_Class.class).build();

        adapter = new RVAdapter_Doctor_Class(options);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter_Doctor_Class.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String path = documentSnapshot.getReference().getPath();
                showCustomDialog(path);
            }
        });

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
    }
    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    public void showCustomDialog(String path) {
        FirebaseFirestore.getInstance().document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    @NonNull Doctor_Class model = new Doctor_Class();
                    DocumentSnapshot documentSnapshot = task.getResult();

                    final Dialog dialog = new Dialog(BookAppointmentActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.customview_patient);

                    ((TextView) dialog.findViewById(R.id.name_d)).setText(documentSnapshot.getString("Name"));
                    ((TextView) dialog.findViewById(R.id.specialization_d)).setText(documentSnapshot.getString("Specialization"));
                    ((TextView) dialog.findViewById(R.id.hospital_d)).setText(documentSnapshot.getString("Hospital"));
                    ((TextView) dialog.findViewById(R.id.location_d)).setText(documentSnapshot.getString("Location"));
                    ((TextView) dialog.findViewById(R.id.gender_d)).setText(documentSnapshot.getString("Gender"));
                    ((TextView) dialog.findViewById(R.id.timings_d)).setText(documentSnapshot.getString("From_time") + " - " + documentSnapshot.getString("To_time"));
                    ((TextView) dialog.findViewById(R.id.fees_d)).setText(documentSnapshot.getString("Fees"));
                    ((TextView) dialog.findViewById(R.id.contact_d)).setText(documentSnapshot.getString("Contact"));

                    ((Button) dialog.findViewById(R.id.status)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((TextView) dialog.findViewById(R.id.app)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.date_head)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.date)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.time_head)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.time)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.confirm)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.status)).setVisibility(View.GONE);

                            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("IST"));

                            ((Button) dialog.findViewById(R.id.date_head)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final Calendar c = cal.getInstance();
                                    mYear = c.get(Calendar.YEAR);
                                    mMonth = c.get(Calendar.MONTH);
                                    mDay = c.get(Calendar.DAY_OF_MONTH);

                                    DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            ((TextView) dialog.findViewById(R.id.date)).setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            cal.set(year, monthOfYear, dayOfMonth);
                                            timestamp = c.getTimeInMillis();
                                        }
                                    }, mYear, mMonth, mDay);
                                    datePickerDialog.show();
                                }
                            });
                            ((Button) dialog.findViewById(R.id.time_head)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Calendar c = Calendar.getInstance();
                                    mHour = c.get(Calendar.HOUR_OF_DAY);
                                    mMinute = c.get(Calendar.MINUTE);

                                    // Launch Time Picker Dialog
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(BookAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            String hourFormat = String.format("%02d", hourOfDay);
                                            String minFormat = String.format("%02d", minute);
                                            ((TextView) dialog.findViewById(R.id.time)).setText(hourOfDay + " : " + minute);
                                        }
                                    }, mHour, mMinute, true);
                                    timePickerDialog.show();
                                }
                            });
                            ((Button) dialog.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HashMap<String, Object> a = new HashMap<>();
                                    a.put("Doctor", documentSnapshot.getId());
                                    a.put("Patient_Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    a.put("Status", "Requested");
                                    a.put("Date", ((TextView) dialog.findViewById(R.id.date)).getText().toString());
                                    a.put("Time", ((TextView) dialog.findViewById(R.id.time)).getText().toString());
                                    try {
                                        progressBar.setVisibility(View.VISIBLE);
                                        FirebaseFirestore.getInstance().collection("Appointments").document().set(a);
                                        progressBar.setVisibility(View.GONE);
                                        dialog.cancel();

                                        final Dialog dial = new Dialog(BookAppointmentActivity.this);
                                        dial.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dial.setContentView(R.layout.customdialog_booking);
                                        ((Button) dial.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(BookAppointmentActivity.this, HomeActivity.class));
                                            }
                                        });
                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(dial.getWindow().getAttributes());
                                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        dial.show();
                                        dial.getWindow().setAttributes(lp);
                                    }
                                    catch (Exception e){
                                        Toast.makeText(BookAppointmentActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}