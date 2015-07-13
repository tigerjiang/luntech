package com.luntech.launcher.secondary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luntech.launcher.R;

import java.util.List;


/**
 * GridView adapter to show the list of all installed applications.
 */
public class TempraryApplicationsAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<ApplicationInfo> mCustomAppList;;

    public TempraryApplicationsAdapter(Context context, List<ApplicationInfo> apps) {
        mCustomAppList = apps;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return mCustomAppList.get(position);
    }

    @Override
    public int getCount() {
        return mCustomAppList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.custom_application_icon, null);
            viewHolder.mAppIcon = (ImageView) convertView.findViewById(R.id.custom_app_icon_image);
            viewHolder.mAppLabel = (TextView) convertView.findViewById(R.id.custom_app_icon_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ApplicationInfo info = mCustomAppList.get(position);
        Log.d("custom app",info.toString());
        viewHolder.mAppIcon.setImageDrawable(info.getIcon());
        viewHolder.mAppLabel.setText(info.getTitle());

        return convertView;
    }


   static  class ViewHolder {
        ImageView mAppIcon;
        TextView mAppLabel;

    }


@Override
public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
}
  
}
