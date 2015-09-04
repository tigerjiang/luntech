package com.luntech.launcher;


import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hisense.network.utils.EpgDataInfoLoader.HiLauncherLoader;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppDialogFragment;


import java.io.File;
import java.util.List;
import java.util.Map;


public class Q1SLauncher extends Launcher implements View.OnFocusChangeListener, HiLauncherLoader.Callbacks {

    private static final String TAG = "Q1SLauncher";

    //bg image
    private FrameLayout bgFrameLayout;

    //channel menu
    private com.luntech.launcher.view.QisItem channelImView;
    private  com.luntech.launcher.view.QisItem channelImView_unfold;

    //vod menu
    private  com.luntech.launcher.view.QisItem  vodImView;
    private com.luntech.launcher.view.QisItem vodImView_unfold;

    //application menu
    private com.luntech.launcher.view.QisItem applicationImView;
    private LinearLayout app_unfold_Layout;
    private com.luntech.launcher.view.QisItem apprecomView1;
    private com.luntech.launcher.view.QisItem apprecomView2;
    private com.luntech.launcher.view.QisItem apprecomView3;

    //fovarite menu
    private com.luntech.launcher.view.QisItem favoriteImView;
    private LinearLayout favorite_unfold_Layout;
    private com.luntech.launcher.view.QisItem myFavoriteView;
    private com.luntech.launcher.view.QisItem playhistoryView;
    private com.luntech.launcher.view.QisItem favpersonView;
    private com.luntech.launcher.view.QisItem localView;

    //setting menu
    private com.luntech.launcher.view.QisItem settingImView;
    private LinearLayout setting_unfold_layout;
    private com.luntech.launcher.view.QisItem settingBaseView;
    private com.luntech.launcher.view.QisItem settingDispalyView;
    private com.luntech.launcher.view.QisItem settingNetView;
    private com.luntech.launcher.view.QisItem settingUpdateView;
    private com.luntech.launcher.view.QisItem settingMoreView;
    private com.luntech.launcher.view.QisItem settingErweiView;
    //private ImageView settingImView_unfold;
    //module
    private Module mChannelModule, mVodModule, mAppModule1, mAppModule2, mAppModule3,
            mZhihuiModule1, mZhihuiModule2, mZhihuiModule3, mZhihuiModule4, mSettingModue1, mSettingModue2, mSettingModue3,
            mSettingModue4,
            mSettingModue5, mSettingModue6;
    private Group mChannelGroup,mVodGroup,mAppGroup,mSettingGroup,mZhihuiGroup;

    private com.luntech.launcher.view.QisItem mSelectedView;
    private  com.luntech.launcher.view.QisItem mSelectedUnFocusView;

    private View mLastFocusView;
    //main menu background layout
    private FrameLayout focusBkLayout;
    private ImageView focusBkImageView;

    //favorite and play history layout and listview
    private boolean isMyFavoriteItemshow = false;
    private boolean isPlayHistoryItemshow = false;
    private boolean isFavPersonItemshow = false;


    private TranslateAnimation translationAnimDown = null;
    private TranslateAnimation translationAnimRight = null;
    private TranslateAnimation translationAnimTop = null;
    private TranslateAnimation translationAnimLeft = null;


    private int KEY_DIRECTION = -1;


    private HiLauncherLoader mLoader;

    private ToneGenerator mToneGenerator;
    private Object mToneGeneratorLock = new Object();

    private boolean NetConfigSuccess = false;
    private int viewIndex = 0;

    private final static String IMGPATH_STRING = "/data/local/misc/";
    private final static String LAUNCHER_DIR_STRING = "/data/local/misc/launcher";

    private Map<String, String> convertMap = null;

    private IntentFilter mIntentFilter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.q1s_home_layout);
        AppManager.create(this);
        initHandler();
        initParams();
        parseGroupsFromDB();
        notifyAllModuleList();
        initData();
        LauncherApplication app = ((LauncherApplication) getApplication());
        mLoader = app.setLauncher(this);


        initHomeView();

        if (savedInstanceState != null) {
            viewIndex = savedInstanceState.getInt("lastviewindex");
            Log.d(TAG, " savedInstanceState != null onCreate viewIndex = " + viewIndex);
            {
                switch (viewIndex) {
                    case 0:
                        channelImView_unfold.setVisibility(View.VISIBLE);
                        channelImView.setVisibility(View.GONE);
                        setChannelImViewFocus();
                        mLastFocusView = channelImView_unfold;
                        break;
                    case 1:
                        vodImView_unfold.setVisibility(View.VISIBLE);
                        vodImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setVodImViewFocus();
                        mLastFocusView = vodImView_unfold;
                        break;
                    case 2:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setSettingBaseImViewFocus();
                        mLastFocusView = settingBaseView;
                        break;
                    case 3:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setSettingDisplayImViewFocus();
                        mLastFocusView = settingDispalyView;
                        break;
                    case 4:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setSettingNetImViewFocus();
                        mLastFocusView = settingNetView;
                        break;
                    case 5:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setSettingUpdateImViewFocus();
                        mLastFocusView = settingUpdateView;
                        break;
                    case 6:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        setSettingMoreImViewFocus();
                        channelImView.setVisibility(View.VISIBLE);
                        mLastFocusView = settingMoreView;
                        break;
                    case 7:
                        setting_unfold_layout.setVisibility(View.VISIBLE);
                        settingImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setSettingErweiImViewFocus();
                        mLastFocusView = settingErweiView;
                        break;
                    case 8:
                        app_unfold_Layout.setVisibility(View.VISIBLE);
                        applicationImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setApprecomView1Focus();
                        mLastFocusView = apprecomView1;
                        break;
                    case 9:
                        app_unfold_Layout.setVisibility(View.VISIBLE);
                        applicationImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setApprecomView2Focus();
                        mLastFocusView = apprecomView2;
                        break;
                    case 10:
                        app_unfold_Layout.setVisibility(View.VISIBLE);
                        applicationImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setApprecomView3Focus();
                        mLastFocusView = apprecomView3;
                        break;
                    case 11:
                        favorite_unfold_Layout.setVisibility(View.VISIBLE);
                        favoriteImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setMyFavoriteViewFocus();
                        mLastFocusView = myFavoriteView;
                        break;
                    case 12:
                        favorite_unfold_Layout.setVisibility(View.VISIBLE);
                        favoriteImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setPlayhistoryViewFocus();
                        mLastFocusView = playhistoryView;
                        break;
                    case 13:
                        favorite_unfold_Layout.setVisibility(View.VISIBLE);
                        favoriteImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setFavpersonViewFocus();
                        mLastFocusView = favpersonView;
                        break;
                    case 14:
                        favorite_unfold_Layout.setVisibility(View.VISIBLE);
                        favoriteImView.setVisibility(View.GONE);
                        channelImView.setVisibility(View.VISIBLE);
                        setLocalViewFocus();
                        mLastFocusView = localView;
                        break;
                    default:
                        break;
                }
                mLastFocusView.requestFocus();
                mLastFocusView = getCurrentFocus();
            }
        } else {
            channelImView_unfold.setVisibility(View.VISIBLE);
            channelImView_unfold.requestFocus();
            mLastFocusView = getCurrentFocus();
        }

        InitAnim();

    }

    private void initHandler() {
        mHandler = new LauncherHandler();
        mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
        mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW, Launcher.SHOW_DELAY_TIME);
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
//                    mFeatureMenuLayout.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(LauncherHandler.DISMISS_FEATURE_VIEW);
                    mHandler.sendEmptyMessageDelayed(LauncherHandler.DISMISS_FEATURE_VIEW,
                            Launcher.DISMISS_DELAY_TIME);
                    break;
                case DISMISS_FEATURE_VIEW:
                    Log.d("show", "DISMISS_FEATURE_VIEW");
//                    mFeatureMenuLayout.setVisibility(View.GONE);
                    break;
                case NO_OPERATION:
                    Log.d("show", "NO_OPERATION");
//                    mFeatureMenuLayout.setVisibility(View.GONE);
                    mHandler.removeMessages(LauncherHandler.SHOW_FEATURE_VIEW);
                    mHandler.sendEmptyMessageDelayed(LauncherHandler.SHOW_FEATURE_VIEW,
                            SHOW_DELAY_TIME);
                    break;
                case SHOW_SCREEN_SAVER:
                    Log.d("show", "SHOW_SCREEN_SAVER");
                    Intent intent = new Intent(mContext, ScreenSaverActivity.class);
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
        Log.d("show", "onUserInteraction");
//        mHandler.removeMessages(LauncherHandler.NO_OPERATION);
//        mHandler.sendEmptyMessage(LauncherHandler.NO_OPERATION);
//        restartSendShowScreenSaver();
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
    protected void onStart() {
        super.onStart();
        initScreenSaverTime();
        initPrecondition();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Log.d(TAG, "##### on touch event #####");
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        if ((action & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_DOWN) {
            return true;
        }

        Log.d(TAG, "currentX = " + x);
        Log.d(TAG, "currentY = " + y);

        if ((y > 164) && (y < 601)) {
            //channel
            if (mLastFocusView == channelImView_unfold) {
                if ((x > 777) && (x < 882)) {
                    channelImView_unfold.setVisibility(View.GONE);
                    channelImView.setVisibility(View.VISIBLE);

                    vodImView_unfold.setVisibility(View.VISIBLE);
                    vodImView.setVisibility(View.GONE);
                    setVodImViewFocus();
                    mLastFocusView = vodImView_unfold;
                } else if ((x > 882) && (x < 987)) {
                    channelImView_unfold.setVisibility(View.GONE);
                    channelImView.setVisibility(View.VISIBLE);

                    favorite_unfold_Layout.setVisibility(View.VISIBLE);
                    favoriteImView.setVisibility(View.GONE);
                    myFavoriteView.requestFocus();
                    setMyFavoriteViewFocus();
                    mLastFocusView = myFavoriteView;
                } else if ((x > 987) && (x < 1092)) {
                    channelImView_unfold.setVisibility(View.GONE);
                    channelImView.setVisibility(View.VISIBLE);

                    app_unfold_Layout.setVisibility(View.VISIBLE);
                    applicationImView.setVisibility(View.GONE);
                    apprecomView1.requestFocus();
                    setApprecomView1Focus();
                    mLastFocusView = apprecomView1;
                } else if ((x > 1092) && (x < 1197)) {
                    channelImView_unfold.setVisibility(View.GONE);
                    channelImView.setVisibility(View.VISIBLE);

                    setting_unfold_layout.setVisibility(View.VISIBLE);
                    settingImView.setVisibility(View.GONE);
                    settingBaseView.requestFocus();
                    setSettingBaseImViewFocus();
                    mLastFocusView = settingBaseView;
                }
            } else if (mLastFocusView == vodImView_unfold) {  //vod
                if ((x > 73) && (x < 178)) {
                    vodImView_unfold.setVisibility(View.GONE);
                    vodImView.setVisibility(View.VISIBLE);

                    channelImView_unfold.setVisibility(View.VISIBLE);
                    channelImView.setVisibility(View.GONE);
                    setChannelImViewFocus();
                    channelImView.requestFocus();
                    mLastFocusView = channelImView_unfold;
                } else if ((x > 882) && (x < 987)) {
                    vodImView_unfold.setVisibility(View.GONE);
                    vodImView.setVisibility(View.VISIBLE);

                    favorite_unfold_Layout.setVisibility(View.VISIBLE);
                    favoriteImView.setVisibility(View.GONE);
                    myFavoriteView.requestFocus();
                    setMyFavoriteViewFocus();
                    mLastFocusView = myFavoriteView;
                } else if ((x > 987) && (x < 1092)) {
                    vodImView_unfold.setVisibility(View.GONE);
                    vodImView.setVisibility(View.VISIBLE);

                    app_unfold_Layout.setVisibility(View.VISIBLE);
                    applicationImView.setVisibility(View.GONE);
                    apprecomView1.requestFocus();
                    setApprecomView1Focus();
                    mLastFocusView = apprecomView1;
                } else if ((x > 1092) && (x < 1197)) {
                    vodImView_unfold.setVisibility(View.GONE);
                    vodImView.setVisibility(View.VISIBLE);

                    setting_unfold_layout.setVisibility(View.VISIBLE);
                    settingImView.setVisibility(View.GONE);
                    settingBaseView.requestFocus();
                    setSettingBaseImViewFocus();
                    mLastFocusView = settingBaseView;
                }
            }
            //settings
            else if ((mLastFocusView == settingBaseView) || (mLastFocusView == settingDispalyView)
                    || (mLastFocusView == settingNetView) || (mLastFocusView == settingUpdateView)
                    || (mLastFocusView == settingMoreView) || (mLastFocusView == settingErweiView)) {
                Log.d(TAG, "current focus = settings");
                if ((x > 73) && (x < 178)) {
                    setting_unfold_layout.setVisibility(View.GONE);
                    settingImView.setVisibility(View.VISIBLE);

                    channelImView_unfold.setVisibility(View.VISIBLE);
                    channelImView.setVisibility(View.GONE);
                    setChannelImViewFocus();
                    channelImView.requestFocus();
                    mLastFocusView = channelImView_unfold;
                } else if ((x > 178) && (x < 283)) {
                    setting_unfold_layout.setVisibility(View.GONE);
                    settingImView.setVisibility(View.VISIBLE);

                    vodImView_unfold.setVisibility(View.VISIBLE);
                    vodImView.setVisibility(View.GONE);
                    vodImView.requestFocus();
                    setVodImViewFocus();
                    mLastFocusView = vodImView_unfold;
                } else if ((x > 283) && (x < 388)) {

                    setting_unfold_layout.setVisibility(View.GONE);
                    settingImView.setVisibility(View.VISIBLE);

                    favorite_unfold_Layout.setVisibility(View.VISIBLE);
                    favoriteImView.setVisibility(View.GONE);
                    myFavoriteView.requestFocus();
                    setMyFavoriteViewFocus();
                    mLastFocusView = myFavoriteView;
                } else if ((x > 388) && (x < 493)) {

                    setting_unfold_layout.setVisibility(View.GONE);
                    settingImView.setVisibility(View.VISIBLE);

                    app_unfold_Layout.setVisibility(View.VISIBLE);
                    applicationImView.setVisibility(View.GONE);
                    setApprecomView1Focus();
                    apprecomView1.requestFocus();
                    mLastFocusView = apprecomView1;
                }
            }
            //app
            else if ((mLastFocusView == apprecomView1) || (mLastFocusView == apprecomView2)
                    || (mLastFocusView == apprecomView3)) {
                if ((x > 73) && (x < 178)) {
                    app_unfold_Layout.setVisibility(View.GONE);
                    applicationImView.setVisibility(View.VISIBLE);

                    channelImView_unfold.setVisibility(View.VISIBLE);
                    channelImView.setVisibility(View.GONE);
                    setChannelImViewFocus();
                    channelImView.requestFocus();
                    mLastFocusView = channelImView_unfold;
                } else if ((x > 178) && (x < 283)) {
                    app_unfold_Layout.setVisibility(View.GONE);
                    applicationImView.setVisibility(View.VISIBLE);

                    vodImView_unfold.setVisibility(View.VISIBLE);
                    vodImView.setVisibility(View.GONE);
                    vodImView.requestFocus();
                    setVodImViewFocus();
                    mLastFocusView = vodImView_unfold;
                } else if ((x > 283) && (x < 388)) {
                    app_unfold_Layout.setVisibility(View.GONE);
                    applicationImView.setVisibility(View.VISIBLE);

                    favorite_unfold_Layout.setVisibility(View.VISIBLE);
                    favoriteImView.setVisibility(View.GONE);
                    myFavoriteView.requestFocus();
                    setMyFavoriteViewFocus();
                    mLastFocusView = myFavoriteView;
                } else if ((x > 1092) && (x < 1197)) {
                    app_unfold_Layout.setVisibility(View.GONE);
                    applicationImView.setVisibility(View.VISIBLE);

                    setting_unfold_layout.setVisibility(View.VISIBLE);
                    settingImView.setVisibility(View.GONE);
                    settingBaseView.requestFocus();
                    setSettingBaseImViewFocus();
                    mLastFocusView = settingBaseView;
                }
            }
            //favorite
            else if ((mLastFocusView == playhistoryView) || (mLastFocusView == favpersonView)
                    || (mLastFocusView == localView) || (mLastFocusView == myFavoriteView)) {
                if ((x > 73) && (x < 178)) {
                    favorite_unfold_Layout.setVisibility(View.GONE);
                    favoriteImView.setVisibility(View.VISIBLE);

                    channelImView_unfold.setVisibility(View.VISIBLE);
                    channelImView.setVisibility(View.GONE);
                    setChannelImViewFocus();
                    channelImView.requestFocus();
                    mLastFocusView = channelImView_unfold;
                } else if ((x > 178) && (x < 283)) {
                    favorite_unfold_Layout.setVisibility(View.GONE);
                    favoriteImView.setVisibility(View.VISIBLE);

                    vodImView_unfold.setVisibility(View.VISIBLE);
                    vodImView.setVisibility(View.GONE);
                    vodImView.requestFocus();
                    setVodImViewFocus();
                    mLastFocusView = vodImView_unfold;
                } else if ((x > 987) && (x < 1092)) {
                    favorite_unfold_Layout.setVisibility(View.GONE);
                    favoriteImView.setVisibility(View.VISIBLE);

                    app_unfold_Layout.setVisibility(View.VISIBLE);
                    applicationImView.setVisibility(View.GONE);
                    apprecomView1.requestFocus();
                    setApprecomView1Focus();
                    mLastFocusView = apprecomView1;
                } else if ((x > 1092) && (x < 1197)) {
                    favorite_unfold_Layout.setVisibility(View.GONE);
                    favoriteImView.setVisibility(View.VISIBLE);

                    setting_unfold_layout.setVisibility(View.VISIBLE);
                    settingImView.setVisibility(View.GONE);
                    settingBaseView.requestFocus();
                    setSettingBaseImViewFocus();
                    mLastFocusView = settingBaseView;
                }
            }
        }


        return super.onTouchEvent(event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
    }


    protected void onResume() {
        super.onResume();

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                } catch (RuntimeException e) {
                    Log.i(TAG, "Exception caught while creating local tone generator: " + e);
                    mToneGenerator = null;
                }
            }
        }
    }


    public String queryAppInfoByPackageName(String packnameString) {
        String activityNameString = null;
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, 0);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        //Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(pkgName,
                    activityName));
            if (pkgName.equals(packnameString)) {
                activityNameString = activityName;
                Log.d(TAG, appLabel + " activityName---" + activityName
                        + " pkgName---" + pkgName);
                break;
            }
        }

        return activityNameString;
    }

    private void initData() {
        mChannelGroup = mGroups.get(0);
        mChannelModule = mChannelGroup.mModules.get(0);
        mVodGroup = mGroups.get(1);
        mVodModule = mVodGroup.mModules.get(0);
        mAppGroup = mGroups.get(3);
        mAppModule1 = mAppGroup.mModules.get(0);
        mAppModule2 = mAppGroup.mModules.get(1);
        mAppModule3 = mAppGroup.mModules.get(2);
        mZhihuiGroup = mGroups.get(2);
        mZhihuiModule1 = mZhihuiGroup.mModules.get(0);
        mZhihuiModule2 = mZhihuiGroup.mModules.get(1);
        mZhihuiModule3 = mZhihuiGroup.mModules.get(2);
        mZhihuiModule4 = mZhihuiGroup.mModules.get(3);
        mSettingGroup = mGroups.get(4);
        mSettingModue1 = mSettingGroup.mModules.get(0);
        mSettingModue2 = mSettingGroup.mModules.get(1);
        mSettingModue3 = mSettingGroup.mModules.get(2);
        mSettingModue4 = mSettingGroup.mModules.get(3);
        mSettingModue5 = mSettingGroup.mModules.get(4);
        mSettingModue6 = mSettingGroup.mModules.get(5);


    }


    public void onClick_Event(View view) {
        App app = null;
        if (view.getId() == vodImView_unfold.getId()) {
            app = mVodModule.mApps.get(0);
        } else if (view.getId() == channelImView_unfold.getId()) {
            app = mChannelModule.mApps.get(0);
        } else if (view.getId() == apprecomView1.getId()) {

            app = mAppModule1.mApps.get(0);

        } else if (view.getId() == apprecomView2.getId()) {
            app = mAppModule3.mApps.get(0);

        } else if (view.getId() == apprecomView3.getId()) {
            app = mAppModule2.mApps.get(0);

        } else if (view.getId() == myFavoriteView.getId()) {
            app = mZhihuiModule1.mApps.get(0);
        } else if (view.getId() == playhistoryView.getId()) {
            app = mZhihuiModule2.mApps.get(0);
        } else if (view.getId() == favpersonView.getId()) {
            app = mZhihuiModule3.mApps.get(0);
        } else if (view.getId() == settingBaseView.getId()) {
            app = mSettingModue1.mApps.get(0);
        } else if (view.getId() == settingDispalyView.getId()) {
            app = mSettingModue2.mApps.get(0);
        } else if (view.getId() == settingNetView.getId()) {
            app = mSettingModue3.mApps.get(0);
        } else if (view.getId() == settingUpdateView.getId()) {
            app =mSettingModue4.mApps.get(0);
        }
        //else if (view.getId() == settingImView_unfold.getId()){
        else if (view.getId() == settingMoreView.getId()) {
            app = mSettingModue5.mApps.get(0);
        } else if (view.getId() == settingErweiView.getId()) {
            app = mSettingModue6.mApps.get(0);
        } else if (view.getId() == localView.getId()) {
            app = mZhihuiModule4.mApps.get(0);
        }
        ToolUtils.safeStartApk(mContext, app);
    }


    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        int id = v.getId();
        mSelectedView = (com.luntech.launcher.view.QisItem)v;
        if (hasFocus) {
            if (channelImView_unfold.getId() == id) {
                mSelectedApp = mGroups.get(1).mModules.get(0);
                setChannelImViewFocus();
                mLastFocusView = channelImView_unfold;

            } else if (vodImView_unfold.getId() == id) {
                mSelectedApp = mGroups.get(0).mModules.get(0);
                setVodImViewFocus();
                mLastFocusView = vodImView_unfold;

            } else if (settingBaseView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(0);
                setSettingBaseImViewFocus();
                mLastFocusView = settingBaseView;
            } else if (settingDispalyView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(1);
                setSettingDisplayImViewFocus();
                mLastFocusView = settingDispalyView;
            } else if (settingNetView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(2);
                setSettingNetImViewFocus();
                mLastFocusView = settingNetView;
            } else if (settingUpdateView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(3);
                setSettingUpdateImViewFocus();
                mLastFocusView = settingUpdateView;
            } else if (settingMoreView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(4);
                setSettingMoreImViewFocus();
                mLastFocusView = settingMoreView;
            } else if (settingErweiView.getId() == id) {
                mSelectedApp = mGroups.get(4).mModules.get(5);
                setSettingErweiImViewFocus();
                mLastFocusView = settingErweiView;
            } else if (apprecomView1.getId() == id) {
                mSelectedApp = mGroups.get(3).mModules.get(0);
                setApprecomView1Focus();
                mLastFocusView = apprecomView1;

            } else if (apprecomView2.getId() == id) {
                mSelectedApp = mGroups.get(3).mModules.get(2);
                setApprecomView2Focus();
                mLastFocusView = apprecomView2;
            } else if (apprecomView3.getId() == id) {
                mSelectedApp = mGroups.get(3).mModules.get(1);
                setApprecomView3Focus();
                mLastFocusView = apprecomView3;
            } else if (myFavoriteView.getId() == id) {
                mSelectedApp = mGroups.get(2).mModules.get(0);
                setMyFavoriteViewFocus();
                mLastFocusView = myFavoriteView;
            } else if (playhistoryView.getId() == id) {
                mSelectedApp = mGroups.get(2).mModules.get(1);
                setPlayhistoryViewFocus();
                mLastFocusView = playhistoryView;

            } else if (favpersonView.getId() == id) {
                mSelectedApp = mGroups.get(2).mModules.get(2);
                setFavpersonViewFocus();
                mLastFocusView = favpersonView;

            } else if (localView.getId() == id) {
                mSelectedApp = mGroups.get(2).mModules.get(3);
                setLocalViewFocus();
                mLastFocusView = localView;
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        if (mLastFocusView == channelImView_unfold) {
            viewIndex = 0;
        } else if (mLastFocusView == vodImView_unfold) {
            viewIndex = 1;
        } else if (mLastFocusView == settingBaseView) {
            viewIndex = 2;
        } else if (mLastFocusView == settingDispalyView) {
            viewIndex = 3;
        } else if (mLastFocusView == settingNetView) {
            viewIndex = 4;
        } else if (mLastFocusView == settingUpdateView) {
            viewIndex = 5;
        } else if (mLastFocusView == settingMoreView) {
            viewIndex = 6;
        } else if (mLastFocusView == settingErweiView) {
            viewIndex = 7;
        } else if (mLastFocusView == apprecomView1) {
            viewIndex = 8;
        } else if (mLastFocusView == apprecomView2) {
            viewIndex = 9;
        } else if (mLastFocusView == apprecomView3) {
            viewIndex = 10;
        } else if (mLastFocusView == myFavoriteView) {
            viewIndex = 11;
        } else if (mLastFocusView == playhistoryView) {
            viewIndex = 12;
        } else if (mLastFocusView == favpersonView) {
            viewIndex = 13;
        } else if (mLastFocusView == localView) {
            viewIndex = 14;
        }

        outState.putInt("lastviewindex", viewIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        viewIndex = savedInstanceState.getInt("lastviewindex");
        Log.d(TAG, "onRestoreInstanceState viewIndex = " + viewIndex);

    }

    private void setFocusBkLayout(int padLeft, int padTop, int width, int height) {
        focusBkLayout.setVisibility(View.VISIBLE);
        focusBkImageView.setVisibility(View.VISIBLE);
        focusBkLayout.setPadding(padLeft, padTop, 0, 0);

        LayoutParams Params = focusBkImageView.getLayoutParams();
        Params.width = width;
        Params.height = height;
        focusBkImageView.setLayoutParams(Params);
        focusBkImageView.setBackgroundResource(R.drawable.menu_focus);
    }

    private void setTranslationAnimation(int direct, View v) {
        if (direct == Utility.DIRECTION_BOTTOM) {

            if (translationAnimDown != null)
                v.startAnimation(translationAnimDown);
        }
        if (direct == Utility.DIRECTION_RIGHT) {
            if (translationAnimRight != null)
                v.startAnimation(translationAnimRight);
        }
        if (direct == Utility.DIRECTION_LEFT) {
            if (translationAnimLeft != null)
                v.startAnimation(translationAnimLeft);

        }
        if (direct == Utility.DIRECTION_TOP) {
            if (translationAnimTop != null)
                v.startAnimation(translationAnimTop);
        }
    }

    private void setSettingBaseImViewFocus() {
        setFocusBkLayout(388 + 109, 166, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setSettingDisplayImViewFocus() {
        setFocusBkLayout(388 + 109 + 215 + 4, 166, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setSettingNetImViewFocus() {
        setFocusBkLayout(388 + 109 + 215 * 2 + 4 * 2, 166, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setSettingUpdateImViewFocus() {
        setFocusBkLayout(388 + 109, 166 + 184 + 3, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setSettingMoreImViewFocus() {
        setFocusBkLayout(388 + 109 + 215 + 4, 166 + 184 + 3, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setSettingErweiImViewFocus() {
        setFocusBkLayout(388 + 109 + 215 * 2 + 4 * 2, 166 + 184 + 3, 265, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }


    private void setChannelImViewFocus() {
        Log.d(TAG, "setChannelImViewFocus");
        setFocusBkLayout(73, 164, 714, 435);
        focusBkImageView.setBackgroundResource(R.drawable.menu_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setVodImViewFocus() {
        Log.d(TAG, "### set vod focus ###");
        setFocusBkLayout(178, 164, 714, 435);
        focusBkImageView.setBackgroundResource(R.drawable.menu_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setApprecomView1Focus() {
        setFocusBkLayout(391, 166, 264, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setApprecomView2Focus() {
        setFocusBkLayout(391, 166 + 189, 264, 230);
        focusBkImageView.setBackgroundResource(R.drawable.app_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setApprecomView3Focus() {
        Log.d(TAG, " setApprecomView3Focus ");
        setFocusBkLayout(388 + 191, 127, 553, 500);
        focusBkImageView.setBackgroundResource(R.drawable.yingyong4_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }


    private void setMyFavoriteViewFocus() {
        focusBkLayout.setVisibility(View.GONE);
        setFocusBkLayout(388 - 105, 163, 294, 427);
        focusBkImageView.setBackgroundResource(R.drawable.fovorite_focus1);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);

        startLauncherLoder();
    }

    private void setPlayhistoryViewFocus() {
        setFocusBkLayout(632 - 105, 163, 242, 427);
        focusBkImageView.setBackgroundResource(R.drawable.fovorite_focus2);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
        startLauncherLoder();

    }

    private void setFavpersonViewFocus() {
        setFocusBkLayout(826 - 107, 163, 274, 238);
        focusBkImageView.setBackgroundResource(R.drawable.fovorite_focus3);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setLocalViewFocus() {
        setFocusBkLayout(826 - 107, 163 + 190, 274, 238);
        focusBkImageView.setBackgroundResource(R.drawable.fovorite_focus3);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.d("replace", " mSelectedApp === " + mSelectedApp.toString());
            if (mSelectedApp.moduleReplace == 0) {
                // can't replace
                Log.d(TAG, "cacn't replace the app");
                Toast.makeText(mContext, R.string.can_not_replace, Toast.LENGTH_SHORT).show();
                return true;
            } else if (mSelectedApp.moduleReplace == 1) {
                final DialogFragment newFragment = AppDialogFragment.newInstance(Q1SLauncher.this);
                newFragment.show(getFragmentManager(), "dialog");
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

            KEY_DIRECTION = Utility.DIRECTION_LEFT;

            if (channelImView_unfold.hasFocus()) {
                setting_unfold_layout.setVisibility(View.VISIBLE);
                settingImView.setVisibility(View.GONE);

                channelImView_unfold.setVisibility(View.GONE);
                channelImView.setVisibility(View.VISIBLE);
                setChannelImView();

                settingNetView.requestFocus();

                return true;
            } else if (vodImView_unfold.hasFocus()) {
                vodImView_unfold.setVisibility(View.GONE);
                vodImView.setVisibility(View.VISIBLE);

                channelImView_unfold.setVisibility(View.VISIBLE);
                channelImView.setVisibility(View.GONE);

                channelImView_unfold.requestFocus();
                return true;
            } else if (apprecomView2.hasFocus()) {

                app_unfold_Layout.setVisibility(View.GONE);
                applicationImView.setVisibility(View.VISIBLE);
                favorite_unfold_Layout.setVisibility(View.VISIBLE);
                favoriteImView.setVisibility(View.GONE);

                localView.requestFocus();
                return true;

            } else if (apprecomView1.hasFocus()) {
                app_unfold_Layout.setVisibility(View.GONE);
                applicationImView.setVisibility(View.VISIBLE);

                favorite_unfold_Layout.setVisibility(View.VISIBLE);
                favoriteImView.setVisibility(View.GONE);

                favpersonView.requestFocus();
                return true;

            } else if (myFavoriteView.hasFocus() || isMyFavoriteItemshow) {

                favorite_unfold_Layout.setVisibility(View.GONE);
                favoriteImView.setVisibility(View.VISIBLE);

                vodImView.setVisibility(View.GONE);
                vodImView_unfold.setVisibility(View.VISIBLE);
                setChannelImView();
                vodImView_unfold.requestFocus();

                return true;

            } else if (playhistoryView.hasFocus() || isPlayHistoryItemshow) {

                playhistoryView.clearFocus();
                myFavoriteView.requestFocus();
                return true;

            } else if (favpersonView.hasFocus() || isFavPersonItemshow) {

                favpersonView.clearFocus();
                playhistoryView.requestFocus();
                return true;

            } else if ((settingUpdateView.hasFocus()) || (settingBaseView.hasFocus())) {

                applicationImView.setVisibility(View.GONE);
                app_unfold_Layout.setVisibility(View.VISIBLE);

                setting_unfold_layout.setVisibility(View.GONE);
                settingImView.setVisibility(View.VISIBLE);
                apprecomView3.requestFocus();
                setApprecomView3Focus();

                return true;
            }

        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

            KEY_DIRECTION = Utility.DIRECTION_RIGHT;

            if (isMyFavoriteItemshow || myFavoriteView.hasFocus()) {
                playhistoryView.requestFocus();

                return true;
            } else if (playhistoryView.hasFocus() || isPlayHistoryItemshow) {
                playhistoryView.clearFocus();
                favpersonView.requestFocus();

                return true;
            } else if (favpersonView.hasFocus() || isFavPersonItemshow) {
                app_unfold_Layout.setVisibility(View.VISIBLE);
                applicationImView.setVisibility(View.GONE);
                apprecomView1.requestFocus();

                favorite_unfold_Layout.setVisibility(View.GONE);
                favoriteImView.setVisibility(View.VISIBLE);
                return true;
            } else if (channelImView_unfold.hasFocus()) {
                vodImView_unfold.setVisibility(View.VISIBLE);
                vodImView.setVisibility(View.GONE);

                channelImView_unfold.setVisibility(View.GONE);
                channelImView.setVisibility(View.VISIBLE);
                setChannelImView();

                vodImView_unfold.requestFocus();

                return true;

            } else if (vodImView_unfold.hasFocus()) {
                favorite_unfold_Layout.setVisibility(View.VISIBLE);
                favoriteImView.setVisibility(View.GONE);

                vodImView_unfold.setVisibility(View.GONE);
                vodImView.setVisibility(View.VISIBLE);
                setChannelImView();

                favorite_unfold_Layout.requestFocus();

                return true;

            } else if (apprecomView3.hasFocus()) {
                settingImView.setVisibility(View.GONE);
                setting_unfold_layout.setVisibility(View.VISIBLE);

                app_unfold_Layout.setVisibility(View.GONE);
                applicationImView.setVisibility(View.VISIBLE);
                settingBaseView.requestFocus();
                return true;
            } else if (localView.hasFocus()) {
                app_unfold_Layout.setVisibility(View.VISIBLE);
                applicationImView.setVisibility(View.GONE);
                apprecomView2.requestFocus();

                favorite_unfold_Layout.setVisibility(View.GONE);
                favoriteImView.setVisibility(View.VISIBLE);

                return true;

            } else if ((settingNetView.hasFocus()) || (settingErweiView.hasFocus())) {
                channelImView_unfold.setVisibility(View.VISIBLE);
                channelImView.setVisibility(View.GONE);

                setting_unfold_layout.setVisibility(View.GONE);
                settingImView.setVisibility(View.VISIBLE);

                channelImView_unfold.requestFocus();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

        }

        return super.onKeyDown(keyCode, event);
    }


    //channel
    private void setChannelImView() {

        Drawable cacheDrawable = null;
//        mAsyncImageLoader.loadDrawable(mChannelModule.getModuleBg(), channelImView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
        if (cacheDrawable != null) {
            channelImView.setBgView(cacheDrawable);
        } else {
            channelImView.setBgView(R.drawable.dianbo1_bg);
        }
        channelImView.setIconView(R.drawable.dianbo1_logo);
        channelImView.setmNameView(mChannelGroup.getGroupText());
    }

    private void setChannelImViewUnfold() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;
        File channelImView_unfoldFile = new File(IMGPATH_STRING + "channelfocus1.png");
        if (channelImView_unfoldFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "channelfocus1.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.channelfocus1)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
//        channelImView_unfold.setBackgroundDrawable(drawable);
        channelImView_unfold.setBgView(R.drawable.vod_bg);
        channelImView_unfold.setmNameView(mChannelModule.getModuleText());
    }

    //vod
    private void setVodImView() {
        Drawable cacheDrawable = null;
//                mAsyncImageLoader.loadDrawable(mVodModule.getModuleBg(), vodImView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
        if (cacheDrawable != null) {
            vodImView.setBgView(cacheDrawable);
        } else {
            vodImView.setBgView(R.drawable.zhibo1_bg);
        }
        vodImView.setIconView(R.drawable.zhibo1_logo);
        vodImView.setmNameView(mVodGroup.getGroupText());
    }

    private void setVodImView_unfoldView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;
        File vodImView_unfoldFile = new File(IMGPATH_STRING + "vodfocus1.png");
        if (vodImView_unfoldFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "vodfocus1.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.channel_bg)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
//        vodImView_unfold.setBgView(drawable);
        vodImView_unfold.setBgView(R.drawable.channel_bg);
        bm = null;
        bitmap = null;
        drawable = null;
    }

    //app
    private void setApplicationImView() {
        File applicationImViewFile = new File(IMGPATH_STRING + "appnofocus1.png");
        if (applicationImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "appnofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            applicationImView.setBackgroundDrawable(drawable);
        } else {
            applicationImView.setBgView(R.drawable.yingyong1_bg);
        }
        applicationImView.setIconView(R.drawable.yingyong1_logo);
        applicationImView.setmNameView(mAppGroup.getGroupText());
    }

    private void setApprecomView1() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mAppModule1.getModuleBg(), apprecomView1.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            apprecomView1.setBgView(cacheDrawable);
//        } else {
//            apprecomView1.setBgView(R.drawable.appfocus1);
//        }
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mAppModule1.getModuleIcon(), apprecomView1.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            apprecomView1.setIconView(cacheDrawable1);
//        } else {
//            apprecomView1.setIconView(R.drawable.appfocus1);
//        }
        apprecomView1.setBgView(R.drawable.apprecom1_bg);
        apprecomView1.setIconView(R.drawable.apprecom1_icon);
        apprecomView1.setmNameView(mAppModule1.getModuleText());
    }

    private void setApprecomView2() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mAppModule3.getModuleBg(), apprecomView2.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            apprecomView2.setBgView(cacheDrawable);
//        } else {
//            apprecomView2.setBgView(R.drawable.appfocus2);
//        }
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mAppModule3.getModuleIcon(), apprecomView2.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            apprecomView2.setIconView(cacheDrawable1);
//        } else {
//            apprecomView2.setIconView(R.drawable.appfocus2);
//        }
        apprecomView2.setBgView(R.drawable.apprecom2_bg);
        apprecomView2.setIconView(R.drawable.apprecom2_icon);
        apprecomView2.setmNameView(mAppModule3.getModuleText());
    }

    private void setApprecomView3() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mAppModule2.getModuleBg(), apprecomView3.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            apprecomView3.setBgView(cacheDrawable);
//        } else {
//            apprecomView3.setBgView(R.drawable.appfocus3);
//        }
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mAppModule2.getModuleIcon(), apprecomView3.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            apprecomView3.setIconView(cacheDrawable1);
//        } else {
//            apprecomView3.setIconView(R.drawable.appfocus3);
//        }
        apprecomView3.setBgView(R.drawable.apprecom3_bg);
        apprecomView3.setIconView(R.drawable.apprecom3_icon);
        apprecomView3.setmNameView(mAppModule2.getModuleText());
    }

    //zhihui
    private void setFavoriteImView() {
        File favoriteImViewFile = new File(IMGPATH_STRING + "zhihuinofocus1.png");
        if (favoriteImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuinofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            favoriteImView.setBackgroundDrawable(drawable);
        } else {
            favoriteImView.setBgView(R.drawable.shenghuo1_bg);
        }
        favoriteImView.setIconView(R.drawable.shenghuo1_logo);
        favoriteImView.setmNameView(mZhihuiGroup.getGroupText());
    }

    private void setMyFavoriteView() {

//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mZhihuiModule1.getModuleBg(), myFavoriteView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            myFavoriteView.setBgView(cacheDrawable);
//        } else {
//            myFavoriteView.setBgView(R.drawable.zhihuifocus1);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mZhihuiModule1.getModuleIcon(), myFavoriteView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            myFavoriteView.setIconView(cacheDrawable1);
//        } else {
//            myFavoriteView.setIconView(R.drawable.zhihuifocus1);
//        }
        myFavoriteView.setBgView(R.drawable.zhihui1_bg);
        myFavoriteView.setIconView(R.drawable.zhihui1_icon);
        myFavoriteView.setmNameView(mZhihuiModule1.getModuleText());
    }

    private void setPlayhistoryView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mZhihuiModule2.getModuleBg(), playhistoryView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            playhistoryView.setBgView(cacheDrawable);
//        } else {
//            playhistoryView.setBgView(R.drawable.zhihuifocus2);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mZhihuiModule2.getModuleIcon(), playhistoryView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            playhistoryView.setIconView(cacheDrawable1);
//        } else {
//            playhistoryView.setIconView(R.drawable.zhihuifocus2);
//        }
        playhistoryView.setBgView(R.drawable.zhihui2_bg);
        playhistoryView.setIconView(R.drawable.zhihui2_icon);
        playhistoryView.setmNameView(mZhihuiModule2.getModuleText());
    }

    private void setFavpersonView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mZhihuiModule3.getModuleBg(), favpersonView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            favpersonView.setBgView(cacheDrawable);
//        } else {
//            favpersonView.setBgView(R.drawable.zhihuifocus3);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mZhihuiModule3.getModuleIcon(), favpersonView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            favpersonView.setIconView(cacheDrawable1);
//        } else {
//            favpersonView.setIconView(R.drawable.zhihuifocus3);
//        }
        favpersonView.setBgView(R.drawable.zhihui3_bg);
        favpersonView.setIconView(R.drawable.zhihui3_icon);
        favpersonView.setmNameView(mZhihuiModule3.getModuleText());
    }

    private void setLocalView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mZhihuiModule4.getModuleBg(), localView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            localView.setBgView(cacheDrawable);
//        } else {
//            localView.setBgView(R.drawable.zhihuifocus4);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mZhihuiModule4.getModuleIcon(), localView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            localView.setIconView(cacheDrawable1);
//        } else {
//            localView.setIconView(R.drawable.zhihuifocus4);
//        }
        localView.setBgView(R.drawable.zhihui4_bg);
        localView.setIconView(R.drawable.zhihui4_icon);
        localView.setmNameView(mZhihuiModule4.getModuleText());
    }

    //setting
    private void setSettingImView() {
        File settingImViewFile = new File(IMGPATH_STRING + "settingnofocus1.png");
        if (settingImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingnofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            settingImView.setBackgroundDrawable(drawable);
        } else {
            settingImView.setBgView(R.drawable.shezhi1_bg);
        }
        settingImView.setIconView(R.drawable.shezhi1_logo);
        settingImView.setmNameView(mSettingGroup.getGroupText());
    }

    private void setSettingBaseView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue1.getModuleBg(), settingBaseView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingBaseView.setBgView(cacheDrawable);
//        } else {
//            settingBaseView.setBgView(R.drawable.setting_focus1);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue1.getModuleIcon(), settingBaseView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingBaseView.setIconView(cacheDrawable1);
//        } else {
//            settingBaseView.setIconView(R.drawable.setting_focus1);
//        }
        settingBaseView.setBgView(R.drawable.setting_item_bg);
        settingBaseView.setIconView(R.drawable.setting1_icon);
        settingBaseView.setmNameView(mSettingModue1.getModuleText());
    }

    private void setSettingDispalyView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue2.getModuleBg(), settingDispalyView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingDispalyView.setBgView(cacheDrawable);
//        } else {
//            settingDispalyView.setBgView(R.drawable.settingfocus3);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue2.getModuleIcon(), settingDispalyView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingDispalyView.setIconView(cacheDrawable1);
//        } else {
//            settingDispalyView.setIconView(R.drawable.settingfocus3);
//        }
        settingDispalyView.setBgView(R.drawable.setting_item_bg);
        settingDispalyView.setIconView(R.drawable.setting2_icon);
        settingDispalyView.setmNameView(mSettingModue2.getModuleText());
    }

    private void setSettingNetView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue3.getModuleBg(), settingNetView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingNetView.setBgView(cacheDrawable);
//        } else {
//            settingNetView.setBgView(R.drawable.settingfocus5);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue3.getModuleIcon(), settingNetView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingNetView.setIconView(cacheDrawable1);
//        } else {
//            settingNetView.setIconView(R.drawable.settingfocus5);
//        }
        settingNetView.setBgView(R.drawable.setting_item_bg);
        settingNetView.setIconView(R.drawable.setting3_icon);
        settingNetView.setmNameView(mSettingModue3.getModuleText());
    }

    private void setSettingUpdateView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue4.getModuleBg(), settingUpdateView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingUpdateView.setBgView(cacheDrawable);
//        } else {
//            settingUpdateView.setBgView(R.drawable.settingfocus2);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue4.getModuleIcon(), settingUpdateView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingUpdateView.setIconView(cacheDrawable1);
//        } else {
//            settingUpdateView.setIconView(R.drawable.settingfocus2);
//        }
        settingUpdateView.setBgView(R.drawable.setting_item_bg);
        settingUpdateView.setIconView(R.drawable.setting4_icon);
        settingUpdateView.setmNameView(mSettingModue4.getModuleText());
    }

    private void setSettingMoreView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue5.getModuleBg(), settingMoreView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingMoreView.setBgView(cacheDrawable);
//        } else {
//            settingMoreView.setBgView(R.drawable.settingfocus4);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue5.getModuleIcon(), settingMoreView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingMoreView.setIconView(cacheDrawable1);
//        } else {
//            settingMoreView.setIconView(R.drawable.settingfocus4);
//        }
        settingMoreView.setBgView(R.drawable.setting_item_bg);
        settingMoreView.setIconView(R.drawable.setting5_icon);
        settingMoreView.setmNameView(mSettingModue5.getModuleText());
    }

    private void setSettingErweiView() {
//        Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(mSettingModue6.getModuleBg(), settingErweiView.getBgView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable != null) {
//            settingErweiView.setBgView(cacheDrawable);
//        } else {
//            settingErweiView.setBgView(R.drawable.settingfocus6);
//        }
//
//        Drawable cacheDrawable1 = mAsyncImageLoader.loadDrawable(mSettingModue6.getModuleIcon(), settingErweiView.getIconView(), new AsyncImageLoader.ImageCallback() {
//
//            @Override
//            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
//                if (imageDrawable != null) {
//                    imageView.setImageDrawable(imageDrawable);
//                } else {
//                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
//                            imageUrl));
//                }
//            }
//        });
//        if (cacheDrawable1 != null) {
//            settingErweiView.setIconView(cacheDrawable1);
//        } else {
//            settingErweiView.setIconView(R.drawable.settingfocus6);
//        }
        settingErweiView.setBgView(R.drawable.setting_item_bg);
        settingErweiView.setIconView(R.drawable.setting6_icon);
        settingErweiView.setmNameView(mSettingModue6.getModuleText());
    }

    private void initHomeView() {
        mAsyncImageLoader = new AsyncImageLoader(mContext);
        bgFrameLayout = (FrameLayout) findViewById(R.id.homeLayout);
        String bgPath = ToolUtils.getValueFromSP(mContext, FULL_BG_KEY);
        if (!TextUtils.isEmpty(bgPath)) {
            try {
                bgFrameLayout.setBackground(Drawable.createFromPath(bgPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        focusBkLayout = (FrameLayout) findViewById(R.id.mainmenu_focusbk_layout);
        focusBkImageView = (ImageView) findViewById(R.id.mainmenu_focusbk);

        //channel
        channelImView = (com.luntech.launcher.view.QisItem) findViewById(R.id.channel);
        channelImView.setOnFocusChangeListener(this);
        channelImView_unfold = (com.luntech.launcher.view.QisItem) findViewById(R.id.channel_unfold);
        channelImView_unfold.setOnFocusChangeListener(this);
        setChannelImView();
        setChannelImViewUnfold();


        //vod
        vodImView = (com.luntech.launcher.view.QisItem) findViewById(R.id.vod);
        vodImView.setOnFocusChangeListener(this);
        vodImView_unfold = (com.luntech.launcher.view.QisItem) findViewById(R.id.vod_unfold);
        vodImView_unfold.setOnFocusChangeListener(this);
        setVodImView();
        setVodImView_unfoldView();

        //app
        applicationImView = (com.luntech.launcher.view.QisItem) findViewById(R.id.application);
        app_unfold_Layout = (LinearLayout) findViewById(R.id.app_unfold);
        apprecomView1 = (com.luntech.launcher.view.QisItem) findViewById(R.id.app_recom1);
        apprecomView1.setOnFocusChangeListener(this);
        apprecomView2 = (com.luntech.launcher.view.QisItem) findViewById(R.id.app_recom2);
        apprecomView2.setOnFocusChangeListener(this);
        apprecomView3 = (com.luntech.launcher.view.QisItem) findViewById(R.id.app_recom3);
        apprecomView3.setOnFocusChangeListener(this);
        setApplicationImView();
        setApprecomView1();
        setApprecomView2();
        setApprecomView3();

        //zhihui
        favoriteImView = (com.luntech.launcher.view.QisItem) findViewById(R.id.favorite);
        favorite_unfold_Layout = (LinearLayout) findViewById(R.id.favorite_unfold);
        myFavoriteView = (com.luntech.launcher.view.QisItem) findViewById(R.id.myfavorite);
        myFavoriteView.setOnFocusChangeListener(this);
        playhistoryView = (com.luntech.launcher.view.QisItem) findViewById(R.id.playhistory);
        playhistoryView.setOnFocusChangeListener(this);
        favpersonView = (com.luntech.launcher.view.QisItem) findViewById(R.id.fav_person);
        favpersonView.setOnFocusChangeListener(this);
        localView = (com.luntech.launcher.view.QisItem) findViewById(R.id.local);
        localView.setOnFocusChangeListener(this);
        setFavoriteImView();
        setMyFavoriteView();
        setPlayhistoryView();
        setFavpersonView();
        setLocalView();

        //setting
        settingImView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting);
        setting_unfold_layout = (LinearLayout) findViewById(R.id.setting_unfold_layout);
        settingBaseView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_base);
        settingBaseView.setOnFocusChangeListener(this);
        settingDispalyView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_display);
        settingDispalyView.setOnFocusChangeListener(this);
        settingNetView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_net);
        settingNetView.setOnFocusChangeListener(this);
        settingUpdateView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_update);
        settingUpdateView.setOnFocusChangeListener(this);
        settingMoreView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_more);
        settingMoreView.setOnFocusChangeListener(this);
        settingErweiView = (com.luntech.launcher.view.QisItem) findViewById(R.id.setting_erwei);
        settingErweiView.setOnFocusChangeListener(this);
        setSettingImView();
        setSettingBaseView();
        setSettingDispalyView();
        setSettingNetView();
        setSettingUpdateView();
        setSettingErweiView();
        setSettingMoreView();
    }


    private void InitAnim() {

        translationAnimDown = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        translationAnimDown.setDuration(0);
        translationAnimDown.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));

        translationAnimRight = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        translationAnimRight.setDuration(0);
        translationAnimRight.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));

        translationAnimLeft = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        translationAnimLeft.setDuration(0);
        translationAnimLeft.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));

        translationAnimTop = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        translationAnimTop.setDuration(0);
        translationAnimTop.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));

    }

    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());

        super.onDestroy();
    }


    public static Bitmap createReflectedImage(Bitmap originalImage, Bitmap originalImage1) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int reflectionHeigh = 110;
        int y = height - reflectionHeigh;
        final int reflectionGap = 0;
        Bitmap reflectionImage = null;

        Matrix matrix = new Matrix();

        matrix.preScale(1, -1);

        if (originalImage1 == null)
            reflectionImage = Bitmap.createBitmap(originalImage, 0, y, width, reflectionHeigh, matrix, false);
        else
            reflectionImage = Bitmap.createBitmap(originalImage1, 0, y, width, reflectionHeigh, matrix, false);

        Bitmap finalReflection = Bitmap.createBitmap(width, (height + reflectionHeigh), Config.ARGB_8888);

        Canvas canvas = new Canvas(finalReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint shaderPaint = new Paint();

        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0, finalReflection.getHeight() + reflectionGap
                , 0x30ffffff, 0x00eeeeee, TileMode.MIRROR);

        shaderPaint.setShader(shader);
        shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, height, width, finalReflection.getHeight(), shaderPaint);
        return finalReflection;
    }


    private void startLauncherLoder() {
        mLoader.startLoader(this, true);
    }

    @Override
    public void getWorkspaceRecomInfo() {
        // TODO Auto-generated method stub

    }


    @Override
    public void refreshWorkspace() {
        // TODO Auto-generated method stub

    }

    private void initParams() {
        mCategoryFile = getCategoryFileName();
        mFilePrefix = getFilePrefix();
        mType = getType();
        mAdConfigureFile = getAdConfigureFile();
    }

    public String getType() {
        return Q1S_TYPE;
    }

    public String getAdConfigureFile() {
        return Q1S_AD_CONFIGURE_FILE;
    }

    public String getFilePrefix() {
        return Q1S_FILE_PREFIX;
    }

    public String getCategoryFileName() {
        return Q1S_CATEGORY_FILE;
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
            mSelectedView.setIconView(mSelectedApp.moduleIconDrawable);
            mSelectedView.setmNameView(mSelectedApp.moduleText);
            Log.d("jzh", "setResult  RESULT_OK " + app.toString());
        } else {
            setResult(RESULT_CANCELED, null);
            Log.d("jzh", "setResult RESULT_CANCELED ");
        }
    }

}
