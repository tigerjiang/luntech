
package com.luntech.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryItemAdapter extends BaseAdapter {

    private List<CustomApplication> mAppList;
    private List<CustomApplication> mCatgoryAppList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CategoryItemAdapter(List<CustomApplication> appList, Context context) {
        super();
        this.mAppList = appList;
        initList();
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mCatgoryAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCatgoryAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CustomApplication application = mCatgoryAppList.get(position);
        final CustomApplication.Module module = application.mGroup.mModules.get(0);
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

        holder.appBgView.setImageDrawable(module.getModuleBgDrawable());
        holder.appLogoView.setImageDrawable(module.getModuleIconDrawable());
        holder.appShadowView.setImageDrawable(module.getModuleShadowDrawable());
        holder.mAppLabel.setText(module.getModuleText());
        return convertView;
    }

    static class ViewHolder {
        ImageView appLogoView;
        ImageView appBgView;
        ImageView appShadowView;
        TextView mAppLabel;
    }

    private void initList() {
        Iterator<CustomApplication> iter = mAppList.iterator();
        while (iter.hasNext()) {
            CustomApplication app = iter.next();
            if (app.mGroup.mModules.get(0).moduleText
                    .equals(mAppList.get(0).mGroup.mModules.get(0).moduleText)
                    || app.mGroup.mModules.get(0).moduleText.equals(mAppList.get(1).mGroup.mModules
                            .get(0).moduleText)
                    || app.mGroup.mModules.get(0).moduleText.equals(mAppList.get(2).mGroup.mModules
                            .get(0).moduleText)) {
                iter.remove();
            }
        }
        mCatgoryAppList = new ArrayList<CustomApplication>(mAppList);
    }
}
