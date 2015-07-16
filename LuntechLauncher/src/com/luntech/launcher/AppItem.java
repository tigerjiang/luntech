
package com.luntech.launcher;

public class AppItem {
    private String mType;
    private String mName;
    private String mComponentName;
    private String mBackground;
    private boolean mAvailable;
    private boolean mConfigured;
    private String mConfiguredComponent;
    private String mLogo;
    private String mShadow;
    private String mLabel;

    public AppItem() {

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
    }

    public boolean isAvailable() {
        return mAvailable;
    }

    public void setAvailable(boolean available) {
        this.mAvailable = available;
    }

    public boolean isConfigured() {
        return mConfigured;
    }

    public void setConfigured(boolean configured) {
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
    }

    public String getShadow() {
        return mShadow;
    }

    public void setShadow(String shadow) {
        this.mShadow = shadow;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

}
