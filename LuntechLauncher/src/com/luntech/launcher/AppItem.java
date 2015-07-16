
package com.luntech.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppItem {
    private String mType;
    private String mName;
    private String mComponentName;
    private String mBackground;
    private String mAvailable;
    private String mConfigured;
    private String mConfiguredComponent;
    private String mLogo;
    private String mShadow;
    public CharSequence mLabel;

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

    public String getComponentName() {
        return mComponentName;
    }

    public void setComponentName(String componentName) {
        this.mComponentName = componentName;
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

    @Override
    public String toString() {
        return "AppItem [mType=" + mType + ", mName=" + mName + ", mComponentName="
                + mComponentName + ", mBackground=" + mBackground + ", mAvailable=" + mAvailable
                + ", mConfigured=" + mConfigured + ", mConfiguredComponent=" + mConfiguredComponent
                + ", mLogo=" + mLogo + ", mShadow=" + mShadow + ", mLabel=" + mLabel + "]";
    }
    
    
}
