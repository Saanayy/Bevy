package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    //Field Variables
    List<Note> notes = new ArrayList<>();
    String uid, projectId;

    // Views
    ImageView ivNewNote, ivBack;
    RecyclerView rvNotes;
    RecyclerView.LayoutManager layoutManager;
    NoteAdapter noteAdapter;

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

    }


    private void fetchNotes() {
        DatabaseReference noteRef = databaseReference.child("users").child(uid).child("projects").child(projectId).child("notes");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    notes.add(note);
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
    }
}
