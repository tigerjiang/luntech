
package com.luntech.launcher;

import java.util.ArrayList;

public class CustomApplication {
    public Group mGroup;
    public ArrayList<Module> mModules;

    static class Group {
        public static final String GROUP_TAG = "group";
        public static final String GROUP_CODE_TAG = "code";
        public static final String GROUP_MOVEABLE_TAG = "moveable";
        public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
        public static final String GROUP_NAME_TAG = "group_name";
        public static final String GROUP_SHOWTEXT_TAG = "group_showtext";
        public static final String GROUP_BG_TAG = "group_bg";
        public static final String GROUP_ICON_TAG = "group_icon";

        public Group() {

        }

        public String groupCode;
        public int groupMoveable;
        public int groupFlag;
        public String groupName;
        public String groupShowText;
        public String groupBg;
        public String groupIcon;

        @Override
        public String toString() {
            return "Group [groupCode=" + groupCode + ", groupMoveable=" + groupMoveable
                    + ", groupFlag=" + groupFlag + ", groupName=" + groupName + ", groupShowText="
                    + groupShowText + ", groupBg=" + groupBg + ", groupIcon=" + groupIcon + "]";
        }

    }

    static class Module {
        public static final String MODULE_TAG = "module";
        public static final String MODULE_CODE_TAG = "code";
        public static final String MODULE_REPLACE_TAG = "replace";
        public static final String MODULE_TYPE_TAG = "type";
        public static final String MODULE_SHOWTEXT_TAG = "module_showtext";
        public static final String MODULE_BG_TAG = "module_bg";
        public static final String MODULE_ICON_TAG = "module_icon";
        public static final String MODULE_SHADOW_TAG = "module_shadow";
        public ArrayList<App> mApps;

        public Module() {

        }

        public String moduleCode;
        public int moduleReplace;
        public int moduleType;
        public String moduleShowText;
        public String moduleBg;
        public String moduleIcon;
        public String moduleShadow;

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
                    + moduleShowText + ", moduleBg=" + moduleBg + ", moduleIcon=" + moduleIcon
                    + ", moduleShadow=" + moduleShadow + "]";
        }

    }

    static class App {
        public static final String APP_TAG = "app";
        public static final String APPS_TAG = "apps";
        public static final String APP_NAME_TAG = "app_name";
        public static final String APP_PACKAGE_NAME_TAG = "app_package_name";
        public static final String APP_ACTIVITY_TAG = "app_activity";
        public static final String APP_VERSION_TAG = "app_version";
        public static final String APP_SIZE_TAG = "app_size";
        public static final String APP_ICON_TAG = "app_icon";
        public static final String APP_URL_TAG = "app_url";

        public App() {

        }

        public String appName;
        public String appPackagename;
        public String appActivity;
        public int appVersion;
        public int appSize;
        public String appIcon;
        public String appUrl;

        @Override
        public String toString() {
            return "App [appName=" + appName + ", appPackagename=" + appPackagename
                    + ", appActivity=" + appActivity + ", appVersion=" + appVersion + ", appSize="
                    + appSize + ", appIcon=" + appIcon + ", appUrl=" + appUrl + "]";
        }

    }

    public void addModule(Module module) {
        if (mModules == null) {
            mModules = new ArrayList<CustomApplication.Module>();
        } else {
            mModules.add(module);
        }
    }

}
