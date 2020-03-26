package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.tvName.setText(task.getName());
        holder.tvDate.setText(task.getDate());
        holder.cbCheck.setChecked(task.isCheck());

        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDialog(isChecked, task.getTaskKey());
            }
        });
    }

    private void showDialog(final boolean isChecked, final String taskKey) {
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
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ProjectStatus projectStatus = dataSnapshot.getValue(ProjectStatus.class);
                int taskCompleted = projectStatus.getCompleteTaskCount();
                if (isChecked) {
                    taskCompleted = taskCompleted + 1;
                } else {
                    taskCompleted = taskCompleted - 1;
                }
                projectStatus.setCompleteTaskCount(taskCompleted);
                final int finalTaskCompleted = taskCompleted;
                statusRef.child("completeTaskCount").setValue(taskCompleted)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Completed task count updated", Toast.LENGTH_SHORT).show();
                                statusRef.child("tasks").child(taskKey).child("check").setValue(isChecked)
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

                                updateCompletedStatistics(finalTaskCompleted);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to mark task completion Status", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Cancelled detching the project", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCompletedStatistics(final int taskCompleted) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        final DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("projects")
                .child(projectId).child("projectstatus").child(statusID);
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProjectStatus projectStatus = dataSnapshot.getValue(ProjectStatus.class);
                int taskCount = projectStatus.getTaskCount();
                int completionPercentage = projectStatus.getCompletionPercentage();
                completionPercentage = (int) ((taskCompleted / (taskCount * 1.0)) * 100);
                statusRef.child("completeTaskCount").setValue(taskCount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Completed Tast count incremented", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Completed Tast Count increment failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                statusRef.child("completionPercentage").setValue(completionPercentage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Completion percentage set", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Completion percentage set failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Setting completion statistics failed", Toast.LENGTH_SHORT).show();
            }
        });

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