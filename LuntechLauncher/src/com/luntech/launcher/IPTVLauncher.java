
package com.luntech.launcher;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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


public class IPTVLauncher extends Launcher {

    private static final String TAG = "IPTVLauncher";
    private static final boolean DEBUG = true;
    private GridView mGridView;
    private Module mSelectedApp;
    private ModuleAdapter mModuleAdapter;
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


    private Handler mHandler;

    private int mGridPosition = 0;

    Module mFirstApp;
    Module mSecondApp;
    Module mThirdApp;

    private RelativeLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iptv_home_layout);
        AppManager.create(this);
        initHandler();
        initParams();
        parseGroupsFromDB();
        initView();
        initScreenSaverTime();
        initPrecondition();
    }


    private void initHandler() {
        mHandler = new LauncherHandler();
        mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
        mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW, Launcher.SHOW_DELAY_TIME);
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
        mAppManager.getAllApplications();
        mAppManager.getSelectedApplications();
        mGridView = (GridView) findViewById(R.id.category_layout);
        String adContent = ToolUtils.getCommonValueFromSP(mContext, ADVERTISEMENT_KEY);
        StringBuilder adSb = new StringBuilder();
        Log.d(TAG, "ad" + adContent);
        if (!TextUtils.isEmpty(adContent)) {
            adSb.append(adContent);
            if (adContent.length() < 255) {
                for (int i = 0; i < 1000; i++) {
                    adSb.append(" ");
                }
            }
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
        mModuleAdapter = new ModuleAdapter(mModules, mContext);
        mGridView.setAdapter(mModuleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedApp = (Module) parent.getItemAtPosition(position);
                App app = mSelectedApp.mApps.get(0);
                Logger.d(" clicke app for " + app.toString());
                if (mToolUtils.isExsitsKey(mContext, mSelectedApp.getModuleCode())) {
                    String pkg = mToolUtils.getConfiguredPkg(mContext, mSelectedApp.getModuleCode());
                    AppManager appManager = AppManager.getInstance();
                    appManager.getAllApplications();
                    ApplicationInfo descApp = appManager.getInfoFromAllActivitys(pkg);
                    if (descApp != null) {
                        descApp.startApplication(mContext);
                    } else {
                        mToolUtils.clearConfiguredPkg(mContext, mSelectedApp.getModuleCode());
                        mToolUtils.clearConfiguredPkg(mContext, pkg);
                        ToolUtils.safeStartApk(IPTVLauncher.this, app);
                    }
                } else {
                    ToolUtils.safeStartApk(IPTVLauncher.this, app);
                }
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

    private void refreshLocakedThumbnail() {
        mSelectedApp = mModules.get(0);
        mFirstApp = mModules.get(0);
        mSecondApp = mModules.get(1);
        mThirdApp = mModules.get(2);
        refreshFeatureMenuView();
        Bitmap icon = new AsyncImageLoader(mContext).loadDrawable(mFirstApp.getModuleBg(), mThumb_1_view, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (icon == null) {
            mThumb_1_view.setImageResource(R.drawable.global_thumb_1_logo);
        } else {
            mThumb_1_view.setImageBitmap(icon);
        }
        // mThumb_1_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        Bitmap shadow = new AsyncImageLoader(mContext).loadDrawable(mFirstApp.getModuleShadow(), mThumb_1_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (shadow == null) {
            mThumb_1_shadow.setImageResource(R.drawable.global_thumb_1_shadow);
        } else {
            mThumb_1_shadow.setImageBitmap(shadow);
        }
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
        Bitmap icon2 = new AsyncImageLoader(mContext).loadDrawable(mSecondApp.getModuleBg(), mThumb_2_view, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (icon2 == null) {
            mThumb_2_view.setImageResource(R.drawable.global_thumb_2_logo);
        } else {
            mThumb_2_view.setImageBitmap(icon2);
        }
        // mThumb_1_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        // module1.getModuleIcon()));
        Bitmap shadow2 = new AsyncImageLoader(mContext).loadDrawable(mSecondApp.getModuleShadow(), mThumb_2_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (shadow2 == null) {
            mThumb_2_shadow.setImageResource(R.drawable.global_thumb_2_shadow);
        } else {
            mThumb_2_shadow.setImageBitmap(shadow2);
        }
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

        Bitmap icon3 = new AsyncImageLoader(mContext).loadDrawable(mThirdApp.getModuleBg(), mThumb_3_view, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (icon3 == null) {
            mThumb_3_view.setImageResource(R.drawable.global_thumb_3_logo);
        } else {
            mThumb_3_view.setImageBitmap(icon3);
        }
        // mThumb_1_view.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
        Bitmap shadow3 = new AsyncImageLoader(mContext).loadDrawable(mThirdApp.getModuleShadow(), mThumb_3_shadow, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap imageDrawable, ImageView imageView, String imageUrl) {
                imageView.setImageBitmap(imageDrawable);
            }
        });
        if (shadow3 == null) {
            mThumb_3_shadow.setImageResource(R.drawable.global_thumb_3_shadow);
        } else {
            mThumb_3_shadow.setImageBitmap(shadow3);
        }
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

                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });
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
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
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
//                    final DialogFragment newFragment = AppDialogFragment.newInstance(IPTVLauncher.this);
//                    newFragment.show(getFragmentManager(), "dialog");
                    Intent intent = new Intent();
                    intent.setClass(mContext, AppSelectedActivity.class);
                    startActivityForResult(intent, 1);
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
                Log.d("jzh", "setResult cancel for duplicate ");
                Toast.makeText(mContext, R.string.duplicate_alert, Toast.LENGTH_SHORT).show();
                return;
            }
            if (mToolUtils.isExsitsKey(mContext, mSelectedApp.getModuleCode())) {
                String key = mToolUtils.getConfigured(mContext, mSelectedApp.getModuleCode());
//                mToolUtils.clearConfiguredPkg(mContext, mSelectedApp.getModuleCode());
                mToolUtils.clearConfiguredPkg(mContext, key);
            }
            mToolUtils.setConfigured(mContext, app.getPackageName(), mSelectedApp.getModuleCode());
            mToolUtils.setConfiguredPkg(mContext, mSelectedApp.getModuleCode(), app.getPackageName());
            mSelectedApp.moduleIconDrawable = app.getIcon();
            mSelectedApp.moduleText = app.getTitle();
            mSelectedApp.moduleReplace = 1;
            mSelectedApp.mApps.get(0).componentName = app.mComponent;
            Log.d("replace", " mSelectedApp " + mSelectedApp.toString());
            notifyModuleList(mSelectedApp);
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


    class LauncherHandler extends Handler {

        public static final int SHOW_FEATURE_VIEW = 1;
        public static final int DISMISS_FEATURE_VIEW = 2;
        public static final int NO_OPERATION = 3;
        public static final int SHOW_SCREEN_SAVER = 4;

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
                case SHOW_FEATURE_VIEW:
                    // Log.d("show", "SHOW_FEATURE_VIEW");
                    mFeatureMenuLayout.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(LauncherHandler.DISMISS_FEATURE_VIEW);
                    mHandler.sendEmptyMessageDelayed(LauncherHandler.DISMISS_FEATURE_VIEW,
                            Launcher.DISMISS_DELAY_TIME);
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
                    Log.d("show", "SHOW_SCREEN_SAVER");
                    if (ToolUtils.isApplicationBroughtToBackground(mContext)) {
                        Log.d("show", "current task  is background, can't bring up the screensaver");
                    } else {
                        sendBroadcast(new Intent(SHOW_SCREENSAVER_ACTION));
                    }
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

    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());

        super.onDestroy();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
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
        if (!sCloseScreenSaver) {
            Message msg = mHandler.obtainMessage(LauncherHandler.SHOW_SCREEN_SAVER);
            mHandler.removeMessages(LauncherHandler.SHOW_SCREEN_SAVER);
            mHandler.sendMessageDelayed(msg, showScreenSaverTime);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initParams() {
        mCategoryFile = getCategoryFileName();
        mFilePrefix = getFilePrefix();
        mType = getType();
        mAdConfigureFile = getAdConfigureFile();
        mHiddenFile = getHiddenConfigureFile();
    }

    public String getType() {
        return IPTV_TYPE;
    }

    public String getAdConfigureFile() {
        return IPTV_AD_CONFIGURE_FILE;
    }

    public String getFilePrefix() {
        return IPTV_FILE_PREFIX;
    }

    public String getCategoryFileName() {
        return Launcher.IPTV_CATEGORY_FILE;
    }

    public String getHiddenConfigureFile() {
        return IPTV_HIDDEN_FILE;
    }
}
