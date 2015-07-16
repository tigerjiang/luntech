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
    private void getInfoFromConfig(Context context, int fileId){
        Resources r = context.getResources();
        XmlResourceParser xrp = r.getXml(fileId); 
        List<AppItem> applist = new ArrayList<AppItem>();
        try {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    if (name.equals(APP_TAG)) {
                        int count = xrp.getAttributeCount();
                        if (count == 1) {
                            xmlPackageName = xrp.getAttributeValue(0);
                            for (String installedPackageName : installedPackageList) {
                                if (installedPackageName.equals(xmlPackageName)) {
                                    packageList.add(xmlPackageName);
                                    packagecount++;
                                    break;
                                }
                            }
                        }
                    }
                } else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    
                }
                xrp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException occurs " + e);
        } catch (IOException e) {
            Log.e(TAG, "packagefilter occurs " + e);
        }
        }
}
