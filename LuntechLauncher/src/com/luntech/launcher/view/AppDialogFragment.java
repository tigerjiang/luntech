
package com.luntech.launcher.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.luntech.launcher.Launcher;
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
        Button okBtn = (Button) view.findViewById(R.id.ok);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Activity activity = getActivity();
                if (activity instanceof Launcher) {
                    ((Launcher) activity).setResult(mSelectedApp, true);
                }
                dismiss();
                Log.d("jzh", "ok for " + mSelectedApp.toString());

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Activity activity = getActivity();
                if (activity instanceof Launcher) {
                    ((Launcher) activity).setResult(mSelectedApp, false);
                }
                Log.d("jzh", "cancel");
                dismiss();

            }
        });
        okBtn.requestFocus();
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
        getDialog().getWindow().setAttributes(params);
    }
    /**
     * Creates a new applications adapter for the grid view and registers it.
     */

    public void onAppManagerReady() {
        mAppList = mAppManager.getSelectedApplications();
        sortByInstallTime(mAppList);
        mAdapter = new ApplicationsAdapter(mContext, mAppList);
        mAdapter.setIndex(0);
        mGrid.setAdapter(mAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // mSelectedApp = mAppList.get(0);
            }
        });
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setIndex(position);
                mAdapter.notifyDataSetChanged();
                mSelectedApp = mAppList.get(position);
                Log.d("jzh", "onItemClick " + mSelectedApp.toString());
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

        public void setIndex(int selected) {
            Log.d("jzh", "setIndex " + selected);
            mIndex = selected;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.selected_application_icon, null);
                viewHolder.mAppIcon = (ImageView) convertView
                        .findViewById(R.id.selected_app_icon_image);
                viewHolder.mAppLabel = (TextView) convertView
                        .findViewById(R.id.selected_app_icon_label);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ApplicationInfo info = mAppList.get(position);

            viewHolder.mAppIcon.setImageDrawable(info.getIcon());
            viewHolder.mAppLabel.setText(info.getTitle());
            if (mIndex == position) {
                Log.d("jzh", "mIndex " + mIndex + " position " + position);
                convertView.setBackgroundColor(Color.GRAY);
                viewHolder.mAppLabel.setTextColor(Color.WHITE);
            }
            else {
                viewHolder.mAppLabel.setTextColor(Color.LTGRAY);
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView mAppIcon;
        TextView mAppLabel;

    }

}
