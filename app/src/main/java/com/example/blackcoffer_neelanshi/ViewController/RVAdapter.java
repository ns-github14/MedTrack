package com.example.blackcoffer_neelanshi.ViewController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackcoffer_neelanshi.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

class RVAdapter extends FirestoreRecyclerAdapter<Doctor_Class, RVAdapter.RVViewHolder> {

    private OnItemClickListener listener;
    //TextView name_d, location_d, specialization_d, hospital_d, gender_d, contact_d, timings_d, fees_d;

    public RVAdapter(@NonNull FirestoreRecyclerOptions<Doctor_Class> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RVViewHolder holder, int position, @NonNull Doctor_Class model){
        holder.name.setText(model.getName());
        holder.specialization.setText(model.getSpecialization());
        holder.hospital.setText(model.getHospital());

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customview);

                name_d.setText(model.getName());
                specialization_d.setText(model.getSpecialization());
                hospital_d.setText(model.getHospital());
                location_d.setText(model.getLocation());
                gender_d.setText(model.getGender());
                timings_d.setText(model.getFrom_time() + " - " + model.getTo_time());
                fees_d.setText(String.valueOf(model.getFees()));
                contact_d.setText(String.valueOf(model.getContact()));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });*/
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview, parent, false);
        return new RVAdapter.RVViewHolder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class RVViewHolder extends RecyclerView.ViewHolder {

        TextView name, specialization, hospital;

        public RVViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            specialization = itemView.findViewById(R.id.specialization);
            hospital = itemView.findViewById(R.id.hospital);
           /* name_d = itemView.findViewById(R.id.name_d);
            specialization_d = itemView.findViewById(R.id.specialization_d);
            hospital_d = itemView.findViewById(R.id.hospital_d);
            timings_d = itemView.findViewById(R.id.timings_d);
            gender_d = itemView.findViewById(R.id.gender_d);
            contact_d = itemView.findViewById(R.id.contact_d);
            location_d = itemView.findViewById(R.id.location_d);
            fees_d = itemView.findViewById(R.id.fees_d); */
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
