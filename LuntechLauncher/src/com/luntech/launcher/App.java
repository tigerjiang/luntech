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
    public static final String DOWNLOAD_STATUS_COMPLETED = "DOWNLOAD_COMPLETED";
    public static final String DOWNLOAD_STATUS_DOWNLOADING = "DOWNLOAD_DOWNLOADING";

    public static class Columns {
        public static final String _ID = "_id";
        public static final String Download_id = "download_id";
        public static final String DOWNLOAD_STATUS = "download_status";
        public static final String A_NAME = "a_name";
        public static final String A_PACKAGE = "a_package";
        public static final String A_REPLACE_PACKAGE = "a_replace_package";
        public static final String A_HAS_REPLACE = "a_has_replace";
        public static final String A_ACTIVITY = "a_activity";
        public static final String A_ICON = "a_icon";
        public static final String A_URL = "a_url";
        public static final String A_FILE_NAME = "a_file_name";
        public static final String A_M_CODE = "a_m_code";
    }

    public App() {

    }

    public String appName;
    public String appPackage;
    public String appActivity;
    public String appIcon;
    public String appUrl;
    public String moduleCode;
    public ComponentName componentName;
    public String downloadStatus;
    public String fileName;
    public String replacePackage;
    private int hasReplace;
    private int downloadId;

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
        if (!TextUtils.isEmpty(appActivity)) {
            String[] info = this.appActivity.split("/");
            setComponentName(new ComponentName(info[0], info[0] + info[1]));

        } else {
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

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }


    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getReplacePackage() {
        return replacePackage;
    }

    public void setReplacePackage(String replacePackage) {
        this.replacePackage = replacePackage;
    }

    public int getHasReplace() {
        return hasReplace;
    }

    public void setHasReplace(int hasReplace) {
        this.hasReplace = hasReplace;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    @Override
    public String toString() {
        return "App{" +
                "appName='" + appName + '\'' +
                ", appPackage='" + appPackage + '\'' +
                ", appActivity='" + appActivity + '\'' +
                ", appIcon='" + appIcon + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", moduleCode='" + moduleCode + '\'' +
                ", componentName=" + componentName +
                ", downloadStatus='" + downloadStatus + '\'' +
                ", fileName='" + fileName + '\'' +
                ", replacePackage='" + replacePackage + '\'' +
                ", hasReplace=" + hasReplace +
                ", downloadId=" + downloadId +
                '}';
    }
}