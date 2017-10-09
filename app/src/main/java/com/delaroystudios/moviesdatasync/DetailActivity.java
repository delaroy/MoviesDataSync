package com.delaroystudios.moviesdatasync;

import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.delaroystudios.moviesdatasync.data.MovieContract;

/**
 * Created by delaroy on 8/24/17.
 */

public class DetailActivity extends AppCompatActivity{

    private ImageView mImage;


    String movieName, plot, poster;
    Double rating;



    public static final String  MOVIE_TITLE = "movTitle";
    public static final String  USER_RATING = "movRating";
    public static final String  PLOT_SYNOPSIS = "plotSynopsis";
    public static final String  POSTER_PATH = "posterPath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mImage = (ImageView) findViewById(R.id.movieImage);
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(MOVIE_TITLE)) {
            movieName = getIntent().getExtras().getString(MOVIE_TITLE);
            plot = getIntent().getExtras().getString(PLOT_SYNOPSIS);
            rating = getIntent().getExtras().getDouble(USER_RATING);
            poster = getIntent().getExtras().getString(POSTER_PATH);

            TextView plotSyn = (TextView) findViewById(R.id.plot);
            plotSyn.setText(plot);
            float f = Float.parseFloat(Double.toString(rating));

            setTitle(movieName);

            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingLevel);
            ratingBar.setRating(f);

            String posterPath = "https://image.tmdb.org/t/p/w500" + poster;

            Glide.with(this)
                    .load(posterPath)
                    .placeholder(R.drawable.load)
                    .into(mImage);
        }


    }


}
