package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity_Doc extends AppCompatActivity {

    private FirebaseFirestore medtrack;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);
        Intent intent = getIntent();
        medtrack = FirebaseFirestore.getInstance();
        email = intent.getStringExtra("email");
    }

    @Override
    public void onStart(){
        super.onStart();
        medtrack.collection("Doctors").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    ((EditText) (findViewById(R.id.editTextTextPersonName))).setText(doc.getString("Name"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity_Doc.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onEnable(View v){
        boolean bool;
        String btn_text;
        int color;
        if(((Button)findViewById(R.id.button)).getText() == "Edit Profile") {
            btn_text = "Save Changes";
            color = R.color.Cyan;
            bool = true;
        }
        else{
            btn_text = "Edit Profile";
            color = R.color.colorAccent;
            bool = false;
        }
        ((Button)findViewById(R.id.button)).setText(btn_text);
        ((Button)findViewById(R.id.button)).setBackgroundResource(color);

        (findViewById(R.id.editTextTextPersonName)).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextPersonName))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextTextPersonName2))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextPersonName2))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextTextPersonName3))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextPersonName3))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextTextEmailAddress))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextEmailAddress))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextPhone))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextPhone))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextNumber))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextNumber))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextNumber2))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextNumber2))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextNumber3))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextNumber3))).setCursorVisible(bool);

        ((EditText) (findViewById(R.id.editTextTextMultiLine))).setFocusable(bool);
        ((EditText) (findViewById(R.id.editTextTextMultiLine))).setCursorVisible(bool);
    }
}
