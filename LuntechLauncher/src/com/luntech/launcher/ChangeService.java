
package com.luntech.launcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

public class ChangeService extends Service {
    private static final String TAG= "ChangeService";
    private ChangeHandler mHandler;
    private static final int NEW_INTENT_RECEIVED = 1;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        if (action != null) {
            if (mHandler == null) {
                final HandlerThread thread = new HandlerThread("changeService",
                        Process.THREAD_PRIORITY_DISPLAY);
                thread.start();
                mHandler = new ChangeHandler(
                        thread.getLooper());
            }
            final Message msg = mHandler.obtainMessage();
            msg.what = NEW_INTENT_RECEIVED;
            msg.obj = intent;
            mHandler.sendMessage(msg);
        } else {
            Log.w(TAG, "Action is null, intent=" + intent);
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private class ChangeHandler extends Handler {
        public ChangeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == NEW_INTENT_RECEIVED) {
                Intent intent = null;
                try {
                    intent = (Intent) msg.obj;
                } catch (final ClassCastException ex) {
                    Log.e(TAG, "Wrong message passed to the handler. ", ex);
                    return;
                }
                final String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    Log.e(TAG,
                            "No action is specified for the received intent!");
                    return;
                }
                Log.i(TAG, "Action: " + action);
            }
        }

    }
}
