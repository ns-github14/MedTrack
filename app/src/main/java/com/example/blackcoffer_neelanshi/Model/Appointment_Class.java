package com.example.blackcoffer_neelanshi.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Appointment_Class {
    private String Patient_Email, Date, Time, Status, Doctor;

    public Appointment_Class() {}

    public String getDate() { return Date; }

    public String getTime() { return Time; }

    public String getPatient_Email() { return Patient_Email; }

    public String getDoctor() {
        return Doctor;
    }
}