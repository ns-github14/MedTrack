package com.example.blackcoffer_neelanshi.ViewController.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.ViewController.Doctor.HomeActivity_Doc;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Launcher extends AppCompatActivity implements View.OnClickListener{

    Button b1;
    TextView t;
    String type;
    private FirebaseAuth auth;
    private FirebaseFirestore medtrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        t = (TextView) findViewById(R.id.login_link);
        b1 = (Button) findViewById(R.id.signup_button);
        auth = FirebaseAuth.getInstance();
        medtrack = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null) {
            medtrack.collection("Users").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        type = doc.getString("Type");
                        Toast.makeText(Launcher.this, type, Toast.LENGTH_LONG).show();
                        if(type.equals("Patient")) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                        if(type.equals("Doctor")) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity_Doc.class);
                            startActivity(intent);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    public void onClick(View v){
        if(v.getId()==R.id.signup_button){
            Intent intent = new Intent(Launcher.this, SignupActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.login_link){
            Intent intent = new Intent(Launcher.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}
