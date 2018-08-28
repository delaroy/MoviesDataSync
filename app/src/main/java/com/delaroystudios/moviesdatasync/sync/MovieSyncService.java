package com.delaroystudios.moviesdatasync.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by delaroy on 8/21/17.
 */

public class MovieSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter eMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MovieSyncService", "onCreate - MovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (eMovieSyncAdapter == null) {
                eMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return eMovieSyncAdapter.getSyncAdapterBinder();
    }
}
