package com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.adapter.RVAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RVAdapter adapter;
    Query base;
    private ProgressBar progressBar;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        base = FirebaseFirestore.getInstance().collection("Doctors");
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Doctor_Class> options = new FirestoreRecyclerOptions.Builder<Doctor_Class>().setQuery(base, Doctor_Class.class).build();

        adapter = new RVAdapter(options);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String path = documentSnapshot.getReference().getPath();
                showCustomDialog(path);
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
                    ((TextView) dialog.findViewById(R.id.fees_d)).setText(String.valueOf(documentSnapshot.get("Fees")));
                    ((TextView) dialog.findViewById(R.id.contact_d)).setText(String.valueOf(documentSnapshot.getLong("Contact")));

                    ((Button) dialog.findViewById(R.id.status)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Button) dialog.findViewById(R.id.status)).setVisibility(View.GONE);
                            ((TextView) dialog.findViewById(R.id.app)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.date_head)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.date)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.time_head)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.time)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.desc_head)).setVisibility(View.VISIBLE);
                            ((EditText) dialog.findViewById(R.id.desc)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.confirm)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.date_head)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //DatePicker datePicker = new DatePicker(BookAppointmentActivity.this);
                                    final Calendar c = Calendar.getInstance();
                                    mYear = c.get(Calendar.YEAR);
                                    mMonth = c.get(Calendar.MONTH);
                                    mDay = c.get(Calendar.DAY_OF_MONTH);
                                    String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                                    DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            ((TextView) dialog.findViewById(R.id.date)).setText(selectedDate);
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
                                            ((TextView) dialog.findViewById(R.id.time)).setText(hourOfDay + ":" + minute);
                                        }
                                    }, mHour, mMinute, false);
                                    timePickerDialog.show();
                                }
                            });
                            ((Button) dialog.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String[] name = new String[1];
                                    FirebaseFirestore.getInstance().collection("Patients").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot d = task.getResult();
                                            name[0] = (String) d.get("Name");
                                        }
                                    });
                                    Map<String, Object> a = new HashMap<>();
                                    a.put("Doctor", documentSnapshot.getId());
                                    a.put("Patient_Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    a.put("Patient", name);
                                    a.put("Status", false);
                                    a.put("Time", ((TextView) dialog.findViewById(R.id.time)).getText());
                                    a.put("Date", ((TextView) dialog.findViewById(R.id.date)).getText());
                                    a.put("Description",((EditText) dialog.findViewById(R.id.desc)).getText());
                                    try {
                                        progressBar.setVisibility(View.VISIBLE);
                                        FirebaseFirestore.getInstance().collection("Appointments").document().set(a);
                                        progressBar.setVisibility(View.GONE);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(BookAppointmentActivity.this);
                                        ViewGroup viewGroup = findViewById(android.R.id.content);
                                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customdialog_booking, viewGroup, false);
                                        builder.setView(dialogView);
                                        AlertDialog alertDialog = builder.create();
                                        ((Button) alertDialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(BookAppointmentActivity.this, HomeActivity.class));
                                            }
                                        });
                                        alertDialog.show();
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