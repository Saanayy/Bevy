package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.ProjectStatus;
import com.example.myapplication.R;

import java.util.List;

public class ProjectStatusAdapter extends RecyclerView.Adapter<ProjectStatusViewHolder> {
    Context context;
    List<ProjectStatus> statuses;

    public ProjectStatusAdapter(Context context, List<ProjectStatus> statuses) {
        this.context = context;
        this.statuses = statuses;
    }


    @NonNull
    @Override
    public ProjectStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_project_status, parent, false);

        return new ProjectStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectStatusViewHolder holder, int position) {
        holder.tvType.setText(statuses.get(position).getName());
        holder.tvDate.setText(statuses.get(position).getDate());
        holder.tvCompletion.setText("" + statuses.get(position).getCompletionPercentage());
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }
}

class ProjectStatusViewHolder extends RecyclerView.ViewHolder {

    TextView tvType, tvDate, tvCompletion;

    public ProjectStatusViewHolder(@NonNull View itemView) {
        super(itemView);
        tvType = itemView.findViewById(R.id.single_project_status_type);
        tvDate = itemView.findViewById(R.id.single_project_status_date);
        tvCompletion = itemView.findViewById(R.id.single_project_status_completion);
    }
}