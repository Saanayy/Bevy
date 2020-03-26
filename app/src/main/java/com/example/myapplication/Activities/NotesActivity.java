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

import com.example.myapplication.Adapters.NoteAdapter;
import com.example.myapplication.Modals.Note;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    //Field Variables
    private static final String TAG = "Note";
    List<Note> notes = new ArrayList<>();
    String uid, projectId;
    List<Note> newNotes = new ArrayList<>();
    List<Note> allNotes = new ArrayList<>();
    boolean reset = false;

    // Views
    ImageView ivNewNote, ivBack;
    RecyclerView rvNotes;
    RecyclerView.LayoutManager layoutManager;
    NoteAdapter noteAdapter;
    private TextView tvSearch, tvSort;
    private EditText etSearchText;

    //Firebase variables
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectid");

        initialiseAllViews();
        initialiseAllFirebaseVariables();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
                intent.putExtra("projectid", projectId);
                startActivity(intent);
            }
        });
        setRecylerView();
        fetchNotes();
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
                    notes.clear();
                    notes.addAll(allNotes);
                    newNotes.clear();
                    noteAdapter.notifyDataSetChanged();
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
                    Collections.sort(notes);
                else if (rbDateDsc.isChecked())
                    Collections.sort(notes, Collections.<Note>reverseOrder());
                else {
                    Comparator<Note> compareByName = new Comparator<Note>() {
                        @Override
                        public int compare(Note o1, Note o2) {
                            return o1.getHeading().compareTo(o2.getHeading());
                        }
                    };
                    Collections.sort(notes, compareByName);
                }
                noteAdapter.notifyDataSetChanged();
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
            newNotes = new ArrayList<>(notes);
            notes.clear();
            Log.d(TAG, "doSearch: " + newNotes.size());
            for (int i = 0; i < newNotes.size(); i++) {
                String name = newNotes.get(i).getHeading();
                Log.d(TAG, "doSearch: " + newNotes.size());
                if (name.contains(search)) {
                    notes.add(newNotes.get(i));
                    Log.d(TAG, "doSearch: " + newNotes.get(i).getHeading());
                }
            }
            noteAdapter.notifyDataSetChanged();
        }
    }


    private void fetchNotes() {
        DatabaseReference noteRef = databaseReference.child("users").child(uid).child("projects").child(projectId).child("notes");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes.clear();
                allNotes.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    notes.add(note);
                    allNotes.add(note);
                }
                noteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotesActivity.this, "Fetching into the array cancelled. ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvNotes.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(this, notes);
        rvNotes.hasFixedSize();
        rvNotes.setAdapter(noteAdapter);
        rvNotes.addItemDecoration(new DividerItemDecoration(rvNotes.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initialiseAllFirebaseVariables() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

    }


    private void initialiseAllViews() {
        ivNewNote = findViewById(R.id.notes_add);
        rvNotes = findViewById(R.id.notes_list);
        ivBack = findViewById(R.id.notes_back);
        tvSearch = findViewById(R.id.notes_searchOK);
        tvSort = findViewById(R.id.notes_sort);
        etSearchText = findViewById(R.id.notes_searchtext);
    }
}
