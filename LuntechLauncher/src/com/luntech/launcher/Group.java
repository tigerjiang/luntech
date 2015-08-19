package com.luntech.launcher;

import java.util.ArrayList;

/**
 * Created by tiger on 8/19/15.
 */
public class Group {
    public static final String GROUP_TAG = "group";
    public static final String GROUP_CODE_TAG = "code";
    public static final String GROUP_MOVEABLE_TAG = "moveable";
    public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
    public static final String GROUP_TEXT_TAG = "g_text";
    public static final String GROUP_BG_TAG = "g_bg";
    public static final String GROUP_ICON_TAG = "g_icon";
    public ArrayList<Module> mModules;

    public static class Columns {
        public static final String G_CODE = "g_code";
        public static final String G_MOVEABLE = "g_moveable";
        public static final String G_FLAG = "g_flag";

        public static final String G_TEXT = "g_text";
        public static final String G_BG = "g_bg";
        public static final String G_ICON = "g_icon";
    }

    public Group() {

    }

    public String groupCode;
    public int groupMoveable;
    public int groupFlag;
    public String groupText;
    public String groupBg;
    public String groupIcon;

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

    public void addModule(Module module) {
        if (mModules == null) {
            mModules = new ArrayList<Module>();
            mModules.add(module);
        } else {
            mModules.add(module);
        }
    }

    @Override
    public String toString() {
        return "Group [mModules=" + mModules + ", groupCode=" + groupCode + ", groupMoveable="
                + groupMoveable + ", groupFlag=" + groupFlag + ", groupText=" + groupText
                + ", groupBg=" + groupBg + ", groupIcon=" + groupIcon + "]";
    }

}
