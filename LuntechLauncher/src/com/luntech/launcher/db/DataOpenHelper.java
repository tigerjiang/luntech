
package com.luntech.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "app_info.db";
    public static final String GROUP_TABLE_NAME = "group_info";
    public static final String MODULE_TABLE_NAME = "module_info";
    public static final String APP_TABLE_NAME = "app_info";


    private static final String GROUP_TABLE_CREATE = "CREATE TABLE " + GROUP_TABLE_NAME
            + " ("
            + "_id" + " integer primary key autoincrement,"
            + "g_code" + " varchar, "
            + "g_moveable" + " varchar, "
            + "g_flag" + " varchar, "
            + "g_text" + " varchar, "
            + "g_bg" + " varchar, "
            + "g_icon" + " varchar)";

    private static final String MODULE_TABLE_CREATE = "CREATE TABLE " + MODULE_TABLE_NAME
            + " ("
            + "_id" + " integer primary key autoincrement,"
            + "m_code" + " varchar, "
            + "m_replace" + " varchar, "
            + "m_type" + " varchar, "
            + "m_text" + " varchar, "
            + "m_bg" + " varchar, "
            + "m_icon" + " varchar, "
            + "m_shadow" + " varchar, "
            + "m_g_code" + " varchar)";

    private static final String APP_TABLE_CREATE = "CREATE TABLE " + APP_TABLE_NAME
            + " ("
            + "_id" + " integer primary key autoincrement,"
            + "download_id" + " integer  default 0, "
            + "download_status" + " varchar, "
            + "a_name" + " varchar, "
            + "a_package" + " varchar, "
            + "a_replace_package" + " varchar, "
            + "a_has_replace" + " integer  default 0, "
            + "a_activity" + " varchar, "
            + "a_icon" + " varchar, "
            + "a_url" + " varchar, "
            + "a_file_name" + " varchar, "
            + "a_m_code" + " varchar)";

    public DataOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GROUP_TABLE_CREATE);
        db.execSQL(MODULE_TABLE_CREATE);
        db.execSQL(APP_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(APP_TABLE_NAME, null, null);
        db.delete(MODULE_TABLE_NAME, null, null);
        db.delete(GROUP_TABLE_NAME, null, null);
        onConfigure(db);
    }

}
