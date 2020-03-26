package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.example.myapplication.Adapters.TaskAdapter;
import com.example.myapplication.Modals.ProjectStatus;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationDetailActivity extends AppCompatActivity {

    //Field Variables
    private final String TAG = "ApplicationDetail";
    List<Task> tasks = new ArrayList<>();
    List<Task> newTasks = new ArrayList<>();
    List<Task> allTasks = new ArrayList<>();
    boolean reset = false;
    String uid, projectID, statusID;

    // Views
    ImageView ivNewTask, ivBack;
    RecyclerView rvTasks;
    RecyclerView.LayoutManager layoutManager;
    TaskAdapter taskAdapter;
    //Firebase variables
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;
    private TextView tvSearch, tvSort;
    private EditText etSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);
        projectID = getIntent().getStringExtra("projectid");
        statusID = getIntent().getStringExtra("statusid");
        initialiseAllViews();
        initialiseAllFirebaseVariables();

        fetchData();
        setRecylerView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

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
                    tasks.clear();
                    tasks.addAll(allTasks);
                    allTasks.clear();
                    taskAdapter.notifyDataSetChanged();
                }
            }
        });
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
                    Collections.sort(tasks);
                else if (rbDateDsc.isChecked())
                    Collections.sort(tasks, Collections.<Task>reverseOrder());
                else {
                    Comparator<Task> compareByName = new Comparator<Task>() {
                        @Override
                        public int compare(Task o1, Task o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    };
                    Collections.sort(tasks, compareByName);
                }
                taskAdapter.notifyDataSetChanged();
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

    private void doSearch() {
        String search = etSearchText.getText().toString().trim();
        if (search.length() == 0) {
            Toast.makeText(this, "Enter some text to search", Toast.LENGTH_SHORT).show();
        } else {
            reset = true;
            tvSearch.setText("RESET");
            newTasks = new ArrayList<>(tasks);
            tasks.clear();
            Log.d(TAG, "doSearch: " + newTasks.size());
            for (int i = 0; i < newTasks.size(); i++) {
                String name = newTasks.get(i).getName();
                Log.d(TAG, "doSearch: " + newTasks.size());
                if (name.contains(search)) {
                    tasks.add(newTasks.get(i));
                    Log.d(TAG, "doSearch: " + newTasks.get(i).getName());
                }
            }
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void addTask() {
        askForDetailsAndMakeTask();
    }

    private void askForDetailsAndMakeTask() {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.add_task_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final EditText etName = alertLayout.findViewById(R.id.dialog_task_name);
        final EditText etDate = alertLayout.findViewById(R.id.dialog_task_date);
        final ImageView ivCal = alertLayout.findViewById(R.id.dialog_task_date_icon);

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
                String date = etDate.getText().toString().trim();

                if (name.length() == 0 || date.length() == 0) {
                    Toast.makeText(ApplicationDetailActivity.this, "Empty fields, Not sent to database", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference taskRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus").child(statusID).child("tasks");
                    String key = taskRef.push().getKey();
                    // Todo: create dynamic task here
                    Task task = new Task(name, date, false, key);
                    taskRef.child(key).setValue(task)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ApplicationDetailActivity.this, "Task Addition successful", Toast.LENGTH_SHORT).show();
                                    getTaskCountAndUpdateValues();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ApplicationDetailActivity.this, "Task Addition failed", Toast.LENGTH_SHORT).show();
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

    private void getTaskCountAndUpdateValues() {
        final DatabaseReference statusRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus").child(statusID);
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProjectStatus projectStatus = dataSnapshot.getValue(ProjectStatus.class);
                int taskCount = projectStatus.getTaskCount();
                int completionPercentage = projectStatus.getCompletionPercentage();
                int taskCompleted = projectStatus.getCompleteTaskCount();
                taskCount++;
                completionPercentage = (int) ((taskCompleted / (taskCount * 1.0)) * 100);
                statusRef.child("taskCount").setValue(taskCount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ApplicationDetailActivity.this, "Tast count incremented", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ApplicationDetailActivity.this, "Task increment failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                statusRef.child("completionPercentage").setValue(completionPercentage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ApplicationDetailActivity.this, "Completion percentage set", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ApplicationDetailActivity.this, "Completion percentage set failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ApplicationDetailActivity.this, "Setting completion statistics failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setDateFromListner(final EditText editText) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                ApplicationDetailActivity.this,
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

    private void fetchData() {
        DatabaseReference taskRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("projectstatus").child(statusID).child("tasks");
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasks.clear();
                allTasks.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    tasks.add(task);
                    allTasks.add(task);
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
        taskAdapter = new TaskAdapter(this, tasks, projectID, statusID);
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
        ivBack = findViewById(R.id.application_detail_back);
        tvSearch = findViewById(R.id.application_detail_searchOK);
        tvSort = findViewById(R.id.application_detail_sort);
        etSearchText = findViewById(R.id.application_detail_searchtext);
    }
}
