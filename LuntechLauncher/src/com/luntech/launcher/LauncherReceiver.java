/*
 * Copyright 2014 - Jamdeo
 */
package com.luntech.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LauncherReceiver extends BroadcastReceiver {
    private static final String TAG = "LauncherReceiver";
    private final static boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DEBUG) {
            Log.d(TAG, "received request, passing to service");
        }
        intent.setClass(context, LauncherService.class);
        context.startService(intent);
    }
};
