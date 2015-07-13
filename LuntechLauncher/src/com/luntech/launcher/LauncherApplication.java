
package com.luntech.launcher;


import android.app.Application;
import android.util.Log;

import com.luntech.launcher.secondary.AppManager;

public class LauncherApplication extends Application {
    private static final String TAG = LauncherApplication.class.getSimpleName();

    private static final boolean DEBUG = true;

    private AppManager mAppManager; // hold the reference for prevent from
                                    // removing object by GC while app is alive

    @Override
    public void onCreate() {
        if (DEBUG) {
            Log.d(TAG, "start");
        }
        super.onCreate();
        init();
    }

    private void init() {
        mAppManager = AppManager.create(this);
    }

    @Override
    public void onLowMemory() {
        if (DEBUG){
            Log.d(TAG, "low memory");
        }
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        if (DEBUG) {
            Log.d(TAG, "terminate");
        }
        mAppManager = null;
        super.onTerminate();
    }
}
