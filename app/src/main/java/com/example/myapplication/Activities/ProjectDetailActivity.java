package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Modals.Project;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProjectDetailActivity extends AppCompatActivity {

    //Field Variables
    String projectId, uid;
    Project project;

    // Views
    Button btnPaymentStatus, btnProjectStatus, btnUpdateDetails, btnNotes;
    TextView tvconfirmationDate, tvDeadlienDate, tvAmount, tvCompletionPercent;
    me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar;

    //Firebase variables
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;

    // Todo: Ovveride activity lifecycle callbacks here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectid");

        initialiseAllViews();

        initialiseAllFirebaseVariables();
        progressBar.setVisibility(View.GONE);
        fetchProjectAndSetDefaultData();


        btnPaymentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, PaymentStatusActivity.class);
                intent.putExtra("projectid", projectId);
                startActivity(intent);
            }
        });

        btnProjectStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, ProjectStatusActivity.class);
                intent.putExtra("projectid", projectId);
                startActivity(intent);
            }
        });

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, NotesActivity.class);
                intent.putExtra("projectid", projectId);
                startActivity(intent);
            }
        });
    }

    private void fetchProjectAndSetDefaultData() {
        progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference projectRef = databaseReference.child("users").child(uid).child("projects").child(projectId);
        projectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                project = dataSnapshot.getValue(Project.class);
                setDefaultValues(project);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProjectDetailActivity.this, "Project fetch cancelled", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setDefaultValues(Project project) {
        tvconfirmationDate.setText(project.getConfirmationDate().toString());
        tvDeadlienDate.setText(project.getDeadline().toString());
        tvAmount.setText(project.getAmount() + "");
        progressBar.setVisibility(View.GONE);

    }

    private void initialiseAllFirebaseVariables() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

    }

    private void initialiseAllViews() {
        btnPaymentStatus = findViewById(R.id.project_detail_paymentStatus);
        btnProjectStatus = findViewById(R.id.project_detail_projectStatus);
        btnUpdateDetails = findViewById(R.id.project_detail_update_detail);
        btnNotes = findViewById(R.id.project_detail_notes);
        tvconfirmationDate = findViewById(R.id.project_detail_confirmationdate);
        tvDeadlienDate = findViewById(R.id.project_detail_deadlinedate);
        tvAmount = findViewById(R.id.project_detail_amount);
        tvCompletionPercent = findViewById(R.id.project_detail_completionPercent);
        progressBar = findViewById(R.id.project_detail_progress);

    }


}
