
package com.luntech.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class Module {
    public static final String MODULE_TAG = "module";
    public static final String MODULE_CODE_TAG = "code";
    public static final String MODULE_REPLACE_TAG = "replace";
    public static final String MODULE_TYPE_TAG = "type";
    public static final String MODULE_TEXT_TAG = "m_text";
    public static final String MODULE_BG_TAG = "m_bg";
    public static final String MODULE_ICON_TAG = "m_icon";
    public static final String MODULE_SHADOW_TAG = "m_shadow";
    public ArrayList<App> mApps;

    private Context mContext;
    private Resources mResources;
    private String mPackageName;

    public Module(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();
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

    public void setModuleBg(String moduleBg) {
        this.moduleBg = moduleBg;
        if (this.moduleBg.endsWith(".png")
                || this.moduleBg.endsWith(".jpg")) {
            setModuleBgDrawable(changeFiletoDrawable(this.moduleBg));
        } else {
            setModuleBgDrawable(changeIdtoDrawable(this.moduleBg));
        }
    }

    public String getModuleIcon() {
        return moduleIcon;
    }

    public void setModuleIcon(String moduleIcon) {
        this.moduleIcon = moduleIcon;
        if (this.moduleIcon.equalsIgnoreCase(".png")
                || this.moduleIcon.equalsIgnoreCase(".jpg")) {
            setModuleIconDrawable(changeFiletoDrawable(this.moduleIcon));
        } else {
            setModuleIconDrawable(changeIdtoDrawable(this.moduleIcon));
        }
    }

    public String getModuleShadow() {
        return moduleShadow;
    }

    public void setModuleShadow(String moduleShadow) {
        this.moduleShadow = moduleShadow;
        if (this.moduleShadow.equalsIgnoreCase(".png")
                || this.moduleShadow.equalsIgnoreCase(".jpg")) {
            setModuleShadowDrawable(changeFiletoDrawable(this.moduleShadow));
        } else {
            setModuleShadowDrawable(changeIdtoDrawable(this.moduleShadow));
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
            mApps = new ArrayList<App>();
            mApps.add(app);
        } else {
            mApps.add(app);
        }
    }

    private Drawable changeIdtoDrawable(String name) {
        Drawable icon = null;
        int resId = mResources.getIdentifier(name, "drawable", mPackageName);
        if (resId == 0) {
            Log.e("error", mPackageName + " resource not found for " + name);
        } else {
            icon = mResources.getDrawable(resId);
        }
        return icon;
    }

    private Drawable changeFiletoDrawable(String fileName) {
        Drawable icon = null;
        String path = Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.FILE_PREFIX + "/" + fileName;
        Log.d("jzh", "change path "+path);
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
