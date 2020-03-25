package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.Task;
import com.example.myapplication.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    Context context;
    List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.tvName.setText(task.getName());
        holder.tvDate.setText(task.getDate());
        holder.cbCheck.setChecked(task.isCheck());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

class TaskViewHolder extends RecyclerView.ViewHolder {
    TextView tvName, tvDate;
    CheckBox cbCheck;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.single_task_name);
        tvDate = itemView.findViewById(R.id.single_task_date);
        cbCheck = itemView.findViewById(R.id.single_task_check);
    }
}