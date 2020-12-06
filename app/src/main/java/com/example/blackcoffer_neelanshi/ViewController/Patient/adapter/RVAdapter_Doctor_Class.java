package com.example.blackcoffer_neelanshi.ViewController.Patient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Patient.Appointment.Doctor_Class;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RVAdapter_Doctor_Class extends FirestoreRecyclerAdapter<Doctor_Class, RVAdapter_Doctor_Class.RVViewHolder> {

    private OnItemClickListener listener;

    public RVAdapter_Doctor_Class(@NonNull FirestoreRecyclerOptions<Doctor_Class> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RVViewHolder holder, int position, @NonNull Doctor_Class model){
        holder.name.setText(model.getName());
        holder.specialization.setText(model.getSpecialization());
        holder.hospital.setText(model.getHospital());
        holder.location.setText(model.getLocation());
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_doctor, parent, false);
        return new RVAdapter_Doctor_Class.RVViewHolder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class RVViewHolder extends RecyclerView.ViewHolder {

        TextView name, specialization, hospital, location;

        public RVViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            specialization = itemView.findViewById(R.id.specialization);
            hospital = itemView.findViewById(R.id.hospital);
            location = itemView.findViewById(R.id.location);
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
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}