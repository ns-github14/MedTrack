package com.example.blackcoffer_neelanshi.ViewController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BookAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RVAdapter adapter;
    Query base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        base = FirebaseFirestore.getInstance().collection("Doctors");
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Doctor_Class> options = new FirestoreRecyclerOptions.Builder<Doctor_Class>().setQuery(base, Doctor_Class.class).build();

        adapter = new RVAdapter(options);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Doctor_Class doctor_class = documentSnapshot.toObject(Doctor_Class.class);
                String id = documentSnapshot.getId();
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
                    dialog.setContentView(R.layout.customview);

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
                            ((TextView) dialog.findViewById(R.id.date_head)).setVisibility(View.VISIBLE);
                            ((EditText) dialog.findViewById(R.id.date)).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.time_head)).setVisibility(View.VISIBLE);
                            ((EditText) dialog.findViewById(R.id.time)).setVisibility(View.VISIBLE);
                            ((Button) dialog.findViewById(R.id.confirm)).setVisibility(View.VISIBLE);
                            ((EditText) dialog.findViewById(R.id.date)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //DatePicker datePicker = new DatePicker(BookAppointmentActivity.this);
                                    //((EditText) dialog.findViewById(R.id.date))
                                      //      .setText(datePicker.getDayOfMonth() + "" + datePicker.getMonth() + "" + datePicker.getYear());

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