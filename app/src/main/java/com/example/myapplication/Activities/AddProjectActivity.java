package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Modals.Project;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddProjectActivity extends AppCompatActivity {

    // Field variables
    private static final String TAG = "register";
    String result = "";

    // Views
    EditText etName, etConfDate, etAmount, etDeadLineDate;
    ImageView ivConfCal, ivdeadLineCal;
    Button btnAdd;
    // Firebase Variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean[] checkBoxes = new Boolean[8];
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
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


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_android:
                if (checked)
                    checkBoxes[0] = true;
                else
                    checkBoxes[0] = false;
                break;
            case R.id.checkbox_website:
                if (checked)
                    checkBoxes[1] = true;
                else
                    checkBoxes[1] = false;
                break;
            case R.id.checkbox_admin:
                if (checked)
                    checkBoxes[2] = true;
                else
                    checkBoxes[2] = false;
                break;
            case R.id.checkbox_maintainance:
                if (checked)
                    checkBoxes[3] = true;
                else
                    checkBoxes[3] = false;
                break;
            case R.id.checkbox_deployment:
                if (checked)
                    checkBoxes[4] = true;
                else
                    checkBoxes[4] = false;
                break;
            case R.id.checkbox_logo:
                if (checked)
                    checkBoxes[5] = true;
                else
                    checkBoxes[5] = false;
                break;
            case R.id.checkbox_uiux:
                if (checked)
                    checkBoxes[6] = true;
                else
                    checkBoxes[6] = false;
                break;
            case R.id.checkbox_cms:
                if (checked)
                    checkBoxes[7] = true;
                else
                    checkBoxes[7] = false;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        initialiseAllViews();

        initaliseFireBaseVariables();

        initialiseBooleanArray(false);

        ivConfCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFromListner(etConfDate);
            }
        });

        ivdeadLineCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFromListner(etDeadLineDate);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setEnabled(false);
                // Fetch all the data from the views
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

                // Go ahead with the next button
                convertBooleanToString();
                addProjectToDatabase(name, deadline, amount, conf, confDate, deadlinedate, result);
                Log.d(TAG, "onClick: " + result);

            }
        });

    }

    private void convertBooleanToString() {
        for (int i = 0; i < 8; i++) {
            result = result + ((checkBoxes[i]) ? "true," : "false,");
        }

    }

    private void initialiseBooleanArray(boolean b) {
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = b;
        }
    }

    private void addProjectToDatabase(String name, String deadline, String amount, String conf, Date confDate, Date deadlinedate, String result) {
        if (name.length() != 0 && deadline.length() != 0 && amount.length() != 0 && conf.length() != 0) {
            // Fetch the unique key of the project and the user id of the user.
            // Push two instances of the project, one to the users node and one
            // to the projects node.
            String uid = mAuth.getUid();
            DatabaseReference userProjectRef = databaseReference.child("users").child(uid).child("projects");
            String userProjectKey = userProjectRef.push().getKey();

            final DatabaseReference projectRef = databaseReference.child("projects").child(userProjectKey);
            // create an object from the data.
            final Project project = new Project(userProjectKey, name, confDate, Double.parseDouble(amount), deadlinedate, result);
            // Add the project to the user node.
            userProjectRef.child(userProjectKey).setValue(project)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddProjectActivity.this, "Project Added", Toast.LENGTH_SHORT).show();
                            btnAdd.setEnabled(true);
                            // add the project to the projects node only when the project addition to the user is successfull.
                            projectRef.setValue(project)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddProjectActivity.this, "Project Added for Normalisation", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddProjectActivity.this, "Project addition for normalisation Failed ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProjectActivity.this, "Project adding failed", Toast.LENGTH_SHORT).show();
                            btnAdd.setEnabled(true);
                        }
                    });

        } else {
            Toast.makeText(AddProjectActivity.this, "Data Connot be empty", Toast.LENGTH_SHORT).show();
            btnAdd.setEnabled(true);
        }
    }

    private void setDateFromListner(final EditText editText) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                AddProjectActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dateStr = dayOfMonth + "/" + monthOfYear + "/" + year;
                        editText.setText(dateStr);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        dialog.show();
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

    }

}
