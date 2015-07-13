
package com.luntech.launcher.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.luntech.launcher.AppSelectedActivity;
import com.luntech.launcher.R;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;

import java.util.Collections;
import java.util.List;

public class AppDialogFragment extends DialogFragment {
    private List<ApplicationInfo> mAppList;

    private ApplicationsAdapter mAdapter;

    private GridView mGrid;
    private static Context mContext;
    protected static AppManager mAppManager;
    ApplicationInfo mSelectedApp;

    public AppDialogFragment() {

    }

    public static AppDialogFragment newInstance(Context context) {
        mContext = context;
        mAppManager = AppManager.getInstance();
        final AppDialogFragment sDialogFragment = new AppDialogFragment();
        return sDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.app_dialog, null);
        mGrid = (GridView) view.findViewById(R.id.select_apps);
        mGrid.setSelector(R.drawable.focus_selector);
        onAppManagerReady();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                Activity activity = getActivity();
                if (activity instanceof AppSelectedActivity) {
                    ((AppSelectedActivity) activity).setResult(mSelectedApp, true);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                Activity activity = getActivity();
                if (activity instanceof AppSelectedActivity) {
                    ((AppSelectedActivity) activity).setResult(mSelectedApp, false);
                }
            }
        });

        builder.setTitle(R.string.select_app);
        Dialog dialog = builder.create();
        return dialog;
    }

    /**
     * Creates a new applications adapter for the grid view and registers it.
     */

    public void onAppManagerReady() {
        mAppList = mAppManager.getAllAppsApplications();
        sortByInstallTime(mAppList);
        mAdapter = new ApplicationsAdapter(mContext, mAppList);

        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedApp = mAppList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedApp = mAppList.get(0);
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
