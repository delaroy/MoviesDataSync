package com.delaroystudios.moviesdatasync;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.delaroystudios.moviesdatasync.data.Movie;
import com.delaroystudios.moviesdatasync.data.MovieContract;
import com.google.android.flexbox.AlignSelf;
import com.google.android.flexbox.FlexboxLayoutManager;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.delaroystudios.moviesdatasync.DetailActivity.MOVIE_TITLE;
import static com.delaroystudios.moviesdatasync.DetailActivity.PLOT_SYNOPSIS;
import static com.delaroystudios.moviesdatasync.DetailActivity.POSTER_PATH;
import static com.delaroystudios.moviesdatasync.DetailActivity.USER_RATING;
import static com.delaroystudios.moviesdatasync.R.id.thumbnail;

/**
 * Created by delaroy on 8/22/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Cursor dataCursor;
    Context context;
    int id;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userrating;
        public ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
          //  title = (TextView) itemView.findViewById(R.id.title);
          //  userrating = (TextView) itemView.findViewById(R.id.userrating);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(MOVIE_TITLE, getItem(pos).title);
                        intent.putExtra(USER_RATING, getItem(pos).userRating);
                        intent.putExtra(POSTER_PATH, getItem(pos).posterPath);
                        intent.putExtra(PLOT_SYNOPSIS, getItem(pos).plotSynopsis);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    public MovieAdapter(Activity mContext, Cursor cursor) {
        dataCursor = cursor;
        context = mContext;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new ViewHolder(cardview);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {


        dataCursor.moveToPosition(position);

        id = dataCursor.getInt(dataCursor.getColumnIndex(MovieContract.MovieEntry._ID));
        String mTitle = dataCursor.getString(dataCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        Double mUserrating = dataCursor.getDouble(dataCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USERRATING));
        String mPosterpath = dataCursor.getString(dataCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));

        String vote = Double.toString(mUserrating);
       // holder.title.setText(mTitle);
        //holder.userrating.setText(vote);

        String poster = "https://image.tmdb.org/t/p/w500" + mPosterpath;

        Glide.with(context)
                .load(poster)
                .placeholder(R.drawable.load)
                .into(holder.thumbnail);

        //TODO
        ViewGroup.LayoutParams lp = holder.thumbnail.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexGrow(1.0f);
            flexboxLp.setAlignSelf(AlignSelf.FLEX_END);
        }

    }


    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public Movie getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("Item position is out of adapter's range");
        } else if (dataCursor.moveToPosition(position)) {
            return new Movie(dataCursor);
        }
        return null;
    }
}


