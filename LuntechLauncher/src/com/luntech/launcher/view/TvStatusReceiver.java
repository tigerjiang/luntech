package com.luntech.launcher.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

import com.luntech.launcher.ChangeNotifyManager;

public class TvStatusReceiver extends BroadcastReceiver {
    final ChangeNotifyManager changeNotifyManager = ChangeNotifyManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Uri uri = intent.getData();
        String path = uri == null ? "" : uri.getPath();
        Log.d("TvStatusReceiver", "action: " + action + " path: " + path);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                || action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                || action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                || action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
            changeNotifyManager.notifyNetworkChange(0);
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
//            Uri uri = intent.getData();
//            String path = uri == null ? "" : uri.getPath();
            changeNotifyManager.notifyMountUsbChange(0);

        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            changeNotifyManager.notifyMountUsbChange(1);
        }
    }
}
