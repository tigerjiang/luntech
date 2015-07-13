
package com.luntech.launcher.secondary;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luntech.launcher.R;

public class LongTaiAppsGridActivity extends Activity {

    private static final String TAG = LongTaiAppsGridActivity.class.getSimpleName();

    private List<ApplicationInfo> mAppList;
    private ApplicationInfo mFirstApp;

    private CustomApplicationsAdapter mAdapter;
    private TextView mTitleView;
    private ImageView mFirstAppIcon;
    private TextView mFirstAppLabel;
    private GridView mGrid;
    protected AppManager mAppManager;

    private LinearLayout mFirstAppLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        setContentView(R.layout.custom_grid_fragment);
        mAppManager = AppManager.getInstance();
        mFirstAppIcon = (ImageView) findViewById(R.id.first_app_icon);
        mFirstAppLabel = (TextView) findViewById(R.id.first_app_label);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(R.string.lontai_title);
        mFirstAppLayout = (LinearLayout) findViewById(R.id.first_app_layout);
        // mFirstAppLayout.setBackgroundResource(R.drawable.focus_selector);
        mFirstAppLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFirstApp.startApplication(LongTaiAppsGridActivity.this);

            }
        });
        // mTitleView.setText(R.string.media_title);
        mGrid = (GridView) findViewById(R.id.all_apps);
        // mGrid.setSelector(R.drawable.focus_selector);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		this.registerReceiver(mAppUpdateReceiver, filter);
        onAppManagerReady();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(mAppUpdateReceiver);
    }

    /**
     * Creates a new applications adapter for the grid view and registers it.
     */
    public void onAppManagerReady() {
        mAppList = mAppManager.getLonApplications();
        if (mAppList.size() == 0) {
            return;
        }
        mFirstApp = mAppList.get(0);
        mFirstAppIcon.setImageResource(R.drawable.lontai_first);
        mFirstAppLabel.setText(mFirstApp.getTitle());
        Log.d(TAG, mAppList.toString());
        sortByInstallTime(mAppList);
        Iterator<ApplicationInfo> iter = mAppList.iterator();
        while (iter.hasNext()) {
            ApplicationInfo app = iter.next();
            if (app.mComponent.equals(mFirstApp.mComponent)) {
                iter.remove();
            }
        }
        mAdapter = new CustomApplicationsAdapter(this, mAppList);

        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAppList.get(position).startApplication(LongTaiAppsGridActivity.this);
            }
        });
    }

    /* package */ApplicationInfo getSelectedItem() {
        return (ApplicationInfo) mGrid.getSelectedItem();
    }

    private void sortByInstallTime(final List<ApplicationInfo> apps) {
        Collections.sort(apps, ApplicationInfo.INSTALL_TIME_COMPARATOR);
    }
    private BroadcastReceiver mAppUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
					|| intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)
					|| intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)
					|| intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				onAppManagerReady();
				String packageName = intent.getDataString();
				Log.d("Secondary", "packageName ===== "+packageName);
			}
		}
	};
}
