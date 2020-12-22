package com.example.blackcoffer_neelanshi.ViewController.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.AddActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.MedSchedFragment;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.PillBoxActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm.ScheduleActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.AppFragment;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.BookAppointmentActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    String email;
    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    ImageView imageView;
    View header;

    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_patient);

        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                        i.putExtra("email", email);
                        i.putExtra("Location", documentSnapshot.getString("Location"));
                        startActivity(i);
                    }
                });
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else if(id == R.id.nav_app) {
                    FirebaseFirestore.getInstance().collection("Patients")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent i = new Intent(getApplicationContext(), BookAppointmentActivity.class);
                            i.putExtra("Location", documentSnapshot.getString("Location"));
                            startActivity(i);
                        }
                    });
                } else if(id == R.id.nav_add_med) {
                    startActivity(new Intent(getApplicationContext(), AddActivity.class));
                } else if(id == R.id.nav_pillbox) {
                    startActivity(new Intent(getApplicationContext(), PillBoxActivity.class));
                } else if(id == R.id.nav_week) {
                    startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                } else if(id == R.id.nav_logout) {
                    
                    FirebaseFirestore.getInstance().collection("Messages")
                            .document(FirebaseInstanceId.getInstance().getToken()).delete();
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(auth.getCurrentUser().getEmail()).update("TokenId", "");
                    auth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        openFragment(new AppFragment());
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.home:
                        openFragment(new AppFragment());
                        return true;

                    case R.id.setting:
                        openFragment(new MedSchedFragment());
                        return true;
                }
                return false;
            }
        });
    }

    void openFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
