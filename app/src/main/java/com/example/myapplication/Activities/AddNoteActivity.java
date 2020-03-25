package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Modals.Note;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteActivity extends AppCompatActivity {

    //Field Variables
    private static final String TAG = "AddNote";
    String projectId;

    //Firebase Variables
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;
    private String uid;

    //Views
    private ImageView ivBack;
    private Button btnSave;
    private EditText etHeading, etDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectid");

        intialiseAllViews();
        initialiseFirebaseVaraibles();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }


    private void saveNote() {
        String heading = etHeading.getText().toString().trim();
        String desc = etDetails.getText().toString().trim();

        if (heading.length() != 0 && desc.length() != 0) {
            final DatabaseReference notesRef = databaseReference.child("notes");
            DatabaseReference userNotesRef = databaseReference.child("users").child(uid).child("projects").child(projectId).child("notes");

            final String keynotesRef = notesRef.push().getKey();

            final Note note = new Note(keynotesRef, heading, desc);
            userNotesRef.child(keynotesRef).setValue(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddNoteActivity.this, "Added to the database", Toast.LENGTH_SHORT).show();
                            notesRef.child(keynotesRef).setValue(note)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddNoteActivity.this, "Normalisation Note added", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddNoteActivity.this, "Normalisation note addition failed ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNoteActivity.this, "Addition to the database failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Data cannot be empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void initialiseFirebaseVaraibles() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
    }

    private void intialiseAllViews() {
        etHeading = findViewById(R.id.add_notes_heading);
        etDetails = findViewById(R.id.add_notes_description);
        btnSave = findViewById(R.id.add_notes_save);
        ivBack = findViewById(R.id.add_notes_back);
    }
}
