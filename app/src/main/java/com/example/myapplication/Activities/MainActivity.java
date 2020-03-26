package com.example.myapplication.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    // Field variables
    me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference().child("users");
    ProjectAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Project> projects = new ArrayList<>();
    List<Project> newProjects = new ArrayList<>();
    List<Project> allProjects = new ArrayList<>();
    boolean reset = false;
    EditText etSearchText;
    private TextView tvLogout;
    private String uid;


    // Views
    private RecyclerView rvRecycler;
    private ImageView ivAdd;
    private TextView tvSort, tvSearch;

    // Firebase variables
    private FirebaseAuth mAuth;

    // Activity lifecycle callbacks
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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

        tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChoiceDialog();
            }
        });

        setRecylerView();

        fetchData();
        setRecylerView();
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reset)
                    doSearch();
                else {
                    reset = false;
                    etSearchText.setText("");
                    tvSearch.setText("OK");
                    projects.clear();
                    projects.addAll(allProjects);
                    newProjects.clear();
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
            newProjects = new ArrayList<>(projects);
            projects.clear();
            Log.d(TAG, "doSearch: " + newProjects.size());
            for (int i = 0; i < newProjects.size(); i++) {
                String name = newProjects.get(i).getName();
                Log.d(TAG, "doSearch: " + newProjects.size());
                if (name.contains(search)) {
                    projects.add(newProjects.get(i));
                    Log.d(TAG, "doSearch: " + newProjects.get(i).getName());
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
                    Collections.sort(projects);
                else if (rbDateDsc.isChecked())
                    Collections.sort(projects, Collections.<Project>reverseOrder());
                else {
                    Comparator<Project> compareByName = new Comparator<Project>() {
                        @Override
                        public int compare(Project o1, Project o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    };
                    Collections.sort(projects, compareByName);
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

    private void fetchData() {
        if (uid == null) return;
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference userProjectRef = usersRef.child(uid).child("projects");
        userProjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProjects.clear();
                projects.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Project project = postSnapshot.getValue(Project.class);
                    projects.add(project);
                    allProjects.add(project);
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
        tvSearch = findViewById(R.id.main_searchOK);
        tvLogout = findViewById(R.id.main_logout);
        ivAdd = findViewById(R.id.main_add);
        rvRecycler = findViewById(R.id.main_project_recycler);
        progressBar = findViewById(R.id.main_progress);
        tvSort = findViewById(R.id.main_sort);
        etSearchText = findViewById(R.id.main_searchtext);
    }
}
