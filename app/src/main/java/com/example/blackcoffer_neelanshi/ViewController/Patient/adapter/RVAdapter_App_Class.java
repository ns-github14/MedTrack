package com.example.blackcoffer_neelanshi.ViewController.Patient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.Model.Appointment_Class;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RVAdapter_App_Class extends FirestoreRecyclerAdapter<Appointment_Class, RVAdapter_App_Class.RVViewHolder> {

    private RVAdapter_App_Class.OnItemClickListener listener;

    public RVAdapter_App_Class(@NonNull FirestoreRecyclerOptions<Appointment_Class> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RVAdapter_App_Class.RVViewHolder holder, int position, @NonNull Appointment_Class model){
        FirebaseFirestore.getInstance().collection("Doctors").document(model.getDoctor()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    String name = d.getString("Name");
                    holder.name.setText(name);
                }
            }
        });

        holder.email.setText(model.getDoctor());
        holder.date.setText(model.getDate());
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public RVAdapter_App_Class.RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_patient, parent, false);
        return new RVAdapter_App_Class.RVViewHolder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class RVViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, date, time;
        Button status;

        public RVViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(RVAdapter_App_Class.OnItemClickListener listener) {
        this.listener = listener;
    }
}
