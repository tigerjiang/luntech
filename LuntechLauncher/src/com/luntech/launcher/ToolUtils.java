
package com.luntech.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ToolUtils {

    private static final String APP_TAG = "app";
    private static final String TYPE_TAG = "type";
    private static final String COMPONENT_TAG = "component";
    private static final String LABEL_TAG = "label";
    private static final String BACKGROUND_TAG = "background";
    private static final String SHADOW_TAG = "shadow";
    private static final String LOGO_TAG = "logo";
    private static final String CONFIGURED_TAG = "configured";
    private static final String AVAILABLE_TAG = "available";
    private static final String CONFIGURED_COMPONENT_TAG = "configured_component";
    private static final String TAG = "ToolUtils";

    public static List<AppItem> getInfoFromConfig(Context context, int fileId) {
        Resources r = context.getResources();
        XmlResourceParser xrp = r.getXml(fileId);
        List<AppItem> applist = new ArrayList<AppItem>();
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
                    } else if (name.equals(COMPONENT_TAG)) {
                        app.setComponentName(xrp.nextText());
                    } else if (name.equals(LABEL_TAG)) {
                        app.setLabel(xrp.nextText());
                    } else if (name.equals(BACKGROUND_TAG)) {
                        app.setBackground(xrp.nextText());
                    } else if (name.equals(SHADOW_TAG)) {
                        app.setShadow(xrp.nextText());
                    } else if (name.equals(LOGO_TAG)) {
                        app.setLogo(xrp.nextText());
                    }else if (name.equals(CONFIGURED_TAG)) {
                        app.setConfigured(xrp.nextText());
                    } else if (name.equals(AVAILABLE_TAG)) {
                        app.setAvailable(xrp.nextText());
                    } else if (name.equals(CONFIGURED_COMPONENT_TAG)) {
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
}
