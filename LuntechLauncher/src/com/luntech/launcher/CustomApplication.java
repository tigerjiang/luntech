
package com.luntech.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher.R;

import java.io.File;
import java.util.ArrayList;

public class CustomApplication {
    public Group mGroup;
    public ArrayList<Module> mModules;

    
    private Context mContext;
    private Resources mResources;
    private String mPackageName;

    public CustomApplication(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();

    }

    static class Group {
        public static final String GROUP_TAG = "group";
        public static final String GROUP_CODE_TAG = "code";
        public static final String GROUP_MOVEABLE_TAG = "moveable";
        public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
        public static final String GROUP_TEXT_TAG = "g_text";
        public static final String GROUP_BG_TAG = "g_bg";
        public static final String GROUP_ICON_TAG = "g_icon";

        public Group() {

        }

        public String groupCode;
        public int groupMoveable;
        public int groupFlag;
        public String groupText;
        public String groupBg;
        public String groupIcon;
        

        @Override
        public String toString() {
            return "Group [groupCode=" + groupCode + ", groupMoveable=" + groupMoveable
                    + ", groupFlag=" + groupFlag + ", groupName=" + groupText + ", groupBg=" + groupBg + ", groupIcon=" + groupIcon + "]";
        }

    }

    static class Module {
        public static final String MODULE_TAG = "module";
        public static final String MODULE_CODE_TAG = "code";
        public static final String MODULE_REPLACE_TAG = "replace";
        public static final String MODULE_TYPE_TAG = "type";
        public static final String MODULE_TEXT_TAG = "m_text";
        public static final String MODULE_BG_TAG = "m_bg";
        public static final String MODULE_ICON_TAG = "m_icon";
        public static final String MODULE_SHADOW_TAG = "m_shadow";
        public ArrayList<App> mApps;

        public Module() {

        }

        public String moduleCode;
        public int moduleReplace;
        public int moduleType;
        public String moduleText;
        public String moduleBg;
        public String moduleIcon;
        public String moduleShadow;

        public Drawable moduleBgDrawable;
        public Drawable moduleIconDrawable;
        public Drawable moduleShadowDrawable;

        public String getModuleBg() {
            return moduleBg;
        }

        public void setModuleBg(String moduleBg) {
            this.moduleBg = moduleBg;
        }

        public String getModuleIcon() {
            return moduleIcon;
        }

        public void setModuleIcon(String moduleIcon) {
            this.moduleIcon = moduleIcon;
        }

        public String getModuleShadow() {
            return moduleShadow;
        }

        public void setModuleShadow(String moduleShadow) {
            this.moduleShadow = moduleShadow;
        }

        public Drawable getModuleBgDrawable() {
            return moduleBgDrawable;
        }

        public void setModuleBgDrawable(Drawable moduleBgDrawable) {
            this.moduleBgDrawable = moduleBgDrawable;
        }

        public Drawable getModuleIconDrawable() {
            return moduleIconDrawable;
        }

        public void setModuleIconDrawable(Drawable moduleIconDrawable) {
            this.moduleIconDrawable = moduleIconDrawable;
        }

        public Drawable getModuleShadowDrawable() {
            return moduleShadowDrawable;
        }

        public void setModuleShadowDrawable(Drawable moduleShadowDrawable) {
            this.moduleShadowDrawable = moduleShadowDrawable;
        }

        public void addApp(App app) {
            if (mApps == null) {
                mApps = new ArrayList<CustomApplication.App>();
            } else {
                mApps.add(app);
            }
        }

        @Override
        public String toString() {
            return "Module [mApps=" + mApps + ", moduleCode=" + moduleCode + ", moduleReplace="
                    + moduleReplace + ", moduleType=" + moduleType + ", moduleShowText="
                    + moduleText + ", moduleBg=" + moduleBg + ", moduleIcon=" + moduleIcon
                    + ", moduleShadow=" + moduleShadow + "]";
        }

    }

    static class App {
        public static final String APP_TAG = "app";
        public static final String APPS_TAG = "apps";
        public static final String APP_NAME_TAG = "a_name";
        public static final String APP_PACKAGE_TAG = "a_package";
        public static final String APP_ACTIVITY_TAG = "a_activity";
        public static final String APP_ICON_TAG = "a_icon";
        public static final String APP_URL_TAG = "a_url";

        public App() {

        }

        public String appName;
        public String appPackage;
        public String appActivity;
        public String appIcon;
        public String appUrl;

        @Override
        public String toString() {
            return "App [appName=" + appName + ", appPackagename=" + appPackage
                    + ", appActivity=" + appActivity  + ", appIcon=" + appIcon + ", appUrl=" + appUrl + "]";
        }

    }

    public void addModule(Module module) {
        if (mModules == null) {
            mModules = new ArrayList<CustomApplication.Module>();
        } else {
            mModules.add(module);
        }
    }

    private Drawable changeIdtoDrawable(String name) {
        Drawable icon = null;
        int resId = mResources.getIdentifier(name, "drawable", mPackageName);
        if (resId == 0) {
            Log.e("error", "resource not found for " + name);
        } else {
            icon = mResources.getDrawable(resId);
        }
        return icon;
    }

    private Drawable changeFiletoDrawable(String path) {
        Drawable icon = null;
        if (!TextUtils.isEmpty(path)) {
            icon = Drawable.createFromPath(path);
        }
        return icon;
    }

}
