
package com.luntech.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class CustomApplication {
    public Group mGroup;

    static Context mContext;
    static Resources mResources;
    static String mPackageName;

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
        public ArrayList<Module> mModules;
        public Group() {

        }

        public String groupCode;
        public int groupMoveable;
        public int groupFlag;
        public String groupText;
        public String groupBg;
        public String groupIcon;

        public String getGroupCode() {
            return groupCode;
        }

        public void setGroupCode(String groupCode) {
            this.groupCode = groupCode;
        }

        public int getGroupMoveable() {
            return groupMoveable;
        }

        public void setGroupMoveable(int groupMoveable) {
            this.groupMoveable = groupMoveable;
        }

        public int getGroupFlag() {
            return groupFlag;
        }

        public void setGroupFlag(int groupFlag) {
            this.groupFlag = groupFlag;
        }

        public String getGroupText() {
            return groupText;
        }

        public void setGroupText(String groupText) {
            this.groupText = groupText;
        }

        public String getGroupBg() {
            return groupBg;
        }

        public void setGroupBg(String groupBg) {
            this.groupBg = groupBg;
        }

        public String getGroupIcon() {
            return groupIcon;
        }

        public void setGroupIcon(String groupIcon) {
            this.groupIcon = groupIcon;
        }

        public void addModule(Module module) {
            if (mModules == null) {
                mModules = new ArrayList<CustomApplication.Module>();
                mModules.add(module);
            } else {
                mModules.add(module);
            }
        }
        @Override
        public String toString() {
            return "Group [groupCode=" + groupCode + ", groupMoveable=" + groupMoveable
                    + ", groupFlag=" + groupFlag + ", groupName=" + groupText + ", groupBg="
                    + groupBg + ", groupIcon=" + groupIcon +", module=" + mModules + "]";
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
        public CharSequence moduleText;
        public String moduleBg;
        public String moduleIcon;
        public String moduleShadow;

        public Drawable moduleBgDrawable;
        public Drawable moduleIconDrawable;
        public Drawable moduleShadowDrawable;

        public String getModuleCode() {
            return moduleCode;
        }

        public void setModuleCode(String moduleCode) {
            this.moduleCode = moduleCode;
        }

        public int getModuleReplace() {
            return moduleReplace;
        }

        public void setModuleReplace(int moduleReplace) {
            this.moduleReplace = moduleReplace;
        }

        public int getModuleType() {
            return moduleType;
        }

        public void setModuleType(int moduleType) {
            this.moduleType = moduleType;
        }

        public CharSequence getModuleText() {
            return moduleText;
        }

        public void setModuleText(String moduleText) {
            this.moduleText = moduleText;
        }

        public String getModuleBg() {
            return moduleBg;
        }

        public void setModuleBg(boolean defaultValue, String moduleBg) {
            this.moduleBg = moduleBg;
            if (defaultValue) {
                setModuleBgDrawable(changeIdtoDrawable(this.moduleBg));
            } else {
                setModuleBgDrawable(changeFiletoDrawable(this.moduleBg));
            }
        }

        public String getModuleIcon() {
            return moduleIcon;
        }

        public void setModuleIcon(boolean defaultValue, String moduleIcon) {
            this.moduleIcon = moduleIcon;
            if (defaultValue) {
                setModuleBgDrawable(changeIdtoDrawable(this.moduleIcon));
            } else {
                setModuleBgDrawable(changeFiletoDrawable(this.moduleIcon));
            }
        }

        public String getModuleShadow() {
            return moduleShadow;
        }

        public void setModuleShadow(boolean defaultValue, String moduleShadow) {
            this.moduleShadow = moduleShadow;
            if (defaultValue) {
                setModuleBgDrawable(changeIdtoDrawable(this.moduleShadow));
            } else {
                setModuleBgDrawable(changeFiletoDrawable(this.moduleShadow));
            }
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
                mApps.add(app);
            } else {
                mApps.add(app);
            }
        }

        private  Drawable changeIdtoDrawable(String name) {
            Drawable icon = null;
            int resId = mResources.getIdentifier(name, "drawable", mPackageName);
            if (resId == 0) {
                Log.e("error", mPackageName+ " resource not found for " + name);
            } else {
                icon = mResources.getDrawable(resId);
            }
            return icon;
        }

        private  Drawable changeFiletoDrawable(String path) {
            Drawable icon = null;
            if (!TextUtils.isEmpty(path)) {
                icon = Drawable.createFromPath(path);
            }
            return icon;
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
        public ComponentName componentName;
        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppPackage() {
            return appPackage;
        }

        public void setAppPackage(String appPackage) {
            this.appPackage = appPackage;
        }

        public String getAppActivity() {
            return appActivity;
        }

        public void setAppActivity(String appActivity) {
            this.appActivity = appActivity;
            if(!TextUtils.isEmpty(appActivity)){
                String[] info = this.appActivity.split("/");
                setComponentName(new ComponentName(info[0], info[0] + info[1]));
                
            }else{
                Logger.e("activity is null");
            }
        }

        public String getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(String appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public ComponentName getComponentName() {
            return componentName;
        }

        public void setComponentName(ComponentName componentName) {
            this.componentName = componentName;
        }

        @Override
        public String toString() {
            return "App [appName=" + appName + ", appPackagename=" + appPackage + ", appActivity="
                    + appActivity + ", appIcon=" + appIcon + ", appUrl=" + appUrl + "]";
        }

    }

}
