package com.delaroystudios.moviesdatasync;


import android.content.res.Configuration;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.delaroystudios.moviesdatasync.data.MovieContract;
import com.delaroystudios.moviesdatasync.data.MovieDbHelper;
import com.delaroystudios.moviesdatasync.sync.MovieSyncAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    MovieDbHelper dbHelper;
    private RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    private static final int MOVIE_LOADER = 0;
    FlexboxLayoutManager flexboxLayoutManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MovieSyncAdapter.initializeSyncAdapter(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
      /*  if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }*/

        flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.COLUMN);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_END);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        movieAdapter = new MovieAdapter(this, null);
        recyclerView.setAdapter(movieAdapter);

       // dbHelper = new MovieDbHelper(this);

        getLoaderManager().initLoader(MOVIE_LOADER,null,this);



    }

    @Override
    public void onResume(){
        super.onResume();
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_USERRATING,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                MovieContract.MovieEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        movieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        movieAdapter.swapCursor(null);
    }
}
