
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
    private static final String NETWORK_INFO = "network_info";

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
        Module module = null;
        ArrayList<App> apps = null;
        App app = null;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    Log.d(TAG, name);
                    // group info
                    if (name.equals(CustomApplication.Group.GROUP_TAG)) {
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
                        Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        module.mApps = apps;
                        Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        application.mGroup.addModule(module);
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
        Module module = null;
        ArrayList<App> apps = null;
        App app = null;
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
                        Log.d(TAG, "end app " + app.toString());
                        apps.add(app);
                    } else if (name.equals(App.APPS_TAG)) {
                        module.mApps = apps;
                        Log.d(TAG, "end apps " + module.mApps.toString());
                    } else if (name.equals(Module.MODULE_TAG)) {
                        application.mGroup.addModule(module);
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

    public boolean getConfigured(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(CUSTOM_INFO, Context.MODE_PRIVATE);
        return sp.getBoolean(name, false);
    }

}
