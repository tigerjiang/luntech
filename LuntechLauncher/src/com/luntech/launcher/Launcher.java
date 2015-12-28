
package com.luntech.launcher;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.db.DBDao;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.TvStatusBar;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Launcher extends Activity {

    private static final String TAG = "Launcher";
    private static final boolean DEBUG = true;
    private Resources mResources;
    protected Module mSelectedApp;
    protected static Context mContext;
    private ChangeReceiver mChangeReceiver;
    private Configuration mConfig = new Configuration();

    private static boolean sIsConnectedToServer = false;
    public static PackageInfo sPackageInfo;
    public static String sPackageName;
    public static int sVersionCode;
    public static final long REQUEST_DELAY_TIME_ONE_MINUTES = 1 * 60 * 1000;
    public static final long REQUEST_DELAY_TIME_THREE_MINUTES = 2 * 60 * 1000;
    public static final long REQUEST_DELAY_TIME_FIVE_MINUTES = 3 * 60 * 1000;
    public static final long REQUEST_DELAY_TIME_SEVEN_MINUTES = 4 * 60 * 1000;
    public static final long SHOW_DELAY_TIME = 10 * 1000;
    public static final long DISMISS_DELAY_TIME = 3 * 1000;
    public static long showScreenSaverTime = 5 * 60 * 1000;
    public static boolean sCloseScreenSaver = false;

    public static final String IPTV_CATEGORY_FILE = "iptv_network_config.xml";
    public static final String IPTV_AD_CONFIGURE_FILE = "iptv_ad_config.xml";
    public static final String IPTV_FILE_PREFIX = "launcher_iptv";
    public static final String IPTV_TYPE = "iptv";
    public static final String IPTV_HIDDEN_FILE = "iptv_hidden_config.xml";

    public static final String Q1S_TYPE = "q1s";
    public static final String Q1S_CATEGORY_FILE = "q1s_network_config.xml";
    public static final String Q1S_AD_CONFIGURE_FILE = "q1s_ad_config.xml";
    public static final String Q1S_FILE_PREFIX = "launcher_q1s";
    public static final String Q1S_HIDDEN_FILE = "q1s_hidden_config.xml";
    public static final String THEME_KEY = "theme";
    public static final String UPDATE_CONFIGURE_FILE = "update_config.xml";

    public static final String SCREENSAVER_CONFIGURE_FILE = "screensaver_config.xml";

    public static final String CAPTURE_CATEGORY_CONFIG_ACTION = "com.luntech.action.GET_APP";

    public static final String CAPTURE_UPDATE_CONFIGURE_ACTION = "com.luntech.action.GET_UPDATE";
    public static final String CAPTURE_AD_CONFIGURE_ACTION = "com.luntech.action.GET_AD";
    public static final String CAPTURE_HIDDEN_CONFIGURE_ACTION = "com.luntech.action.HIDDEN_APP";
    public static final String CAPTURE_SCREENSAVER_CONFIGURE_ACTION = "com.luntech.action.GEAT_SAVER";
    public static final String SHOW_SCREENSAVER_ACTION = "com.luntech.action.SHOW_SAVER";
    public static final String APP_START_ACTION = "com.luntech.action.START";

    public static final String HAS_CHECK_UPDATE = "has_check_update";

    public static final String IPTV_THEME = "IPTV";

    public static final String Q1S_THEME = "Q1S";
    protected String mThemeType;

    public static String mAdConfigureFile = IPTV_AD_CONFIGURE_FILE;
    protected static String mHiddenFile = IPTV_HIDDEN_FILE;
    protected static String mCategoryFile = IPTV_CATEGORY_FILE;
    protected static String mFilePrefix = IPTV_FILE_PREFIX;
    protected static String mType = IPTV_TYPE;
    public static String mUpdateConfigureFile = UPDATE_CONFIGURE_FILE;
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

    protected ToolUtils mToolUtils;
    protected ArrayList<Group> mGroups;
    protected ArrayList<Module> mModules;
    protected DBDao mdao;

    public static ArrayList<String> sScreenSaverFileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = LauncherApplication.getAppContext();
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
        mToolUtils = ToolUtils.getInstance(LauncherApplication.getAppContext());
        ToolUtils.storeCommonValueIntoSP(mContext,HAS_CHECK_UPDATE,"0");
        mThemeType = ToolUtils.getCommonValueFromSP(mContext, THEME_KEY);
        AppManager.create(this);
        if (TextUtils.isEmpty(mThemeType)) {
            mThemeType = IPTV_THEME;
        }
        Intent themeIntent = new Intent();
        if (IPTV_THEME.equals(mThemeType)) {
            themeIntent.setClass(mContext, IPTVLauncher.class);
        } else if (Q1S_THEME.equals(mThemeType)) {
            themeIntent.setClass(mContext, Q1SLauncher.class);
        } else {
            themeIntent.setClass(mContext, Q1SLauncher.class);
        }
        themeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(themeIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (mStatusBar != null) {
                        mStatusBar.searchWeather(getUserCity());
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void initScreenSaverTime() {
        String time = ToolUtils.getCommonValueFromSP(mContext, "saver_time");
        String[] arrayTime = getResources().getStringArray(R.array.screensaver_array);
        if (!TextUtils.isEmpty(time)) {
            for (int i = 0; i < arrayTime.length; i++) {
                if (time.equals(arrayTime[i])) {
                    if (i == 0) {
                        Log.e(TAG, "screen saver is off!");
                        sCloseScreenSaver = true;
                    } else {
                        sCloseScreenSaver = false;
                        showScreenSaverTime = i * 5 * 60 * 1000;
                    }
                }
            }
        } else {
            ToolUtils.storeCommonValueIntoSP(mContext, "saver_time", arrayTime[0]);
        }
    }


    protected void initPrecondition() {


        if (HttpUtils.checkConnectivity(mContext)) {

        final Handler handler = new Handler();
        if (sIsConnectedToServer) {
            Log.d(TAG, "Has request server, doesn't need try again!");
        }else {
            final Runnable runable = new Runnable() {

                @Override
                public void run() {
                    sIsConnectedToServer = true;
                    sendBroadcast(new Intent(CAPTURE_UPDATE_CONFIGURE_ACTION));


                }
            };
            if (runable != null) {
                handler.removeCallbacks(runable);
                handler.postDelayed(runable
                        , REQUEST_DELAY_TIME_THREE_MINUTES);
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(CAPTURE_CATEGORY_CONFIG_ACTION));
            }
        }, REQUEST_DELAY_TIME_ONE_MINUTES);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(CAPTURE_HIDDEN_CONFIGURE_ACTION));
            }
        }, REQUEST_DELAY_TIME_ONE_MINUTES);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(CAPTURE_AD_CONFIGURE_ACTION));
            }
        }, REQUEST_DELAY_TIME_FIVE_MINUTES);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(CAPTURE_SCREENSAVER_CONFIGURE_ACTION));
            }
        }, REQUEST_DELAY_TIME_SEVEN_MINUTES);

    }
}

    protected void parseGroupsFromDB() {
        mGroups = mdao.fetchGroups(mType.toUpperCase());
        Log.d(TAG,"size "+mGroups.size());
        if (mGroups != null && mGroups.size() > 0) {

        } else {
            String networkConfig = DOWNLOAD_TO_PATH + "/" + mCategoryFile;
            File configFile = new File(networkConfig);
            if (configFile.exists()) {
                String resourcesPath = DOWNLOAD_TO_PATH + "/" + mFilePrefix;
                File resourcesFile = new File(resourcesPath);
                if (resourcesFile.exists()) {
                    mGroups = mdao.fetchGroups(mType.toUpperCase());
                    if (mGroups == null || mGroups.size() < 1) {
                        Log.d(TAG, "Can't parse the network config ");
                        if (mType.equals(IPTV_TYPE)) {
                            mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.iptv_config);
                        } else if (mType.equals(Q1S_TYPE)) {
                            mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.q1s_config);
                        }
                    }
                } else {
                    if (mType.equals(IPTV_TYPE)) {
                        mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.iptv_config);
                    } else if (mType.equals(Q1S_TYPE)) {
                        mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.q1s_config);
                    }
                }
            } else {
                if (mType.equals(IPTV_TYPE)) {
                    mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.iptv_config);
                } else if (mType.equals(Q1S_TYPE)) {
                    mGroups = ToolUtils.getGroupsFromConfig(mContext, R.xml.q1s_config);
                }
            }
            Collections.sort(mGroups, PARSED_APPS_COMPARATOR);
            Log.d(TAG, "type " + mType);
        }
        mModules = mdao.fetchAllModules(mType.toUpperCase());
    }

    // ///////////////// Private API Helpers //////////////////////////

    private final Comparator<Group> PARSED_APPS_COMPARATOR = new Comparator<Group>() {

        @Override
        public int compare(Group lhs, Group rhs) {
            String l_flags = lhs.getGroupCode();
            String r_flags = rhs.getGroupCode();
            boolean flag = false;
            flag = Integer.parseInt(l_flags.replaceAll("\\D+", "")) < Integer.parseInt(r_flags
                    .replaceAll("\\D+", ""));
            return flag ? -1 : 0;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int changes = newConfig.diff(mConfig);
        if (DEBUG) {
            Log.d(TAG, "newConfig " + newConfig.locale + "-----------------");
            Log.d(TAG, "OnConfiguration changed was called: " + newConfig + "diff is:" + changes);
        }
        if ((changes & ActivityInfo.CONFIG_LOCALE) != 0) {
            if (mStatusBar != null) {
                mStatusBar.searchWeather(getUserCity());
            }
            Log.d("jzh", "local change----------------" + getUserCity());
        }
        // set our copy of the configuration for comparing with in
        // onConfigurationChanged
        mConfig.setTo(getResources().getConfiguration());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected String getUserCity() {
        String cityString = "青岛";
        cityString = Settings.System.getString(mContext.getContentResolver(), "city");
        if (TextUtils.isEmpty(cityString)) {
            cityString = "青岛";
            Settings.System.putString(mContext.getContentResolver(), "city", cityString);
        }
        return cityString;
    }




    protected void notifyModuleList(Module newModule) {
        for (int j = 0; j < mGroups.size(); j++) {
            Group group = mGroups.get(j);
            ArrayList<Module> modules = group.mModules;
            for (int i = 0; i < modules.size(); i++) {
                if (newModule.getModuleCode()
                        .equals(modules.get(i).getModuleCode())) {
                    modules.set(i, newModule);
                }
            }
        }
    }

    protected void notifyAllModuleList() {
//        for (int j = 0; j < mGroups.size(); j++) {
//            Group group = mGroups.get(j);
//            ArrayList<Module> modules = group.mModules;
//            for (int i = 0; i < modules.size(); i++) {
//                final Module module = modules.get(i);
//                String key = module.moduleCode;
//                String pkg = mToolUtils.getConfiguredPkg(mContext, key);
//                Log.d(TAG, "key  for " + key + pkg);
//                if (!TextUtils.isEmpty(pkg)) {
//                    ApplicationInfo app = AppManager.getInstance().getInfoFromAllActivitys(pkg);
//                    if (app != null) {
//                        module.moduleIconDrawable = app.getIcon();
//                        module.moduleText = app.getTitle();
//                        module.mApps.get(0).componentName = app.mComponent;
//                    } else {
//                        mToolUtils.clearConfiguredPkg(mContext, key);
//                    }
//                }
//            }
//        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.d(TAG, "do nothing");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    public static String getCustomType(){
        return mType;
    }
}
