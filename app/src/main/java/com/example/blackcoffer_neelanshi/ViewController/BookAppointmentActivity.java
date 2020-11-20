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
        FirebaseFirestore.getInstance().collection("Doctor").document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    @NonNull Doctor_Class model = new Doctor_Class();
                    DocumentSnapshot documentSnapshot = task.getResult();

                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.customview);

                    ((TextView)findViewById(R.id.name_d)).setText(model.getName());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getSpecialization());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getHospital());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getLocation());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getGender());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getFrom_time() + " - " + model.getTo_time());
                    ((TextView)findViewById(R.id.name_d)).setText(model.getFees());
                    ((TextView)findViewById(R.id.name_d)).setText(String.valueOf(model.getContact()));

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