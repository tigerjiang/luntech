
package com.luntech.launcher;

import java.util.ArrayList;

public class CustomApplication {
    public Group mGroup;
    public ArrayList<Module> mModules;
    public ArrayList<App> mApps;

    static class Group {
        public static final String GROUP_TAG = "group";
        public static final String GROUP_CODE_TAG = "code";
        public static final String GROUP_MOVEABLE_TAG = "moveable";
        public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
        public static final String GROUP_NAME_TAG = "name";
        public static final String GROUP_SHOWTEXT_TAG = "showtext";
        public static final String GROUP_BG_TAG = "bg";
        public static final String GROUP_ICON_TAG = "icon";

        public Group() {

        }

        public String groupCode;
        public int groupMoveable;
        public int groupFlag;
        public String groupName;
        public String groupShowText;
        public String groupBg;
        public String groupIcon;

    }

    static class Module {
        public static final String MODULE_TAG = "module";
        public static final String MODULE_CODE_TAG = "code";
        public static final String MODULE_REPLACE_TAG = "replace";
        public static final String MODULE_TYPE_TAG = "type";
        public static final String MODULE_SHOWTEXT_TAG = "showtext";
        public static final String MODULE_BG_TAG = "bg";
        public static final String MODULE_ICON_TAG = "icon";
        public static final String MODULE_SHADOW_TAG = "shadow";

        public Module() {

        }

        public String moduleCode;
        public int moduleReplace;
        public int moduleType;
        public String moduleShowText;
        public String moduleBg;
        public String moduleIcon;
        public String moduleShadow;
    }

    static class App {
        public static final String APP_TAG = "app";
        public static final String APPS_TAG = "apps";
        public static final String APP_NAME_TAG = "name";
        public static final String APP_PACKAGE_NAME_TAG = "package_name";
        public static final String APP_ACTIVITY_TAG = "activity";
        public static final String APP_VERSION_TAG = "version";
        public static final String APP_SIZE_TAG = "size";
        public static final String APP_ICON_TAG = "icon";
        public static final String APP_URL_TAG = "url";

        public App() {

        }

        public String appName;
        public String appPackagename;
        public String appActivity;
        public int appVersion;
        public int appSize;
        public String appIcon;
        public String appUrl;
    }

    public void addModule(Module module) {
        if (mModules == null) {
            mModules = new ArrayList<CustomApplication.Module>();
        } else {
            mModules.add(module);
        }
    }

    public void addApp(App app) {
        if (mApps == null) {
            mApps = new ArrayList<CustomApplication.App>();
        } else {
            mApps.add(app);
        }
    }
}
