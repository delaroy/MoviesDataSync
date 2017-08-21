package com.delaroystudios.moviesdatasync;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delaroystudios.moviesdatasync.data.MovieDbHelper;
import com.delaroystudios.moviesdatasync.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity {

    MovieDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MovieDbHelper(this);
        MovieSyncAdapter.initializeSyncAdapter(this);

    }
}
