package com.luntech.launcher;

import android.content.ComponentName;
import android.text.TextUtils;

public class App {
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
                + appActivity + ", appIcon=" + appIcon + ", appUrl=" + appUrl + ", componentName=" + componentName+ "]";
    }

}