package com.luntech.launcher;

import android.graphics.drawable.Drawable;

public class CategoryItem {
	public String mComponentName;
	public String mLabel;
	public Drawable mAppIcon;
	public Drawable mBackgroundIcon;
	public Drawable mShadowIcon;
    
	public CategoryItem() {

	}

	public CategoryItem(String mComponentName, String mLabel,
			Drawable mAppIcon, Drawable mBackgroundIcon, Drawable mShadowIcon) {
		this.mComponentName = mComponentName;
		this.mLabel = mLabel;
		this.mAppIcon = mAppIcon;
		this.mBackgroundIcon = mBackgroundIcon;
		this.mShadowIcon = mShadowIcon;
	}
	public String getComponentName() {
		return mComponentName;
	}
	public void setComponentName(String componentName) {
		this.mComponentName = componentName;
	}
	public String getLabel() {
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

}
