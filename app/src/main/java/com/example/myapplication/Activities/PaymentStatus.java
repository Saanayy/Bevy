package com.example.myapplication.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class PaymentStatus extends AppCompatActivity {

    //Views
    Button btnUpdatePayment, btnAmountHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);

        initialiseAllViews();

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
            public void onClick(DialogInterface dialog, int id) {
                // Todo: check for numeric inconsistencies.
                String val = etAmount.getText().toString().trim();
                if (val.length() == 0) {
                    Toast.makeText(PaymentStatus.this, "wrong val", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentStatus.this, "val " + val, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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
}
