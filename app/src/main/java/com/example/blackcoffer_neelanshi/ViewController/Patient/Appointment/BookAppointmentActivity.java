package com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.blackcoffer_neelanshi.MySingleton;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.AddActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.PillBoxActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.ScheduleActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.ProfileActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.adapter.RVAdapter_Doctor_Class;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RVAdapter_Doctor_Class adapter;
    Query base;
    private ProgressBar progressBar;
    private int mYear, mMonth, mDay, mHour, mMinute, msec;
    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    ImageView imageView;
    View header;

    final String TAG = "NOTIFICATION TAG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    private String serverKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

                Toast.makeText(BookAppointmentActivity.this, getIntent().getStringExtra("Location"), Toast.LENGTH_SHORT).show();
                base = FirebaseFirestore.getInstance().collection("Doctors").whereEqualTo("Location", getIntent().getStringExtra("Location"));
                recyclerView = findViewById(R.id.rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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

                    if(!documentSnapshot.exists()){
                        TextView t = (TextView) findViewById(R.id.noresult);
                        t.setVisibility(View.VISIBLE);
                        t.setText("No doctors nearby...Try changing location in your Profile.");
                    }
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

                            Calendar cal = Calendar.getInstance();

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
                                            String mm = String.format("%02d", monthOfYear+1);
                                            String dd = String.format("%02d", dayOfMonth);
                                            ((TextView) dialog.findViewById(R.id.date)).setText(year + "-" + mm + "-" + dd);
                                            mYear = year;
                                            mMonth = Integer.parseInt(mm) - 1;
                                            mDay = Integer.parseInt(dd);
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
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            String hh = String.format("%02d", hourOfDay);
                                            String mm = String.format("%02d", minute);
                                            ((TextView) dialog.findViewById(R.id.time)).setText(hh + " : " + mm);
                                            mHour = Integer.parseInt(hh);
                                            mMinute = Integer.parseInt(mm);

                                            //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                                            //Toast.makeText(BookAppointmentActivity.this, sdf.format(date), Toast.LENGTH_SHORT).show();
                                        }
                                    }, mHour, mMinute, true);
                                    timePickerDialog.show();
                                }
                            });
                            ((Button) dialog.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cal.set(mYear, mMonth, mDay, mHour, mMinute, 0);
                                    if (System.currentTimeMillis() + 86400000 > cal.getTimeInMillis()) {
                                        Toast.makeText(BookAppointmentActivity.this, "Make sure to select a time atleast 24-hr from now!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        HashMap<String, Object> a = new HashMap<>();
                                        a.put("Doctor", documentSnapshot.getId());
                                        a.put("Patient_Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        a.put("Status", "Requested");
                                        a.put("Date", cal.getTimeInMillis());

                                        try {
                                            progressBar.setVisibility(View.VISIBLE);
                                            String id = FirebaseFirestore.getInstance().collection("Appointments").document().getId();
                                            FirebaseFirestore.getInstance().collection("Appointments").document(id).set(a);
                                            progressBar.setVisibility(View.GONE);
                                            dialog.cancel();


                                            FirebaseFirestore.getInstance()
                                                    .document("Users/serverkey")
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        serverKey = "key=" + task.getResult().getString("key");

                                                        FirebaseFirestore.getInstance()
                                                                .collection("Users")
                                                                .document(documentSnapshot.getId())
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                String TOPIC = "/Messages/" + documentSnapshot.getString("TokenId"); //topic must match with what the receiver subscribed to
                                                                String NOTIFICATION_TITLE = "New Appointment Request";
                                                                String NOTIFICATION_MESSAGE = "You've got a new appointment request from " + FirebaseAuth.getInstance().getCurrentUser().getEmail();                                                                JSONObject notification = new JSONObject();
                                                                JSONObject notifcationBody = new JSONObject();

                                                                try {
                                                                    notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                    notification.put("to", TOPIC);
                                                                    notification.put("data", notifcationBody);
                                                                } catch (JSONException e) {
                                                                    Log.e(TAG, "onCreate: " + e.getMessage());
                                                                }
                                                                sendNotification(notification);
                                                            }
                                                        });
                                                    }
                                                }
                                            });


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
                                        } catch (Exception e) {
                                            Toast.makeText(BookAppointmentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
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

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BookAppointmentActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}