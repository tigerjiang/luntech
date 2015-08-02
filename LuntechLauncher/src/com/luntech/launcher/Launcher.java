
package com.luntech.launcher;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
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
import android.provider.ContactsContract.Directory;
import android.text.Layout;
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
    private ArrayList<CustomApplication> mAllAppList = new ArrayList<CustomApplication>();
    private CustomApplication mSelectedApp;
    private CategoryItemAdapter mCategoryItemAdapter;
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
    private static final long REQUEST_DELAY_TIME = 10 * 1000;
    private static final long SHOW_DELAY_TIME = 10 * 1000;
    private static final long DISMISS_DELAY_TIME = 3 * 1000;

    public static final String CAPTURE_TIME = "capture_time";
    public static final String CATEGORY_FILE = "network_config.xml";
    public static final String AD_CONFIGURE_FILE = "ad_config.xml";
    public static final String LOCAL_CONFIG_FILE_ = "local_config.xml";
    public static String FILE_PREFIX = "launcher";
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

    CustomApplication mFirstApp;
    CustomApplication mSecondApp;
    CustomApplication mThirdApp;

    private RelativeLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = getApplicationContext();
        sPackageName = this.getPackageName();
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
        parseCategoryItem();
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
                Logger.e("request url " + app_url);
                // capture the category config
                new FetchTask(config_url, DOWNLOAD_TO_PATH + "/" + AD_CONFIGURE_FILE,
                        LauncherHandler.RETURN_SYSTEM_CONFIG_CODE).execute();

                // String result =
                // HttpUtils.requestAndWriteResourcesFromServer(url,
                // DOWNLOAD_TO_PATH + "/"
                // + CAPTURE_FILE
                // );
                // if (TextUtils.isEmpty(result)) {
                // Logger.e("Doesn't found any info from server");
                // return;
                // } else {
                // Logger.d("result = " + result);
                // Message msg = mHandler
                // .obtainMessage(LauncherHandler.RETURN_CATEGORY_CONFIG_CODE);
                // msg.obj = result;
                // mHandler.sendMessage(msg);
                // }

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
        notifyAllAppList();
        refreshThumbnail();
        mCategoryItemAdapter = new CategoryItemAdapter(mAllAppList, mContext);
        mGridView.setAdapter(mCategoryItemAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App app = ((CustomApplication) parent.getItemAtPosition(position)).mGroup.mModules
                        .get(0).mApps.get(0);
                Logger.d(" clicke app for " + app.toString());
                ComponentName componentName = app.getComponentName();
                safeStartApk(componentName);
            }
        });
        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Logger.d("select position " + position);
                mSelectedApp = (CustomApplication) parent.getItemAtPosition(position);
                refreshFeatureMenuView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void safeStartApk(ComponentName componentName) {
        try {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {

            // Toast.makeText(mContext, "App no found for " + componentName,
            // Toast.LENGTH_SHORT)
            // .show();
            Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.toString());
        }
    }

    private void refreshThumbnail() {
        mSelectedApp = mAllAppList.get(0);
        mFirstApp = mAllAppList.get(0);
        mSecondApp = mAllAppList.get(1);
        mThirdApp = mAllAppList.get(2);
        refreshFeatureMenuView();
        final Module module1 = mFirstApp.mGroup.mModules.get(0);
        Logger.d("first module " + module1.toString());
        mAsyncImageLoader = new AsyncImageLoader(mContext);
        mAsyncImageLoader.loadDrawable(module1.getModuleIcon(), mThumb_1_view, new ImageCallback() {

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
        mThumb_1_shadow.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                module1.getModuleShadow()));
        mThumb_1_label.setText(module1.getModuleText());
        mThumb_1_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(module1.mApps.get(0).getComponentName());

            }
        });
        final Module module2 = mSecondApp.mGroup.mModules.get(0);
        Logger.d("second module " + module2.toString());
        // mThumb_2_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        // module2.getModuleIcon()));
        mAsyncImageLoader.loadDrawable(module2.getModuleIcon(), mThumb_2_view, new ImageCallback() {

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
        mThumb_2_shadow.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                module2.getModuleShadow()));
        mThumb_2_label.setText(module2.getModuleText());
        mThumb_2_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.skzh.elifetv",
                        "com.skzh.elifetv.MainActivity"));
                intent.putExtra("frag_index", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // safeStartApk(module2.mApps.get(0).getComponentName());

            }
        });
        final Module module3 = mThirdApp.mGroup.mModules.get(0);
        Logger.d("third module " + module3.toString());
        mAsyncImageLoader.loadDrawable(module3.getModuleIcon(), mThumb_3_view, new ImageCallback() {

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
        mThumb_3_shadow.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                module3.getModuleShadow()));
        mThumb_3_label.setText(module3.getModuleText());
        mThumb_3_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.skzh.elifetv",
                        "com.skzh.elifetv.MainActivity"));
                intent.putExtra("frag_index", "3");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // safeStartApk(module3.mApps.get(0).getComponentName());

            }
        });
    }

    private void parseCategoryItem() {
        String networkConfig = DOWNLOAD_TO_PATH + "/" + CATEGORY_FILE;
        File configFile = new File(networkConfig);
        if (configFile.exists()) {
            String resourcesPath = DOWNLOAD_TO_PATH + "/" + FILE_PREFIX;
            File resourcesFile = new File(resourcesPath);
            if (resourcesFile.exists()) {
                mAllAppList = ToolUtils.getCustomConfigureFromConfig(mContext, configFile);
                if (mAllAppList == null || mAllAppList.size() < 1) {
                    Log.d(TAG, "Can't parse the network config ");
                }
            } else {
                mAllAppList = ToolUtils.getCustomInfoFromConfig(mContext, R.xml.config);
            }
        } else {
            mAllAppList = ToolUtils.getCustomInfoFromConfig(mContext, R.xml.config);
        }
        Collections.sort(mAllAppList, PARSED_APPS_COMPARATOR);
        // Log.d(TAG, "Sort " + mAllAppList.toString());
        // CategoryItem item1 = new CategoryItem();
        // item1.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_1_logo);
        // item1.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_1_bg);
        // item1.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_1_shadow);
        // item1.mLabel = mResources.getString(R.string.categore_app_1_label);
        // mAppList.add(0, item1);
        // CategoryItem item2 = new CategoryItem();
        // item2.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_2_logo);
        // item2.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_2_bg);
        // item2.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_2_shadow);
        // item2.mLabel = mResources.getString(R.string.categore_app_2_label);
        // mAppList.add(1, item2);
        // CategoryItem item3 = new CategoryItem();
        // item3.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_3_logo);
        // item3.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_3_bg);
        // item3.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_3_shadow);
        // item3.mLabel = mResources.getString(R.string.categore_app_3_label);
        // mAppList.add(2, item3);
        // CategoryItem item4 = new CategoryItem();
        // item4.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_4_logo);
        // item4.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_4_bg);
        // item4.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_4_shadow);
        // item4.mLabel = mResources.getString(R.string.categore_app_4_label);
        // mAppList.add(3, item4);
        // CategoryItem item5 = new CategoryItem();
        // item5.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_5_logo);
        // item5.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_5_bg);
        // item5.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_5_shadow);
        // item5.mLabel = mResources.getString(R.string.categore_app_5_label);
        // mAppList.add(4, item5);
        // CategoryItem item6 = new CategoryItem();
        // item6.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_6_logo);
        // item6.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_6_bg);
        // item6.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_6_shadow);
        // item6.mLabel = mResources.getString(R.string.categore_app_6_label);
        // mAppList.add(5, item6);
        // CategoryItem item7 = new CategoryItem();
        // item7.mAppIcon =
        // mResources.getDrawable(R.drawable.categore_app_7_logo);
        // item7.mBackgroundIcon =
        // mResources.getDrawable(R.drawable.categore_app_7_bg);
        // item7.mShadowIcon =
        // mResources.getDrawable(R.drawable.categore_app_7_shadow);
        // item7.mLabel = mResources.getString(R.string.categore_app_7_label);
        // mAppList.add(6, item7);
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
                    mSelectedApp = mAllAppList.get(0);
                } else if (mThumb_2_layout.isFocused()) {
                    mSelectedApp = mAllAppList.get(1);
                } else if (mThumb_3_layout.isFocused()) {
                    mSelectedApp = mAllAppList.get(2);
                }
                refreshFeatureMenuView();
                if (mSelectedApp.mGroup.mModules.get(0).moduleReplace == 0) {
                    // can't replace
                    Log.d(TAG, "cacn't replace the app");
                    Toast.makeText(mContext, R.string.can_not_replace, Toast.LENGTH_SHORT).show();
                    return true;
                } else if (mSelectedApp.mGroup.mModules.get(0).moduleReplace == 1) {
                    final DialogFragment newFragment = AppDialogFragment.newInstance(Launcher.this);
                    newFragment.show(getFragmentManager(), "dialog");
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mThumb_1_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(1);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mThumb_1_layout.isFocused()) {
                    Log.d(TAG, "mThumb_1_layout ");
                    mGridView.setSelection(0);
                    mGridView.requestFocus();
                    mSelectedApp = mAllAppList.get(3);
                } else if (mThumb_2_layout.isFocused()) {
                    Log.d(TAG, "mThumb_2_layout ");
                    mThumb_3_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(2);
                } else if (mThumb_3_layout.isFocused()) {
                    Log.d(TAG, "mThumb_3_layout ");
                    mGridView.setSelection(0);
                    mGridView.requestFocus();
                    mSelectedApp = mAllAppList.get(3);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mGridView.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(0);
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(1);
                }
                refreshFeatureMenuView();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mThumb_2_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(0);
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                    mSelectedApp = mAllAppList.get(0);
                }
                refreshFeatureMenuView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setResult(ApplicationInfo app, boolean isSelected) {
        if (isSelected && app != null && mSelectedApp != null) {
            Log.d("jzh", "setResult " + app.toString());
            String value = mToolUtils.getConfigured(mContext, app.getPackageName());
            if (!TextUtils.isEmpty(value)) {
                if (value.equals(mSelectedApp.mGroup.mModules.get(0).getModuleCode())) {
                    mToolUtils.clearConfiguredPkg(mContext, app.getPackageName());
                } else {
                    Log.d("jzh", "setResult cancel for duplicate ");
                    Toast.makeText(mContext, R.string.duplicate_alert, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mToolUtils.setConfigured(mContext, app.getPackageName(), mSelectedApp.mGroup.mModules
                    .get(0).getModuleCode());
            mToolUtils.setConfiguredPkg(mContext, mSelectedApp.mGroup.mModules.get(0)
                    .getModuleCode(), app.getPackageName());
            mSelectedApp.mGroup.mModules.get(0).moduleIconDrawable = app.getIcon();
            mSelectedApp.mGroup.mModules.get(0).moduleText = app.getTitle();
            mSelectedApp.mGroup.mModules.get(0).moduleReplace = 1;
            mSelectedApp.mGroup.mModules.get(0).mApps.get(0).componentName = app.mComponent;
            notifyAppList(mSelectedApp);
            refreshThumbnail();
            mCategoryItemAdapter.notifyDataSetChanged();
            Log.d("jzh", "setResult  RESULT_OK " + app.toString());
        } else {
            setResult(RESULT_CANCELED, null);
            Log.d("jzh", "setResult RESULT_CANCELED ");
        }
    }

    private void notifyAppList(CustomApplication newApp) {
        for (int i = 0; i < mAllAppList.size(); i++) {
            if (newApp.mGroup.mModules.get(0).getModuleCode()
                    .equals(mAllAppList.get(i).mGroup.mModules.get(0).getModuleCode())) {
                mAllAppList.set(i, newApp);
            }
        }
    }

    private void notifyAllAppList() {
        for (int i = 0; i < mAllAppList.size(); i++) {
            CustomApplication application = mAllAppList.get(i);
            final Module module = application.mGroup.mModules.get(0);
            String key = application.mGroup.mModules.get(0).moduleCode;
            String pkg = mToolUtils.getConfiguredPkg(mContext, key);
            Log.d(TAG, "key  for " + key + pkg);
            if (!TextUtils.isEmpty(pkg)) {
                ApplicationInfo app = mAppManager.getInfoFromAllActivitys(pkg);
                if (app != null) {
                    module.moduleIconDrawable = app.getIcon();
                    module.moduleText = app.getTitle();
                    module.mApps.get(0).componentName = app.mComponent;
                    // notifyAppList(application);
                } else {
                    mToolUtils.clearConfiguredPkg(mContext, key);
                }
            }
        }
        Log.d(TAG, mAllAppList.toString());
    }

    class LauncherHandler extends Handler {

        public static final int RETURN_CATEGORY_CONFIG_CODE = 1;
        public static final int RETURN_UNZIP_CONFIG_CODE = 2;
        public static final int RETURN_HIDDEN_CONFIG_CODE = 3;
        public static final int RETURN_UPDATE_CONFIG_CODE = 4;
        public static final int RETURN_SYSTEM_CONFIG_CODE = 5;
        public static final int SHOW_FEATURE_VIEW = 6;
        public static final int DISMISS_FEATURE_VIEW = 7;
        public static final int NO_OPERATION = 8;

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
                case RETURN_UNZIP_CONFIG_CODE:

                    break;
                case RETURN_HIDDEN_CONFIG_CODE:
                    break;
                case RETURN_UPDATE_CONFIG_CODE:
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
            }
            super.handleMessage(msg);
        }
    }

    private void refreshFeatureMenuView() {
        try {
            if (mSelectedApp.mGroup.mModules.get(0).moduleReplace == 0) {
                mFeatureView.setText(R.string.feature_menu_1);
            } else if (mSelectedApp.mGroup.mModules.get(0).moduleReplace == 1) {
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
        // }
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
                        mDownloadTask = new DownloadTask(DOWNLOAD_TO_PATH, downloadUrl, listener);
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

    
}
