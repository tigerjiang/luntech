
package com.luntech.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luntech.launcher.secondary.AppManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Launcher extends Activity {

    private static final String TAG = "Launcher";
    private static final boolean DEBUG = true;
    private GridView mGridView;
    private Resources mResources;
    // private List<CategoryItem> mAppList = new ArrayList<CategoryItem>();
    private List<AppItem> mAppList = new ArrayList<AppItem>();
    private CategoryItemAdapter mCategoryItemAdapter;
    private Context mContext;
    private ChangeReceiver mChangeReceiver;
    private Configuration mConfig = new Configuration();
    private AppManager mAppManager;
    private RelativeLayout mThumb_1_layout;
    private ImageView mThumb_1_shadow;
    private TextView mThumb_1_label;

    private RelativeLayout mThumb_2_layout;
    private ImageView mThumb_2_shadow;
    private TextView mThumb_2_label;

    private RelativeLayout mThumb_3_layout;
    private ImageView mThumb_3_shadow;
    private TextView mThumb_3_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mResources = getResources();
        mContext = getApplicationContext();
        parseCategoryItem();
        initView();
        // Intent intentService = new Intent();
        // intentService.setClass(this, ChangeService.class);
        // startService(intentService);
        // registerReceiver();
    }

    private void initView() {

        mThumb_1_layout = (RelativeLayout) findViewById(R.id.thumb_1_view);
        mThumb_1_shadow = (ImageView) findViewById(R.id.thumb_1_cover_view);
        mThumb_1_label = (TextView) findViewById(R.id.thumb_1_label);
        mThumb_2_layout = (RelativeLayout) findViewById(R.id.thumb_2_view);
        mThumb_2_shadow = (ImageView) findViewById(R.id.thumb_2_cover_view);
        mThumb_2_label = (TextView) findViewById(R.id.thumb_2_label);
        mThumb_3_layout = (RelativeLayout) findViewById(R.id.thumb_3_view);
        mThumb_3_shadow = (ImageView) findViewById(R.id.thumb_3_cover_view);
        mThumb_3_label = (TextView) findViewById(R.id.thumb_3_label);
        mAppManager = AppManager.getInstance();
        mGridView = (GridView) findViewById(R.id.category_layout);
        final AppItem mFirstApp = mAppList.get(0);
        final AppItem mSecondApp = mAppList.get(1);
        final AppItem mThirdApp = mAppList.get(2);
        mThumb_1_layout.setBackground(mFirstApp.getBackgroundIcon());
        mThumb_1_shadow.setImageDrawable(mFirstApp.getShadowIcon());
        mThumb_1_label.setText(mFirstApp.getLabel());
        mThumb_1_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(mFirstApp.getComponentName());

            }
        });
        mThumb_2_layout.setBackground(mSecondApp.getBackgroundIcon());
        mThumb_2_shadow.setImageDrawable(mSecondApp.getShadowIcon());
        mThumb_2_label.setText(mSecondApp.getLabel());
        mThumb_2_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(mSecondApp.getComponentName());

            }
        });
        mThumb_3_layout.setBackground(mThirdApp.getBackgroundIcon());
        mThumb_3_shadow.setImageDrawable(mThirdApp.getShadowIcon());
        mThumb_3_label.setText(mThirdApp.getLabel());
        mThumb_3_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                safeStartApk(mThirdApp.getComponentName());

            }
        });
        Iterator<AppItem> iter = mAppList.iterator();
        while (iter.hasNext()) {
            AppItem app = iter.next();
            if (app.mLabel.equals(mFirstApp.mLabel) || app.mLabel.equals(mSecondApp.mLabel)
                    || app.mLabel.equals(mThirdApp.mLabel)) {
                iter.remove();
            }
        }
        mCategoryItemAdapter = new CategoryItemAdapter(mAppList, mContext);
        mGridView.setAdapter(mCategoryItemAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppItem app = mAppList.get(position);
                String componentName = app.getComponentName();
                safeStartApk(componentName);
            }
        });
    }

    void safeStartApk(String componentName) {
        try {
            String[] info = componentName.split("/");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(info[0], componentName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "App no found for "+componentName, Toast.LENGTH_SHORT).show();

            Log.d(TAG, e.toString());
        }
    }

    private void registerReceiver() {
        mChangeReceiver = new ChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(mChangeReceiver, filter);
    }

    private void parseCategoryItem() {
        mAppList = ToolUtils.getInfoFromConfig(mContext, R.xml.default_config);
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
        Log.d(TAG, "action " + action + "    keycode" + keyCode);
        if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                Intent intent = new Intent();
                intent.setClass(mContext, AppSelectedActivity.class);
                startActivityForResult(intent, 0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                break;
            case RESULT_CANCELED:
                break;
            default:
                break;
        }

    }
}
