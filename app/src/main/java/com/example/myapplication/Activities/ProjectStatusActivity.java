package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ProjectStatusAdapter;
import com.example.myapplication.Modals.ProjectStatus;
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

public class ProjectStatusActivity extends AppCompatActivity {

    // Field variables
    private final String TAG = "ProjectStatus";
    String uid, projectID;
    ProjectStatusAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<ProjectStatus> statuses = new ArrayList<>();

    // Views
    ImageView ivAdd;
    RecyclerView rvProjectStatusList;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_status);
        Intent intent = getIntent();
        projectID = intent.getStringExtra("projectid");
        initialiseAllViews();
        initialiseAllFirebase();

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProjectStatus();
            }
        });
        fetchData();
        setRecylerView();
    }

    private void addProjectStatus() {
        DatabaseReference projectStatusRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus");
        String key = projectStatusRef.push().getKey();
        //Todo: create projectstatus dynamic here
        ProjectStatus projectStatus = new ProjectStatus("android", "20/10/1290", 80, key);
        projectStatusRef.child(key).setValue(projectStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProjectStatusActivity.this, "Added status successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProjectStatusActivity.this, "Adding status failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchData() {
        final DatabaseReference projectStatusRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus");
        projectStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statuses.clear();
                for (DataSnapshot projectSnapshot : dataSnapshot.getChildren()) {
                    ProjectStatus projectStatus = projectSnapshot.getValue(ProjectStatus.class);
                    statuses.add(projectStatus);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProjectStatusActivity.this, "Fetching data cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvProjectStatusList.setLayoutManager(layoutManager);
        mAdapter = new ProjectStatusAdapter(this, statuses, projectID);
        rvProjectStatusList.hasFixedSize();
        rvProjectStatusList.setAdapter(mAdapter);
        rvProjectStatusList.addItemDecoration(new DividerItemDecoration(rvProjectStatusList.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initialiseAllFirebase() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        uid = mAuth.getUid();
    }

    private void initialiseAllViews() {
        ivAdd = findViewById(R.id.project_status_add);
        rvProjectStatusList = findViewById(R.id.project_status_list);
    }


}
