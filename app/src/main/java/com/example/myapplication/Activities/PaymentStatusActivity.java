package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.PaymentStatusAdapter;
import com.example.myapplication.Modals.History;
import com.example.myapplication.Modals.PaymentStatus;
import com.example.myapplication.Modals.Project;
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
import java.util.List;

public class PaymentStatusActivity extends AppCompatActivity {

    //Field Variables
    me.zhanghai.android.materialprogressbar.MaterialProgressBar progressBar;
    private static final String TAG = "PaymentStatus";
    PaymentStatusAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvPaymentList;
    ImageView ivAdd;
    private String uid;

    //Views
    Button btnUpdatePayment, btnAmountHistory;
    private String projectID;
    private ImageView ivBack;
    private List<PaymentStatus> statuses = new ArrayList<>();
    private TextView tvTotalAmount, tvCollectedAmount;

    // Firebase variables
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);
        Intent intent = getIntent();
        projectID = intent.getStringExtra("projectid");


        initialiseAllViews();

        initialiseFirebaseVaraibles();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar.setVisibility(View.GONE);

        setRecylerView();

        fetchData();

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

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
            }
        });

    }

    private void addPayment() {

        //Todo: Add payment status here
        askForDetailsAndMakePaymentStatus();

    }

    private void askForDetailsAndMakePaymentStatus() {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.payment_status_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final EditText etName = alertLayout.findViewById(R.id.dialog_payment_status_name);
        final EditText etAmount = alertLayout.findViewById(R.id.dialog_payment_status_amount);
        final EditText etDate = alertLayout.findViewById(R.id.dialog_payment_status_date);
        final ImageView ivCal = alertLayout.findViewById(R.id.dialog_payment_status_date_icon);

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
                String amount = etAmount.getText().toString().trim();
                String date = etDate.getText().toString().trim();

                if (name.length() == 0 || date.length() == 0 || amount.length() == 0) {
                    Toast.makeText(PaymentStatusActivity.this, "Empty fields, Not sent to database", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference paymentRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("paymentstatus");
                    String key = paymentRef.push().getKey();
                    PaymentStatus paymentStatus = new PaymentStatus(name, amount, date, false, key);

                    paymentRef.child(key).setValue(paymentStatus)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PaymentStatusActivity.this, "Payment Added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PaymentStatusActivity.this, "Payment Addition Failed", Toast.LENGTH_SHORT).show();
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
        if (uid == null) return;
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference paymentRef =
                databaseReference.child("users").child(uid).child("projects").child(projectID).child("paymentstatus");
        paymentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statuses.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PaymentStatus paymentStatus = postSnapshot.getValue(PaymentStatus.class);
                    statuses.add(paymentStatus);
                }
                progressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentStatusActivity.this, "Cancelled Loading the data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


        DatabaseReference projectRef = databaseReference.child("users").child(uid).child("projects").child(projectID);
        projectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Project project = dataSnapshot.getValue(Project.class);
                tvTotalAmount.setText(project.getAmount() + "");
                tvCollectedAmount.setText(project.getCollectedAmount() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentStatusActivity.this, "Fetch project Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecylerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvPaymentList.setLayoutManager(layoutManager);
        mAdapter = new PaymentStatusAdapter(this, statuses, projectID);
        rvPaymentList.hasFixedSize();
        rvPaymentList.setAdapter(mAdapter);
        rvPaymentList.addItemDecoration(new DividerItemDecoration(rvPaymentList.getContext(), DividerItemDecoration.VERTICAL));
    }


    private void showAmountHistory() {

        Intent intent = new Intent(PaymentStatusActivity.this, AmountHistoryActivity.class);
        intent.putExtra("projectid", projectID);
        startActivity(intent);
    }

    private void doUpdate() {
        createDialog();

    }

    private void createDialog() {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.update_payment_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final EditText etAmount = alertLayout.findViewById(R.id.dialog_payment_amount);
        final EditText etDate = alertLayout.findViewById(R.id.dialog_payment_date);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFromListner(etDate);
            }
        });

        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
//                // Todo: check for numeric inconsistencies.
                String val = etAmount.getText().toString().trim();
                String date = etDate.getText().toString().trim();

                if (val.length() == 0 || date.length() == 0) {
                    Toast.makeText(PaymentStatusActivity.this, "Empty fields, Not sent to database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentStatusActivity.this, "val " + val, Toast.LENGTH_SHORT).show();
                    // The code to push updates to the database here
                    //Toast.makeText(PaymentStatus.this, ""+ uid + " " + projectID, Toast.LENGTH_SHORT).show();
                    DatabaseReference historyRef = databaseReference.child("users").child(uid).child("projects").child(projectID).child("amounthistory");

                    History history = new History(date, val);
                    historyRef.push().setValue(history)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PaymentStatusActivity.this, "amount updated successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PaymentStatusActivity.this, "failed to update amount", Toast.LENGTH_SHORT).show();
                                }
                            });

                    DatabaseReference projectRef = databaseReference.child("users").child(uid).child("projects").child(projectID);
                    projectRef.child("amount").setValue(Double.parseDouble(val))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PaymentStatusActivity.this, "Amount updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PaymentStatusActivity.this, "Amount updation failed", Toast.LENGTH_SHORT).show();
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

    private void initialiseAllViews() {
        ivBack = findViewById(R.id.payment_status_back);
        btnUpdatePayment = findViewById(R.id.payment_status_update_amount);
        btnAmountHistory = findViewById(R.id.payment_status_amount_history);
        rvPaymentList = findViewById(R.id.payment_status_list);
        progressBar = findViewById(R.id.payment_status_progress);
        ivAdd = findViewById(R.id.payment_status_add);
        tvCollectedAmount = findViewById(R.id.payment_status_collected_amount);
        tvTotalAmount = findViewById(R.id.payment_status_total_amount);

    }

    private void setDateFromListner(final EditText editText) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                PaymentStatusActivity.this,
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

    private void initialiseFirebaseVaraibles() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
