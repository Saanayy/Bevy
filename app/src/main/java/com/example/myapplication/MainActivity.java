package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adapters.ProjectAdapter;
import com.example.myapplication.Modals.Project;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView rvRecycler;

    ProjectAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    private List<Project> projects = new ArrayList<>();

    private TextView tvLogout;
    ImageView ivAdd;

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

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvRecycler.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new ProjectAdapter(this, projects);
        rvRecycler.hasFixedSize();
        rvRecycler.setAdapter(mAdapter);
        rvRecycler.addItemDecoration(new DividerItemDecoration(rvRecycler.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseAllViews();

        initaliseFireBaseVariables();

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

        //TODO: Fetch data here and set to recycler view

    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initaliseFireBaseVariables() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initialiseAllViews() {
        tvLogout = findViewById(R.id.main_logout);
        ivAdd = findViewById(R.id.main_add);
        rvRecycler = findViewById(R.id.main_project_recycler);
    }
}
