
package com.luntech.launcher;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryItemAdapter extends BaseAdapter {

    private List<CategoryItem> mAppList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CategoryItemAdapter(List<CategoryItem> appList, Context context) {
        super();
        this.mAppList = appList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CategoryItem categoryItem = mAppList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.global_category_item, null);
            holder.appBgView = (ImageView) convertView.findViewById(R.id.app_bg);
            holder.appLogoView = (ImageView) convertView.findViewById(R.id.app_logo);
            holder.appShadowView = (ImageView) convertView.findViewById(R.id.cover_view);
            holder.mAppLabel = (TextView) convertView.findViewById(R.id.app_label);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.appBgView.setImageDrawable(categoryItem.getBackgroundIcon());
        holder.appLogoView.setImageDrawable(categoryItem.getAppIcon());
        holder.appShadowView.setImageDrawable(categoryItem.getShadowIcon());
        holder.mAppLabel.setText(categoryItem.getLabel());
        return convertView;
    }

    static class ViewHolder {
        ImageView appLogoView;
        ImageView appBgView;
        ImageView appShadowView;
        TextView mAppLabel;
    }
}
