package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.HistoryAdapter;
import com.example.myapplication.Modals.History;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AmountHistoryActivity extends AppCompatActivity {

    //Field Variables
    private static final String TAG = "History";
    List<History> histories = new ArrayList<>();
    String uid;
    String projectId;
    HistoryAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    //Views
    RecyclerView rvAmountHistory;

    //Firebase variables
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_history);
        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectid");

        initialiseAllViews();

        initialiseFirebaseVaribales();

        setRecylerView();
        fetchData();
        setRecylerView();
    }

    private void fetchData() {
//        Log.d(TAG, "fetchData: " + projectId);
        final DatabaseReference historyRef = databaseReference.child("users").child(uid).child("projects").child(projectId).child("amounthistory");
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                histories.clear();
                for (DataSnapshot historysnapshot : dataSnapshot.getChildren()) {
                    History history = historysnapshot.getValue(History.class);
                    histories.add(history);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmountHistoryActivity.this, "Fetching data cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvAmountHistory.setLayoutManager(layoutManager);
        mAdapter = new HistoryAdapter(this, histories);
        rvAmountHistory.hasFixedSize();
        rvAmountHistory.setAdapter(mAdapter);
        rvAmountHistory.addItemDecoration(new DividerItemDecoration(rvAmountHistory.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initialiseFirebaseVaribales() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

    }

    private void initialiseAllViews() {
        rvAmountHistory = findViewById(R.id.amount_history_recycler);
    }
}
