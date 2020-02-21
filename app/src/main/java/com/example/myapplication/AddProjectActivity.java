package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Modals.Project;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddProjectActivity extends AppCompatActivity {

    private static final String TAG = "register";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText etName, etConfDate, etAmount, etDeadLineDate;
    ImageView ivConfCal, ivdeadLineCal;
    androidx.core.widget.ContentLoadingProgressBar progress;
    Button btnAdd;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(AddProjectActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        initialiseAllViews();

        initaliseFireBaseVariables();

        ivConfCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(
                        AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String dateStr = dayOfMonth + "/" + monthOfYear + "/" + year;
                                etConfDate.setText(dateStr);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }
        });

        ivdeadLineCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(
                        AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String dateStr = dayOfMonth + "/" + monthOfYear + "/" + year;
                                etDeadLineDate.setText(dateStr);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
                String name = etName.getText().toString().trim();
                Date deadlinedate = null, confDate = null;
                String deadline = etDeadLineDate.getText().toString().trim();
                try {
                    deadlinedate = new SimpleDateFormat("dd/MM/yyyy").parse(deadline);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String amount = etAmount.getText().toString().trim();

                String conf = etConfDate.getText().toString().trim();
                try {
                    confDate = new SimpleDateFormat("dd/MM/yyyy").parse(conf);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Project project = new Project(name, confDate, Double.parseDouble(amount), deadlinedate);
                if (name.length() != 0 && deadline.length() != 0 && amount.length() != 0 && conf.length() != 0) {
                    db.collection("Projects").add(project).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddProjectActivity.this, "Added", Toast.LENGTH_SHORT).show();
                            finish();
                            btnAdd.setEnabled(true);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProjectActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnAdd.setEnabled(true);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(AddProjectActivity.this, "Data Connot be empty", Toast.LENGTH_SHORT).show();
                    btnAdd.setEnabled(true);
                    progress.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void initaliseFireBaseVariables() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initialiseAllViews() {
        etName = findViewById(R.id.addproj_name);
        etConfDate = findViewById(R.id.addproj_confdate);
        etAmount = findViewById(R.id.addproj_amount);
        etDeadLineDate = findViewById(R.id.addproj_deadlinedate);
        ivConfCal = findViewById(R.id.addproj_confdate_icon);
        ivdeadLineCal = findViewById(R.id.addproj_deadlinedate_icon);
        btnAdd = findViewById(R.id.addproj_add);
        progress = findViewById(R.id.addproj_progress);
    }

}
