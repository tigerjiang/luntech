
package com.luntech.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.AsyncImageLoader.ImageCallback;
import com.luntech.launcher.db.DBDao;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppDialogFragment;
import com.luntech.launcher.view.TvStatusBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.ZipException;

public class Launcher extends Activity {

    private static final String TAG = "Launcher";
    private static final boolean DEBUG = true;
    private GridView mGridView;
    private Resources mResources;
    private Module mSelectedApp;
    private static Context mContext;
    private ChangeReceiver mChangeReceiver;
    private Configuration mConfig = new Configuration();
    private AppManager mAppManager;

    public static PackageInfo sPackageInfo;
    public static String sPackageName;
    public static int sVersionCode;
    private static final long REQUEST_DELAY_TIME = 10 * 1000;
    private static final long SHOW_DELAY_TIME = 10 * 1000;
    private static final long DISMISS_DELAY_TIME = 3 * 1000;
    public static long showScreenSaverTime = 5 * 60 * 1000;

    public static final String IPTV_CATEGORY_FILE = "iptv_network_config.xml";
    public static final String IPTV_UPDATE_CONFIGURE_FILE = "iptv_update_config.xml";
    public static final String IPTV_FILE_PREFIX = "launcher_iptv";
    public static final String IPTV_TYPE = "iptv";

    public static final String Q1S_TYPE = "q1s";
    public static final String Q1S_CATEGORY_FILE = "q1s_network_config.xml";
    public static final String Q1S_UPDATE_CONFIGURE_FILE = "q1s_update_config.xml";
    public static final String Q1S_FILE_PREFIX = "launcher_q1s";


    public static final String AD_CONFIGURE_FILE = "ad_config.xml";
    public static final String SCREENSAVER_CONFIGURE_FILE = "screensaver_config.xml";

    public static final String CAPTURE_CATEGORY_config_ACTION = "com.luntech.action.GET_APP";
    public static final String CAPTURE_UPDATE_CONFIGURE_ACTION = "com.luntech.action.GET_UPDATE";
    public static final String CAPTURE_AD_CONFIGURE_ACTION = "com.luntech.action.GET_AD";
    public static final String CAPTURE_SCREENSAVER_CONFIGURE_ACTION = "com.luntech.action.GEAT_SAVER";

    protected static String mCategoryFile;
    protected static String mUpdateConfigureFile;
    protected static String mFilePrefix;
    protected static String mType;
    public static String FILE_SCREENSAVER = "screensaver";
    public static final String ADVERTISEMENT_KEY = "advertisement_key";
    public static final String FULL_BG_KEY = "full_bg_key";
    public static String DOWNLOAD_TO_PATH;
    protected TvStatusBar mStatusBar;
    protected LinearLayout mFeatureMenuLayout;
    protected TextView mAdvertisementView;
    protected TextView mFeatureView;
    protected DownloadManager mDownloadManager;
    AsyncImageLoader mAsyncImageLoader;
    protected Handler mHandler;
    private HandlerThread mThread;
    private DownloadTask mDownloadTask;

    private boolean mIsShowAlert = false;
    private int mGridPosition = 0;

    private ToolUtils mToolUtils;
    public ArrayList<Module> mModules;
    private DBDao mdao;

    public static ArrayList<String> sScreenSaverFileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = Launcher.this;
        sPackageName = this.getPackageName();
        mdao = new DBDao(mContext);
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DOWNLOAD_TO_PATH = this.getFilesDir().getAbsolutePath();
        try {
            sPackageInfo = this.getPackageManager().getPackageInfo(sPackageName, 0);
            sVersionCode = sPackageInfo.versionCode;
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }
        mToolUtils = ToolUtils.getInstance();
        AppManager.create(this);
        initHandler();
        initPrecondition();
        initScreenSaverTime();
        parseScreenSaverCover();
        parseModulesFromDB();
        notifyAllModuleList();

    }

    private void initHandler() {
        mHandler = new LauncherHandler();
        mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
        mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW, SHOW_DELAY_TIME);
    }

    private void initScreenSaverTime() {
        String time = ToolUtils.getValueFromSP(mContext, "saver_time");
        String[] arrayTime = getResources().getStringArray(R.array.screensaver_array);
        if (!TextUtils.isEmpty(time)) {
            for (int i = 0; i < arrayTime.length; i++) {
                if (time.equals(arrayTime[i])) {
                    showScreenSaverTime = (i + 1) * 5 * 60 * 1000;
                }
            }
        } else {
            ToolUtils.storeValueIntoSP(mContext, "saver_time", arrayTime[0]);
        }
    }

    private void initPrecondition() {

        if (!HttpUtils.checkConnectivity(mContext)) {
            return;
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                String httpArg = "&package_name=" + sPackageName + "&version=" + sVersionCode;
                String app_url = HttpUtils.HTTP_CONFIG_APP_URL + httpArg;
                Logger.e("request url " + app_url);
                // capture the category config
                new FetchTask(app_url, DOWNLOAD_TO_PATH + "/" + mCategoryFile,
                        LauncherHandler.RETURN_CATEGORY_CONFIG_CODE).execute();

                String config_url = HttpUtils.HTTP_CONFIG_URL + httpArg;
                Logger.e("request url " + config_url);
                // capture the category config
                new FetchTask(config_url, DOWNLOAD_TO_PATH + "/" + AD_CONFIGURE_FILE,
                        LauncherHandler.RETURN_SYSTEM_CONFIG_CODE).execute();

                String update_url = HttpUtils.HTTP_UPDATE_APP_URL + httpArg;
                Logger.e("request url " + update_url);
                // capture the category config
                new FetchTask(update_url, DOWNLOAD_TO_PATH + "/" + mUpdateConfigureFile,
                        LauncherHandler.RETURN_UPDATE_CONFIG_CODE).execute();

                String screensaver_url = HttpUtils.HTTP_SCREEN_SAVER_URL;
                Logger.e("request url " + screensaver_url);
                // capture the category config
                new FetchTask(screensaver_url, DOWNLOAD_TO_PATH + "/" + SCREENSAVER_CONFIGURE_FILE,
                        LauncherHandler.RETURN_SCREENSAVER_CONFIG_CODE).execute();

            }
        }, REQUEST_DELAY_TIME);

    }

    protected void safeStartApk(final App app) {
        try {
            Intent intent = new Intent();
            intent.setComponent(app.getComponentName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            try {
                mAppManager.getAllApplications();
                ApplicationInfo descApp = mAppManager.getInfoFromAllActivitys(app.getAppPackage());
                descApp.startApplication(mContext);
            } catch (Exception e1) {
                e.printStackTrace();

                if (TextUtils.isEmpty(app.appUrl)) {
                    Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.app_background_download);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!TextUtils.isEmpty(app.downloadStatus)) {
                            if (!app.downloadStatus.equals(App.DOWNLOAD_STATUS_DOWNLOADING)) {
                                app.downloadStatus = App.DOWNLOAD_STATUS_DOWNLOADING;
                                downloadApk(mContext, app);
                            } else {
                                Toast.makeText(mContext, R.string.app_is_downloading,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            app.downloadStatus = App.DOWNLOAD_STATUS_DOWNLOADING;
                            downloadApk(mContext, app);
                        }
                        arg0.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                Log.d(TAG, e.toString());
            }
        }
    }

    private void parseScreenSaverCover() {
        String screenSaverConfig = DOWNLOAD_TO_PATH + "/" + SCREENSAVER_CONFIGURE_FILE;
        File configFile = new File(screenSaverConfig);
        if (configFile.exists()) {
            String resourcesPath = DOWNLOAD_TO_PATH + "/" + FILE_SCREENSAVER;
            File resourcesFile = new File(resourcesPath);
            if (resourcesFile.exists() && resourcesFile.isDirectory()) {
                for (File file : resourcesFile.listFiles()) {
                    sScreenSaverFileList.add(file.getAbsolutePath());
                }
            } else {

            }
        } else {

        }
    }

    private void parseModulesFromDB() {
        mModules = mdao.fetchModules();
        if (mModules != null && mModules.size() > 0) {

        } else {
            String networkConfig = DOWNLOAD_TO_PATH + "/" + mCategoryFile;
            File configFile = new File(networkConfig);
            if (configFile.exists()) {
                String resourcesPath = DOWNLOAD_TO_PATH + "/" + mFilePrefix;
                File resourcesFile = new File(resourcesPath);
                if (resourcesFile.exists()) {
                    mModules = mdao.fetchModules();
                    if (mModules == null || mModules.size() < 1) {
                        Log.d(TAG, "Can't parse the network config ");
                        mModules = ToolUtils.getModulsFromConfig(mContext, R.xml.config);
                    }
                } else {
                    mModules = ToolUtils.getModulsFromConfig(mContext, R.xml.config);
                }
            } else {
                mModules = ToolUtils.getModulsFromConfig(mContext, R.xml.config);
            }
            Collections.sort(mModules, PARSED_APPS_COMPARATOR);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int changes = newConfig.diff(mConfig);
        if (DEBUG) {
            Log.d(TAG, "newConfig " + newConfig.locale + "-----------------");
            Log.d(TAG, "OnConfiguration changed was called: " + newConfig + "diff is:" + changes);
        }
        if ((changes & ActivityInfo.CONFIG_LOCALE) != 0) {
            mStatusBar.searchWeather(getUserCity());
            Log.d("jzh", "local change----------------" + getUserCity());
        }
        // set our copy of the configuration for comparing with in
        // onConfigurationChanged
        mConfig.setTo(getResources().getConfiguration());
    }

    @Override
    protected void onResume() {
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    mStatusBar.searchWeather(getUserCity());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private String getUserCity() {
        String cityString = "青岛";
        cityString = Settings.System.getString(mContext.getContentResolver(), "city");
        if (TextUtils.isEmpty(cityString)) {
            cityString = "青岛";
            Settings.System.putString(mContext.getContentResolver(), "city", cityString);
        }
        return cityString;
    }


    public void setResult(ApplicationInfo app, boolean isSelected) {
        if (isSelected && app != null && mSelectedApp != null) {
            String value = mToolUtils.getConfigured(mContext, app.getPackageName());
            if (!TextUtils.isEmpty(value)) {
                if (value.equals(mSelectedApp.getModuleCode())) {
                    mToolUtils.clearConfiguredPkg(mContext, app.getPackageName());
                } else {
                    Log.d("jzh", "setResult cancel for duplicate ");
                    Toast.makeText(mContext, R.string.duplicate_alert, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mToolUtils.setConfigured(mContext, app.getPackageName(), mSelectedApp.getModuleCode());
            mToolUtils.setConfiguredPkg(mContext, mSelectedApp.getModuleCode(), app.getPackageName());
            mSelectedApp.moduleIconDrawable = app.getIcon();
            mSelectedApp.moduleText = app.getTitle();
            mSelectedApp.moduleReplace = 1;
            mSelectedApp.mApps.get(0).componentName = app.mComponent;
            Log.d("replace", " mSelectedApp " + mSelectedApp.toString());
            notifyModuleList(mSelectedApp);
            Log.d("jzh", "setResult  RESULT_OK " + app.toString());
        } else {
            setResult(RESULT_CANCELED, null);
            Log.d("jzh", "setResult RESULT_CANCELED ");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("replace", "onActivityResult");
        switch (resultCode) {
            case RESULT_OK:
                String pkg = data.getStringExtra("app");
                ApplicationInfo app = mAppManager.getInfoFromAllActivitys(pkg);
                setResult(app, true);
                Log.d("replace", "launcher " + app.toString());
                break;
            case RESULT_CANCELED:
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void notifyModuleList(Module newModule) {
        for (int i = 0; i < mModules.size(); i++) {
            if (newModule.getModuleCode()
                    .equals(mModules.get(i).getModuleCode())) {
                mModules.set(i, newModule);
            }
        }
    }

    private void notifyAllModuleList() {
        for (int i = 0; i < mModules.size(); i++) {
            final Module module = mModules.get(i);
            String key = module.moduleCode;
            String pkg = mToolUtils.getConfiguredPkg(mContext, key);
            Log.d(TAG, "key  for " + key + pkg);
            if (!TextUtils.isEmpty(pkg)) {
                ApplicationInfo app = mAppManager.getInfoFromAllActivitys(pkg);
                if (app != null) {
                    module.moduleIconDrawable = app.getIcon();
                    module.moduleText = app.getTitle();
                    module.mApps.get(0).componentName = app.mComponent;
                } else {
                    mToolUtils.clearConfiguredPkg(mContext, key);
                }
            }
        }
    }



    private void refreshFeatureMenuView() {
        if (mFeatureView != null) {
            try {
                if (mSelectedApp.moduleReplace == 0) {
                    mFeatureView.setText(R.string.feature_menu_1);
                } else if (mSelectedApp.moduleReplace == 1) {
                    mFeatureView.setText(R.string.feature_menu_0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        // if(!mIsShowAlert){
        mIsShowAlert = true;
        Log.d("show", "onUserInteraction");
        mHandler.removeMessages(LauncherHandler.NO_OPERATION);
        mHandler.sendEmptyMessage(LauncherHandler.NO_OPERATION);
        restartSendShowScreenSaver();
    }




    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.d(TAG, "do nothing");
    }


    private void restartSendShowScreenSaver() {
        Message msg = mHandler.obtainMessage(LauncherHandler.SHOW_SCREEN_SAVER);
        mHandler.removeMessages(LauncherHandler.SHOW_SCREEN_SAVER);
        mHandler.sendMessageDelayed(msg, showScreenSaverTime);
    }




    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mCompleteReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mCompleteReceiver, new IntentFilter(

                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
