package com.example.blackcoffer_neelanshi.ViewController.Patient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.blackcoffer_neelanshi.MySingleton;
import com.example.blackcoffer_neelanshi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey;
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    String sender = getIntent().getStringExtra("sender");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FirebaseFirestore.getInstance()
                .document("Users/serverkey")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    serverKey = "key=" + task.getResult().getString("key");

                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(sender)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            TOPIC = "/Messages/" + documentSnapshot.getString("TokenId"); //topic must match with what the receiver subscribed to
                            NOTIFICATION_TITLE = getIntent().getStringExtra("title");
                            NOTIFICATION_MESSAGE = getIntent().getStringExtra("message");

                            JSONObject notification = new JSONObject();
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
                        Toast.makeText(TestActivity.this, "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
