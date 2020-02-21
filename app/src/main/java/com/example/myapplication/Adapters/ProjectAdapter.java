package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.Project;
import com.example.myapplication.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHolder> {


    Context context;
    List<Project> projects;

    public ProjectAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_project_layout, parent, false);

        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.tvName.setText(project.getName());
        holder.tvDate.setText(project.getConfirmationDate().toString());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}

class ProjectViewHolder extends RecyclerView.ViewHolder {
    TextView tvName, tvDate;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.single_project_name);
        tvDate = itemView.findViewById(R.id.single_project_date);
    }
}
