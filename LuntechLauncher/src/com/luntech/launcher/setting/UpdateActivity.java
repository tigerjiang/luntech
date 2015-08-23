package com.luntech.launcher.setting;


import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.luntech.launcher.Launcher;
import com.luntech.launcher.OtaInfo;
import com.luntech.launcher.R;
import com.luntech.launcher.ToolUtils;

import java.io.File;

public class UpdateActivity extends Activity {
    private static final String TAG = "Update";
    private DownloadManager mDownloadManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        setContentView(R.layout.update_layout);
        Fragment currFragment = UpdateFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, currFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }


    public void downloadOta(final Context context, final OtaInfo ota) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ota.getUrl()));
        request.setDestinationInExternalPublicDir("download", getUrlFileName(ota.getUrl()));
        request.allowScanningByMediaScanner();//表示允许MediaScanner扫描到这个文件，默认不允许。
        request.setTitle("程序更新");//设置下载中通知栏提示的标题
        request.setDescription("程序更新正在下载中:" + Launcher.DOWNLOAD_TO_PATH);//设置下载中通知栏提示的介绍
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        @SuppressWarnings("unused")
        long downloadId = mDownloadManager.enqueue(request);
        ToolUtils.storeValueIntoSP(mContext, "ota_id", String.valueOf(downloadId));
    }

    private String getUrlFileName(String url) {
        return url.substring(url.lastIndexOf("/"));
    }


    private BroadcastReceiver mCompleteReceiver = new BroadcastReceiver() {
        //public static HashMap<long, String> downloadSavePath = new HashMap<long, String>();

        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Log.d(TAG, "download complete apk ");
                long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ToolUtils.getValueFromSP(mContext, "ota_id").equals(String.valueOf(completeDownloadId))) {
                    Uri installUri = mDownloadManager.getUriForDownloadedFile(completeDownloadId);
                    ToolUtils.install(mContext, installUri);
                }
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mCompleteReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(mCompleteReceiver, new IntentFilter(

                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
