package com.luntech.launcher;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.zip.ZipException;

/**
 * Created by tiger on 15-8-27.
 */
public class LauncherService extends Service {

    private static final String TAG = "LauncherService";
    private static final boolean DEBUG = true;
    private static final int NEW_INTENT_RECEIVED = 1001;
    private static final String KEY_AUTO_TIME = "auto_time";
    private static final String KEY_AUTO_TIME_ZONE = "auto_time_zone";
    private static final int SYNC_DELAY_TIME = 1 * 60 * 1000;
    private LauncherHandler mLauncherHandler;

    private Context mContext;
    private String mResult;
    protected DownloadManager mDownloadManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "The service is onCreate.");
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mContext = LauncherApplication.getAppContext();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            if (DEBUG) {
                Log.d(TAG, "The service is starting.");
            }
            return START_STICKY;
        }
        if (DEBUG) {
            Log.d(TAG, "onStartCommand for intent " + intent.toString());
        }
        final String action = intent.getAction();
        if (action != null) {
            if (mLauncherHandler == null) {
                final HandlerThread thread = new HandlerThread("LauncherService",
                        android.os.Process.THREAD_PRIORITY_DISPLAY);
                thread.start();
                mLauncherHandler = new LauncherHandler(
                        thread.getLooper());
                final Message msg = mLauncherHandler.obtainMessage();
                msg.what = LauncherHandler.SYNC_YIME;
                mLauncherHandler.sendMessageAtTime(msg, SYNC_DELAY_TIME);
            }
            final Message msg = mLauncherHandler.obtainMessage();
            msg.what = NEW_INTENT_RECEIVED;
            msg.obj = intent;
            mLauncherHandler.sendMessage(msg);
        } else {
            Log.w(TAG, "Action is null, intent=" + intent);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class LauncherHandler extends Handler {

        public static final int RETURN_CATEGORY_CONFIG_CODE = 1;
        public static final int RETURN_UNZIP_CONFIG_CODE = 2;
        public static final int RETURN_HIDDEN_CONFIG_CODE = 3;
        public static final int RETURN_UPDATE_CONFIG_CODE = 4;
        public static final int RETURN_SYSTEM_CONFIG_CODE = 5;
        public static final int RETURN_SCREENSAVER_CONFIG_CODE = 6;
        public static final int START_SCREEN_SAVER = 7;
        public static final int SYNC_YIME = 8;
        public static final int NO_OPERATION = 9;
        public static final int SHOW_SCREEN_SAVER = 10;

        public LauncherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {


            if (msg.obj instanceof String) {
                mResult = (String) msg.obj;
                if (TextUtils.isEmpty(mResult)) {
                    Logger.e("result is empty");
                }
            }
            switch (msg.what) {
                case NEW_INTENT_RECEIVED:
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
                    String commonArg = "&package_name=" + Launcher.sPackageName + "&version=" + Launcher.sVersionCode;
                    if (Launcher.CAPTURE_AD_CONFIGURE_ACTION.equals(action)) {
                        String config_url = HttpUtils.HTTP_CONFIG_URL + commonArg + "&type=" + Launcher.mType;
                        ;
                        Logger.e("request url " + config_url);
                        // capture the category config
                        new FetchTask(config_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mAdConfigureFile,
                                LauncherHandler.RETURN_SYSTEM_CONFIG_CODE).execute();
                    } else if (Launcher.CAPTURE_CATEGORY_CONFIG_ACTION.equals(action)) {
                        String httpArg = commonArg + "&type=" + Launcher.mType;
                        String app_url = HttpUtils.HTTP_CONFIG_APP_URL + httpArg;
                        Logger.e("request url " + app_url);
                        // capture the category config
                        new FetchTask(app_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mCategoryFile,
                                LauncherHandler.RETURN_CATEGORY_CONFIG_CODE).execute();

                    } else if (Launcher.CAPTURE_HIDDEN_CONFIGURE_ACTION.equals(action)) {
                        String httpArg =  "&package_name=" + Launcher.mType+"&version=" + Launcher.sVersionCode;
                        String hidden_url = HttpUtils.HTTP_HIDDEN_APP_URL + httpArg;
                        Logger.e("request url hidden_url " + hidden_url);
                        // capture the category config
                        new FetchTask(hidden_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mHiddenFile,
                                LauncherHandler.RETURN_HIDDEN_CONFIG_CODE).execute();

                    } else if (Launcher.CAPTURE_SCREENSAVER_CONFIGURE_ACTION.equals(action)) {
                        String screensaver_url = HttpUtils.HTTP_SCREEN_SAVER_URL;
                        Logger.e("request url " + screensaver_url);
                        // capture the category config
//                        new FetchTask(screensaver_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.SCREENSAVER_CONFIGURE_FILE,
//                                LauncherHandler.RETURN_SCREENSAVER_CONFIG_CODE).execute();

                    } else if (Launcher.CAPTURE_UPDATE_CONFIGURE_ACTION.equals(action)) {
                        String update_url = HttpUtils.HTTP_UPDATE_APP_URL + commonArg;
                        Logger.e("request url " + update_url);
                        // capture the category config
                        new FetchTask(update_url, Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mUpdateConfigureFile,
                                LauncherHandler.RETURN_UPDATE_CONFIG_CODE).execute();


                    } else if (Launcher.SHOW_SCREENSAVER_ACTION.equals(action)) {
                        Log.d(TAG, "SHOW_SCREEN_SAVER");
                        mLauncherHandler.post(new Runnable() {
                            @Override
                            public void run() {
//                                parseScreenSaverCover();
                            }
                        });
                    } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        Log.d(TAG, "download complete apk ");
                        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (ToolUtils.getValueFromSP(mContext, "ota_id").equals(String.valueOf(completeDownloadId))) {
                            Uri installUri = mDownloadManager.getUriForDownloadedFile(completeDownloadId);
                            ToolUtils.install(mContext, installUri);
                        } else {
                            Uri installUri = mDownloadManager.getUriForDownloadedFile(completeDownloadId);
                            ToolUtils.install(mContext, installUri);
                        }
                    }
                    break;
                case RETURN_CATEGORY_CONFIG_CODE:
                    // ToolUtils.getCustomConfigureFromConfig(mContext,
                    // new ByteArrayInputStream(result.getBytes()));
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            getCustomConfigureFromConfig(mContext,
                                    new ByteArrayInputStream(mResult.getBytes()));
                        }
                    }).start();
                    break;
                case RETURN_SCREENSAVER_CONFIG_CODE:
                    // ToolUtils.getCustomConfigureFromConfig(mContext,
                    // new ByteArrayInputStream(result.getBytes()));
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
//                            getScreenSaverFromConfig(mContext,
//                                    new ByteArrayInputStream(mResult.getBytes()));
                        }
                    }).start();
                    break;
//                case START_SCREEN_SAVER:
//                    Log.d("show", "SHOW_SCREEN_SAVER");
//                    if(ToolUtils.isApplicationBroughtToBackground(mContext)){
//                        Log.d(TAG,"current task is background. can't show screensaver");
//                    }else{
//                        Intent saverIntent = new Intent(mContext, ScreenSaverActivity.class);
//                        saverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(saverIntent);
//                    }
//                    break;
                case RETURN_HIDDEN_CONFIG_CODE:
                    ToolUtils.parseHiddenConfigureFromConfig(mContext,
                            new ByteArrayInputStream(mResult.getBytes()));
                    break;
                case RETURN_UPDATE_CONFIG_CODE:
                    try {
                        final OtaInfo ota = ToolUtils.parseUpdateInfo(mContext,
                                new ByteArrayInputStream(mResult.getBytes()));
                        if (ota.currentVersion.equals(String.valueOf(Launcher.sVersionCode))) {
                            if (Integer.parseInt(ota.currentVersion) < Integer.parseInt(ota.newVersion)) {
                                Log.d(TAG, "find new version for update " + ota.newVersion);
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.update);
                                builder.setMessage(ota.remark);
                                builder.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                ToolUtils.downloadOta(mContext, ota);
                                                arg0.dismiss();
                                            }
                                        });
                                builder.setNegativeButton(R.string.cancel, null);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                dialog.show();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case RETURN_SYSTEM_CONFIG_CODE:
                    String adContent = ToolUtils.getAdConfigureFromConfig(mContext,
                            new ByteArrayInputStream(mResult.getBytes()));
                    Log.d(TAG, "ad " + adContent);
                    break;
                case SYNC_YIME:
                    if (Settings.Global.getInt(mContext.getContentResolver(), KEY_AUTO_TIME, 0) == 0) {
                        Settings.Global.putInt(mContext.getContentResolver(), KEY_AUTO_TIME, 1);
                    } else {
                        Settings.Global.putInt(mContext.getContentResolver(), KEY_AUTO_TIME, 0);
                    }

                    if (Settings.Global.getInt(mContext.getContentResolver(), KEY_AUTO_TIME_ZONE, 0) == 0) {
                        Settings.Global.putInt(mContext.getContentResolver(), KEY_AUTO_TIME_ZONE, 1);
                    } else {
                        Settings.Global.putInt(mContext.getContentResolver(), KEY_AUTO_TIME_ZONE, 0);
                    }
                    final Message msg1 = mLauncherHandler.obtainMessage();
                    msg.what = LauncherHandler.SYNC_YIME;
                    mLauncherHandler.sendMessageAtTime(msg1, SYNC_DELAY_TIME);
                    break;


            }
            super.handleMessage(msg);
        }
    }

    private void getScreenSaverFromConfig(Context context, InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals("time")) {
                        String time = parser.nextText().trim();
                        String storeTime = ToolUtils.getCommonValueFromSP(context, "screen_saver_time");
                        if (!TextUtils.isEmpty(storeTime)) {
                            if (time.equals(storeTime)) {
                                Logger.d("Desn't need get config from server,Beacuse of the time is same as local "
                                        + storeTime);
                                break;
                            } else {
                                ToolUtils.storeCommonValueIntoSP(context, "screen_saver_time", time);
                            }
                        } else {
                            ToolUtils.storeCommonValueIntoSP(context, "screen_saver_time", time);
                        }
                    } else if (name.equals("url")) {
                        String downloadUrl = parser.nextText().trim();
                        ToolUtils.storeCommonValueIntoSP(context, "screen_saver_url", downloadUrl);
                        // Download the new zip resources
                        IDownloadListener listener = new IDownloadListener() {

                            @Override
                            public void onError(String errorCode) {

                            }

                            @Override
                            public void onCompleted(final File file) {

                                // Complete download the zip file
                                Log.d(TAG, "download file for " + file.getAbsolutePath());
                                final long time = System.currentTimeMillis();
                                mLauncherHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                            File descDir = new File(Launcher.DOWNLOAD_TO_PATH + "/"
                                                    + Launcher.FILE_SCREENSAVER);
                                            if (!descDir.exists()) {
                                                descDir.mkdir();
                                            }
                                            ZipUtils.upZipFile(file, Launcher.DOWNLOAD_TO_PATH + "/"
                                                    + Launcher.FILE_SCREENSAVER);
                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        };
                        DownloadTask downloadTask = new DownloadTask(Launcher.DOWNLOAD_TO_PATH, downloadUrl, listener);
                        new Thread(downloadTask).start();
                        break;
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    // Log.d(TAG, name);
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
    }

    private void getCustomConfigureFromConfig(Context context, InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {
                        String time = parser.nextText().trim();
                        String storeTime = ToolUtils.getValueFromSP(context,
                                CustomApplication.TIME_TAG);
                        if (!TextUtils.isEmpty(storeTime)) {
                            if (time.equals(storeTime)) {
                                Log.d(TAG, "Desn't need get config from server,Beacuse of the time is same as local "
                                        + storeTime);
                                return;
                            } else {
                                ToolUtils.storeValueIntoSP(context, CustomApplication.TIME_TAG,
                                        time);
                                File config = new File(Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mCategoryFile);
                                if (config.exists()) {
                                    ToolUtils.parseCustomConfigureFromInputStream(context, new FileInputStream(config));
                                }
                            }
                        } else {
                            ToolUtils.storeValueIntoSP(context, CustomApplication.TIME_TAG, time);
                            File config = new File(Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mCategoryFile);
                            if (config.exists()) {
                                ToolUtils.parseCustomConfigureFromInputStream(context, new FileInputStream(config));
                            }
                        }
                    } else if (name.equals(CustomApplication.URL_TAG)) {
                        String downloadUrl = parser.nextText().trim();
                        ToolUtils.storeValueIntoSP(context, CustomApplication.URL_TAG, downloadUrl);
                        // Download the new zip resources
                        IDownloadListener listener = new IDownloadListener() {

                            @Override
                            public void onError(String errorCode) {

                            }

                            @Override
                            public void onCompleted(final File file) {

                                // Complete download the zip file
                                Log.d(TAG, "download complete file for " + file.getAbsolutePath());
                                final long time = System.currentTimeMillis();
                                mLauncherHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                            File descDir = new File(Launcher.DOWNLOAD_TO_PATH + "/"
                                                    + Launcher.mFilePrefix);
                                            if (!descDir.exists()) {
                                                descDir.mkdir();
                                            }
                                            ZipUtils.upZipFile(file, Launcher.DOWNLOAD_TO_PATH + "/"
                                                    + Launcher.mFilePrefix);
                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        };

                        DownloadTask mDownloadTask = new DownloadTask(Launcher.DOWNLOAD_TO_PATH,
                                downloadUrl, listener);
                        new Thread(mDownloadTask).start();
                        return;
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    // Log.d(TAG, name);
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
    }

    class FetchTask extends AsyncTask<Void, Integer, String> {

        private String mUrl;
        private String mFileName;
        private int mReturnCode;

        public FetchTask(String mUrl, String mFileName, int returnCode) {
            super();
            this.mUrl = mUrl;
            this.mFileName = mFileName;
            this.mReturnCode = returnCode;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                Logger.e("Doesn't found any info from server");
                return;
            } else {
                Logger.d("result = " + result);
                Message msg = mLauncherHandler.obtainMessage(mReturnCode);
                msg.obj = result;
                mLauncherHandler.sendMessage(msg);
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = HttpUtils.requestAndWriteResourcesFromServer(mUrl, mFileName);
            return result;
        }
    }

    // ///////////////// Private API Helpers //////////////////////////

    private final Comparator<Module> PARSED_APPS_COMPARATOR = new Comparator<Module>() {

        @Override
        public int compare(Module lhs, Module rhs) {
            String l_flags = lhs.getModuleCode();
            String r_flags = rhs.getModuleCode();
            boolean flag = false;
            flag = Integer.parseInt(l_flags.replaceAll("\\D+", "")) < Integer.parseInt(r_flags
                    .replaceAll("\\D+", ""));
            return flag ? -1 : 0;
        }
    };


    private void parseScreenSaverCover() {
        String screenSaverConfig = Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.SCREENSAVER_CONFIGURE_FILE;
        Log.d(TAG, " screenSaverConfig" + screenSaverConfig);
        File configFile = new File(screenSaverConfig);
        if (configFile.exists()) {
            String resourcesPath = Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.FILE_SCREENSAVER;
            Log.d(TAG, " resourcesPath" + resourcesPath);
            File resourcesFile = new File(resourcesPath);
            if (resourcesFile.exists() && resourcesFile.isDirectory()) {
                for (File file : resourcesFile.listFiles()) {
                    Launcher.sScreenSaverFileList.add(file.getAbsolutePath());
                }
                Log.d(TAG, " Launcher.sScreenSaverFileList " + Launcher.sScreenSaverFileList.toString());
                Message msg = mLauncherHandler.obtainMessage(LauncherHandler.START_SCREEN_SAVER);
                mLauncherHandler.sendMessage(msg);
            } else {

            }
        } else {

        }
    }
}
