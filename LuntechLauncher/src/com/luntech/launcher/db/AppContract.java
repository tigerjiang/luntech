
package com.luntech.launcher.db;

public class AppContract {
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

        public static final String M_CODE = "m_code";
        public static final String M_REPLACE = "m_replace";
        public static final String M_TYPE = "m_type";
        public static final String M_TEXT = "m_text";
        public static final String M_BG = "m_bg";
        public static final String M_ICON = "m_icon";
        public static final String M_SHADOW = "m_shadow";

        public static final String G_CODE = "g_code";
        public static final String G_MOVEABLE = "g_moveable";
        public static final String G_FLAG = "g_flag";

        public static final String G_TEXT = "g_text";
        public static final String G_BG = "g_bg";
        public static final String G_ICON = "g_icon";

    }

    public static class TAG {
        public static final String TIME_TAG = "time";
        public static final String URL_TAG = "url";
        //group tag
        public static final String GROUP_TAG = "group";
        public static final String GROUP_CODE_TAG = "code";
        public static final String GROUP_MOVEABLE_TAG = "moveable";
        public static final String GROUP_GROUP_FLAG_TAG = "group_flag";
        public static final String GROUP_TEXT_TAG = "g_text";
        public static final String GROUP_BG_TAG = "g_bg";
        public static final String GROUP_ICON_TAG = "g_icon";
        //module tag
        public static final String MODULE_TAG = "module";
        public static final String MODULE_CODE_TAG = "code";
        public static final String MODULE_REPLACE_TAG = "replace";
        public static final String MODULE_TYPE_TAG = "type";
        public static final String MODULE_TEXT_TAG = "m_text";
        public static final String MODULE_BG_TAG = "m_bg";
        public static final String MODULE_ICON_TAG = "m_icon";
        public static final String MODULE_SHADOW_TAG = "m_shadow";
        //app tag
        public static final String APP_TAG = "app";
        public static final String APPS_TAG = "apps";
        public static final String APP_NAME_TAG = "a_name";
        public static final String APP_PACKAGE_TAG = "a_package";
        public static final String APP_ACTIVITY_TAG = "a_activity";
        public static final String APP_ICON_TAG = "a_icon";
        public static final String APP_URL_TAG = "a_url";
    }


}
