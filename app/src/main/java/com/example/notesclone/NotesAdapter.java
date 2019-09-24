package com.example.notesclone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesclone.activities.UpdateNoteActivity;
import com.example.notesclone.model.NoteModal.Note;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private ArrayList<Note> notesList;
    private Context context;

    public NotesAdapter(Context context, ArrayList<Note> notesList) {
        this.notesList = notesList;
        this.context = context;
    }

    public void setData(ArrayList<Note> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.note_view, parent, false);
        return new NotesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, final int position) {
        holder.bind(notesList.get(position).getContent().getTitle()
                , notesList.get(position).getContent().getNote());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), UpdateNoteActivity.class);
            intent.putExtra("id", notesList.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        void bind(String title, String note) {
            this.binding.setVariable(BR.title, title);
            this.binding.setVariable(BR.note, note);
            this.binding.executePendingBindings();
        }

        NotesViewHolder(ViewDataBinding databinding) {
            super(databinding.getRoot());
            binding = databinding;
        }
    }
}
