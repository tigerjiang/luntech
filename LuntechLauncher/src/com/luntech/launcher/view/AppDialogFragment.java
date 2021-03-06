
package com.luntech.launcher.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.luntech.launcher.IPTVLauncher;
import com.luntech.launcher.Q1SLauncher;
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
        onAppManagerReady();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check

        if (getDialog() == null) {
            return;
        }
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        params.dimAmount = 0.8f;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }

    /**
     * Creates a new applications adapter for the grid view and registers it.
     */

    public void onAppManagerReady() {
        mAppList = mAppManager.getSelectedApplications();
        sortByInstallTime(mAppList);
        mAdapter = new ApplicationsAdapter(mContext, mAppList);
        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedApp = mAppList.get(position);
                Activity activity = getActivity();
                if (activity instanceof IPTVLauncher) {
                    ((IPTVLauncher) activity).setResult(mSelectedApp, true);
                } else if (activity instanceof Q1SLauncher) {
                    ((Q1SLauncher) activity).setResult(mSelectedApp, true);
                }
                dismiss();
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
        private int mIndex = 0;

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
                convertView = mLayoutInflater.inflate(R.layout.app_item_icon, null);
                viewHolder.mAppIcon = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                viewHolder.mAppLabel = (TextView) convertView
                        .findViewById(R.id.app_label);
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
