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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddProjectActivity extends AppCompatActivity {

    // Field variables
    private static final String TAG = "register";
    String result = "", projectId, uid;
    Boolean update;

    // Views
    me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar;
    EditText etName, etConfDate, etAmount, etDeadLineDate;
    ImageView ivConfCal, ivdeadLineCal;
    Button btnAdd;

    // Firebase Variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean[] checkBoxes = new Boolean[8];
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private java.util.Arrays Arrays;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(AddProjectActivity.this, LoginActivity.class);
            update = getIntent().getBooleanExtra("update", false);
            startActivity(intent);
        }
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_android:
                checkBoxes[0] = checked;
                break;
            case R.id.checkbox_website:
                checkBoxes[1] = checked;
                break;
            case R.id.checkbox_admin:
                checkBoxes[2] = checked;
                break;
            case R.id.checkbox_maintainance:
                checkBoxes[3] = checked;
                break;
            case R.id.checkbox_deployment:
                checkBoxes[4] = checked;
                break;
            case R.id.checkbox_logo:
                checkBoxes[5] = checked;
                break;
            case R.id.checkbox_uiux:
                checkBoxes[6] = checked;
                break;
            case R.id.checkbox_cms:
                checkBoxes[7] = checked;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        update = getIntent().getBooleanExtra("update", false);
        projectId = getIntent().getStringExtra("projectid");

        initialiseAllViews();

        progressBar.setVisibility(View.GONE);

        initaliseFireBaseVariables();
        if (update == true)
            setValuesToExistingViews();

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

                progressBar.setVisibility(View.VISIBLE);
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

    private void setValuesToExistingViews() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference projectRef = databaseReference.child("users").child(uid).child("projects").child(projectId);
        projectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Project project = dataSnapshot.getValue(Project.class);
                etName.setText(project.getName());
                etConfDate.setText(project.getConfirmationDate().toString());
                etAmount.setText(project.getAmount() + "");
                etDeadLineDate.setText(project.getDeadline().toString());
                result = project.getResult();
                setCheckBoxes(result);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddProjectActivity.this, "Fetching project details Cancelled", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setCheckBoxes(String result) {
        List<String> checkList = Arrays.asList(result.split(","));

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = (checkList.get(i).equalsIgnoreCase("true") == true) ? true : false;
        }
//        Toast.makeText(this, ""+checkBoxes[0], Toast.LENGTH_SHORT).show();

        ((CheckBox) findViewById(R.id.checkbox_android)).setChecked(checkBoxes[0]);
        ((CheckBox) findViewById(R.id.checkbox_website)).setChecked(checkBoxes[1]);
        ((CheckBox) findViewById(R.id.checkbox_admin)).setChecked(checkBoxes[2]);
        ((CheckBox) findViewById(R.id.checkbox_maintainance)).setChecked(checkBoxes[3]);
        ((CheckBox) findViewById(R.id.checkbox_deployment)).setChecked(checkBoxes[4]);
        ((CheckBox) findViewById(R.id.checkbox_logo)).setChecked(checkBoxes[5]);
        ((CheckBox) findViewById(R.id.checkbox_uiux)).setChecked(checkBoxes[6]);
        ((CheckBox) findViewById(R.id.checkbox_cms)).setChecked(checkBoxes[7]);

    }

    private void convertBooleanToString() {
        result = "";
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

            progressBar.setVisibility(View.VISIBLE);
            // Fetch the unique key of the project and the user id of the user.
            // Push two instances of the project, one to the users node and one
            // to the projects node.
            String uid = mAuth.getUid();
            DatabaseReference userProjectRef = databaseReference.child("users").child(uid).child("projects");

            String userProjectKey;

            if (update == true)
                userProjectKey = projectId;
            else
                userProjectKey = userProjectRef.push().getKey();

            final DatabaseReference projectRef = databaseReference.child("projects").child(userProjectKey);
            // create an object from the data.
            final Project project = new Project(userProjectKey, name, conf, Double.parseDouble(amount), deadline, result);
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

                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProjectActivity.this, "Project adding failed", Toast.LENGTH_SHORT).show();
                            btnAdd.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

        } else {
            Toast.makeText(AddProjectActivity.this, "Data Connot be empty", Toast.LENGTH_SHORT).show();
            btnAdd.setEnabled(true);
            progressBar.setVisibility(View.GONE);
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
        uid = mAuth.getUid();
    }

    private void initialiseAllViews() {
        progressBar = findViewById(R.id.addproj_progress);
        etName = findViewById(R.id.addproj_name);
        etConfDate = findViewById(R.id.addproj_confdate);
        etAmount = findViewById(R.id.addproj_amount);
        etDeadLineDate = findViewById(R.id.addproj_deadlinedate);
        ivConfCal = findViewById(R.id.addproj_confdate_icon);
        ivdeadLineCal = findViewById(R.id.addproj_deadlinedate_icon);
        btnAdd = findViewById(R.id.addproj_add);

    }

}
