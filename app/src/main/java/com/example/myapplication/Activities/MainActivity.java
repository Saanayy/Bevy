package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ProjectAdapter;
import com.example.myapplication.Modals.Project;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Field variables
    me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar;
    private static final String TAG = "Main";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference().child("users");
    ProjectAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    private List<Project> projects = new ArrayList<>();
    private TextView tvLogout;
    private String uid;

    // Views
    private RecyclerView rvRecycler;
    private ImageView ivAdd;

    // Firebase variables
    private FirebaseAuth mAuth;

    // Activity lifecycle callbacks
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseAllViews();

        initaliseFireBaseVariables();
        progressBar.setVisibility(View.GONE);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });

        setRecylerView();

        fetchData();
        setRecylerView();
    }

    private void fetchData() {
        if (uid == null) return;
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference userProjectRef = usersRef.child(uid).child("projects");
        userProjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projects.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Project project = postSnapshot.getValue(Project.class);
                    projects.add(project);
                }
                progressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Cancelled Loading the data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvRecycler.setLayoutManager(layoutManager);
        mAdapter = new ProjectAdapter(this, projects);
        rvRecycler.hasFixedSize();
        rvRecycler.setAdapter(mAdapter);
        rvRecycler.addItemDecoration(new DividerItemDecoration(rvRecycler.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initaliseFireBaseVariables() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
    }

    private void initialiseAllViews() {
        tvLogout = findViewById(R.id.main_logout);
        ivAdd = findViewById(R.id.main_add);
        rvRecycler = findViewById(R.id.main_project_recycler);
        progressBar = findViewById(R.id.main_progress);
    }
}
