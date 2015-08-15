
package com.luntech.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "app_info.db";
    public static final String APPLICATION_TABLE_NAME = "app_info";
    private static final String APPLICATION_TABLE_CREATE = "CREATE TABLE " + APPLICATION_TABLE_NAME
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
            + "m_code" + " varchar, "
            + "m_replace" + " varchar, "
            + "m_type" + " varchar, "
            + "m_text" + " varchar, "
            + "m_bg" + " varchar, "
            + "m_icon" + " varchar, "
            + "m_shadow" + " varchar, "
            
            + "g_code" + " varchar, "
            + "g_moveable" + " varchar, "
            + "g_flag" + " varchar, "
            + "g_text" + " varchar, "
            + "g_bg" + " varchar, "
            + "g_icon" + " varchar)";

    public DataOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(APPLICATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(APPLICATION_TABLE_NAME, null, null);
        onConfigure(db);
    }

}
