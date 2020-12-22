package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.ViewPagerAdapter;
import com.example.blackcoffer_neelanshi.ViewController.Login.LoginActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.HomeActivity;
import com.example.blackcoffer_neelanshi.ViewController.Patient.ProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class HomeActivity_Doc extends AppCompatActivity {

    private FirebaseAuth auth;
    String email;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_doctor);

        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        getMenuInflater().inflate(R.menu.menu_main_doc, menu);
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
            FirebaseFirestore.getInstance().collection("Doctors")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity_Doc.class);
                    i.putExtra("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    i.putExtra("Location", documentSnapshot.getString("Location"));
                    startActivity(i);
                }
            });
            return true;
        }

        if(id == R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}