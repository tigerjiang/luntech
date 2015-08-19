
package com.luntech.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.luntech.launcher.db.AppItem;
import com.luntech.launcher.db.DBDao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipException;

public class ToolUtils {
    private static ToolUtils sInstance = new ToolUtils();

    private static final String APP_TAG = "app";
    private static final String TYPE_TAG = "type";
    private static final String ACTIVITY_TAG = "activity";
    private static final String LABEL_TAG = "label";
    private static final String BACKGROUND_TAG = "background";
    private static final String SHADOW_TAG = "shadow";
    private static final String LOGO_TAG = "logo";
    private static final String CONFIGURED_TAG = "configured";
    private static final String AVAILABLE_TAG = "available";
    private static final String CONFIGURED_ACTIVITY_TAG = "configured_activity";
    private static final String TAG = "ToolUtils";

    private static final String CUSTOM_INFO = "custom_info";
    private static final String NETWORK_INFO = "network_info";

    private static DBDao mDBdao ;

    private ToolUtils() {

    }

    public static ToolUtils getInstance() {
        if (sInstance == null) {
            return new ToolUtils();
        } else {
            return sInstance;
        }
    }

    public static ArrayList<CustomApplication> getCustomInfoFromConfig(Context context, int fileId) {
        Resources r = context.getResources();
        XmlResourceParser parser = r.getXml(fileId);
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        CustomApplication application = null;
        Module module = null;
        ArrayList<App> apps = null;
        App app = null;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {

                    } else if (name.equals(CustomApplication.URL_TAG)) {

                    }
                    // group info
                    else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        application.mGroup = new CustomApplication.Group();
                        application.mGroup.setGroupCode(parser.getAttributeValue(0).trim());
                        application.mGroup.setGroupMoveable(Integer.parseInt(parser
                                .getAttributeValue(1).trim()));
                        application.mGroup.setGroupFlag(Integer.parseInt(parser
                                .getAttributeValue(2).trim()));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        application.mGroup.setGroupText(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        application.mGroup.setGroupBg(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        application.mGroup.setGroupIcon(parser.nextText().trim());
                    }
                    // Module info

                    else if (name.equals(Module.MODULE_TAG)) {
                        module = new Module(context);
                        module.setModuleCode(parser.getAttributeValue(0).trim());
                        module.setModuleReplace(Integer
                                .parseInt(parser.getAttributeValue(1).trim()));
                        module.setModuleType(Integer.parseInt(parser.getAttributeValue(2).trim()));
                    } else if (name.equals(Module.MODULE_TEXT_TAG)) {
                        module.setModuleText(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_BG_TAG)) {
                        module.setModuleBg(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_ICON_TAG)) {
                        module.setModuleIcon(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_SHADOW_TAG)) {
                        module.setModuleShadow(parser.nextText().trim());
                        // apps
                    } else if (name.equals(App.APPS_TAG)) {
                        apps = new ArrayList<App>();
                    } else if (name.equals(App.APP_TAG)) {
                        app = new App();
                    } else if (name.equals(App.APP_NAME_TAG)) {
                        app.setAppName(parser.nextText().trim());
                    } else if (name.equals(App.APP_PACKAGE_TAG)) {
                        app.setAppPackage(parser.nextText().trim());
                    } else if (name.equals(App.APP_ACTIVITY_TAG)) {
                        app.setAppActivity(parser.nextText().trim());
                    } else if (name.equals(App.APP_ICON_TAG)) {
                        app.setAppIcon(parser.nextText().trim());
                    } else if (name.equals(App.APP_URL_TAG)) {
                        app.setAppUrl(parser.nextText().trim());
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(App.APP_TAG)) {
                        // Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        module.mApps = apps;
                        // Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        application.mGroup.addModule(module);
                        // Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        applications.add(application);
                        // Logger.d("end group" + application.toString());
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return applications;
    }

    public static ArrayList<CustomApplication> getCustomConfigureFromConfig(Context context,
            File file) {
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        CustomApplication application = null;
        Module module = null;
        ArrayList<App> apps = null;
        App app = null;
        try {
            InputStream is = new FileInputStream(file);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {

                    } else if (name.equals(CustomApplication.URL_TAG)) {

                    }
                    // group info
                    else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        application.mGroup = new CustomApplication.Group();
                        application.mGroup.setGroupCode(parser.getAttributeValue(0).trim());
                        application.mGroup.setGroupMoveable(Integer.parseInt(parser
                                .getAttributeValue(1).trim()));
                        application.mGroup.setGroupFlag(Integer.parseInt(parser
                                .getAttributeValue(2).trim()));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        application.mGroup.setGroupText(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        application.mGroup.setGroupBg(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        application.mGroup.setGroupIcon(parser.nextText().trim());
                    }
                    // Module info

                    else if (name.equals(Module.MODULE_TAG)) {
                        module = new Module(context);
                        module.setModuleCode(parser.getAttributeValue(0).trim());
                        module.setModuleReplace(Integer
                                .parseInt(parser.getAttributeValue(1).trim()));
                        module.setModuleType(Integer.parseInt(parser.getAttributeValue(2).trim()));
                    } else if (name.equals(Module.MODULE_TEXT_TAG)) {
                        module.setModuleText(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_BG_TAG)) {
                        module.setModuleBg(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_ICON_TAG)) {
                        module.setModuleIcon(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_SHADOW_TAG)) {
                        module.setModuleShadow(parser.nextText().trim());
                        // apps
                    } else if (name.equals(App.APPS_TAG)) {
                        apps = new ArrayList<App>();
                    } else if (name.equals(App.APP_TAG)) {
                        app = new App();
                    } else if (name.equals(App.APP_NAME_TAG)) {
                        app.setAppName(parser.nextText().trim());
                    } else if (name.equals(App.APP_PACKAGE_TAG)) {
                        app.setAppPackage(parser.nextText().trim());
                    } else if (name.equals(App.APP_ACTIVITY_TAG)) {
                        app.setAppActivity(parser.nextText().trim());
                    } else if (name.equals(App.APP_ICON_TAG)) {
                        app.setAppIcon(parser.nextText().trim());
                    } else if (name.equals(App.APP_URL_TAG)) {
                        app.setAppUrl(parser.nextText().trim());
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(App.APP_TAG)) {
                        // Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        module.mApps = apps;
                        // Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        application.mGroup.addModule(module);
                        // Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        applications.add(application);
                        // Logger.d("end group" + application.toString());
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return applications;
    }


    public static ArrayList<AppItem> parseCustomConfigureFromConfig(Context context,
                                                                    File file) {
        mDBdao = new DBDao(context);
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        ArrayList<AppItem> apps = new ArrayList<AppItem>();
        CustomApplication application = null;
        CustomApplication.Group group = null;
        Module module = null;
        AppItem app = null;
        try {
            InputStream is = new FileInputStream(file);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {

                    } else if (name.equals(CustomApplication.URL_TAG)) {

                    }
                    // group info
                    else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        group = new CustomApplication.Group();
                        group.setGroupCode(parser.getAttributeValue(0).trim());
                        group.setGroupMoveable(Integer.parseInt(parser
                                .getAttributeValue(1).trim()));
                        group.setGroupFlag(Integer.parseInt(parser
                                .getAttributeValue(2).trim()));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        group.setGroupText(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        group.setGroupBg(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        group.setGroupIcon(parser.nextText().trim());
                    }
                    // Module info

                    else if (name.equals(Module.MODULE_TAG)) {
                        module = new Module(context);
                        module.setModuleCode(parser.getAttributeValue(0).trim());
                        module.setModuleReplace(Integer
                                .parseInt(parser.getAttributeValue(1).trim()));
                        module.setModuleType(Integer.parseInt(parser.getAttributeValue(2).trim()));
                    } else if (name.equals(Module.MODULE_TEXT_TAG)) {
                        module.setModuleText(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_BG_TAG)) {
                        module.setModuleBg(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_ICON_TAG)) {
                        module.setModuleIcon(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_SHADOW_TAG)) {
                        module.setModuleShadow(parser.nextText().trim());
                        // apps
                    } else if (name.equals(App.APPS_TAG)) {
                        apps = new ArrayList<AppItem>();
                    } else if (name.equals(App.APP_TAG)) {
                        app = new AppItem();
                    } else if (name.equals(App.APP_NAME_TAG)) {
                        app.setAppName(parser.nextText().trim());
                    } else if (name.equals(App.APP_PACKAGE_TAG)) {
                        app.setAppPackage(parser.nextText().trim());
                    } else if (name.equals(App.APP_ACTIVITY_TAG)) {
                        app.setAppActivity(parser.nextText().trim());
                    } else if (name.equals(App.APP_ICON_TAG)) {
                        app.setAppIcon(parser.nextText().trim());
                    } else if (name.equals(App.APP_URL_TAG)) {
                        app.setAppUrl(parser.nextText().trim());
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(App.APP_TAG)) {
                        // Log.d(TAG, "end app " + app.toString());
                        //group
                        app.setGroupCode(group.groupCode);
                        app.setGroupFlag(group.groupFlag);
                        app.setGroupMoveable(group.groupMoveable);
                        app.setGroupBg(group.groupBg);
                        app.setGroupIcon(group.groupIcon);
                        app.setGroupText(group.groupText);
                        //module
                        app.setModuleCode(module.moduleCode);
                        app.setModuleReplace(module.moduleReplace);
                        app.setModuleType(module.moduleType);
                        app.setModuleBg(module.moduleBg);
                        app.setModuleIcon(module.moduleIcon);
                        app.setModuleShadow(module.moduleShadow);
                        app.setModuleText(module.moduleText);
                        mDBdao.insert(app);
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        // Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        // Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        // Logger.d("end group" + application.toString());
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return apps;
    }

    public static ArrayList<AppItem> parseCustomConfigureFromInputStream(Context context,
                                                                    InputStream is) {
        mDBdao = new DBDao(context);
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        ArrayList<AppItem> apps = new ArrayList<AppItem>();
        CustomApplication application = null;
        CustomApplication.Group group = null;
        Module module = null;
        AppItem app = null;
          try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {

                    } else if (name.equals(CustomApplication.URL_TAG)) {

                    }
                    // group info
                    else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        group = new CustomApplication.Group();
                        group.setGroupCode(parser.getAttributeValue(0).trim());
                        group.setGroupMoveable(Integer.parseInt(parser
                                .getAttributeValue(1).trim()));
                        group.setGroupFlag(Integer.parseInt(parser
                                .getAttributeValue(2).trim()));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        group.setGroupText(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        group.setGroupBg(parser.nextText().trim());
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        group.setGroupIcon(parser.nextText().trim());
                    }
                    // Module info

                    else if (name.equals(Module.MODULE_TAG)) {
                        module = new Module(context);
                        module.setModuleCode(parser.getAttributeValue(0).trim());
                        module.setModuleReplace(Integer
                                .parseInt(parser.getAttributeValue(1).trim()));
                        module.setModuleType(Integer.parseInt(parser.getAttributeValue(2).trim()));
                    } else if (name.equals(Module.MODULE_TEXT_TAG)) {
                        module.setModuleText(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_BG_TAG)) {
                        module.setModuleBg(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_ICON_TAG)) {
                        module.setModuleIcon(parser.nextText().trim());
                    } else if (name.equals(Module.MODULE_SHADOW_TAG)) {
                        module.setModuleShadow(parser.nextText().trim());
                        // apps
                    } else if (name.equals(App.APPS_TAG)) {
                        apps = new ArrayList<AppItem>();
                    } else if (name.equals(App.APP_TAG)) {
                        app = new AppItem();
                    } else if (name.equals(App.APP_NAME_TAG)) {
                        app.setAppName(parser.nextText().trim());
                    } else if (name.equals(App.APP_PACKAGE_TAG)) {
                        app.setAppPackage(parser.nextText().trim());
                    } else if (name.equals(App.APP_ACTIVITY_TAG)) {
                        app.setAppActivity(parser.nextText().trim());
                    } else if (name.equals(App.APP_ICON_TAG)) {
                        app.setAppIcon(parser.nextText().trim());
                    } else if (name.equals(App.APP_URL_TAG)) {
                        app.setAppUrl(parser.nextText().trim());
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(App.APP_TAG)) {
                        // Log.d(TAG, "end app " + app.toString());
                        //group
                        app.setGroupCode(group.groupCode);
                        app.setGroupFlag(group.groupFlag);
                        app.setGroupMoveable(group.groupMoveable);
                        app.setGroupBg(group.groupBg);
                        app.setGroupIcon(group.groupIcon);
                        app.setGroupText(group.groupText);
                        //module
                        app.setModuleCode(module.moduleCode);
                        app.setModuleReplace(module.moduleReplace);
                        app.setModuleType(module.moduleType);
                        app.setModuleBg(module.moduleBg);
                        app.setModuleIcon(module.moduleIcon);
                        app.setModuleShadow(module.moduleShadow);
                        app.setModuleText(module.moduleText);
                        mDBdao.insert(app);
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        // Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        // Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        // Logger.d("end group" + application.toString());
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return apps;
    }

    public static String getAdConfigureFromConfig(final Context context, InputStream is) {
        StringBuffer AdContent = new StringBuffer();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals("bg")) {
                        String url = parser.nextText().trim();
                        String bgFileName = url.substring(url.lastIndexOf(".") + 1);

                        IDownloadListener listener = new IDownloadListener() {

                            @Override
                            public void onError(String errorCode) {

                            }

                            @Override
                            public void onCompleted(final File file) {
                                storeValueIntoSP(context, Launcher.FULL_BG_KEY,
                                        file.getAbsolutePath());
                            }
                        };
                        DownloadTask task = new DownloadTask(Launcher.DOWNLOAD_TO_PATH, url,
                                listener);
                        new Thread(task).start();
                    } else if (name.equals("marquees")) {
                        Log.d(TAG, "start ad over");
                    } else if (name.equals("marquee")) {
                        String content = parser.nextText().trim();
                        AdContent.append(content).append("                             ");
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    if (name.equals("marquees")) {
                        storeValueIntoSP(context, Launcher.ADVERTISEMENT_KEY, AdContent.toString());
                        Log.d(TAG, "end ad over");
                    }
                }

                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return AdContent.toString();
    }

    public static void getCustomConfigureFromConfig(Context context, InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.TIME_TAG)) {
                        String time = parser.nextText().trim();
                        Logger.d("time " + time);
                    } else if (name.equals(CustomApplication.URL_TAG)) {
                        String url = parser.nextText().trim();
                        // Download the new zip resources
                        Logger.d("url " + url);
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                }

                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
    }

    public void setConfigured(Context context, String key, String pkg) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putString(key, pkg);
        spe.commit();
    }

    public void setConfiguredPkg(Context context, String keyCode, String pkg) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putString(keyCode, pkg);
        spe.commit();
    }

    public String getConfiguredPkg(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getString(name, null);
    }

    public void clearConfiguredPkg(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.remove(key);
        spe.commit();
    }

    public String getConfigured(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getString(name, null);
    }

    public static void storeValueIntoSP(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putString(key, value);
        spe.commit();
    }

    public static String getValueFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void writeFile(InputStream is, String localFile) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            writer = new BufferedWriter(new FileWriter(localFile));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                writer.write(strRead);
                sbf.append("\r\n");
            }
            writer.flush();
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renameFile(File destFile, File sourceFile) {
        while (destFile.exists()) {
            destFile.delete();
        }
        try {
            sourceFile.renameTo(destFile);
        } catch (Exception e) {
            Logger.w("", e);
        }
    }

    public static Drawable changeIdtoDrawable(Context context, String name) {
        Drawable icon = null;
        int resId = context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName());
        if (resId == 0) {
            Log.e("error", context.getPackageName() + " resource not found for " + name);
        } else {
            icon = context.getResources().getDrawable(resId);
        }
        return icon;
    }

    public static Drawable changeFiletoDrawable(Context context, String fileName) {
        Drawable icon = null;
        String path = Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.FILE_PREFIX + "/" + fileName;
        Log.d("jzh", "change path " + path);
        if (!TextUtils.isEmpty(path)) {
            icon = Drawable.createFromPath(path);
        }
        return icon;
    }

    public static Drawable getDrawableFromAttribute(Context context, String attribute) {
        if (attribute.endsWith(".png") | attribute.endsWith(".PNG") | attribute.endsWith(".JPG")
                | attribute.endsWith(".jpg")) {
            return changeFiletoDrawable(context, attribute);
        } else {
            return changeIdtoDrawable(context, attribute);
        }
    }

    /**
     * install app
     * 
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath),
                    "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }
}
