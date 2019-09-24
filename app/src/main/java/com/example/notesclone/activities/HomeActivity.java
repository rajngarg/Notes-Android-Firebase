package com.example.notesclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesclone.BaseActivity;
import com.example.notesclone.NotesAdapter;
import com.example.notesclone.R;
import com.example.notesclone.model.NoteModal.Note;
import com.example.notesclone.presenter.ApiPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ApiPresenter.OnGetAllNotesListener {
    RecyclerView recyclerView;
    ArrayList<Note> notes = new ArrayList<>();
    EditText etSearch;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_home);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddNoteActivity.class)));

        initUi();
        setRecyclerView(notes);
        setDrawer();
        setStatusBar();
        apiPresenter.setOnGetAllNotesListener(this);
    }

    private void initUi() {
        etSearch = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.recycler_view);
    }

    CompositeDisposable disposable = new CompositeDisposable();

    private void setSearch() {
        String query = etSearch.getText().toString();
        if (!query.equals(""))
            setObserver(query);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Observable<CharSequence> observable = Observable.create(emitter -> emitter.onNext(s));

                disposable.add(observable.debounce(700, TimeUnit.MILLISECONDS).subscribe(charSequence -> {
                    if (charSequence.length() != 0)
                        setObserver(charSequence);
                }));

                if (s.length() == 0) {
                    adapter.setData(notes);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public DisposableObserver<Note> getDisposableObserver() {
        ArrayList<Note> filteredNotes = new ArrayList<>();
        filteredNotes.clear();
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                filteredNotes.add(note);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                adapter.setData(filteredNotes);
                dispose();
            }
        };
    }


    public void setObserver(CharSequence s) {
        Observable<Note> observable = Observable
                .fromIterable(notes)
                .filter(note ->
                        note.getContent().getNote().contains(s) || note.getContent().getTitle().contains(s));

        disposable.add(
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getDisposableObserver())
        );
    }

    private void setDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        getNotes();
        super.onResume();
    }

    private void getNotes() {
        apiPresenter.getAllNotes();
    }

    public void setRecyclerView(ArrayList<Note> notes) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onGetAllNotes(ArrayList<Note> notes) {
        this.notes.clear();
        adapter.setData(notes);
        this.notes = notes;
        setSearch();
    }

    @Override
    public void onError(Throwable err) {
        err.printStackTrace();
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
    }

}
