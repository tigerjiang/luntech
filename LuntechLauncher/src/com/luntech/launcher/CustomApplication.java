
package com.luntech.launcher;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

public class CustomApplication {
    public Group mGroup;

    static class Group {
        public static final String GROUP_TAG = "group";
        public static final String GROUP_CODE_TAG = "code";
        public static final String GROUP_MOVEABLE_TAG = "moveable";
        public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
        public static final String GROUP_TEXT_TAG = "g_text";
        public static final String GROUP_BG_TAG = "g_bg";
        public static final String GROUP_ICON_TAG = "g_icon";
        public ArrayList<Module> mModules;

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

    @Override
    public String toString() {
        return "CustomApplication [mGroup=" + mGroup + "]";
    }

}
