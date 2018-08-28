package com.delaroystudios.moviesdatasync.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by delaroy on 8/24/17.
 */

public class Movie {

    public int id;

    public String title;
    public Double userRating;
    public String posterPath;
    public String plotSynopsis;


    public Movie(Cursor cursor) {
        this.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        this.userRating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USERRATING));
        this.posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        this.plotSynopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS));
    }

}