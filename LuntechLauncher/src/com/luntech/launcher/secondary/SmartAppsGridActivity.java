
package com.luntech.launcher.secondary;

import java.util.ArrayList;
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
import android.widget.Toast;

import com.luntech.launcher.R;

public class SmartAppsGridActivity extends Activity {

    private static final String TAG = SmartAppsGridActivity.class.getSimpleName();

//    private List<ApplicationInfo> mAppList;
    private ApplicationInfo mFirstApp;
    private List<ApplicationInfo> mTempAppList;
    private CustomApplicationsAdapter mAdapter;
    private TextView mTitleView;
    private ImageView mFirstAppIcon;
    private TextView mFirstAppLabel;
    private GridView mGrid;
    protected AppManager mAppManager;

    private LinearLayout mFirstAppLayout;
    private static int[] mTempResIds;
    static {
        mTempResIds = new int[] {R.drawable.house_first,
                R.drawable.smart_curtain, R.drawable.smart_energy, R.drawable.smart_light,
                R.drawable.smart_media,
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        setContentView(R.layout.smart_grid_fragment);
        mTempAppList = new ArrayList<ApplicationInfo>();
//        mAppManager = AppManager.getInstance();
        mFirstAppIcon = (ImageView) findViewById(R.id.first_app_icon);
        mFirstAppLabel = (TextView) findViewById(R.id.first_app_label);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(R.string.house_title);
        mFirstAppLayout = (LinearLayout) findViewById(R.id.first_app_layout);
        // mFirstAppLayout.setBackgroundResource(R.drawable.focus_selector);
        mFirstAppLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClass(getApplicationContext(), DownApkActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(intent);
//                mFirstApp.startApplication(SmartAppsGridActivity.this);

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
/*        mAppList = mAppManager.getHouseApplications();
        if (mAppList.size() == 0) {
            return;
        }
        mFirstApp = mAppList.get(0);
        mFirstAppIcon.setImageResource(R.drawable.house_first);
        mFirstAppLabel.setText(mFirstApp.getTitle());
        Log.d(TAG, mAppList.toString());
        sortByInstallTime(mAppList);*/
        
        mTempAppList.clear();
        String[] labelArray = getResources().getStringArray(R.array.scenario_array);
        Log.d(TAG, "titles:"+labelArray.toString());
        for (int i = 0; i < labelArray.length; i++) {
            ApplicationInfo ai = new ApplicationInfo();
            ai.mIcon = getResources().getDrawable(mTempResIds[i]);
            ai.mTitle = labelArray[i];
            mTempAppList.add(ai);
        }
        
        mFirstApp = mTempAppList.get(0);
        mFirstAppIcon.setImageResource(R.drawable.house_first);
        mFirstAppLabel.setText(mFirstApp.getTitle());
        Iterator<ApplicationInfo> iter = mTempAppList.iterator();
        while (iter.hasNext()) {
            ApplicationInfo app = iter.next();
            if (app.mTitle.equals(mFirstApp.mTitle)) {
                iter.remove();
            }
        }
       
        mAdapter = new CustomApplicationsAdapter(this, mTempAppList);

        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent intent = new Intent();
            	intent.setClass(SmartAppsGridActivity.this, SmartDetialActivity.class);
            	intent.putExtra("title", mTempAppList.get(position).mTitle);
            	startActivity(intent);
//                Toast.makeText(getApplicationContext(), R.string.alert, Toast.LENGTH_SHORT).show();
//                 mAppList.get(position).startApplication(HouseAppsGridActivity.this);
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
                Log.d("Secondary", "packageName ===== " + packageName);
            }
        }
    };
}
