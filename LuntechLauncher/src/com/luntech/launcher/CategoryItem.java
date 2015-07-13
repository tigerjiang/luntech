
package com.luntech.launcher;

import android.graphics.drawable.Drawable;

import com.luntech.launcher.secondary.ApplicationInfo;


public class CategoryItem {
    public String mPackageName;
    public CharSequence mLabel;
    public Drawable mAppIcon;
    public Drawable mBackgroundIcon;
    public Drawable mShadowIcon;
    public ApplicationInfo mApplicationInfo;

    public CategoryItem() {

    }

    public CategoryItem(ApplicationInfo applicationInfo,
            Drawable mBackgroundIcon, Drawable mShadowIcon) {
        mApplicationInfo = applicationInfo;
        this.mLabel = mApplicationInfo.getTitle();
        this.mAppIcon = mApplicationInfo.getIcon();
        this.mBackgroundIcon = mBackgroundIcon;
        this.mShadowIcon = mShadowIcon;
    }
    

    public CategoryItem(String mPackageName, CharSequence mLabel, Drawable mAppIcon,
            Drawable mBackgroundIcon, Drawable mShadowIcon) {
        super();
        this.mPackageName = mPackageName;
        this.mLabel = mLabel;
        this.mAppIcon = mAppIcon;
        this.mBackgroundIcon = mBackgroundIcon;
        this.mShadowIcon = mShadowIcon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packagename) {
        this.mPackageName = packagename;
    }

    public CharSequence getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.mAppIcon = appIcon;
    }

    public Drawable getBackgroundIcon() {
        return mBackgroundIcon;
    }

    public void setBackgroundIcon(Drawable backgroundIcon) {
        this.mBackgroundIcon = backgroundIcon;
    }

    public Drawable getShadowIcon() {
        return mShadowIcon;
    }

    public void setShadowIcon(Drawable shadowIcon) {
        this.mShadowIcon = shadowIcon;
    }

    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

}
