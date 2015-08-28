package com.luntech.launcher.db;import android.content.Context;import android.database.Cursor;import android.database.sqlite.SQLiteDatabase;import android.util.Log;import com.luntech.launcher.App;import com.luntech.launcher.Group;import com.luntech.launcher.Module;import java.util.ArrayList;public class DBDao {    private DataOpenHelper dbHelper;    private Context context;    private static final String TAG = "DBDao";    public static String SORT_MODE_DESC = "desc";    public static String SORT_MODE_ASC = "asc";    public DBDao(Context _context) {        context = _context;        dbHelper = new DataOpenHelper(context);    }    /**     * 获取DB对象     */    public SQLiteDatabase getDB() {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        return dbHelper.getWritableDatabase();    }    /**     * 插入新数据     */    public void insertApp(App app) {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        Log.d(TAG, "insert app" + app.getAppPackage());        SQLiteDatabase db = dbHelper.getWritableDatabase();        StringBuffer sql = new StringBuffer();        sql.append("insert into " + DataOpenHelper.APP_TABLE_NAME + " (")                .append("a_name , ")                .append("a_package , ")                .append("a_activity , ")                .append("a_icon , ")                .append("a_url , ")                .append("a_m_code ) ")                .append("values (?, ?, ?, ?, ?, ?) ");        Object[] conditions = new Object[]{                app.getAppName(), app.getAppPackage(), app.getAppActivity(), app.getAppIcon(),                app.getAppUrl(),                app.getModuleCode()        };        db.execSQL(sql.toString(), conditions);    }    /**     * 插入新数据     */    public void insertModule(Module module) {        Log.d(TAG, "insert module" + module.getModuleCode());        SQLiteDatabase db = dbHelper.getWritableDatabase();        StringBuffer sql = new StringBuffer();        sql.append("insert into " + DataOpenHelper.MODULE_TABLE_NAME + " (")                .append("_id , ")                .append("m_code , ")                .append("m_replace , ")                .append("m_type , ")                .append("m_text , ")                .append("m_bg , ")                .append("m_icon , ")                .append("m_shadow , ")                .append("m_g_code ) ")                .append("values (?,?, ?, ?, ?, ?, ?,?,?) ");        Object[] conditions = new Object[]{ module.getModuleCode().replaceAll("\\D+", ""),                module.getModuleCode(), module.getModuleReplace(),                module.getModuleType(), module.getModuleText(),                module.getModuleBg(), module.getModuleIcon(),                module.getModuleShadow(), module.getGroupCode()        };        db.execSQL(sql.toString(), conditions);    }    /**     * 插入新数据     */    public void insertGroup(Group group) {        Log.d(TAG, "insert group" + group.getGroupCode());        SQLiteDatabase db = dbHelper.getWritableDatabase();        StringBuffer sql = new StringBuffer();        sql.append("insert into " + DataOpenHelper.GROUP_TABLE_NAME + " (")                .append("_id , ")                .append("g_code , ")                .append("g_moveable , ")                .append("g_flag , ")                .append("g_text , ")                .append("g_bg , ")                .append("g_icon ) ")                .append("values (?,?, ?, ?, ?, ?, ?) ");        Object[] conditions = new Object[]{group.getGroupCode().replaceAll("\\D+", ""),                group.getGroupCode(), group.getGroupMoveable(),                group.getGroupFlag(), group.getGroupText(),                group.getGroupBg(), group.getGroupIcon()        };        db.execSQL(sql.toString(), conditions);    }    /**     * 更新数据     */    public void updateReplace(App app) {        if (app != null) {            // DatabaseHelper dbHelper = new DatabaseHelper(context);            SQLiteDatabase db = dbHelper.getWritableDatabase();            StringBuffer sql = new StringBuffer();            sql.append("update " + DataOpenHelper.APP_TABLE_NAME + " set ")                    .append("a_replace_package = ?, ").append("a_has_replace = ?, ")                    .append("where a_package =? ");            Object[] conditions = new Object[]{                    app.getReplacePackage(),                    app.getHasReplace(),                    app.getAppPackage()            };            db.execSQL(sql.toString(), conditions);        }    }    /**     * 更新数据     */    public void updateDownload(App app) {        if (app != null) {            // DatabaseHelper dbHelper = new DatabaseHelper(context);            SQLiteDatabase db = dbHelper.getWritableDatabase();            StringBuffer sql = new StringBuffer();            sql.append("update " + DataOpenHelper.APP_TABLE_NAME + " set ")                    .append("download_id = ?, ").append("download_status = ?, ")                    .append("a_file_name = ? ")                    .append("where a_package = ? ");            Object[] conditions = new Object[]{                    app.getDownloadId(),                    app.getDownloadStatus(),                    app.getFileName(),                    app.getAppPackage()            };            db.execSQL(sql.toString(), conditions);        }    }    /**     * 删除数据     */    public void deleteApp(App app) {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getWritableDatabase();        db.execSQL(                "delete from " + DataOpenHelper.APP_TABLE_NAME + " where a_package=? ",                new Object[]{                        app.getAppPackage()                });    }    /**     * 删除数据     */    public void deleteModule(Module module) {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getWritableDatabase();        db.execSQL(                "delete from " + DataOpenHelper.MODULE_TABLE_NAME + " where m_code=? ",                new Object[]{                        module.getModuleCode()                });    }    /**     * 删除数据     */    public void deleteGroup(Group group) {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getWritableDatabase();        db.execSQL(                "delete from " + DataOpenHelper.GROUP_TABLE_NAME + " where g_code=? ",                new Object[]{                        group.getGroupCode()                });    }    /**     * 删除数据     */    public void delete() {        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getWritableDatabase();        db.execSQL(                "delete from " + DataOpenHelper.GROUP_TABLE_NAME);        db.execSQL(                "delete from " + DataOpenHelper.MODULE_TABLE_NAME);        db.execSQL(                "delete from " + DataOpenHelper.APP_TABLE_NAME);    }    /**     * 更新替换应用数据     */    public void setReplaceInfo(App app) {        if (app != null) {            // DatabaseHelper dbHelper = new DatabaseHelper(context);            SQLiteDatabase db = dbHelper.getWritableDatabase();            StringBuffer sql = new StringBuffer();            sql.append("update " + DataOpenHelper.APP_TABLE_NAME + " set ")                    .append("a_replace_package = ?, ").append("a_has_replace = ? ")                    .append("where a_package =? ");            Object[] conditions = new Object[]{                    app.getReplacePackage(),                    app.getHasReplace(),                    app.getAppPackage()            };            db.execSQL(sql.toString(), conditions);        }    }    /**     * 记录下载信息     */    public  boolean isExsitsForApp(App app) {        boolean result = false;        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.APP_TABLE_NAME                + " where  a_m_code=? and a_package= ?", new String[]{                app.getModuleCode(), app.getAppPackage()        });        if (cursor != null) {            if (cursor.getCount() > 0) {                result = true;            }        }        cursor.close();        return result;    }    /**     * 记录下载信息     */    public  boolean isExsitsForGroup(Group group) {        boolean result = false;        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.GROUP_TABLE_NAME                + " where  g_code=? ", new String[]{                group.getGroupCode()        });        if (cursor != null) {            if (cursor.getCount() > 0) {                result = true;            }        }        cursor.close();        return result;    }    /**     * 记录下载信息     */    public  boolean isExsitsForModule(Module module) {        boolean result = false;        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.MODULE_TABLE_NAME                + " where m_g_code=? and m_code=? ", new String[]{                module.getGroupCode(), module.getModuleCode()        });        if (cursor != null) {            if (cursor.getCount() > 0) {                result = true;            }        }        cursor.close();        return result;    }    /**     * 记录下载信息     */    public boolean isDownLoadedForApp(String pkg, String downloadStatus) {        boolean result = false;        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.APP_TABLE_NAME                + " where pkg=? and download_status=? ", new String[]{                String.valueOf(pkg), downloadStatus        });        if (cursor != null) {            if (cursor.getCount() > 0) {                result = true;            }        }        cursor.close();        return result;    }    /**     * 记录下载信息     */    public boolean isDownLoadedForApp(long downloadId) {        boolean result = false;        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.APP_TABLE_NAME                + " where download_id =? ", new String[]{                String.valueOf(downloadId)        });        if (cursor != null) {            if (cursor.getCount() > 0) {                result = true;            }        }        cursor.close();        return result;    }    public App fetchApp(String pkg) {        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.rawQuery("select * from "                + DataOpenHelper.APP_TABLE_NAME                + " where a_package=? ", new String[]{                pkg        });        App app = null;        boolean result = cursor.moveToFirst();        if (result)            app = populateApp(cursor);        cursor.close();        return app;    }    /**     * 获取数据集合 list     */    public ArrayList<App> fetchAppList() {        ArrayList<App> appList = new ArrayList<App>();        // DatabaseHelper dbHelper = new DatabaseHelper(context);        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.query(DataOpenHelper.APP_TABLE_NAME,                new String[]{                        "*"                }, null, null, null,                null, "_id " + SORT_MODE_ASC);        if (cursor != null) {            cursor.moveToFirst();            while (!cursor.isAfterLast()) {                appList.add(populateApp(cursor));                cursor.moveToNext();            }        }        cursor.close();        return appList;    }    /**     * 关闭数据库     */    public void destory() {        dbHelper.close();    }    private App populateApp(Cursor c) {        App app = new App();        app.setDownloadId(c.getInt(c.getColumnIndexOrThrow(App.Columns.Download_id)));        app.setDownloadStatus(c.getString(c                .getColumnIndexOrThrow(App.Columns.DOWNLOAD_STATUS)));        app.setAppName(c.getString(c.getColumnIndexOrThrow(App.Columns.A_NAME)));        app.setAppPackage(c.getString(c.getColumnIndexOrThrow(App.Columns.A_PACKAGE)));        app.setReplacePackage(c.getString(c.getColumnIndexOrThrow(App.Columns.A_REPLACE_PACKAGE)));        app.setHasReplace(c.getInt(c.getColumnIndexOrThrow(App.Columns.A_HAS_REPLACE)));        app.setAppActivity(c.getString(c.getColumnIndexOrThrow(App.Columns.A_ACTIVITY)));        app.setAppIcon(c.getString(c.getColumnIndexOrThrow(App.Columns.A_ICON)));        app.setAppUrl(c.getString(c.getColumnIndexOrThrow(App.Columns.A_URL)));        app.setFileName(c.getString(c.getColumnIndexOrThrow(App.Columns.A_FILE_NAME)));        app.setModuleCode(c.getString(c.getColumnIndexOrThrow(App.Columns.A_M_CODE)));        return app;    }    private Module populateModule(Cursor c) {        Module module = new Module();        module.setModuleCode(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_CODE)));        module.setGroupCode(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_G_CODE)));        module.setModuleType(c.getInt(c.getColumnIndexOrThrow(Module.Columns.M_TYPE)));        module.setModuleReplace(c.getInt(c.getColumnIndexOrThrow(Module.Columns.M_REPLACE)));        module.setModuleBg(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_BG)));        module.setModuleIcon(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_ICON)));        module.setModuleShadow(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_SHADOW)));        module.setModuleText(c.getString(c.getColumnIndexOrThrow(Module.Columns.M_TEXT)));        return module;    }    private Group populateGroup(Cursor c) {        Group group = new Group();        group.setGroupCode(c.getString(c.getColumnIndexOrThrow(Group.Columns.G_CODE)));        group.setGroupFlag(c.getInt(c.getColumnIndexOrThrow(Group.Columns.G_FLAG)));        group.setGroupMoveable(c.getInt(c.getColumnIndexOrThrow(Group.Columns.G_MOVEABLE)));        group.setGroupBg(c.getString(c.getColumnIndexOrThrow(Group.Columns.G_BG)));        group.setGroupIcon(c.getString(c.getColumnIndexOrThrow(Group.Columns.G_ICON)));        group.setGroupText(c.getString(c.getColumnIndexOrThrow(Group.Columns.G_TEXT)));        return group;    }    /**     * 根据Group code数据集合 Module List     */    public ArrayList<Module> fetchModuleListByGroup(String groupCode) {        ArrayList<Module> modules = new ArrayList<Module>();        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.query(DataOpenHelper.MODULE_TABLE_NAME,                new String[]{                        "*"                }, Module.Columns.M_G_CODE + "=? ", new String[]{                        groupCode                }, null,                null, "_id " + SORT_MODE_ASC);        if (cursor != null) {            cursor.moveToFirst();            while (!cursor.isAfterLast()) {                Module module = populateModule(cursor);                module.mApps = fetchAppListByModule(module.moduleCode);                modules.add(module);                cursor.moveToNext();            }        }        cursor.close();        return modules;    }    /**     * 根据Module code数据集合 App List     */    public ArrayList<App> fetchAppListByModule(String moduleCode) {        ArrayList<App> apps = new ArrayList<App>();        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.query(DataOpenHelper.APP_TABLE_NAME,                new String[]{                        "*"                }, App.Columns.A_M_CODE + "=? ", new String[]{                        moduleCode                }, null,                null, "_id " + SORT_MODE_ASC);        if (cursor != null) {            cursor.moveToFirst();            while (!cursor.isAfterLast()) {                apps.add(populateApp(cursor));                cursor.moveToNext();            }        }        cursor.close();        return apps;    }    /**     * Get the group list     */    public ArrayList<Group> fetchGroups(String groupCode) {        ArrayList<Group> groups = new ArrayList<Group>();        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.query(DataOpenHelper.MODULE_TABLE_NAME,                new String[]{                        "*"                }, Group.Columns.G_CODE + " LIKE ?%", new String[]{                        groupCode                }, null,                null, "_id " + SORT_MODE_ASC);        if (cursor != null) {            cursor.moveToFirst();            while (!cursor.isAfterLast()) {                Group group = populateGroup(cursor);                group.mModules = fetchModuleListByGroup(group.groupCode);                groups.add(group);                cursor.moveToNext();            }        }        cursor.close();        return groups;    }    /**     * Get the module list     */    public ArrayList<Module> fetchModules() {        ArrayList<Module> modules = new ArrayList<Module>();        SQLiteDatabase db = dbHelper.getReadableDatabase();        Cursor cursor = db.query(DataOpenHelper.MODULE_TABLE_NAME,                new String[]{                        "*"                }, null, null, null,                null, "_id " + SORT_MODE_ASC);        if (cursor != null) {            cursor.moveToFirst();            while (!cursor.isAfterLast()) {                Module module = populateModule(cursor);                module.mApps = fetchAppListByModule(module.moduleCode);                modules.add(module);                cursor.moveToNext();            }        }        cursor.close();        return modules;    }}