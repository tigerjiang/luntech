
package com.luntech.launcher;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
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
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppDialogFragment;

import java.util.ArrayList;
import java.util.Iterator;

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
    private ImageView mThumb_1_view;
    private ImageView mThumb_1_shadow;
    private TextView mThumb_1_label;

    private RelativeLayout mThumb_2_layout;
    private ImageView mThumb_2_view;
    private ImageView mThumb_2_shadow;
    private TextView mThumb_2_label;

    private RelativeLayout mThumb_3_layout;
    private ImageView mThumb_3_view;
    private ImageView mThumb_3_shadow;
    private TextView mThumb_3_label;
    private ToolUtils mToolUtils;

    private static PackageInfo sPackageInfo;
    private static String sPackageName;
    private static int sVersionCode;
    private static final long REQUEST_DELAY_TIME = 10 * 1000;
    private Handler mHandler;
    private HandlerThread mThread;

    CustomApplication mFirstApp;
    CustomApplication mSecondApp;
    CustomApplication mThirdApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = getApplicationContext();
        sPackageName = this.getPackageName();
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
        mThread = new HandlerThread("launcher", Process.THREAD_PRIORITY_DISPLAY);
        mThread.start();
        mHandler = new LauncherHandler(mThread.getLooper());
    }

    private void initPrecondition() {
        if (!HttpUtils.checkConnectivity(mContext)) {
            return;
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                String httpArg = "&package_name=" + sPackageName + "&version=" + sVersionCode;
                String url = HttpUtils.HTTP_CONFIG_APP_URL + httpArg;

                String result = HttpUtils.requestResourcesFromServer(url);
                if (TextUtils.isEmpty(result)) {
                    Logger.e("Doesn't found any info from server");
                    return;
                } else {
                    Logger.d("result = " + result);
                    Message msg = mHandler
                            .obtainMessage(LauncherHandler.RETURN_CATEGORY_CONFIG_CODE);
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }

            }
        }, REQUEST_DELAY_TIME);

    }

    private void initView() {

        mThumb_1_layout = (RelativeLayout) findViewById(R.id.thumb_1_layout);
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
        mAppManager = AppManager.getInstance();
        mAppManager.getAllApplications();
        mGridView = (GridView) findViewById(R.id.category_layout);

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
            Toast.makeText(mContext, "App no found for " + componentName, Toast.LENGTH_SHORT)
                    .show();

            Log.d(TAG, e.toString());
        }
    }

    private void refreshThumbnail() {
        mFirstApp = mAllAppList.get(0);
        mSecondApp = mAllAppList.get(1);
        mThirdApp = mAllAppList.get(2);
        final Module module1 = mFirstApp.mGroup.mModules.get(0);
        Logger.d("first module " + module1.toString());
        mThumb_1_view.setBackground(module1.getModuleBgDrawable());
        mThumb_1_shadow.setImageDrawable(module1.getModuleShadowDrawable());
        mThumb_1_label.setText(module1.getModuleText());
        mThumb_1_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(module1.mApps.get(0).getComponentName());

            }
        });
        final Module module2 = mSecondApp.mGroup.mModules.get(0);
        Logger.d("second module " + module2.toString());
        mThumb_2_view.setBackground(module2.getModuleBgDrawable());
        mThumb_2_shadow.setImageDrawable(module2.getModuleShadowDrawable());
        mThumb_2_label.setText(module2.getModuleText());
        mThumb_2_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(module2.mApps.get(0).getComponentName());

            }
        });
        final Module module3 = mThirdApp.mGroup.mModules.get(0);
        Logger.d("third module " + module3.toString());
        mThumb_3_view.setBackground(module3.getModuleBgDrawable());
        mThumb_3_shadow.setImageDrawable(module3.getModuleShadowDrawable());
        mThumb_3_label.setText(module3.getModuleText());
        mThumb_3_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(module3.mApps.get(0).getComponentName());

            }
        });
    }

    private void registerReceiver() {
        mChangeReceiver = new ChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(mChangeReceiver, filter);
    }

    private void parseCategoryItem() {
        mAllAppList = ToolUtils.getCustomInfoFromConfig(mContext, R.xml.config);
        for (CustomApplication application : mAllAppList) {
            Logger.d("application " + application.mGroup.toString());
        }
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

        }
        // set our copy of the configuration for comparing with in
        // onConfigurationChanged
        mConfig.setTo(getResources().getConfiguration());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            Log.d(TAG, "action " + action + "    keycode" + keyCode);
            Log.d(TAG, "focus " + "    keycode" + keyCode);

            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (mThumb_1_layout.isFocused()) {
                    mSelectedApp = mAllAppList.get(0);
                } else if (mThumb_2_layout.isFocused()) {
                    mSelectedApp = mAllAppList.get(1);
                } else if (mThumb_3_layout.isFocused()) {
                    mSelectedApp = mAllAppList.get(2);
                }
                final DialogFragment newFragment = AppDialogFragment.newInstance(Launcher.this);
                newFragment.show(getFragmentManager(), "dialog");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mThumb_1_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                }
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
                } else if (mThumb_3_layout.isFocused()) {
                    Log.d(TAG, "mThumb_3_layout ");
                    mGridView.setSelection(0);
                    mGridView.requestFocus();
                    mSelectedApp = mAllAppList.get(3);
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mGridView.isFocused()) {
                    mThumb_1_layout.requestFocus();
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_2_layout.requestFocus();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mThumb_2_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                } else if (mThumb_3_layout.isFocused()) {
                    mThumb_1_layout.requestFocus();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * @Override protected void onActivityResult(int requestCode, int
     * resultCode, Intent data) { super.onActivityResult(requestCode,
     * resultCode, data); Log.d(TAG, "requestCode "+ requestCode +
     * "resultCode  "+resultCode); switch (resultCode) { case RESULT_OK:
     * Log.d(TAG, "RESULT_OK"); ApplicationInfo app =
     * data.getParcelableExtra("app"); Log.d(TAG, "app "+app.toString());
     * mToolUtils.setConfigured(mContext, app, true);
     * mToolUtils.setConfiguredPkg(mContext, mSelectedApp.mIndex, app);
     * mSelectedApp.mAppIcon = app.getIcon(); mSelectedApp.mLabel =
     * app.getTitle(); mSelectedApp.mComponentName = app.mComponent;
     * notifyAppList(mSelectedApp); mCategoryItemAdapter.notifyDataSetChanged();
     * break; case RESULT_CANCELED: Log.d(TAG, "RESULT_CANCELED"); break;
     * default: break; } }
     */

    public void setResult(ApplicationInfo app, boolean isSelected) {
        if (isSelected && app != null && mSelectedApp != null) {
            Log.d("jzh", "setResult " + app.toString());
            if ((mToolUtils.getConfigured(mContext,
                    app.toString()))) {
                Log.d("jzh", "setResult cancel for duplicate ");
                Toast.makeText(mContext, R.string.duplicate_alert, Toast.LENGTH_SHORT).show();
                return;
            }
            mToolUtils.setConfigured(mContext, app, true);
            mToolUtils.setConfiguredPkg(mContext, mSelectedApp.mGroup.mModules.get(0)
                    .getModuleCode(), app.getPackageName());
            mSelectedApp.mGroup.mModules.get(0).moduleIconDrawable = app.getIcon();
            mSelectedApp.mGroup.mModules.get(0).moduleText = app.getTitle();
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
        public static final int RETURN_HIDDEN_CONFIG_CODE = 2;
        public static final int RETURN_UPDATE_CONFIG_CODE = 3;
        public static final int RETURN_SYSTEM_CONFIG_CODE = 4;

        public LauncherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            if (TextUtils.isEmpty(result)) {
                Logger.e("result is empty");
            }
            switch (msg.what) {
                case RETURN_CATEGORY_CONFIG_CODE:

                break;
                case RETURN_HIDDEN_CONFIG_CODE:
                break;
                case RETURN_UPDATE_CONFIG_CODE:
                break;
                case RETURN_SYSTEM_CONFIG_CODE:
                break;
            }
            super.handleMessage(msg);
        }
    }

    
}
