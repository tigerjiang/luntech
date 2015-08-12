
package com.luntech.launcher.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LauncherOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "app_info.db";
    private static final String APPLICATION_TABLE_NAME = "app_info";
    private static final String APPLICATION_TABLE_CREATE = "CREATE TABLE " + APPLICATION_TABLE_NAME
            + " ("
            + "_id " + " INTEGER, "
            + "download_id " + " INTEGER, "
            + "a_name " + " TEXT, "
            + "a_package " + " TEXT, "
            + "a_activity " + " TEXT, "
            + "a_icon " + " TEXT, "
            + "a_url " + " TEXT, "
            + "m_code " + " TEXT, "
            + "m_replace " + " TEXT, "
            + "m_type " + " TEXT, "
            + "m_text " + " TEXT, "
            + "m_bg " + " TEXT, "
            + "m_icon " + " TEXT, "
            + "m_shadow " + " TEXT, "
            + "g_code " + " TEXT, "
            + "g_flag " + " TEXT, "
            + "g_text " + " TEXT, "
            + "g_icon " + " TEXT)";

    public LauncherOpenHelper(Context context) {
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
