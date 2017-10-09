package com.delaroystudios.moviesdatasync.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.delaroystudios.moviesdatasync.BuildConfig;
import com.delaroystudios.moviesdatasync.R;
import com.delaroystudios.moviesdatasync.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * Created by delaroy on 8/21/17.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the MOVIEDB API, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    ContentResolver mContentResolver;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");
        BufferedReader reader = null;

        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {

            final String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/movie/popular?";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    .build();


            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "The URL link is  " + url);

            // Create the request to MOVIEDB API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000000 /* milliseconds */);
            urlConnection.setConnectTimeout(1500000/* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            forecastJsonStr = buffer.toString();
            getMoviesData(forecastJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error passing data ", e);
            // If the code didn't successfully get the movies data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return;
    }



    private void getMoviesData(String forecastJsonStr)
            throws JSONException {

        final String BGS_MOVIES = "results";


        final String BGS_MOVIEID = "id";
        final String BGS_TITLE = "title";
        final String BGS_USERRATING = "vote_average";
        final String BGS_POSTERPATH = "poster_path";
        final String BGS_PLOT = "overview";

        try {

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(BGS_MOVIES);

            for (int i = 0; i < moviesArray.length(); i++) {


                String title;
                double userrating;
                String posterpath;
                String plot;
                int movieid;

                JSONObject moviesDetails = moviesArray.getJSONObject(i);

                title = moviesDetails.getString(BGS_TITLE);
                userrating = moviesDetails.getDouble(BGS_USERRATING);
                posterpath = moviesDetails.getString(BGS_POSTERPATH);
                plot = moviesDetails.getString(BGS_PLOT);
                movieid = moviesDetails.getInt(BGS_MOVIEID);



                ContentValues moviesValues = new ContentValues();

                moviesValues.put(MovieContract.MovieEntry.COLUMN_MOVIEID, movieid);
                moviesValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                moviesValues.put(MovieContract.MovieEntry.COLUMN_USERRATING, userrating);
                moviesValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterpath);
                moviesValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, plot);


                mContentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, moviesValues);

            }

            Log.d(LOG_TAG, "Sync Complete. " + moviesArray.length() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }





}
