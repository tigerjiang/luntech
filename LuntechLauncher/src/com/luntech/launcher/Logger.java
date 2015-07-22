
package com.luntech.launcher;

import android.util.Log;

public class Logger {

    private static final boolean DEBUG = true;
    private static final String TAG = "ZHIHE";

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void e(String message, Throwable e) {
        Log.e(TAG, message, e);
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String msg, Exception e) {
        Log.w(TAG, msg, e);
    }

}
