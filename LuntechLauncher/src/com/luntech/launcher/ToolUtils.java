
package com.luntech.launcher;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.luntech.launcher.db.DBDao;
import com.luntech.launcher.secondary.AppManager;
import com.luntech.launcher.secondary.ApplicationInfo;

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
import java.util.List;

public class ToolUtils {
    private static ToolUtils sInstance = null;

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

    private static final String IPTV_CUSTOM_INFO = "iptv_custom_info";
    private static final String Q1S_CUSTOM_INFO = "q1s_custom_info";

    private static DBDao sDBdao;


    private ToolUtils(Context context) {
        sDBdao = new DBDao(context);
    }

    public static ToolUtils getInstance(Context context) {
        if (sInstance == null) {
            return new ToolUtils(context);
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


    public static ArrayList<Group> getGroupsFromConfig(Context context, int fileId) {
        Resources r = context.getResources();
        XmlResourceParser parser = r.getXml(fileId);
        Group group = null;
        ArrayList<Group> groups = new ArrayList<Group>();
        ArrayList<Module> modlues = new ArrayList<Module>();
        Module module = null;
        ArrayList<App> apps = new ArrayList<App>();
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
                        group = new Group();
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
                        //group
                        app.setModuleCode(module.moduleCode);
                        module.addApp(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        sDBdao.deleteApp(module.getModuleCode());
                        for(App a:module.mApps){
                            sDBdao.deleteApp(a);
                            sDBdao.insertApp(a);
                        }
                        Log.d(TAG, "end apps " + app.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        module.setGroupCode(group.groupCode);
                        modlues.add(module);
                        group.addModule(module);
                        sDBdao.deleteModule(module);
                        sDBdao.insertModule(module);
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        sDBdao.deleteGroup(group);
                        sDBdao.insertGroup(group);
                        groups.add(group);
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (Exception e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return groups;
    }

    public static void parseCustomConfigureFromInputStream(Context context,
                                                           InputStream is) {
//        sDBdao.delete();
        Group group = null;
        Module module = null;
        ArrayList<App> apps = new ArrayList<App>();
        App app = null;
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
                        group = new Group();
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
                        //group
                        app.setModuleCode(module.moduleCode);
                        Log.d(TAG, "end app " + app.toString());
                        module.addApp(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        sDBdao.deleteApp(module.getModuleCode());
                        for(App a:module.mApps){
                            sDBdao.deleteApp(a);
                            sDBdao.insertApp(a);
                        }
                        Log.d(TAG, "end apps " + app.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        module.setGroupCode(group.groupCode);
                        sDBdao.deleteModule(module);
                        sDBdao.insertModule(module);
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        sDBdao.deleteGroup(group);
                        sDBdao.insertGroup(group);
                    }
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (Exception e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
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
                        AdContent.append(content).append("                              ");
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    if (name.equals("marquees")) {
                        AdContent.append("                              ").append(AdContent).append("                              ").append(AdContent).append("                              ").append(AdContent).append("                              ").append(AdContent);
                        storeCommonValueIntoSP(context, Launcher.ADVERTISEMENT_KEY, AdContent.toString());
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


    public static  void parseHiddenConfigureFromConfig(final Context context, InputStream is) {
        StringBuffer hiddenContent = new StringBuffer();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals("time")) {
                        String time = parser.nextText().trim();
                        String storeTime = ToolUtils.getValueFromSP(context, "hidden_time");
                        if (!TextUtils.isEmpty(storeTime)) {
                            if (time.equals(storeTime)) {
                                Logger.d("Desn't need get config from server,Beacuse of the time is same as local "
                                        + storeTime);
                                break;
                            } else {
                                ToolUtils.storeValueIntoSP(context, "hidden_time", time);
                            }
                        } else {
                            ToolUtils.storeValueIntoSP(context, "hidden_time", time);
                        }
                    } else if (name.equals("package_name")) {
                        String content = parser.nextText().trim();
                        hiddenContent.append(content).append(",");
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }

        String tmp = hiddenContent.toString();
        String content = tmp.substring(0,tmp.lastIndexOf(","));
        ToolUtils.storeValueIntoSP(context, "hidden_app", content);
        Log.d(TAG, "hidden app " + content);
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
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be stored!");
            return;
        }
        Editor spe = sp.edit();
        spe.putString(key, pkg);
        spe.commit();
    }

    public void setConfiguredPkg(Context context, String keyCode, String pkg) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be stored!");
            return;
        }
        Editor spe = sp.edit();
        spe.putString(keyCode, pkg);
        spe.commit();
    }

    public String getConfiguredPkg(Context context, String name) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be get!");
            return null;
        }
        return sp.getString(name, null);
    }

    public static void  clearConfiguredPkg(Context context, String key) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be done!");
            return;
        }
        Editor spe = sp.edit();
        spe.remove(key);
        spe.commit();
    }

    public String getConfigured(Context context, String name) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be get!");
            return null;
        }
        return sp.getString(name, null);
    }

    public boolean isExsitsKey(Context context, String key) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be get!");
            return false;
        }
        return sp.contains(key);
    }

    public static void storeValueIntoSP(Context context, String key, String value) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be stored!");
            return;
        }
        Editor spe = sp.edit();
        spe.putString(key, value);
        spe.commit();
    }

    public static String getValueFromSP(Context context, String key) {
        SharedPreferences sp = null;
        if (Launcher.mType.equals(Launcher.Q1S_TYPE)) {
            sp = context.getSharedPreferences(Q1S_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else if (Launcher.mType.equals(Launcher.IPTV_TYPE)) {
            sp = context.getSharedPreferences(IPTV_CUSTOM_INFO, Context.MODE_PRIVATE);
        } else {
            Logger.e("nothing can be get!");
            return "";
        }
        return sp.getString(key, "");
    }

    public static String getCommonValueFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void storeCommonValueIntoSP(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putString(key, value);
        spe.commit();
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
            Log.e("jzh", context.getPackageName() + " resource not found for " + name);
        } else {
            icon = context.getResources().getDrawable(resId);
        }
        return icon;
    }

    public static Drawable changeFiletoDrawable(Context context, String fileName) {
        Drawable icon = null;
        String path = Launcher.DOWNLOAD_TO_PATH + "/" + Launcher.mFilePrefix + "/" + fileName;
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
        Log.d(TAG, "install apk for " + filePath);
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

    /**
     * install app
     *
     * @param context
     * @return whether apk exist
     */
    public static boolean install(Context context, Uri installUri) {
        Log.d(TAG, "install apk for " + installUri.toString());
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (installUri != null) {
            i.setDataAndType(installUri,
                    "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

    public static OtaInfo parseUpdateInfo(Context context, InputStream is) {
        OtaInfo ota = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals("ota")) {
                        ota = new OtaInfo();
                    } else if (name.equals("cur_version")) {
                        ota.currentVersion = parser.nextText().trim();
                    } else if (name.equals("new_version")) {
                        ota.newVersion = parser.nextText().trim();
                    } else if (name.equals("remark")) {
                        ota.remark = parser.nextText().trim();
                    } else if (name.equals("filesize")) {
                        ota.fileSize = parser.nextText().trim();
                    } else if (name.equals("url")) {
                        ota.url = parser.nextText().trim();
                    } else if (name.equals("md5")) {
                        ota.md5 = parser.nextText().trim();
                    }
                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    if (name.equals("ota")) {
                        Log.d(TAG, "ota info" + ota.toString());
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return ota;
    }

    public static void doUpdate(final Context context, OtaInfo ota) {
        IDownloadListener listener = new IDownloadListener() {

            @Override
            public void onError(String errorCode) {

            }

            @Override
            public void onCompleted(final File file) {
                ToolUtils.install(context, file.getAbsolutePath());
            }
        };
        DownloadTask task = new DownloadTask(Launcher.DOWNLOAD_TO_PATH, ota.url, listener);
        new Thread(task).start();
    }

    public static void safeStartApk(final Context context, final App app) {
        try {
            Intent intent = new Intent();
            intent.setComponent(app.getComponentName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            AppManager appManager = AppManager.getInstance();
            try {
                appManager.getAllApplications();
                ApplicationInfo descApp = appManager.getInfoFromAllActivitys(app.getAppPackage());
                descApp.startApplication(context);
            } catch (Exception e1) {
                e.printStackTrace();

                if (TextUtils.isEmpty(app.appUrl)) {
                    Toast.makeText(context, R.string.app_no_fund, Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.app_background_download);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!TextUtils.isEmpty(app.downloadStatus)) {
                            if (!app.downloadStatus.equals(App.DOWNLOAD_STATUS_DOWNLOADING)) {
                                app.downloadStatus = App.DOWNLOAD_STATUS_DOWNLOADING;
                                downloadApk(context, app);
                            } else {
                                Toast.makeText(context, R.string.app_is_downloading,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            app.downloadStatus = App.DOWNLOAD_STATUS_DOWNLOADING;
                            downloadApk(context, app);
                        }
                        arg0.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                Log.d(TAG, e.toString());
            }
        }
    }

    public static void downloadApk(final Context context, final App app) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(app.appUrl));
        request.setDestinationInExternalPublicDir("download", getUrlFileName(app.appUrl));
        request.allowScanningByMediaScanner();//表示允许MediaScanner扫描到这个文件，默认不允许。
        request.setTitle("程序更新");//设置下载中通知栏提示的标题
        request.setDescription("程序更新正在下载中:");//设置下载中通知栏提示的介绍
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        @SuppressWarnings("unused")
        long downloadId = downloadManager.enqueue(request);
        sDBdao.updateDownload(app);
    }

    public static void downloadOta(final Context context, final OtaInfo ota) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ota.getUrl()));
        request.setDestinationInExternalPublicDir("download", getUrlFileName(ota.getUrl()));
        request.allowScanningByMediaScanner();//表示允许MediaScanner扫描到这个文件，默认不允许。
        request.setTitle("程序更新");//设置下载中通知栏提示的标题
        request.setDescription("程序更新正在下载中:");//设置下载中通知栏提示的介绍
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        @SuppressWarnings("unused")
        long downloadId = downloadManager.enqueue(request);
        ToolUtils.storeCommonValueIntoSP(context, "ota_id", String.valueOf(downloadId));
    }

    private static String getUrlFileName(String url) {
        return url.substring(url.lastIndexOf("/"));
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
