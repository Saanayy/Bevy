package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.PaymentStatus;
import com.example.myapplication.R;

import java.util.List;

public class PaymentStatusAdapter extends RecyclerView.Adapter<PaymentStatusViewHolder> {
    Context context;
    List<PaymentStatus> statuses;

    public PaymentStatusAdapter(Context context, List<PaymentStatus> statuses) {
        this.context = context;
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public PaymentStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_payment_status_layout, parent, false);
        return new PaymentStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentStatusViewHolder holder, int position) {
        PaymentStatus paymentStatus = statuses.get(position);
        holder.tvAmount.setText(paymentStatus.getAmount());
        holder.tvName.setText(paymentStatus.getName());
        holder.tvDate.setText(paymentStatus.getDate());
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }
}

class PaymentStatusViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvDate, tvAmount;
    CheckBox cbPaid;

    public PaymentStatusViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.single_payment_name);
        tvAmount = itemView.findViewById(R.id.single_payment_amount);
        tvDate = itemView.findViewById(R.id.single_payment_date);
        cbPaid = itemView.findViewById(R.id.single_payment_check);
    }
}