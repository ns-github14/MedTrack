package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.Launcher;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity_Doc extends AppCompatActivity{

    ConstraintLayout ll1, ll2;
    Button b1, b2, b3;
    private FirebaseAuth auth;
    private FirebaseFirestore medtrack;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_doctor);

        auth = FirebaseAuth.getInstance();
        medtrack = FirebaseFirestore.getInstance();
        email = auth.getCurrentUser().getEmail();

        b1 = findViewById(R.id.status);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity_Doc.class);
            intent.putExtra("email", email);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStatus(View v) {
        if(b1.getText() == "Pending") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Appointment Status");
            builder.setMessage("Confirm this appointment?");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            b1.setBackgroundResource(R.color.Cyan);
                            b1.setText("Confirmed");
                        }
                    });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(),
                            android.R.string.no, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Appointment Status");
            builder.setMessage("Cancel this appointment?");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            b1.setBackgroundResource(R.color.dark_gray);
                            b1.setText("Pending");
                        }
                    });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(),
                            android.R.string.no, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }
}