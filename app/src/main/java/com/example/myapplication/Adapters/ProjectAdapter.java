package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ProjectDetailActivity;
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
    public void onBindViewHolder(@NonNull final ProjectViewHolder holder, int position) {
        final Project project = projects.get(position);
        holder.tvName.setText(project.getName());
        holder.tvDate.setText(project.getConfirmationDate() + "");
        holder.rvMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectDetailActivity.class);
                intent.putExtra("projectid", project.getProjectId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}

class ProjectViewHolder extends RecyclerView.ViewHolder {
    TextView tvName, tvDate;
    RelativeLayout rvMainLayout;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.single_project_name);
        tvDate = itemView.findViewById(R.id.single_project_date);
        rvMainLayout = itemView.findViewById(R.id.single_project_layout);
    }
}
