package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectStatusActivity extends AppCompatActivity {

    // Field variables
    private final String TAG = "ProjectStatus";
    String uid, projectID;
    ProjectStatusAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<ProjectStatus> statuses = new ArrayList<>();
    List<ProjectStatus> newStatuses = new ArrayList<>();
    List<ProjectStatus> allStatuses = new ArrayList<>();
    boolean reset = false;

    // Views
    ImageView ivAdd, ivBack;
    RecyclerView rvProjectStatusList;
    private TextView tvSearch, tvSort;
    private EditText etSearchText;

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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProjectStatus();
            }
        });
        fetchData();
        setRecylerView();

        tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChoiceDialog();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reset)
                    doSearch();
                else {
                    reset = false;
                    etSearchText.setText("");
                    tvSearch.setText("OK");
                    statuses.clear();
                    statuses.addAll(allStatuses);
                    newStatuses.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void doSearch() {
        String search = etSearchText.getText().toString().trim();
        if (search.length() == 0) {
            Toast.makeText(this, "Enter some text to search", Toast.LENGTH_SHORT).show();
        } else {
            reset = true;
            tvSearch.setText("RESET");
            newStatuses = new ArrayList<>(statuses);
            statuses.clear();
            Log.d(TAG, "doSearch: " + newStatuses.size());
            for (int i = 0; i < newStatuses.size(); i++) {
                String name = newStatuses.get(i).getName();
                Log.d(TAG, "doSearch: " + newStatuses.size());
                if (name.contains(search)) {
                    statuses.add(newStatuses.get(i));
                    Log.d(TAG, "doSearch: " + newStatuses.get(i).getName());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void viewChoiceDialog() {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.sort_choice_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final RadioButton rbDateAsc = alertLayout.findViewById(R.id.sort_dialog_date_asc);
        final RadioButton rbDateDsc = alertLayout.findViewById(R.id.sort_dialog_date_dsc);
        final RadioButton rbName = alertLayout.findViewById(R.id.sort_dialog_name);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                if (rbDateAsc.isChecked())
                    Collections.sort(statuses);
                else if (rbDateDsc.isChecked())
                    Collections.sort(statuses, Collections.<ProjectStatus>reverseOrder());
                else {
                    Comparator<ProjectStatus> compareByName = new Comparator<ProjectStatus>() {
                        @Override
                        public int compare(ProjectStatus o1, ProjectStatus o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    };
                    Collections.sort(statuses, compareByName);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();

    }

    private void addProjectStatus() {

        askForDetailsAndMakeProjectStatus();


    }

    private void askForDetailsAndMakeProjectStatus() {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.project_status_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final EditText etName = alertLayout.findViewById(R.id.dialog_project_status_name);
        final EditText etAmount = alertLayout.findViewById(R.id.dialog_project_status_amount);
        final EditText etDate = alertLayout.findViewById(R.id.dialog_project_status_date);
        final ImageView ivCal = alertLayout.findViewById(R.id.dialog_project_status_date_icon);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCal.performClick();
            }
        });

        ivCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFromListner(etDate);
            }
        });

        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // Todo: check for numeric inconsistencies.
                String name = etName.getText().toString().trim();
//                String amount = etAmount.getText().toString().trim();
                String date = etDate.getText().toString().trim();

                if (name.length() == 0 || date.length() == 0) {
                    Toast.makeText(ProjectStatusActivity.this, "Empty fields, Not sent to database", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference projectStatusRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus");
                    String key = projectStatusRef.push().getKey();
                    ProjectStatus projectStatus = new ProjectStatus(name, date, 0, key, 0, 0);
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
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        alert.setCancelable(false);
        Dialog dialog = alert.create();
        dialog.show();
    }

    private void fetchData() {
        final DatabaseReference projectStatusRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus");
        projectStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allStatuses.clear();
                statuses.clear();
                for (DataSnapshot projectSnapshot : dataSnapshot.getChildren()) {
                    ProjectStatus projectStatus = projectSnapshot.getValue(ProjectStatus.class);
                    statuses.add(projectStatus);
                    allStatuses.add(projectStatus);
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

    private void setDateFromListner(final EditText editText) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                ProjectStatusActivity.this,
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

    private void initialiseAllFirebase() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        uid = mAuth.getUid();
    }

    private void initialiseAllViews() {
        ivAdd = findViewById(R.id.project_status_add);
        ivBack = findViewById(R.id.project_status_back);
        rvProjectStatusList = findViewById(R.id.project_status_list);
        tvSearch = findViewById(R.id.project_status_searchOK);
        tvSort = findViewById(R.id.project_status_sort);
        etSearchText = findViewById(R.id.project_status_searchtext);
    }


}
