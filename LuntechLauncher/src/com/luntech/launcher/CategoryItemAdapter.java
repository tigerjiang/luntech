
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
        Logger.d("appList " + appList.toString());
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
        final Module module = application.mGroup.mModules.get(0);
        Logger.d(" module " + module.toString());
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
        CustomApplication app1 = mAppList.get(0);
        CustomApplication app2 = mAppList.get(1);
        CustomApplication app3 = mAppList.get(2);
        mCatgoryAppList = new ArrayList<CustomApplication>();
        Iterator<CustomApplication> iter = mAppList.iterator();
        while (iter.hasNext()) {
            CustomApplication app = iter.next();
            Logger.d("module " + app.mGroup.mModules.get(0).toString());
            if (app.mGroup.mModules.get(0).moduleText
                    .equals(app1.mGroup.mModules.get(0).moduleText)
                    || app.mGroup.mModules.get(0).moduleText
                            .equals(app2.mGroup.mModules.get(0).moduleText)
                    || app.mGroup.mModules.get(0).moduleText
                            .equals(app3.mGroup.mModules.get(0).moduleText)) {
                Logger.d("filter app " + app.toString());
            } else {
                mCatgoryAppList.add(app);
            }
        }
        Logger.d("Adapter list size " + mCatgoryAppList.size());
        Logger.d("Adapter list content " + mCatgoryAppList.toString());
        Logger.d("all list content " + mAppList.toString());
    }
}
