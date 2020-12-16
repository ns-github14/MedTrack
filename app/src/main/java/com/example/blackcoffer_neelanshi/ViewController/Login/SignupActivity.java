package com.example.blackcoffer_neelanshi.ViewController.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.ViewController.Doctor.HomeActivity_Doc;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    Button b1;
    RadioButton r;
    EditText e1,e2;
    TextView t1,t2;
    private FirebaseAuth auth;
    private FirebaseFirestore medtrack;
    String email, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        e1 = (EditText) findViewById(R.id.email_text);
        e2 = (EditText) findViewById(R.id.password_text);
        t1 = (TextView) findViewById(R.id.login_link);
        b1 = (Button) findViewById(R.id.signup_button);
        r = (RadioButton) findViewById(R.id.privacy_policy);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        medtrack = FirebaseFirestore.getInstance();
    }

    public void onChecked(View v){
        if(v.getId()==R.id.doctor) {
            ((RadioButton)findViewById(R.id.patient)).setChecked(false);
        }
        if(v.getId()==R.id.patient) {
            ((RadioButton)findViewById(R.id.doctor)).setChecked(false);
        }
    }

    private void registerUser(){

        //getting email and password from edit texts
        email = e1.getText().toString().trim();
        password  = e2.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            e1.setError("required");
            return;
        }

        if(TextUtils.isEmpty(password)){
            e2.setError("required");
            return;
        }

        if(!r.isChecked()){
            Toast.makeText(this,"Please agree to Privacy Policy.",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering" + "\n" + "Please Wait...");
        progressDialog.show();
    
        Map<String, Object> doc = new HashMap<>();
        doc.put("Name", "");
        doc.put("Contact", 0);
        doc.put("Fees", 0);
        doc.put("From_time", "");
        doc.put("To_time", "");
        doc.put("Gender", "");
        doc.put("Hospital", "");
        doc.put("Location", "");
        doc.put("Specialization", "");

        Map<String, String> user = new HashMap<String, String>();

        Map<String, Object> pat = new HashMap<>();
        pat.put("Name", "");
        pat.put("Age", 18);
        pat.put("Caretaker_Contact", 0000000000);
        pat.put("Gender", "");
        pat.put("Contact", 0000000000);
        pat.put("Location", "");
        pat.put("Height", 0);
        pat.put("Weight", 0);
        pat.put("Caretaker", "");
        pat.put("Medical_History", "");

        //creating a new user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //checking if success
                if(task.isSuccessful()){
                    //enter in Starting Gradle Daemon...database
                    if(((RadioButton)findViewById(R.id.doctor)).isChecked()) {
                        try {
                            medtrack.collection("Doctors").document(email).set(doc);
                            user.put("Type", "Doctor");
                            user.put("TokenId", FirebaseInstanceId.getInstance().getToken());
                            medtrack.collection("Users").document(email).set(user);
                            startActivity(new Intent(getApplicationContext(), HomeActivity_Doc.class));
                        }
                        catch(Exception e){
                            Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        try {
                            medtrack.collection("Patients").document(email).set(pat);
                            user.put("Type", "Patient");
                            medtrack.collection("Users").document(email).set(user);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        catch(Exception e){
                            Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    //display some message here
                    Toast.makeText(SignupActivity.this,"Registration Error"+task.getException(),Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    public void onClick(View view) {
        if(view.getId()==R.id.signup_button)
            //calling register method on click
            registerUser();
        else{
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}