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

import com.example.myapplication.Modals.ProjectStatus;
import com.example.myapplication.Modals.Task;
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

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    Context context;
    List<Task> tasks;
    String projectId, statusID;
    private String TAG = "TaskAdapter";

    public TaskAdapter(Context context, List<Task> tasks, String projectId, String statusID) {
        this.context = context;
        this.tasks = tasks;
        this.projectId = projectId;
        this.statusID = statusID;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.tvName.setText(task.getName());
        holder.tvDate.setText(task.getDate());
        holder.cbCheck.setChecked(task.isCheck());

        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.cbCheck.isChecked();
                showDialog(holder, isChecked, task.getTaskKey());
            }
        });
//        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                showDialog(isChecked, task.getTaskKey());
//            }
//        });
    }

    private void showDialog(final TaskViewHolder holder, final boolean isChecked, final String taskKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (isChecked) {
            builder.setMessage("Are you sure you want to mark this task complete?");
        } else {
            builder.setMessage("Are you sure you want to mark this task incomplete?");
        }
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        changeCompletionPercent(isChecked, taskKey);
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        holder.cbCheck.setChecked(!isChecked);
                        dialog.dismiss();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void changeCompletionPercent(final boolean isChecked, final String taskKey) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();

        final DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("projects")
                .child(projectId).child("projectstatus").child(statusID);

        DatabaseReference taskRef = statusRef.child("tasks").child(taskKey);
        taskRef.child("check" +
                "").setValue(isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ProjectStatus status = dataSnapshot.getValue(ProjectStatus.class);
                                int completeTaskCount = status.getCompleteTaskCount();
                                int totalTask = status.getTaskCount();
                                int completionPercent = status.getCompletionPercentage();

                                if (isChecked) {
                                    completeTaskCount += 1;
                                } else {
                                    completeTaskCount -= 1;
                                }
                                completionPercent = (int) ((completeTaskCount / (totalTask * 1.0)) * 100);
                                final int finalCompletionPercent = completionPercent;
                                statusRef.child("completeTaskCount").setValue(completeTaskCount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                statusRef.child("completionPercentage").setValue(finalCompletionPercent)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(context, "completion percent updated", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void updateCompletedStatistics(final int taskCompleted) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        final DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("projects")
                .child(projectId).child("projectstatus").child(statusID);


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

class TaskViewHolder extends RecyclerView.ViewHolder {
    TextView tvName, tvDate;
    CheckBox cbCheck;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.single_task_name);
        tvDate = itemView.findViewById(R.id.single_task_date);
        cbCheck = itemView.findViewById(R.id.single_task_check);
    }
}