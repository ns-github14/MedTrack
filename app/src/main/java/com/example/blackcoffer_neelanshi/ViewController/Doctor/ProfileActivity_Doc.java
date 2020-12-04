package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.blackcoffer_neelanshi.Model.GetAddressIntentService;
import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity_Doc extends AppCompatActivity {

    private FirebaseFirestore medtrack;
    String email;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    Map<String, Object> upd = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);
        Intent intent = getIntent();
        medtrack = FirebaseFirestore.getInstance();
        email = intent.getStringExtra("email");
        Button b = findViewById(R.id.button);
        b.setOnClickListener(this::onEnable);
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
                    ((EditText) (findViewById(R.id.editTextTextEmailAddress))).setText(email);
                    ((EditText) (findViewById(R.id.editTextPhone))).setText(doc.getLong("Contact").toString());
                    ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setText(doc.getString("Location"));
                    ((EditText) (findViewById(R.id.editTextNumber))).setText(doc.getString("Hospital"));
                    ((EditText) (findViewById(R.id.editTextTextPersonName2))).setText(doc.getString("Gender"));
                    ((EditText) (findViewById(R.id.editTextNumber2))).setText(doc.getString("Specialization"));
                    ((EditText) (findViewById(R.id.editTextNumber3))).setText(doc.getString("From (Time)"));
                    ((EditText) (findViewById(R.id.editTextTextPersonName3))).setText(doc.getString("To (Time)"));
                    ((EditText) (findViewById(R.id.editTextTextMultiLine))).setText(doc.getLong("Fees").toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity_Doc.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };
        startLocationUpdates();
    }

        @SuppressWarnings("MissingPermission")
        private void startLocationUpdates() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
            else {
                LocationRequest locationRequest = new LocationRequest();
                //locationRequest.setInterval(2000);
                //locationRequest.setFastestInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }
        @SuppressWarnings("MissingPermission")
        private void getAddress() {
            if (!Geocoder.isPresent()) {
                Toast.makeText(ProfileActivity_Doc.this, "Can't find current address, ",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, GetAddressIntentService.class);
            intent.putExtra("add_receiver", addressResultReceiver);
            intent.putExtra("add_location", currentLocation);
            startService(intent);
        }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
        int[] grantResults) {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                else {
                    Toast.makeText(this, "Location permission not granted, " + "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private class LocationAddressResultReceiver extends ResultReceiver {
            LocationAddressResultReceiver(Handler handler) {
                super(handler);
            }
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 0) {
                    Log.d("Address", "Location null retrying");
                    getAddress();
                }
                if (resultCode == 1) {
                    Toast.makeText(ProfileActivity_Doc.this, "Address not found, ", Toast.LENGTH_SHORT).show();
                }
                String currentAdd = resultData.getString("address_result");
                showResults(currentAdd);
            }
        }
        private void showResults(String currentAdd) {
            if(((EditText) (findViewById(R.id.editTextTextPostalAddress))).getText().toString() != currentAdd) {
                Toast.makeText(ProfileActivity_Doc.this, "Different Location", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity_Doc.this);
                builder.setTitle("Different Location Detected");
                builder.setMessage("Your Location is: " + currentAdd);
                builder.setPositiveButton("Update Location",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //write to database
                                ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setText(currentAdd);
                                medtrack.collection("Doctors").document(email)
                                        .update("Location", currentAdd);
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            }
        }

    public void onEnable(View v){
        boolean bool;
        String btn_text;
        int color;
        if(!((EditText)findViewById(R.id.editTextTextPersonName)).isFocusable()) {
            btn_text = "Save Changes";
            color = R.color.colorAccent;
            bool = true;
            ((Button)findViewById(R.id.button)).setText(btn_text);
            findViewById(R.id.button).setBackgroundResource(color);

            (findViewById(R.id.editTextTextPersonName)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName))).setCursorVisible(bool);

            (findViewById(R.id.editTextTextPersonName2)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName2)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName2))).setCursorVisible(bool);

            (findViewById(R.id.editTextTextPersonName3)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName3)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName3))).setCursorVisible(bool);

            (findViewById(R.id.editTextTextPostalAddress)).setFocusable(bool);
            (findViewById(R.id.editTextTextPostalAddress)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setCursorVisible(bool);

            (findViewById(R.id.editTextPhone)).setFocusable(bool);
            (findViewById(R.id.editTextPhone)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextPhone))).setCursorVisible(bool);

            (findViewById(R.id.editTextNumber)).setFocusable(bool);
            (findViewById(R.id.editTextNumber)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber))).setCursorVisible(bool);

            (findViewById(R.id.editTextNumber2)).setFocusable(bool);
            (findViewById(R.id.editTextNumber2)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber2))).setCursorVisible(bool);

            (findViewById(R.id.editTextNumber3)).setFocusable(bool);
            (findViewById(R.id.editTextNumber3)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber3))).setCursorVisible(bool);

            (findViewById(R.id.editTextTextMultiLine)).setFocusable(bool);
            (findViewById(R.id.editTextTextMultiLine)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextMultiLine))).setCursorVisible(bool);
        }
        else{
            btn_text = "Edit Profile";
            color = R.color.Cyan;
            bool = false;
            ((Button)findViewById(R.id.button)).setText(btn_text);
            findViewById(R.id.button).setBackgroundResource(color);

            upd.put("Name", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextTextPersonName)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName))).setCursorVisible(bool);

            upd.put("Gender", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextTextPersonName2)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName2)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName2))).setCursorVisible(bool);

            upd.put("To (Time)", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextTextPersonName3)).setFocusable(bool);
            (findViewById(R.id.editTextTextPersonName3)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPersonName3))).setCursorVisible(bool);

            upd.put("Location", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextTextPostalAddress)).setFocusable(bool);
            (findViewById(R.id.editTextTextPostalAddress)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextPostalAddress))).setCursorVisible(bool);

            upd.put("Contact", (Integer.parseInt(((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString())));
            (findViewById(R.id.editTextPhone)).setFocusable(bool);
            (findViewById(R.id.editTextPhone)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextPhone))).setCursorVisible(bool);

            upd.put("Hospital", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextNumber)).setFocusable(bool);
            (findViewById(R.id.editTextNumber)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber))).setCursorVisible(bool);

            upd.put("Specialization", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextNumber2)).setFocusable(bool);
            (findViewById(R.id.editTextNumber2)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber2))).setCursorVisible(bool);

            upd.put("From (Time)", ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString());
            (findViewById(R.id.editTextNumber3)).setFocusable(bool);
            (findViewById(R.id.editTextNumber3)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextNumber3))).setCursorVisible(bool);

            upd.put("Fees", (Integer.parseInt(((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString())));
            (findViewById(R.id.editTextTextMultiLine)).setFocusable(bool);
            (findViewById(R.id.editTextTextMultiLine)).setFocusableInTouchMode(bool);
            ((EditText) (findViewById(R.id.editTextTextMultiLine))).setCursorVisible(bool);

            medtrack.collection("Doctors").document(email).update(upd);
        }
    }
}
