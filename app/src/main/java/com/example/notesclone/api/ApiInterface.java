package com.example.notesclone.api;

import com.example.notesclone.model.NoteModal.Note;
import com.example.notesclone.model.Status;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("readAll")
    Observable<ArrayList<Note>> getAllNotes();

    @GET("create")
    Maybe<Status> createNote(@Query("title") String title, @Query("note") String note);

    @GET("update")
    Completable updateNote(@Query("id") String id, @Query("title") String title, @Query("note") String note);

    @GET("readOne")
    Single<Note> getNote(@Query("id") String id);

    @GET("deleteOne")
    Completable deleteNote(@Query("id") String id);
}
