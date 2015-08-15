
package com.luntech.launcher.db;

public class AppItem {

    // app
    public String appName;
    public String appPackage;
    public String appActivity;
    public String appIcon;
    public String appUrl;
    // module
    public String moduleCode;
    public int moduleReplace;
    public int moduleType;
    public CharSequence moduleText;
    public String moduleBg;
    public String moduleIcon;
    public String moduleShadow;
    // group
    public String groupCode;
    public int groupMoveable;
    public int groupFlag;
    public String groupText;
    public String groupBg;
    public String groupIcon;
    public String fileName;
    public String replacePackage;
    private int hasReplace;
    private int downloadId;
    private String downloadStatus;

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

    public void setModuleText(CharSequence moduleText) {
        this.moduleText = moduleText;
    }

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

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    @Override
    public String toString() {
        return "AppItem [appName=" + appName + ", appPackage=" + appPackage + ", appActivity="
                + appActivity + ", appIcon=" + appIcon + ", appUrl=" + appUrl + ", moduleCode="
                + moduleCode + ", moduleReplace=" + moduleReplace + ", moduleType=" + moduleType
                + ", moduleText=" + moduleText + ", moduleBg=" + moduleBg + ", moduleIcon="
                + moduleIcon + ", moduleShadow=" + moduleShadow + ", groupCode=" + groupCode
                + ", groupMoveable=" + groupMoveable + ", groupFlag=" + groupFlag + ", groupText="
                + groupText + ", groupBg=" + groupBg + ", groupIcon=" + groupIcon + "]";
    }

}
