package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.TaskAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ApplicationDetailActivity extends AppCompatActivity {

    //Field Variables
    private final String TAG = "ApplicationDetail";
    List<Task> tasks = new ArrayList<>();
    String uid, projectID, statusID;

    // Views
    ImageView ivNewTask;
    RecyclerView rvTasks;
    RecyclerView.LayoutManager layoutManager;
    TaskAdapter taskAdapter;

    //Firebase variables
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);
        projectID = getIntent().getStringExtra("projectid");
        statusID = getIntent().getStringExtra("statusid");
        initialiseAllViews();
        initialiseAllFirebaseVariables();

//        Log.d(TAG, "onCreate: "+projectID + " "+ statusID);
        fetchData();
        setRecylerView();

        ivNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void addTask() {
        DatabaseReference taskRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus").child(statusID).child("tasks");
        String key = taskRef.push().getKey();
        // Todo: create dynamic task here
        Task task = new Task("Task1", "20/10/2010", true, key);
        taskRef.child(key).setValue(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ApplicationDetailActivity.this, "Task Addition successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApplicationDetailActivity.this, "Task Addition failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchData() {
        DatabaseReference taskRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus").child(statusID).child("tasks");
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ApplicationDetailActivity.this, "Fetching tasks cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvTasks.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter(this, tasks);
        rvTasks.hasFixedSize();
        rvTasks.setAdapter(taskAdapter);
        rvTasks.addItemDecoration(new DividerItemDecoration(rvTasks.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initialiseAllFirebaseVariables() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

    }


    private void initialiseAllViews() {
        ivNewTask = findViewById(R.id.application_detail_add);
        rvTasks = findViewById(R.id.application_detail_list);

    }
}
