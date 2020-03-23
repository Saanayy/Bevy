package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ProjectDetailActivity extends AppCompatActivity {

    //Field Variables
    String projectId;

    // Views
    Button btnPaymentStatus, btnProjectStatus, btnUpdateDetails, btnNotes;

    // Todo: Ovveride activity lifecycle callbacks here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectid");
        initialiseAllViews();

        btnPaymentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, PaymentStatus.class);
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


    private void initialiseAllViews() {
        btnPaymentStatus = findViewById(R.id.project_detail_paymentStatus);
        btnProjectStatus = findViewById(R.id.project_detail_projectStatus);
        btnUpdateDetails = findViewById(R.id.project_detail_update_detail);
        btnNotes = findViewById(R.id.project_detail_notes);
    }


}
