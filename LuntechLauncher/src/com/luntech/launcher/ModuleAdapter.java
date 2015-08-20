
package com.luntech.launcher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luntech.launcher.AsyncImageLoader.ImageCallback;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleAdapter extends BaseAdapter {

    private List<Module> mAllModuleList;
    private List<Module> modules;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ModuleAdapter(List<Module> moduleList, Context context) {
        this.mAllModuleList = moduleList;
        initList();
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return modules.size();
    }

    @Override
    public Object getItem(int position) {
        return modules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Module module = modules.get(position);
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

        AsyncImageLoader asyncImageLoader = new AsyncImageLoader(mContext);

        String key = module.moduleCode;
        String pkg = ToolUtils.getValueFromSP(mContext, key);
        if (!TextUtils.isEmpty(pkg)) {
            AppManager appManager = AppManager.getInstance();
            ApplicationInfo app = appManager.getInfoFromAllActivitys(pkg);
            holder.appLogoView.setImageDrawable(app.getIcon());
        } else {
            Drawable icon = asyncImageLoader.loadDrawable(module.getModuleIcon(),
                    holder.appLogoView, new ImageCallback() {

                        @Override
                        public void imageLoaded(Drawable imageDrawable, ImageView imageView,
                                                String imageUrl) {
                            if (imageDrawable != null) {
                                imageView.setImageDrawable(imageDrawable);
                            } else {
                                imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(
                                        mContext, imageUrl));
                            }
                        }
                    });
        }
        asyncImageLoader.loadDrawable(module.getModuleBg(), holder.appBgView, new ImageCallback() {

            @Override
            public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                } else {
                    imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                            imageUrl));
                }
            }
        });
        asyncImageLoader.loadDrawable(module.getModuleShadow(), holder.appShadowView,
                new ImageCallback() {

                    @Override
                    public void imageLoaded(Drawable imageDrawable, ImageView imageView,
                                            String imageUrl) {
                        if (imageDrawable != null) {
                            imageView.setImageDrawable(imageDrawable);
                        } else {
                            imageView.setImageDrawable(ToolUtils.getDrawableFromAttribute(mContext,
                                    imageUrl));
                        }
                    }
                });
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
        Module module1 = mAllModuleList.get(0);
        Module module2 = mAllModuleList.get(1);
        Module module3 = mAllModuleList.get(2);
        modules = new ArrayList<Module>();
        Iterator<Module> iter = mAllModuleList.iterator();
        while (iter.hasNext()) {
            Module module = iter.next();
            if (module.moduleText
                    .equals(module1.moduleText)
                    || module.moduleText
                    .equals(module2.moduleText)
                    || module.moduleText
                    .equals(module3.moduleText)) {
                Logger.d("filter app " + module.toString());
            } else {
                modules.add(module);
            }
        }
    }
}
