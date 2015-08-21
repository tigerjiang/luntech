
package com.luntech.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
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
    // private List<CategoryItem> mAppList = new ArrayList<CategoryItem>();
    private Module mSelectedApp;
    private ModuleAdapter mModuleAdapter;
    private Context mContext;
    private ChangeReceiver mChangeReceiver;
    private Configuration mConfig = new Configuration();
    private AppManager mAppManager;
    private RelativeLayout mThumb_1_layout;

    private TextView mFeatureView;
    private LinearLayout mFeatureMenuLayout;
    private ImageView mThumb_1_view;
    private ImageView mThumb_1_shadow;
    private TextView mThumb_1_label;

    private RelativeLayout mThumb_2_layout;
    private ImageView mThumb_2_view;
    private ImageView mThumb_2_shadow;
    private TextView mThumb_2_label;

    private TextView mAdvertisementView;

    private RelativeLayout mThumb_3_layout;
    private ImageView mThumb_3_view;
    private ImageView mThumb_3_shadow;
    private TextView mThumb_3_label;
    private ToolUtils mToolUtils;

    private static PackageInfo sPackageInfo;
    private static String sPackageName;
    private static int sVersionCode;
    private static final long REQUEST_DELAY_TIME = 2*60 * 1000;
    private static final long SHOW_DELAY_TIME = 10 * 1000;
    private static final long DISMISS_DELAY_TIME = 3 * 1000;
    private static  long showScreenSaverTime = 5 * 60 * 100;

    public static final String CAPTURE_TIME = "capture_time";
    public static final String CATEGORY_FILE = "network_config.xml";
    public static final String AD_CONFIGURE_FILE = "ad_config.xml";
    public static final String UPDATE_CONFIGURE_FILE = "update_config.xml";
    public static final String SCREENSAVER_CONFIGURE_FILE = "screensaver_config.xml";
    public static final String LOCAL_CONFIG_FILE_ = "local_config.xml";
    public static String FILE_PREFIX = "launcher";
    public static String FILE_SCREENSAVER = "screensaver";
    public static final String ADVERTISEMENT_KEY = "advertisement_key";
    public static final String FULL_BG_KEY = "full_bg_key";
    public static final String RESOURCE_DIR = "resource_dir_key";
    public static String DOWNLOAD_TO_PATH;
    private TvStatusBar mStatusBar;

    AsyncImageLoader mAsyncImageLoader;
    private Handler mHandler;
    private HandlerThread mThread;
    private DownloadTask mDownloadTask;

    private boolean mIsShowAlert = false;
    private int mGridPosition = 0;

    public ArrayList<Module> mModules;
    private DBDao mdao;

    Module mFirstApp;
    Module mSecondApp;
    Module mThirdApp;

    private RelativeLayout mRootView;
    public static  ArrayList<String> sScreenSaverFileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = getApplicationContext();
        sPackageName = this.getPackageName();
        mdao = new DBDao(mContext);
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
        parseScreenSaverCover();
        parseModulesFromDB();
        initView();

        // Intent intentService = new Intent();
        // intentService.setClass(this, ChangeService.class);
        // startService(intentService);
        // registerReceiver();
    }

    private void initHandler() {
        // mThread = new HandlerThread("launcher",
        // Process.THREAD_PRIORITY_DISPLAY);
        // mThread.start();
        mHandler = new LauncherHandler();
        mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
        mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW, SHOW_DELAY_TIME);
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
                new FetchTask(app_url, DOWNLOAD_TO_PATH + "/" + CATEGORY_FILE,
                        LauncherHandler.RETURN_CATEGORY_CONFIG_CODE).execute();

                String config_url = HttpUtils.HTTP_CONFIG_URL + httpArg;
                Logger.e("request url " + config_url);
                // capture the category config
                new FetchTask(config_url, DOWNLOAD_TO_PATH + "/" + AD_CONFIGURE_FILE,
                        LauncherHandler.RETURN_SYSTEM_CONFIG_CODE).execute();

                String update_url = HttpUtils.HTTP_UPDATE_APP_URL + httpArg;
                Logger.e("request url " + update_url);
                // capture the category config
                new FetchTask(update_url, DOWNLOAD_TO_PATH + "/" + UPDATE_CONFIGURE_FILE,
                        LauncherHandler.RETURN_UPDATE_CONFIG_CODE).execute();

                String screensaver_url = HttpUtils.HTTP_SCREEN_SAVER_URL;
                Logger.e("request url " + screensaver_url);
                // capture the category config
                new FetchTask(screensaver_url, DOWNLOAD_TO_PATH + "/" + SCREENSAVER_CONFIGURE_FILE,
                        LauncherHandler.RETURN_SCREENSAVER_CONFIG_CODE).execute();

            }
        }, REQUEST_DELAY_TIME);

    }

    private void initView() {
        mRootView = (RelativeLayout) findViewById(R.id.rootview_layout);
        mStatusBar = (TvStatusBar) findViewById(R.id.status_layout);
        mThumb_1_layout = (RelativeLayout) findViewById(R.id.thumb_1_layout);
        mFeatureView = (TextView) findViewById(R.id.feature_menu);
        mFeatureMenuLayout = (LinearLayout) findViewById(R.id.feature_layout);
        mThumb_1_view = (ImageView) findViewById(R.id.thumb_1_view);
        mThumb_1_shadow = (ImageView) findViewById(R.id.thumb_1_cover_view);
        mThumb_1_label = (TextView) findViewById(R.id.thumb_1_label);
        mThumb_2_layout = (RelativeLayout) findViewById(R.id.thumb_2_layout);
        mThumb_2_view = (ImageView) findViewById(R.id.thumb_2_view);
        mThumb_2_shadow = (ImageView) findViewById(R.id.thumb_2_cover_view);
        mThumb_2_label = (TextView) findViewById(R.id.thumb_2_label);
        mThumb_3_layout = (RelativeLayout) findViewById(R.id.thumb_3_layout);
        mThumb_3_view = (ImageView) findViewById(R.id.thumb_3_view);
        mThumb_3_shadow = (ImageView) findViewById(R.id.thumb_3_cover_view);
        mThumb_3_label = (TextView) findViewById(R.id.thumb_3_label);

        mAdvertisementView = (TextView) findViewById(R.id.ad_content_1);
        mAppManager = AppManager.getInstance();
        mAppManager.getSelectedApplications();
        mGridView = (GridView) findViewById(R.id.category_layout);
        String adContent = ToolUtils.getValueFromSP(mContext, ADVERTISEMENT_KEY);
        if (!TextUtils.isEmpty(adContent)) {
            mAdvertisementView.setText(adContent);
        }

        String bgPath = ToolUtils.getValueFromSP(mContext, FULL_BG_KEY);
        if (!TextUtils.isEmpty(bgPath)) {
            try {
                mRootView.setBackground(Drawable.createFromPath(bgPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyAllModuleList();
        refreshLocakedThumbnail();
        mModuleAdapter = new ModuleAdapter(mModules,mContext);
        mGridView.setAdapter(mModuleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App app = ((Module) parent.getItemAtPosition(position)).mApps.get(0);
                Logger.d(" clicke app for " + app.toString());
                ComponentName componentName = app.getComponentName();
                safeStartApk(app);
                mGridPosition = position;
            }
        });
        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Logger.d("select position " + position);
                mSelectedApp = (Module) parent.getItemAtPosition(position);
                mGridPosition = position;
                refreshFeatureMenuView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void safeStartApk(final App app) {
        try {
            Intent intent = new Intent();
            intent.setComponent(app.getComponentName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            if (TextUtils.isEmpty(app.appUrl)) {
                Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.app_background_download);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (!TextUtils.isEmpty(app.downloadStatus)) {
                        if (!app.downloadStatus.equals(App.DOWNLOAD_STATUS_COMPLETED)) {
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


    private void refreshLocakedThumbnail() {
        mSelectedApp = mModules.get(0);
        mFirstApp = mModules.get(0);
        mSecondApp = mModules.get(1);
        mThirdApp = mModules.get(2);
        refreshFeatureMenuView();
        mAsyncImageLoader = new AsyncImageLoader(mContext);
        mAsyncImageLoader.loadDrawable(mFirstApp.getModuleIcon(), mThumb_1_view, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        // mThumb_1_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        // module1.getModuleIcon()));
        mAsyncImageLoader.loadDrawable(mFirstApp.getModuleShadow(), mThumb_1_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        mThumb_1_label.setText(mFirstApp.getModuleText());
        mThumb_1_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setComponent(new ComponentName("com.xike.xkliveplay",
                            "com.xike.xkliveplay.activity.launch.ActivityLaunch"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // safeStartApk(module2.mApps.get(0).getComponentName());
                } catch (Exception e) {

                    // Toast.makeText(mContext, "App no found for " +
                    // componentName,
                    // Toast.LENGTH_SHORT)
                    // .show();
                    Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }

            }
        });
        // mThumb_2_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        // module2.getModuleIcon()));
        mAsyncImageLoader.loadDrawable(mSecondApp.getModuleIcon(), mThumb_2_view, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        mAsyncImageLoader.loadDrawable(mSecondApp.getModuleShadow(), mThumb_2_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        mThumb_2_label.setText(mSecondApp.getModuleText());
        mThumb_2_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setComponent(new ComponentName("com.skzh.elifetv",
                            "com.skzh.elifetv.ui.GovernInfoActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // safeStartApk(module2.mApps.get(0).getComponentName());
                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });

        mAsyncImageLoader.loadDrawable(mThirdApp.getModuleIcon(), mThumb_3_view, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        mAsyncImageLoader.loadDrawable(mThirdApp.getModuleShadow(), mThumb_3_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        // mThumb_3_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        // module3.getModuleIcon()));
        mThumb_3_label.setText(mThirdApp.getModuleText());
        mThumb_3_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.skzh.elifetv",
                            "com.skzh.elifetv.MainActivity"));
                    intent.putExtra("frag_index", "3");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // safeStartApk(module3.mApps.get(0).getComponentName());

                } catch (Exception e) {

                    // Toast.makeText(mContext, "App no found for " +
                    // componentName,
                    // Toast.LENGTH_SHORT)
                    // .show();
                    Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    private void parseScreenSaverCover(){
        String screenSaverConfig = DOWNLOAD_TO_PATH + "/" + SCREENSAVER_CONFIGURE_FILE;
        File configFile = new File(screenSaverConfig);
        if (configFile.exists()) {
            String resourcesPath = DOWNLOAD_TO_PATH + "/" + FILE_SCREENSAVER;
            File resourcesFile = new File(resourcesPath);
            if (resourcesFile.exists()&&resourcesFile.isDirectory()) {
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
        Log.d(TAG, mModules.toString());

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
        mIsShowAlert = false;
        Log.d("show", "action " + action + "    keycode" + keyCode);
        if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            // Log.d(TAG, "action " + action + "    keycode" + keyCode);
            // Log.d(TAG, "focus " + "    keycode" + keyCode);

            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (mThumb_1_layout.isFocused()) {
                    Log.d("replace", "mThumb_1_layout.isFocused()");
                    mSelectedApp = mModules.get(0);
                } else if (mThumb_2_layout.isFocused()) {
                    Log.d("replace", "mThumb_2_layout.isFocused()");
                    mSelectedApp = mModules.get(1);
                } else if (mThumb_3_layout.isFocused()) {
                    Log.d("replace", "mThumb_3_layout.isFocused()");
                    mSelectedApp = mModules.get(2);
                } else if (mGridView.isFocused()) {
                    int pos = mGridView.getSelectedItemPosition();
                    Log.d("replace", "mGridView.isFocused()  " + pos + " --- " + mGridPosition);
                    mSelectedApp = (Module) mGridView.getAdapter()
                            .getItem(mGridPosition);
                }
                Log.d("replace", " mSelectedApp === " + mSelectedApp.toString());
                refreshFeatureMenuView();
                if (mSelectedApp.moduleReplace == 0) {
                    // can't replace
                    Log.d(TAG, "cacn't replace the app");
                    Toast.makeText(mContext, R.string.can_not_replace, Toast.LENGTH_SHORT).show();
                    return true;
                } else if (mSelectedApp.moduleReplace == 1) {
                    final DialogFragment newFragment = AppDialogFragment.newInstance(Launcher.this);
                    newFragment.show(getFragmentManager(), "dialog");
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mThumb_1_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                    mSelectedApp = mModules.get(1);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mThumb_1_layout.isFocused()) {
                    Log.d(TAG, "mThumb_1_layout ");
                    mGridView.setSelection(0);
                    mGridView.requestFocus();
                    mSelectedApp = mModules.get(3);
                } else if (mThumb_2_layout.isFocused()) {
                    Log.d(TAG, "mThumb_2_layout ");
                    mThumb_3_layout.requestFocus();
                    mSelectedApp = mModules.get(2);
                } else if (mThumb_3_layout.isFocused()) {
                    Log.d(TAG, "mThumb_3_layout ");
                    mGridView.setSelection(0);
                    mGridView.requestFocus();
                    mSelectedApp = mModules.get(3);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mGridView.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mModules.get(0);
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                    mSelectedApp = mModules.get(1);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mThumb_2_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mModules.get(0);
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mModules.get(0);
                }
                refreshFeatureMenuView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
//            refreshLocakedThumbnail();
            mModuleAdapter.notifyDataSetChanged();
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

    class LauncherHandler extends Handler {

        public static final int RETURN_CATEGORY_CONFIG_CODE = 1;
        public static final int RETURN_UNZIP_CONFIG_CODE = 2;
        public static final int RETURN_HIDDEN_CONFIG_CODE = 3;
        public static final int RETURN_UPDATE_CONFIG_CODE = 4;
        public static final int RETURN_SYSTEM_CONFIG_CODE = 5;
        public static final int RETURN_SCREENSAVER_CONFIG_CODE = 6;
        public static final int SHOW_FEATURE_VIEW = 7;
        public static final int DISMISS_FEATURE_VIEW = 8;
        public static final int NO_OPERATION = 9;
        public static final int SHOW_SCREEN_SAVER = 10;

        public LauncherHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            final String result = (String) msg.obj;
            if (TextUtils.isEmpty(result)) {
                Logger.e("result is empty");
            }
            switch (msg.what) {
                case RETURN_CATEGORY_CONFIG_CODE:
                    // ToolUtils.getCustomConfigureFromConfig(mContext,
                    // new ByteArrayInputStream(result.getBytes()));
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            getCustomConfigureFromConfig(mContext,
                                    new ByteArrayInputStream(result.getBytes()));
                        }
                    }).start();
                break;
                case RETURN_SCREENSAVER_CONFIG_CODE:
                    // ToolUtils.getCustomConfigureFromConfig(mContext,
                    // new ByteArrayInputStream(result.getBytes()));
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            getScreenSaverFromConfig(mContext,
                                    new ByteArrayInputStream(result.getBytes()));
                        }
                    }).start();
                break;
                case RETURN_UNZIP_CONFIG_CODE:

                break;
                case RETURN_HIDDEN_CONFIG_CODE:
                break;
                case RETURN_UPDATE_CONFIG_CODE:
                    final OtaInfo ota = parseUpdateInfo(mContext,
                            new ByteArrayInputStream(result.getBytes()));
                    if (ota.currentVersion.equals(sVersionCode)) {
                        if (Integer.parseInt(ota.currentVersion) < Integer.parseInt(ota.newVersion)) {
                            Log.d(TAG, "find new version for update " + ota.newVersion);
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.update);
                            builder.setMessage(ota.remark);
                            builder.setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            doUpdate(ota);
                                            arg0.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }
                break;
                case RETURN_SYSTEM_CONFIG_CODE:
                    String adContent = ToolUtils.getAdConfigureFromConfig(mContext,
                            new ByteArrayInputStream(result.getBytes()));
                    // Log.d(TAG, "ad " + adContent);
                    mAdvertisementView.setText(adContent);
                break;
                case SHOW_FEATURE_VIEW:
                    mIsShowAlert = true;
                    // Log.d("show", "SHOW_FEATURE_VIEW");
                    mFeatureMenuLayout.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(LauncherHandler.DISMISS_FEATURE_VIEW);
                    mHandler.sendEmptyMessageDelayed(LauncherHandler.DISMISS_FEATURE_VIEW,
                            DISMISS_DELAY_TIME);
                break;
                case DISMISS_FEATURE_VIEW:
                    Log.d("show", "DISMISS_FEATURE_VIEW");
                    mFeatureMenuLayout.setVisibility(View.GONE);
                break;
                case NO_OPERATION:
                    Log.d("show", "NO_OPERATION");
                    mFeatureMenuLayout.setVisibility(View.GONE);
                    mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
                    mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW,
                            SHOW_DELAY_TIME);
                break;
                case SHOW_SCREEN_SAVER:
                    Intent intent = new Intent(mContext,ScreenSaverActivity.class);
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void refreshFeatureMenuView() {
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

    private OtaInfo parseUpdateInfo(Context context, InputStream is) {
        OtaInfo ota = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals("ota")) {
                        ota = new OtaInfo();
                    } else if (name.equals("cur_version")) {
                        ota.currentVersion = parser.nextText().trim();
                    } else if (name.equals("new_version")) {
                        ota.newVersion = parser.nextText().trim();
                    } else if (name.equals("remark")) {
                        ota.remark = parser.nextText().trim();
                    } else if (name.equals("filesize")) {
                        ota.fileSize = parser.nextText().trim();
                    } else if (name.equals("url")) {
                        ota.url = parser.nextText().trim();
                    } else if (name.equals("md5")) {
                        ota.md5 = parser.nextText().trim();
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    if (name.equals("ota")) {
                        Log.d(TAG, "ota info" + ota.toString());
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return ota;
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
                        String storeTime = ToolUtils.getValueFromSP(context, "screen_saver_time");
                        if (!TextUtils.isEmpty(storeTime)) {
                            if (time.equals(storeTime)) {
                                Logger.d("Desn't need get config from server,Beacuse of the time is same as local "
                                        + storeTime);
                                return;
                            } else {
                                ToolUtils.storeValueIntoSP(context, "screen_saver_time", time);
                            }
                        } else {
                            ToolUtils.storeValueIntoSP(context, "screen_saver_time", time);
                        }
                    } else if (name.equals("url")) {
                        String downloadUrl = parser.nextText().trim();
                        ToolUtils.storeValueIntoSP(context, "screen_saver_url", downloadUrl);
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
                                mHandler.post(new Runnable() {

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
                                            ZipUtils.upZipFile(file, DOWNLOAD_TO_PATH + "/"
                                                    + FILE_SCREENSAVER);
                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        };
                        DownloadTask downloadTask = new DownloadTask(DOWNLOAD_TO_PATH + "/"
                                + FILE_SCREENSAVER, downloadUrl, listener);
                        new Thread(downloadTask).start();
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
                                Logger.d("Desn't need get config from server,Beacuse of the time is same as local "
                                        + storeTime);
                                return;
                            } else {
                                ToolUtils.storeValueIntoSP(context, CustomApplication.TIME_TAG,
                                        time);
                                ToolUtils.parseCustomConfigureFromInputStream(context,is);
                            }
                        } else {
                            ToolUtils.storeValueIntoSP(context, CustomApplication.TIME_TAG, time);
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
                                Log.d(TAG, "download file for " + file.getAbsolutePath());
                                final long time = System.currentTimeMillis();
                                mHandler.post(new Runnable() {

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
                                            ZipUtils.upZipFile(file, DOWNLOAD_TO_PATH + "/"
                                                    + FILE_PREFIX);
                                        } catch (ZipException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        };
                        mDownloadTask = new DownloadTask(DOWNLOAD_TO_PATH + "/" + FILE_PREFIX,
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
                Message msg = mHandler.obtainMessage(mReturnCode);
                msg.obj = result;
                mHandler.sendMessage(msg);
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

    private final Comparator<CustomApplication> PARSED_APPS_COMPARATOR = new Comparator<CustomApplication>() {

        @Override
        public int compare(CustomApplication lhs, CustomApplication rhs) {
            String l_flags = lhs.mGroup.getGroupCode();
            String r_flags = rhs.mGroup.getGroupCode();
            boolean flag = false;
            flag = Integer.parseInt(l_flags.replaceAll("\\D+", "")) < Integer.parseInt(r_flags
                    .replaceAll("\\D+", ""));
            return flag ? -1 : 0;
        }
    };

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.d(TAG, "do nothing");
    }

    private void downloadApk(final Context context, final App app) {
        mdao.updateDownload(app);
        IDownloadListener listener = new IDownloadListener() {

            @Override
            public void onError(String errorCode) {

            }

            @Override
            public void onCompleted(final File file) {
                app.downloadStatus = App.DOWNLOAD_STATUS_COMPLETED;
                ToolUtils.install(context, file.getAbsolutePath());
                mdao.updateDownload(app);
            }
        };
        DownloadTask task = new DownloadTask(Launcher.DOWNLOAD_TO_PATH, app.appUrl, listener);
        new Thread(task).start();
    }

    private void doUpdate(OtaInfo ota) {
        IDownloadListener listener = new IDownloadListener() {

            @Override
            public void onError(String errorCode) {

            }

            @Override
            public void onCompleted(final File file) {
                ToolUtils.install(mContext, file.getAbsolutePath());
            }
        };
        DownloadTask task = new DownloadTask(Launcher.DOWNLOAD_TO_PATH, ota.url, listener);
        new Thread(task).start();
    }

    private void restartSendShowScreenSaver() {
        Message msg = mHandler.obtainMessage(LauncherHandler.SHOW_SCREEN_SAVER);
        mHandler.removeMessages(LauncherHandler.SHOW_SCREEN_SAVER);
        mHandler.sendMessageDelayed(msg, showScreenSaverTime);
    }
}
