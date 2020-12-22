package com.example.blackcoffer_neelanshi.ViewController.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.Model.DbHelper;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.HomeActivity_Doc;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button b1;
    private TextView t;
    private EditText e1,e2;
    private RadioButton r;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    String email, password, type;
    private FirebaseFirestore medtrack;
    HashMap<String, String> m = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e1 = (EditText) findViewById(R.id.email_text);
        e2 = (EditText) findViewById(R.id.password_text);
        t = (TextView) findViewById(R.id.signup_link);
        b1 = (Button) findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
    }

    private void loginUserAccount(){
        progressBar.setVisibility(View.VISIBLE);
        email = e1.getText().toString();
        password = e2.getText().toString();

        if(TextUtils.isEmpty(email)){
            e1.setError("required");
            return;
        }

        if(TextUtils.isEmpty(password)){
            e2.setError("required");
            return;
        }

        Map<String, String> m = new HashMap<>();
        m.put("title", "");
        m.put("message", "");

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            medtrack.collection("Users").document(email).update("TokenId", token);
                            medtrack.collection("Messages").document(token).set(m);
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    onSuccessfulLogin();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onSuccessfulLogin(){
        medtrack = FirebaseFirestore.getInstance();
        medtrack.collection("Users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    type = doc.getString("Type");
                    if(type.equals("Patient")) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    if(type.equals("Doctor")) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity_Doc.class);
                        startActivity(intent);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View v){
        if(v.getId()==R.id.signup_link){
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        }
        else
            loginUserAccount();
    }
}
