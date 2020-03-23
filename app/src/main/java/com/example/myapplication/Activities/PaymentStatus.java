package com.example.myapplication.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Modals.History;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentStatus extends AppCompatActivity {

    private static final String TAG = "PaymentStatus";
    //Views
    Button btnUpdatePayment, btnAmountHistory;
    // Firebase variables
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    //Field Variables
    private String uid;
    private String projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);
        Intent intent = getIntent();
        projectID = intent.getStringExtra("projectid");


        initialiseAllViews();

        initialiseFirebaseVaraibles();

        btnUpdatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdate();
            }
        });

        btnAmountHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmountHistory();
            }
        });

    }


    private void showAmountHistory() {

        Intent intent = new Intent(PaymentStatus.this, AmountHistoryActivity.class);
        intent.putExtra("projectid", projectID);
        startActivity(intent);
    }

    private void doUpdate() {
        createDialog();

    }

    private void createDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.update_payment_dialog, null);

        final EditText etAmount = alertLayout.findViewById(R.id.dialog_payment_amount);
        Button btnSave = alertLayout.findViewById(R.id.dialog_payment_save);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(PaymentStatus.this, "Ho gaya", Toast.LENGTH_SHORT).show();
//
//            }
//        });
        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // Todo: check for numeric inconsistencies.
                String val = etAmount.getText().toString().trim();
                if (val.length() == 0) {
                    Toast.makeText(PaymentStatus.this, "wrong val", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentStatus.this, "val " + val, Toast.LENGTH_SHORT).show();
                    // The code to push updates to the database here
                    //Toast.makeText(PaymentStatus.this, ""+ uid + " " + projectID, Toast.LENGTH_SHORT).show();
                    DatabaseReference historyRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("amounthistory");

                    History history = new History("20/10/2016", "100000");
                    historyRef.push().setValue(history)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PaymentStatus.this, "amount updated successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PaymentStatus.this, "failed to update amount", Toast.LENGTH_SHORT).show();
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


        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void initialiseAllViews() {
        btnUpdatePayment = findViewById(R.id.payment_status_update_amount);
        btnAmountHistory = findViewById(R.id.payment_status_amount_history);
    }

    private void initialiseFirebaseVaraibles() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
