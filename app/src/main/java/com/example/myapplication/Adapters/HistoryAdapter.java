package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.History;
import com.example.myapplication.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    Context context;
    List<History> histories;

    public HistoryAdapter(Context context, List<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_amount_history_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.tvAmount.setText(histories.get(position).getAmount());
        holder.tvDate.setText(histories.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }
}

class HistoryViewHolder extends RecyclerView.ViewHolder {

    TextView tvAmount, tvDate;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        tvAmount = itemView.findViewById(R.id.single_amount_history_amount);
        tvDate = itemView.findViewById(R.id.single_amount_history_date);
    }
}