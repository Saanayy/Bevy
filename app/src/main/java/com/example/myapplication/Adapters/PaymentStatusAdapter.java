package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.PaymentStatus;
import com.example.myapplication.Modals.Project;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PaymentStatusAdapter extends RecyclerView.Adapter<PaymentStatusViewHolder> {
    Context context;
    List<PaymentStatus> statuses;
    String projectId;

    public PaymentStatusAdapter(Context context, List<PaymentStatus> statuses, String projectID) {
        this.context = context;
        this.statuses = statuses;
        this.projectId = projectID;
    }

    @NonNull
    @Override
    public PaymentStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_payment_status_layout, parent, false);
        return new PaymentStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentStatusViewHolder holder, int position) {
        final PaymentStatus paymentStatus = statuses.get(position);
        holder.tvAmount.setText(paymentStatus.getAmount());
        holder.tvName.setText(paymentStatus.getName());
        holder.tvDate.setText(paymentStatus.getDate());
        holder.cbPaid.setChecked(paymentStatus.isCheck());

        holder.cbPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.cbPaid.isChecked();
                showDialog(holder, isChecked, paymentStatus.getPaymentStatusKey(), paymentStatus.getAmount());
            }
        });

//        holder.cbPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                showDialog(isChecked, paymentStatus.getPaymentStatusKey(), paymentStatus.getAmount());
//            }
//        });

    }

    private void
    showDialog(final PaymentStatusViewHolder holder, final boolean isChecked, final String paymentStatusKey, final String amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (isChecked) {
            builder.setMessage("Are you sure you want to add this amount?");
        } else {
            builder.setMessage("Are you sure you want to deduct this amount?");
        }

        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        changeCollectedAmount(isChecked, paymentStatusKey, amount);
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        holder.cbPaid.setChecked(!isChecked);
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void changeCollectedAmount(final boolean isChecked, final String paymentStatusKey, final String amount) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        final DatabaseReference projectRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("projects")
                .child(projectId);
        projectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Project project = dataSnapshot.getValue(Project.class);
                Double collectedAmount = project.getCollectedAmount();
                if (isChecked) {
                    collectedAmount = collectedAmount + Double.parseDouble(amount);
                } else {
                    collectedAmount = collectedAmount - Double.parseDouble(amount);
                }
                project.setCollectedAmount(collectedAmount);
                projectRef.child("collectedAmount").setValue(collectedAmount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Collected amount updated", Toast.LENGTH_SHORT).show();
                                projectRef.child("paymentstatus").child(paymentStatusKey).child("check").setValue(isChecked)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "check updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed check update", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to update collected amount", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Cancelled detching the project", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }
}

class PaymentStatusViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvDate, tvAmount;
    CheckBox cbPaid;

    public PaymentStatusViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.single_payment_name);
        tvAmount = itemView.findViewById(R.id.single_payment_amount);
        tvDate = itemView.findViewById(R.id.single_payment_date);
        cbPaid = itemView.findViewById(R.id.single_payment_check);
    }
}