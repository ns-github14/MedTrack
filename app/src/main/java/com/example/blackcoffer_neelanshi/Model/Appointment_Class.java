package com.example.blackcoffer_neelanshi.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Appointment_Class {
    private String Patient_Email, Status, Doctor;
    private Long Date;

    public Appointment_Class() {}

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(Date);
    }

    public String getPatient_Email() { return Patient_Email; }

    public String getDoctor() {
        return Doctor;
    }
}