package com.example.blackcoffer_neelanshi.ViewController.Doctor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.blackcoffer_neelanshi.Model.Appointment_Class;
import com.example.blackcoffer_neelanshi.MySingleton;
import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.RVAdapter;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.BookAppointmentActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestFragment extends Fragment {

    public RequestFragment() {}

    RecyclerView recyclerView;
    RVAdapter adapter;
    ProgressBar progressBar;

    final String TAG = "NOTIFICATION TAG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    private String serverKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        recyclerView = view.findViewById(R.id.r);

        Query base = FirebaseFirestore.getInstance().collection("Appointments")
                .whereEqualTo("Doctor", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("Status", "Requested")
                .whereGreaterThan("Date", System.currentTimeMillis())
                .orderBy("Date", Query.Direction.ASCENDING);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Appointment_Class> options = new FirestoreRecyclerOptions.Builder<Appointment_Class>().setQuery(base, Appointment_Class.class).build();

        adapter = new RVAdapter(options);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String path = documentSnapshot.getReference().getPath();
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customdialog_status);

                progressBar = dialog.findViewById(R.id.progressBar);
                ((TextView)dialog.findViewById(R.id.heading)).setText("Accept Request?");
                ((TextView)dialog.findViewById(R.id.subtext)).setText("You're accepting this appointment request.\n\n We will move it to 'Pending section' until a payment is made.\n\n Once the payment is received, you can confirm this session.");
                ((Button) dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseFirestore.getInstance().document(path).update("Status", "Pending");
                        progressBar.setVisibility(View.GONE);

                        FirebaseFirestore.getInstance()
                                .document("Users/serverkey")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    serverKey = "key=" + task.getResult().getString("key");

                                    FirebaseFirestore.getInstance()
                                            .collection("Users")
                                            .document(documentSnapshot.getString("Patient_Email"))
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            String TOPIC = "/Messages/" + documentSnapshot.getString("TokenId"); //topic must match with what the receiver subscribed to
                                            String NOTIFICATION_TITLE = "Request Accepted!";
                                            String NOTIFICATION_MESSAGE = "You're appointment request is accepted by " + FirebaseAuth.getInstance().getCurrentUser().getEmail();                                                                JSONObject notification = new JSONObject();
                                            JSONObject notifcationBody = new JSONObject();

                                            try {
                                                notifcationBody.put("title", NOTIFICATION_TITLE);
                                                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                notification.put("to", TOPIC);
                                                notification.put("data", notifcationBody);
                                            } catch (JSONException e) {
                                                Log.e(TAG, "onCreate: " + e.getMessage());
                                            }
                                            sendNotification(notification);
                                        }
                                    });
                                }
                            }
                        });

                        startActivity(new Intent(getContext(), HomeActivity_Doc.class));
                    }
                });

                ((Button) dialog.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = documentSnapshot.getReference().getPath();
                        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Are you sure, You wanted to cancel this appointment?");
                        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseFirestore.getInstance().document(path).delete();
                            }
                        });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}