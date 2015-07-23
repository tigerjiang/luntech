
package com.luntech.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.luntech.launcher.secondary.ApplicationInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    private ToolUtils() {

    }

    public static ToolUtils getInstance() {
        if (sInstance == null) {
            return new ToolUtils();
        } else {
            return sInstance;
        }
    }

    public static ArrayList<AppItem> getInfoFromConfig(Context context, int fileId) {
        Resources r = context.getResources();
        XmlResourceParser xrp = r.getXml(fileId);
        ArrayList<AppItem> applist = new ArrayList<AppItem>();
        AppItem app = null;
        try {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    Log.d(TAG, name);

                    if (name.equals(APP_TAG)) {
                        app = new AppItem(context);
                        app.setName(xrp.getAttributeValue(0));
                    } else if (name.equals(TYPE_TAG)) {
                        app.setType(xrp.nextText());
                    } else if (name.equals(ACTIVITY_TAG)) {
                        app.setActivityName(xrp.nextText());
                    } else if (name.equals(LABEL_TAG)) {
                        app.setLabel(xrp.nextText());
                    } else if (name.equals(BACKGROUND_TAG)) {
                        app.setBackground(xrp.nextText());
                    } else if (name.equals(SHADOW_TAG)) {
                        app.setShadow(xrp.nextText());
                    } else if (name.equals(LOGO_TAG)) {
                        app.setLogo(xrp.nextText());
                    } else if (name.equals(CONFIGURED_TAG)) {
                        app.setConfigured(xrp.nextText());
                    } else if (name.equals(AVAILABLE_TAG)) {
                        app.setAvailable(xrp.nextText());
                    } else if (name.equals(CONFIGURED_ACTIVITY_TAG)) {
                        app.setConfiguredComponent(xrp.nextText());
                    }
                } else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    String name = xrp.getName();
                    Log.d(TAG, name);
                    if (name.equals(APP_TAG)) {
                        Log.d(TAG, "app " + app.toString());
                        applist.add(app);
                    }
                }
                xrp.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        return applist;
    }

    public static ArrayList<CustomApplication> getCustomInfoFromConfig(Context context, int fileId) {
        Resources r = context.getResources();
        XmlResourceParser parser = r.getXml(fileId);
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        CustomApplication application = null;
        CustomApplication.Module module = null;
        ArrayList<CustomApplication.App> apps = null;
        CustomApplication.App app = null;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    // group info
                    if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        application.mGroup = new CustomApplication.Group();
                        application.mGroup.groupCode = parser.getAttributeValue(0);
                        application.mGroup.groupMoveable = Integer.parseInt(parser
                                .getAttributeValue(1));
                        application.mGroup.groupFlag = Integer
                                .parseInt(parser.getAttributeValue(2));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        application.mGroup.groupText = parser.nextText();
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        application.mGroup.groupBg = parser.nextText();
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        application.mGroup.groupIcon = parser.nextText();
                    }
                    // Module info

                    else if (name.equals(CustomApplication.Module.MODULE_TAG)) {
                        module = new CustomApplication.Module();
                        module.moduleCode = parser.getAttributeValue(0);
                        module.moduleReplace = Integer.parseInt(parser.getAttributeValue(1));
                        module.moduleType = Integer.parseInt(parser.getAttributeValue(2));
                    } else if (name.equals(CustomApplication.Module.MODULE_TEXT_TAG)) {
                        module.moduleText = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_BG_TAG)) {
                        module.moduleBg = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_ICON_TAG)) {
                        module.moduleIcon = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_SHADOW_TAG)) {
                        module.moduleShadow = parser.nextText();
                        // apps
                    } else if (name.equals(CustomApplication.App.APPS_TAG)) {
                        apps = new ArrayList<CustomApplication.App>();
                    } else if (name.equals(CustomApplication.App.APP_TAG)) {
                        app = new CustomApplication.App();
                    } else if (name.equals(CustomApplication.App.APP_NAME_TAG)) {
                        app.appName = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_PACKAGE_TAG)) {
                        app.appPackage = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_ACTIVITY_TAG)) {
                        app.appActivity = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_ICON_TAG)) {
                        app.appIcon = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_URL_TAG)) {
                        app.appUrl = parser.nextText();
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.App.APP_TAG)) {
                        Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(CustomApplication.App.APPS_TAG)) {
                        module.mApps = apps;
                        Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(CustomApplication.Module.MODULE_TAG)) {
                        application.addModule(module);
                        Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        applications.add(application);
                        Logger.d("end group" + application.toString());
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

    public static ArrayList<CustomApplication> getCustomInfoFromConfig(Context context,
            InputStream is) {
        ArrayList<CustomApplication> applications = new ArrayList<CustomApplication>();
        CustomApplication application = null;
        CustomApplication.Module module = null;
        ArrayList<CustomApplication.App> apps = null;
        CustomApplication.App app = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    // group info
                    if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        application = new CustomApplication();
                        application.mGroup = new CustomApplication.Group();
                        application.mGroup.groupCode = parser.getAttributeValue(0);
                        application.mGroup.groupMoveable = Integer.parseInt(parser
                                .getAttributeValue(1));
                        application.mGroup.groupFlag = Integer
                                .parseInt(parser.getAttributeValue(2));
                    } else if (name.equals(CustomApplication.Group.GROUP_TEXT_TAG)) {
                        application.mGroup.groupText = parser.nextText();
                    } else if (name.equals(CustomApplication.Group.GROUP_BG_TAG)) {
                        application.mGroup.groupBg = parser.nextText();
                    } else if (name.equals(CustomApplication.Group.GROUP_ICON_TAG)) {
                        application.mGroup.groupIcon = parser.nextText();
                    }
                    // Module info

                    else if (name.equals(CustomApplication.Module.MODULE_TAG)) {
                        module = new CustomApplication.Module();
                        module.moduleCode = parser.getAttributeValue(0);
                        module.moduleReplace = Integer.parseInt(parser.getAttributeValue(1));
                        module.moduleType = Integer.parseInt(parser.getAttributeValue(2));
                    } else if (name.equals(CustomApplication.Module.MODULE_TEXT_TAG)) {
                        module.moduleText = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_BG_TAG)) {
                        module.moduleBg = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_ICON_TAG)) {
                        module.moduleIcon = parser.nextText();
                    } else if (name.equals(CustomApplication.Module.MODULE_SHADOW_TAG)) {
                        module.moduleShadow = parser.nextText();
                        // apps
                    } else if (name.equals(CustomApplication.App.APPS_TAG)) {
                        apps = new ArrayList<CustomApplication.App>();
                    } else if (name.equals(CustomApplication.App.APP_TAG)) {
                        app = new CustomApplication.App();
                    } else if (name.equals(CustomApplication.App.APP_NAME_TAG)) {
                        app.appName = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_PACKAGE_TAG)) {
                        app.appPackage = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_ACTIVITY_TAG)) {
                        app.appActivity = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_ICON_TAG)) {
                        app.appIcon = parser.nextText();
                    } else if (name.equals(CustomApplication.App.APP_URL_TAG)) {
                        app.appUrl = parser.nextText();
                    }

                } else if (parser.getEventType() == XmlResourceParser.END_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    if (name.equals(CustomApplication.App.APP_TAG)) {
                        Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(CustomApplication.App.APPS_TAG)) {
                        module.mApps = apps;
                        Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(CustomApplication.Module.MODULE_TAG)) {
                        application.addModule(module);
                        Logger.d("end module" + module.toString());
                    } else if (name.equals(CustomApplication.Group.GROUP_TAG)) {
                        applications.add(application);
                        Logger.d("end group" + application.toString());
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

    public void setConfigured(Context context, ApplicationInfo app, boolean configured) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putBoolean(app.mTitle.toString(), configured);
        spe.commit();
    }

    public void setConfiguredPkg(Context context, int index, String pkg) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        Editor spe = sp.edit();
        spe.putString("category" + "_" + index, pkg);
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

    public boolean getConfigured(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getBoolean(name, false);
    }

}
