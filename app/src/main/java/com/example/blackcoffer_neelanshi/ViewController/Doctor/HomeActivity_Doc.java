package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.ViewPagerAdapter;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.adapter.TabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity_Doc extends AppCompatActivity {

    Button b1;
    private FirebaseAuth auth;
    private FirebaseFirestore medtrack;
    String email;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_doctor);

        auth = FirebaseAuth.getInstance();
        medtrack = FirebaseFirestore.getInstance();
        email = auth.getCurrentUser().getEmail();

        viewPager = findViewById(R.id.viewPager);
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> tab1 = setTabTitleAndFragment("Requests", new RequestFragment());
        Map<String, Object> tab2 = setTabTitleAndFragment("Pending", new PendingFragment());
        Map<String, Object> tab3 = setTabTitleAndFragment("Confirmed", new ConfirmedFragment());

        mapList.add(tab1);
        mapList.add(tab2);
        mapList.add(tab3);

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, mapList);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(viewPagerAdapter.getTitle(position));
            }
        }).attach();
    }

    Map<String, Object> setTabTitleAndFragment(String title, Fragment fragment) {

        Map<String, Object> fragmentwithTitleMap = new HashMap<>();
        fragmentwithTitleMap.put("fragmentTitle", title);
        fragmentwithTitleMap.put("fragment", fragment);
        return fragmentwithTitleMap;
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


    /*public void onStatus(View v) {
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
    }*/
}