package com.luntech.launcher;


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

import com.hisense.network.utils.EpgDataInfoLoader.HiLauncherLoader;


import java.io.File;
import java.util.List;
import java.util.Map;


public class Q1SLauncher extends Launcher implements View.OnFocusChangeListener, HiLauncherLoader.Callbacks {

    private static final String TAG = "Q1SLauncher";

    //bg image
    private FrameLayout bgFrameLayout;

    //channel menu
    private ImageView channelImView;
    private ImageView channelImView_unfold;

    //vod menu
    private ImageView vodImView;
    private ImageView vodImView_unfold;

    //application menu
    private ImageView applicationImView;
    private LinearLayout app_unfold_Layout;
    private ImageView apprecomView1;
    private ImageView apprecomView2;
    private ImageView apprecomView3;

    //fovarite menu
    private ImageView favoriteImView;
    private LinearLayout favorite_unfold_Layout;
    private ImageView myFavoriteView;
    private ImageView playhistoryView;
    private ImageView favpersonView;
    private ImageView localView;

    //setting menu
    private ImageView settingImView;
    private LinearLayout setting_unfold_layout;
    private ImageView settingBaseView;
    private ImageView settingDispalyView;
    private ImageView settingNetView;
    private ImageView settingUpdateView;
    private ImageView settingMoreView;
    private ImageView settingErweiView;
    //private ImageView settingImView_unfold;

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

        bgFrameLayout = (FrameLayout) findViewById(R.id.homeLayout);
        setBgImage();

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


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
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


    public void onClick_Event(View view) {
        if (view.getId() == vodImView_unfold.getId()) {

            safeStartApk("com.xike.xkliveplay", "com.xike.xkliveplay.activity.launch.ActivityLaunch");

        } else if (view.getId() == channelImView_unfold.getId()) {
            //safeStartApk("com.softwine.secondary", "com.softwine.secondary.MediaAppsGridActivity");
            safeStartApk("cn.com.wasu.main", "cn.com.wasu.main.WasuTVMainActivity");
        } else if (view.getId() == apprecomView1.getId()) {
            safeStartApk("com.qihoo360.mobilesafe_tv", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");

        } else if (view.getId() == apprecomView2.getId()) {
            safeStartApk("com.softwine.secondary", "com.softwine.secondary.ApplicationsGridActivity");

        } else if (view.getId() == apprecomView3.getId()) {
            safeStartApk("com.luntech.zhihemarket", "com.luntech.zhihemarket.LoginActivity");

        } else if (view.getId() == myFavoriteView.getId()) {
            //safeStartApk("com.softwine.secondary", "com.softwine.secondary.SmartAppsGridActivity");
            safeStartApk("com.eastsoft.android.ihome", "com.eastsoft.android.ihome.login.Launcher");
        } else if (view.getId() == playhistoryView.getId()) {
            safeStartApk("com.softwine.secondary", "com.softwine.secondary.LongTaiAppsGridActivity");
        } else if (view.getId() == favpersonView.getId()) {
            //safeStartApk("com.softwine.secondary", "com.softwine.secondary.GameAppsGridActivity");
            String activityNameString = queryAppInfoByPackageName("com.egame.tv");
            safeStartApk("com.egame.tv", activityNameString);
        } else if (view.getId() == settingBaseView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.AboutActivity");
        } else if (view.getId() == settingDispalyView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.DisplayConfigActivity");
        } else if (view.getId() == settingNetView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.NetworkConfigActivity");
        } else if (view.getId() == settingUpdateView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.UpdateActivity");
        }
        //else if (view.getId() == settingImView_unfold.getId()){
        else if (view.getId() == settingMoreView.getId()) {
            safeStartApk("com.android.settings", "com.android.settings.Settings");

        } else if (view.getId() == settingErweiView.getId()) {
            safeStartApk("com.android.settings", "com.sugar.settings.QRActivity");
        } else if (view.getId() == localView.getId()) {
            //safeStartApk("com.example.testapp", "com.example.testapp.MediaAppsGridActivity");
            safeStartApk("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI");
        }
    }

    void safeStartApk(String pkName, String className) {
        try {
            Intent pickIntent = new Intent();
            pickIntent.setClassName(pkName, className);
            startActivity(pickIntent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        int id = v.getId();

        if (hasFocus) {
            if (channelImView_unfold.getId() == id) {
                setChannelImViewFocus();
                mLastFocusView = channelImView_unfold;

            } else if (vodImView_unfold.getId() == id) {
                setVodImViewFocus();
                mLastFocusView = vodImView_unfold;

            } else if (settingBaseView.getId() == id) {
                setSettingBaseImViewFocus();
                mLastFocusView = settingBaseView;
            } else if (settingDispalyView.getId() == id) {
                setSettingDisplayImViewFocus();
                mLastFocusView = settingDispalyView;
            } else if (settingNetView.getId() == id) {
                setSettingNetImViewFocus();
                mLastFocusView = settingNetView;
            } else if (settingUpdateView.getId() == id) {
                setSettingUpdateImViewFocus();
                mLastFocusView = settingUpdateView;
            } else if (settingMoreView.getId() == id) {
                setSettingMoreImViewFocus();
                mLastFocusView = settingMoreView;
            } else if (settingErweiView.getId() == id) {
                setSettingErweiImViewFocus();
                mLastFocusView = settingErweiView;
            } else if (apprecomView1.getId() == id) {
                setApprecomView1Focus();
                mLastFocusView = apprecomView1;

            } else if (apprecomView2.getId() == id) {
                setApprecomView2Focus();
                mLastFocusView = apprecomView2;
            } else if (apprecomView3.getId() == id) {
                setApprecomView3Focus();
                mLastFocusView = apprecomView3;
            } else if (myFavoriteView.getId() == id) {
                setMyFavoriteViewFocus();
                mLastFocusView = myFavoriteView;
            } else if (playhistoryView.getId() == id) {
                setPlayhistoryViewFocus();
                mLastFocusView = playhistoryView;

            } else if (favpersonView.getId() == id) {
                setFavpersonViewFocus();
                mLastFocusView = favpersonView;

            } else if (localView.getId() == id) {
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
        setFocusBkLayout(73, 164, 704, 435);
        focusBkImageView.setBackgroundResource(R.drawable.menu_focus);
        setTranslationAnimation(KEY_DIRECTION, focusBkImageView);
    }

    private void setVodImViewFocus() {
        Log.d(TAG, "### set vod focus ###");
        setFocusBkLayout(178, 164, 704, 435);
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

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

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

    //bg
    private void setBgImage() {
        File bgImageFile = new File(IMGPATH_STRING + "bgnofocus1.png");
        if (bgImageFile.exists()) {
            Bitmap bmBitmap = BitmapFactory.decodeFile(IMGPATH_STRING + "bgnofocus1.png");
            Drawable drawable = new BitmapDrawable(bmBitmap);
            bgFrameLayout.setBackgroundDrawable(drawable);
            Log.d(TAG, " launcher use new bg  ");
        } else {
            Log.d(TAG, " launcher use old bg  ");
            bgFrameLayout.setBackgroundResource(R.drawable.bg);
        }
    }

    //channel
    private void setChannelImView() {
        File channelImViewFile = new File(IMGPATH_STRING + "channelnofocus1.png");
        if (channelImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "channelnofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            channelImView.setBackgroundDrawable(drawable);
        } else {
            channelImView.setBackgroundResource(R.drawable.channelnofocus1);
        }
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
        channelImView_unfold.setBackgroundDrawable(drawable);
    }

    //vod
    private void setVodImView() {
        File vodImViewFile = new File(IMGPATH_STRING + "vodnofocus1.png");
        if (vodImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "vodnofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            vodImView.setBackgroundDrawable(drawable);
        } else {
            vodImView.setBackgroundResource(R.drawable.vodnofocus1);
        }
    }

    private void setVodImView_unfoldView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;
        File vodImView_unfoldFile = new File(IMGPATH_STRING + "vodfocus1.png");
        if (vodImView_unfoldFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "vodfocus1.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.vodfocus1)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        vodImView_unfold.setBackgroundDrawable(drawable);
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
            applicationImView.setBackgroundResource(R.drawable.appnofocus1);
        }
    }

    private void setApprecomView1() {
        File apprecomView1File = new File(IMGPATH_STRING + "appfocus1.png");
        if (apprecomView1File.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "appfocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            apprecomView1.setBackgroundDrawable(drawable);
        } else {
            apprecomView1.setBackgroundResource(R.drawable.appfocus1);
        }
    }

    private void setApprecomView2() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File apprecomView2File = new File(IMGPATH_STRING + "appfocus2.png");
        if (apprecomView2File.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "appfocus2.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.appfocus2)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        apprecomView2.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void setApprecomView3() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File apprecomView3File = new File(IMGPATH_STRING + "appfocus3.png");
        if (apprecomView3File.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "appfocus3.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.appfocus3)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        apprecomView3.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    //zhihui
    private void setFavoriteImView() {
        File favoriteImViewFile = new File(IMGPATH_STRING + "zhihuinofocus1.png");
        if (favoriteImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuinofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            favoriteImView.setBackgroundDrawable(drawable);
        } else {
            favoriteImView.setBackgroundResource(R.drawable.zhihuinofocus1);
        }
    }

    private void setMyFavoriteView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File myFavoriteViewFile = new File(IMGPATH_STRING + "zhihuifocus1.png");
        if (myFavoriteViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuifocus1.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.zhihuifocus1)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        myFavoriteView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void setPlayhistoryView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File playhistoryViewFile = new File(IMGPATH_STRING + "zhihuifocus2.png");
        if (playhistoryViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuifocus2.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.zhihuifocus2)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        playhistoryView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void setFavpersonView() {
        File favpersonViewFile = new File(IMGPATH_STRING + "zhihuifocus3.png");
        if (favpersonViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuifocus3.png");
            Drawable drawable = new BitmapDrawable(bm);
            favpersonView.setBackgroundDrawable(drawable);
        } else {
            favpersonView.setBackgroundResource(R.drawable.zhihuifocus3);
        }
    }

    private void setLocalView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File localViewFile = new File(IMGPATH_STRING + "zhihuifocus4.png");
        if (localViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "zhihuifocus4.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.zhihuifocus4)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        localView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    //setting
    private void setSettingImView() {
        File settingImViewFile = new File(IMGPATH_STRING + "settingnofocus1.png");
        if (settingImViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingnofocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            settingImView.setBackgroundDrawable(drawable);
        } else {
            settingImView.setBackgroundResource(R.drawable.settingnofocus1);
        }
    }

    private void setSettingBaseView() {
        File settingBaseViewFile = new File(IMGPATH_STRING + "settingfocus1.png");
        if (settingBaseViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus1.png");
            Drawable drawable = new BitmapDrawable(bm);
            settingBaseView.setBackgroundDrawable(drawable);
        } else {
            settingBaseView.setBackgroundResource(R.drawable.settingfocus1);
        }
    }

    private void setSettingDispalyView() {
        File settingDispalyViewFile = new File(IMGPATH_STRING + "settingfocus3.png");
        if (settingDispalyViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus3.png");
            Drawable drawable = new BitmapDrawable(bm);
            settingDispalyView.setBackgroundDrawable(drawable);
        } else {
            settingDispalyView.setBackgroundResource(R.drawable.settingfocus3);
        }
    }

    private void setSettingNetView() {
        File settingNetViewFile = new File(IMGPATH_STRING + "settingfocus5.png");
        if (settingNetViewFile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus5.png");
            Drawable drawable = new BitmapDrawable(bm);
            settingNetView.setBackgroundDrawable(drawable);
        } else {
            settingNetView.setBackgroundResource(R.drawable.settingfocus5);
        }
    }

    private void setSettingUpdateView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File settingUpdateViewFile = new File(IMGPATH_STRING + "settingfocus2.png");
        if (settingUpdateViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus2.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.settingfocus2)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        settingUpdateView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void setSettingMoreView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File settingMoreViewFile = new File(IMGPATH_STRING + "settingfocus4.png");
        if (settingMoreViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus4.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.settingfocus4)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        settingMoreView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void setSettingErweiView() {
        Bitmap bm = null;
        Bitmap bitmap = null;
        Drawable drawable = null;

        File settingErweiViewFile = new File(IMGPATH_STRING + "settingfocus6.png");
        if (settingErweiViewFile.exists()) {
            bm = BitmapFactory.decodeFile(IMGPATH_STRING + "settingfocus6.png");
        } else {
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.settingfocus6)).getBitmap();
        }
        bitmap = createReflectedImage(bm, bm);
        drawable = new BitmapDrawable(bitmap);
        settingErweiView.setBackgroundDrawable(drawable);

        bm = null;
        bitmap = null;
        drawable = null;
    }

    private void initHomeView() {
        focusBkLayout = (FrameLayout) findViewById(R.id.mainmenu_focusbk_layout);
        focusBkImageView = (ImageView) findViewById(R.id.mainmenu_focusbk);

        //channel
        channelImView = (ImageView) findViewById(R.id.channel);
        channelImView.setOnFocusChangeListener(this);
        channelImView_unfold = (ImageView) findViewById(R.id.channel_unfold);
        channelImView_unfold.setOnFocusChangeListener(this);
        setChannelImView();
        setChannelImViewUnfold();


        //vod
        vodImView = (ImageView) findViewById(R.id.vod);
        vodImView.setOnFocusChangeListener(this);
        vodImView_unfold = (ImageView) findViewById(R.id.vod_unfold);
        vodImView_unfold.setOnFocusChangeListener(this);
        setVodImView();
        setVodImView_unfoldView();

        //app
        applicationImView = (ImageView) findViewById(R.id.application);
        app_unfold_Layout = (LinearLayout) findViewById(R.id.app_unfold);
        apprecomView1 = (ImageView) findViewById(R.id.app_recom1);
        apprecomView1.setOnFocusChangeListener(this);
        apprecomView2 = (ImageView) findViewById(R.id.app_recom2);
        apprecomView2.setOnFocusChangeListener(this);
        apprecomView3 = (ImageView) findViewById(R.id.app_recom3);
        apprecomView3.setOnFocusChangeListener(this);
        setApplicationImView();
        setApprecomView1();
        setApprecomView2();
        setApprecomView3();

        //zhihui
        favoriteImView = (ImageView) findViewById(R.id.favorite);
        favorite_unfold_Layout = (LinearLayout) findViewById(R.id.favorite_unfold);
        myFavoriteView = (ImageView) findViewById(R.id.myfavorite);
        myFavoriteView.setOnFocusChangeListener(this);
        playhistoryView = (ImageView) findViewById(R.id.playhistory);
        playhistoryView.setOnFocusChangeListener(this);
        favpersonView = (ImageView) findViewById(R.id.fav_person);
        favpersonView.setOnFocusChangeListener(this);
        localView = (ImageView) findViewById(R.id.local);
        localView.setOnFocusChangeListener(this);
        setFavoriteImView();
        setMyFavoriteView();
        setPlayhistoryView();
        setFavpersonView();
        setLocalView();

        //setting
        settingImView = (ImageView) findViewById(R.id.setting);
        setting_unfold_layout = (LinearLayout) findViewById(R.id.setting_unfold_layout);
        settingBaseView = (ImageView) findViewById(R.id.setting_base);
        settingBaseView.setOnFocusChangeListener(this);
        settingDispalyView = (ImageView) findViewById(R.id.setting_display);
        settingDispalyView.setOnFocusChangeListener(this);
        settingNetView = (ImageView) findViewById(R.id.setting_net);
        settingNetView.setOnFocusChangeListener(this);
        settingUpdateView = (ImageView) findViewById(R.id.setting_update);
        settingUpdateView.setOnFocusChangeListener(this);
        settingMoreView = (ImageView) findViewById(R.id.setting_more);
        settingMoreView.setOnFocusChangeListener(this);
        settingErweiView = (ImageView) findViewById(R.id.setting_erwei);
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
        unregisterReceiver(mBroadcastReceiver);
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

}
