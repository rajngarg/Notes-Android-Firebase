package com.example.notesclone.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesclone.BaseActivity;
import com.example.notesclone.R;
import com.example.notesclone.model.NoteModal.Note;
import com.example.notesclone.presenter.ApiPresenter;

public class UpdateNoteActivity extends BaseActivity implements ApiPresenter.OnDeletedListener, ApiPresenter.OnUpdatedListener, ApiPresenter.OnGetNoteListener {

    EditText etTitle, etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        setToolbar();
        setStatusBar();
        init();
    }

    public void init() {
        apiPresenter.setGetNoteListener(this);
        apiPresenter.getNote(getIntent().getStringExtra("id"));
        etTitle = findViewById(R.id.et_title);
        etNote = findViewById(R.id.et_note);
    }

    public void saveNote(View view) {
        apiPresenter.setOnUpdatedListener(this);
        String title = etTitle.getText().toString();
        String note = etNote.getText().toString();
        apiPresenter.updateNote(getIntent().getStringExtra("id"), title, note);
    }

    public void deleteNote(View view) {
        apiPresenter.setOnDeletedListener(this);
        apiPresenter.deleteNote(getIntent().getStringExtra("id"));
    }

    @Override
    public void onDelete() {
        finish();
    }

    @Override
    public void onUpdate() {
        finish();
    }

    @Override
    public void onGetNote(Note note) {
        etTitle.setText(note.getContent().getTitle());
        etNote.setText(note.getContent().getNote());
    }

    @Override
    public void onError(Throwable err) {
        err.printStackTrace();
        Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
    }
}
