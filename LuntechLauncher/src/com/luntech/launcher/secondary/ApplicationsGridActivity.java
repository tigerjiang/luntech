
package com.luntech.launcher.secondary;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;



import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.luntech.launcher.R;

public class ApplicationsGridActivity extends Activity {

    private static final String TAG = ApplicationsGridActivity.class.getSimpleName();

    private List<ApplicationInfo> mAppList;

    private ApplicationsAdapter mAdapter;

    private GridView mGrid;
    protected AppManager mAppManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_apps));
        setContentView(R.layout.applications_grid_fragment);
        mAppManager = AppManager.getInstance();
        mGrid = (GridView) findViewById(R.id.all_apps);
        mGrid.setSelector(R.drawable.focus_selector);
        Log.d(TAG, "oncreate ");
    }

    @Override
    public void onStart() {
        super.onStart();
        onAppManagerReady();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Creates a new applications adapter for the grid view and registers it.
     */
    
    public void onAppManagerReady() {
        mAppList = mAppManager.getAllAppsApplications();
        Log.d(TAG, mAppList.toString());
        sortByInstallTime(mAppList);
        Log.d(TAG, mAppList.toString());
        mAdapter = new ApplicationsAdapter(this, mAppList);

        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAppList.get(position).startApplication(ApplicationsGridActivity.this);
            }
        });
    }

    /* package */ApplicationInfo getSelectedItem() {
        return (ApplicationInfo) mGrid.getSelectedItem();
    }

    private void sortByInstallTime(final List<ApplicationInfo> apps) {
        Collections.sort(apps, ApplicationInfo.INSTALL_TIME_COMPARATOR);
    }

    /**
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
        private LayoutInflater mLayoutInflater;

        public ApplicationsAdapter(Context context, List<ApplicationInfo> apps) {
            super(context, 0, apps);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.collection_application_icon, null);
                viewHolder.mAppIcon = (ImageView) convertView.findViewById(R.id.app_icon_image);
                viewHolder.mAppLabel = (TextView) convertView.findViewById(R.id.app_icon_label);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ApplicationInfo info = mAppList.get(position);

            viewHolder.mAppIcon.setImageDrawable(info.getIcon());
            viewHolder.mAppLabel.setText(info.getTitle());

            return convertView;
        }

    }

        static class ViewHolder {
            ImageView mAppIcon;
            TextView mAppLabel;

        }

}
