package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Modals.Note;
import com.example.myapplication.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    Context context;
    List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_note_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.tvHeading.setText(notes.get(position).getHeading());
        holder.tvDescription.setText(notes.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}

class NoteViewHolder extends RecyclerView.ViewHolder {

    TextView tvHeading, tvDescription;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
        tvHeading = itemView.findViewById(R.id.single_note_heading);
        tvDescription = itemView.findViewById(R.id.single_note_desc);
    }
}