package com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.Appointment_Class;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.HomeActivity_Doc;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.RVAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmedFragment extends Fragment {

    public ConfirmedFragment() {}

    RecyclerView recyclerView;
    RVAdapter adapter;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_confirmed_pat, container, false);

        recyclerView = view.findViewById(R.id.r);

        Query base = FirebaseFirestore.getInstance().collection("Appointments").whereEqualTo("Patient", FirebaseAuth.getInstance().getCurrentUser().getEmail()).whereEqualTo("Status", "Requested");

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

                progressBar = dialog.findViewById(R.id.progressBar);
                ((TextView)dialog.findViewById(R.id.heading)).setText("Accept Request?");
                ((TextView)dialog.findViewById(R.id.subtext)).setText("You're accepting this appointment request.\n\n We will move it to 'Pending section' until a payment is made.\n\n Once the payment is received, you can confirm this session.");
                ((Button) dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseFirestore.getInstance().document(path).update("Status", "Pending");
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