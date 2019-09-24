package com.example.notesclone.presenter;

import com.example.notesclone.api.ApiClient;
import com.example.notesclone.api.ApiInterface;
import com.example.notesclone.model.NoteModal.Note;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ApiPresenter {

    private OnUpdatedListener updatedListener;
    private OnGetNoteListener getNoteListener;
    private OnGetAllNotesListener getAllNotesListener;
    private OnSavedListener savedListener;
    private OnDeletedListener deletedListener;

    private ApiInterface apiInterface;
    Disposable disposable;


    public ApiPresenter() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }


    public void getAllNotes() {
        disposable = apiInterface.getAllNotes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> getAllNotesListener.onGetAllNotes(notes), err -> getAllNotesListener.onError(err));
    }

    public void getNote(String id) {
        disposable = apiInterface.getNote(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> getNoteListener.onGetNote(note), err -> getNoteListener.onError(err));
    }

    public void saveNote(String title, String note) {
        disposable = apiInterface.createNote(title, note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(n -> savedListener.onCreated(), err -> savedListener.onError(err));
    }

    public void deleteNote(String id) {
        disposable = apiInterface.deleteNote(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> deletedListener.onDelete(), err -> deletedListener.onError(err));
    }

    public void updateNote(String id, String title, String note) {
        disposable = apiInterface.updateNote(id, title, note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> updatedListener.onUpdate(), err -> updatedListener.onError(err));
    }

    public void setOnDeletedListener(OnDeletedListener listener) {
        deletedListener = listener;
    }

    public void setOnUpdatedListener(OnUpdatedListener listener) {
        updatedListener = listener;
    }

    public void setOnGetAllNotesListener(OnGetAllNotesListener listener) {
        getAllNotesListener = listener;
    }

    public void setGetNoteListener(OnGetNoteListener listener) {
        getNoteListener = listener;
    }

    public void setOnSaveNoteListener(OnSavedListener listener) {
        savedListener = listener;
    }


    public interface OnUpdatedListener extends OnGetErrorListener {
        void onUpdate();
    }

    public interface OnSavedListener extends OnGetErrorListener {
        void onCreated();
    }

    public interface OnDeletedListener extends OnGetErrorListener {
        void onDelete();
    }

    public interface OnGetNoteListener extends OnGetErrorListener {
        void onGetNote(Note note);
    }

    public interface OnGetAllNotesListener extends OnGetErrorListener {
        void onGetAllNotes(ArrayList<Note> notes);
    }

    public interface OnGetErrorListener {
        void onError(Throwable err);
    }
}
