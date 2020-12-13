package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.blackcoffer_neelanshi.Model.Appointment_Class;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.RVAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class PendingFragment extends Fragment {

    public PendingFragment() {}

    RecyclerView recyclerView;
    RVAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        recyclerView = view.findViewById(R.id.r);

        Query base = FirebaseFirestore.getInstance().collection("Appointments").whereEqualTo("Doctor", FirebaseAuth.getInstance().getCurrentUser().getEmail()).whereEqualTo("Status", "Pending");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Appointment_Class> options = new FirestoreRecyclerOptions.Builder<Appointment_Class>().setQuery(base, Appointment_Class.class).build();

        adapter = new RVAdapter(options);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String path = documentSnapshot.getReference().getPath();
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customdialog_status);

                ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                ((TextView)dialog.findViewById(R.id.heading)).setText("Confirm Session?");
                ((TextView)dialog.findViewById(R.id.subtext)).setText("You're confirming this appointment session.\n\n Make sure you've succesfully received your fee payment.");
                ((Button) dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseFirestore.getInstance().document(path).update("Status", "Confirmed");
                        FirebaseFirestore.getInstance().document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot d = task.getResult();


                                    SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
                                    Calendar dt = Calendar.getInstance();

// Where untilDate is a date instance of your choice, for example 30/01/2012
                                    dt.setTime(dt.getTime());

// If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
                                    dt.add(Calendar.DATE, 1);
                                    String dtUntill = yyyyMMdd.format(dt.getTime());

                                    ContentResolver cr = getContext().getContentResolver();
                                    ContentValues values = new ContentValues();

                                    values.put(CalendarContract.Events.DTSTART, d.getString("Date"));
                                    values.put(CalendarContract.Events.TITLE, "Appointment with "+d.getString("Patient_Email"));
                                    values.put(CalendarContract.Events.DESCRIPTION, "");

                                    TimeZone timeZone = TimeZone.getDefault();
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

// Default calendar
                                    values.put(CalendarContract.Events.CALENDAR_ID, 1);

                                    values.put(CalendarContract.Events.RRULE, "FREQ=ONCE" + dtUntill);
// Set Period for 1 Hour
                                    values.put(CalendarContract.Events.DURATION, "+P1H");

                                    values.put(CalendarContract.Events.HAS_ALARM, 1);

// Insert event to calendar
                                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);



                                }
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getContext(), HomeActivity_Doc.class));
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }
}