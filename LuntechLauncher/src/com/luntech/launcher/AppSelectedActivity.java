
package com.luntech.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
import com.luntech.launcher.view.AppItemView;
import com.luntech.launcher.view.ApplicationsAdapter;

import java.util.Collections;
import java.util.List;

public class AppSelectedActivity extends Activity {
    private List<ApplicationInfo> mAppList;

    private ApplicationsAdapter mAdapter;
    private AppManager mAppManager;
    private GridView mSelectedGrid;
    ApplicationInfo mChangedApp;
    private Context mContext;
    private int mPosition;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selected);
        mContext = this.getApplicationContext();
        AppManager.create(this);
        mAppManager = AppManager.getInstance();
        initSelectedApp();
    }

    private void initSelectedApp() {
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_view);
        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);
        mAppList = mAppManager.getSelectedApplications();
        sortByInstallTime(mAppList);
        int listSize = mAppList.size();
        for (int i = 0; i < listSize; i++) {
            AppItemView appItemView = new AppItemView(mContext, mAppList.get(i));
            appItemView.setFocusable(true);
            appItemView.setFocusableInTouchMode(true);
            appItemView.setBackgroundResource(R.drawable.focus_selector);
            appItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ApplicationInfo app = ((AppItemView) view).getAppnfo();
                    setResult(app, true);
                    finish();
                }
            });
            mContentLayout.addView(appItemView);
        }
//        mContentLayout.getChildAt(0).requestFocus();

    }

    private void sortByInstallTime(final List<ApplicationInfo> apps) {
        Collections.sort(apps, ApplicationInfo.INSTALL_TIME_COMPARATOR);
    }

    public void setResult(ApplicationInfo app, boolean isSelected) {
        Log.d("replace", "setResult" + app.toString());
        if (isSelected && app != null) {
            Intent intent = new Intent();
            intent.putExtra("app", app.mpackageName);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, null);
        }
        Log.d("replace", app.toString());
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {


        }
        return super.onKeyDown(keyCode, event);
    }

}
