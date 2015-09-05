
package com.luntech.launcher.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luntech.launcher.R;
import com.luntech.launcher.secondary.ApplicationInfo;

import java.util.List;

/**
 * GridView adapter to show the list of all installed applications.
 */
public class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
    private LayoutInflater mLayoutInflater;
    private List<ApplicationInfo> mAppList;

    public ApplicationsAdapter(Context context, List<ApplicationInfo> apps) {
        super(context, 0, apps);
        mAppList = apps;
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

    static class ViewHolder {
        ImageView mAppIcon;
        TextView mAppLabel;
    }
}
