package com.example.blackcoffer_neelanshi.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blackcoffer_neelanshi.HomeActivity;
import com.example.blackcoffer_neelanshi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Launcher extends AppCompatActivity implements View.OnClickListener{

    Button b1;
    TextView t;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        t = (TextView) findViewById(R.id.login_link);
        b1 = (Button) findViewById(R.id.signup_button);
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null) {
            Intent intent = new Intent(Launcher.this, HomeActivity.class);
            intent.putExtra("email", currentUser.getEmail());
            startActivity(intent);
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
