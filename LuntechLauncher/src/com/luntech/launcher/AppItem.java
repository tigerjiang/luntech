
package com.luntech.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppItem {
    private String mType;
    private String mName;
    public String mActivityName;
    private String mBackground;
    private String mAvailable;
    private String mConfigured;
    private String mConfiguredComponent;
    private String mLogo;
    private String mShadow;
    public CharSequence mLabel;
    public ComponentName mComponentName;
    public int mIndex;
    public Drawable mAppIcon;
    public Drawable mBackgroundIcon;
    public Drawable mShadowIcon;

    private Context mContext;
    private Resources mResources;
    private String mPackageName;

    public AppItem(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();

    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getActivityName() {
        return mActivityName;
    }

    public void setActivityName(String activityName) {
        this.mActivityName = activityName;
        String[] info = mActivityName.split("/");
        setComponentName(new ComponentName(info[0], info[0] + info[1]));
    }

    public String getBackground() {
        return mBackground;
    }

    public void setBackground(String background) {
        this.mBackground = background;
        setBackgroundIcon(changeIdtoDrawable(mBackground));
    }

    public String isAvailable() {
        return mAvailable;
    }

    public void setAvailable(String available) {
        this.mAvailable = available;
    }

    public String isConfigured() {
        return mConfigured;
    }

    public void setConfigured(String configured) {
        this.mConfigured = configured;
    }

    public String getConfiguredComponent() {
        return mConfiguredComponent;
    }

    public void setConfiguredComponent(String configuredComponent) {
        this.mConfiguredComponent = configuredComponent;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        this.mLogo = logo;
        setAppIcon(changeIdtoDrawable(mLogo));
    }

    public String getShadow() {
        return mShadow;
    }

    public void setShadow(String shadow) {
        this.mShadow = shadow;
        setShadowIcon(changeIdtoDrawable(mShadow));
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

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setComponentName(ComponentName componentName) {
        this.mComponentName = componentName;
    }

    @Override
    public String toString() {
        return "AppItem [mType=" + mType + ", mName=" + mName + ", mComponentName=" + mActivityName
                + ", mBackground=" + mBackground + ", mAvailable=" + mAvailable + ", mConfigured="
                + mConfigured + ", mConfiguredComponent=" + mConfiguredComponent + ", mLogo="
                + mLogo + ", mShadow=" + mShadow + ", mLabel=" + mLabel + "]";
    }

}
