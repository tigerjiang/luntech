
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
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selected);
        mContext = this.getApplicationContext();
        mAppManager = AppManager.getInstance();
        initSelectedApp();
    }

    private void initSelectedApp() {
        mSelectedGrid = (GridView) findViewById(R.id.app_gridView);
        mHorizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontal_view);
        mAppList = mAppManager.getSelectedApplications();
        sortByInstallTime(mAppList);
        mAdapter = new ApplicationsAdapter(mContext, mAppList);
        mSelectedGrid.setAdapter(mAdapter);
        mSelectedGrid.setSelection(0);
        int listSize = mAppList.size();
        // 根据item的数目，动态设定gridview的宽度,现假定每个item的宽度和高度均为100dp，列间距为5dp
        ViewGroup.LayoutParams params = mSelectedGrid.getLayoutParams();
        int itemWidth = 100;
        int spacingWidth = 20;

        params.width = itemWidth * listSize + (listSize - 1) * spacingWidth;
        mSelectedGrid.setStretchMode(GridView.NO_STRETCH); // 设置为禁止拉伸模式
        mSelectedGrid.setNumColumns(listSize);
        mSelectedGrid.setHorizontalSpacing(spacingWidth);
        mSelectedGrid.setColumnWidth(itemWidth);
        mSelectedGrid.setLayoutParams(params);
        mSelectedGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChangedApp = mAppList.get(position);
                Toast.makeText(mContext, "selected app" + mChangedApp.mTitle, Toast.LENGTH_LONG)
                        .show();
                setResult(mChangedApp, true);
            }
        });
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

            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mPosition++;
                if(mPosition>10){
                    mSelectedGrid.scrollTo(100*mPosition, 0);
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mPosition--;
            }
            if (mPosition < mSelectedGrid.getCount() && mPosition >= 0) {
                mSelectedGrid.setSelection(mPosition);
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}
