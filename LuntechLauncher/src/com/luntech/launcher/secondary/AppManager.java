package com.luntech.launcher.secondary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.luntech.launcher.R;

public final class AppManager {

	private static final String TAG = AppManager.class.getSimpleName();

	private static final boolean DEBUG = false;
	private static final String NON_ALPHA_NUMERIC_REG_EX = "\\W+";
	// max number of desktop app icons
	private static final int MAX_COUNT = 10;
	// the tag for desktop app package used by xml parser
	private static final String GAME_APP_TAG = "GameActivity";
	// the tag for excluded app package used by xml parser
	private static final String MEDIA_APP_TAG = "MediaActivity";
	private static final String LONTAI_APP_TAG = "LonActivity";
	private static final String HOUSE_APP_TAG = "HouseActivity";
	private static AppManager sInstance;
	private List<String> mGameAppsNames;
	private List<String> mMediaAppsNames;
	private List<ApplicationInfo> mGameApps;
	private List<String> mHouseAppsNames;
	private List<ApplicationInfo> mHouseApps;

	private List<String> mLonAppsNames;
	private List<ApplicationInfo> mLonApps;
	private List<ApplicationInfo> mMediaApps;
	private Context mContext;

	private static Map<String, ApplicationInfo> mMainActivities = new HashMap<String, ApplicationInfo>();
	private static Map<String, ApplicationInfo> mAllActivities = new HashMap<String, ApplicationInfo>();

	private final PackageManager mPackageManager;

	public static enum State {
		UNKNOWN, READY
	}
	
	private static ArrayList<String> mHidenAllApps = new ArrayList<String>();
	private static ArrayList<String> mHidenApps = new ArrayList<String>();
	
	static {
		mHidenApps.add("com.xike.xkliveplay");
		mHidenApps.add("com.media.box.fte");
		mHidenApps.add("com.android.launcher");
		mHidenApps.add("com.android.providers.downloads.ui");
		mHidenApps.add("com.android.gallery3d");
		mHidenApps.add("com.android.development");
		mHidenApps.add("com.adobe.flashplayer");
		mHidenApps.add("com.sdtv.auth");
		
		mHidenApps.add("com.csidea.kikilobby");
		mHidenApps.add("com.app.xjiajia");
		mHidenApps.add("com.andlisoft.station.game");
		mHidenApps.add("com.Coocaa.BjLbs.mole");
		mHidenApps.add("com.game.gameobstructrun");
		mHidenApps.add("com.dygame.gamezone2");
		mHidenApps.add("com.shenyou.up");
		mHidenApps.add("com.thfd.d9.disneyhurdle");
		mHidenApps.add("com.android.settings");
		mHidenApps.add("org.booster.gundam");
		mHidenApps.add("com.qihoo360.mobilesafe_tv");
		mHidenApps.add("com.eastsoft.android.ihome");
		
	}
	
	static {
		
		mHidenAllApps.add("com.egame.tv");
		mHidenAllApps.add("com.luntech.zhihemarket");
		mHidenAllApps.add("com.luntech.maplivingarea");
		mHidenAllApps.add("com.example.newsdevelop");
		mHidenAllApps.add("cn.com.wasu.main");
		mHidenAllApps.add("com.sohu.inputmethod.sogoupad");
		mHidenAllApps.add("com.qihoo360.mobilesafe_tv");
		mHidenAllApps.add("com.duomi.androidtv");
		mHidenAllApps.add("com.douguo.recipetv");
		mHidenAllApps.add("com.mfw.roadbook");
		mHidenAllApps.add("com.seeme.tvframe");
		
		mHidenAllApps.add("com.wowotuan.tv");
		mHidenAllApps.add("com.suning.tv.ebuy");
		mHidenAllApps.add("com.softwinner.TvdFileManager");
		mHidenAllApps.add("com.android.settings");
		mHidenAllApps.add("com.android.music");
		
		mHidenAllApps.add("com.dianping.v1");
	}

	private State mState = State.UNKNOWN;

	public State getState() {
		return mState;
	}

	// ///////////////////////////////////////////////////////////////////////////////
	private AppManager(Context context) {
		mPackageManager = context.getPackageManager();
		mGameAppsNames = getDedicatedAppListFromXml(context,
				R.xml.game_package, GAME_APP_TAG);
		mMediaAppsNames = getDedicatedAppListFromXml(context,
				R.xml.media_package, MEDIA_APP_TAG);
		mLonAppsNames = getDedicatedAppListFromXml(context,
				R.xml.longtai_package, LONTAI_APP_TAG);
		mHouseAppsNames = getDedicatedAppListFromXml(context,
				R.xml.household_package, HOUSE_APP_TAG);
		Log.d(TAG, "game " + mGameAppsNames.toString());
		Log.d(TAG, "media " + mMediaAppsNames.toString());
	}

	public static AppManager create(Context context) {
		AppManager localManager = sInstance;
		if (localManager == null) {
			synchronized (AppManager.class) {
				localManager = sInstance;
				if (localManager == null) {
					sInstance = localManager = new AppManager(context);
				}
			}
		}
		return localManager;
	}

	public static AppManager getInstance() {
		if (sInstance == null) {
			throw new NullPointerException("you must call create() first");
		}
		return sInstance;
	}

	private ApplicationInfo getInfoFromActInfo(final ActivityInfo ai,
			ComponentName cn) {
		if (cn == null) {
			cn = new ComponentName(ai.applicationInfo.packageName, ai.name);
		}
		final ApplicationInfo info = new ApplicationInfo();
		info.mTitle = ai.applicationInfo.loadLabel(mPackageManager);
		info.mIcon = ai.applicationInfo.loadIcon(mPackageManager);
		info.mComponent = cn;
		return info;
	}

	private ApplicationInfo getInfoFromManActicities(String packageName) {
        return mMainActivities.get(packageName);
    }
	
    public ApplicationInfo getInfoFromAllActivitys(String packageName) {
        return mAllActivities.get(packageName);
    }
	
	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getAllApplications() {
		mMainActivities.clear();
		mAllActivities.clear();
		Log.d("packagename", "show app");
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> acts = mPackageManager.queryIntentActivities(
				mainIntent, 0);
		final int count = acts.size();

		final List<ApplicationInfo> appList = new ArrayList<ApplicationInfo>(
				count);
		appList.clear();
		Collections.sort(acts, INSTALLED_APPS_COMPARATOR);
		Log.d(TAG, "aaaaa"+mHidenApps.toString());
		for (int i = 0; i < count; i++) {
			final ResolveInfo info = acts.get(i);
			if (info != null) {
				final ApplicationInfo appinfo = getInfoFromActInfo(
						info.activityInfo, null);
				try {
					PackageInfo pi = mPackageManager.getPackageInfo(
							info.activityInfo.packageName, 0);
					appinfo.mInstallTime = pi.firstInstallTime;
					appinfo.mpackageName = info.activityInfo.packageName;;
				} catch (PackageManager.NameNotFoundException e) {
					// ignore
				}

				final String packageName = info.activityInfo.packageName;
				
				if (mHidenApps.contains(packageName)) {
					Log.d("packagename hiden","1"+ packageName);
				} else {
					mMainActivities.put(packageName, appinfo);
					appList.add(appinfo);
				}
				mAllActivities.put(packageName, appinfo);
				Log.d("packagename", packageName);
			}
		}

		// then include them ahead
		return appList;
	}

	
	/**
     * All Applications for Content View.
     * 
     * @return
     */
    public List<ApplicationInfo> getSelectedApplications() {
        mAllActivities.clear();
        Log.d("packagename", "show app");
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> acts = mPackageManager.queryIntentActivities(
                mainIntent, 0);
        final int count = acts.size();

        final List<ApplicationInfo> appList = new ArrayList<ApplicationInfo>(
                count);
        appList.clear();
        Collections.sort(acts, INSTALLED_APPS_COMPARATOR);
        Log.d(TAG, "aaaaa"+mHidenApps.toString());
        for (int i = 0; i < count; i++) {
            final ResolveInfo info = acts.get(i);
            if (info != null) {
                final ApplicationInfo appinfo = getInfoFromActInfo(
                        info.activityInfo, null);
                try {
                    PackageInfo pi = mPackageManager.getPackageInfo(
                            info.activityInfo.packageName, 0);
                    appinfo.mInstallTime = pi.firstInstallTime;
                    appinfo.mpackageName = info.activityInfo.packageName;;
                } catch (PackageManager.NameNotFoundException e) {
                    // ignore
                }

                final String packageName = info.activityInfo.packageName;
                appList.add(appinfo);
                mAllActivities.put(packageName, appinfo);
                Log.d("packagename", packageName);
            }
        }

        // then include them ahead
        return appList;
    }

	
	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getGameApplications() {
		getAllApplications();
		mGameApps = retrieveInfoListFromAppNames(mGameAppsNames);
		return mGameApps;
	}

	public List<ApplicationInfo> mAllApps;
	
	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getAllAppsApplications() {
		List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
		List<ApplicationInfo> allApps = getAllApplications();
		for(ApplicationInfo app:allApps){
			if(mHidenAllApps.contains(app.mComponent.getPackageName())){
				continue;
			}
			apps.add(app);
		}
		return apps;
	}
	
	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getMediaApplications() {
		getAllApplications();
		mMediaApps = retrieveInfoListFromAppNames(mMediaAppsNames);
		return mMediaApps;
	}

	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getHouseApplications() {
		getAllApplications();
		mHouseApps = retrieveInfoListFromAppNames(mHouseAppsNames);
		return mHouseApps;
	}

	/**
	 * All Applications for Content View.
	 * 
	 * @return
	 */
	public List<ApplicationInfo> getLonApplications() {
		getAllApplications();
		mLonApps = retrieveInfoListFromAppNames(mLonAppsNames);
		return mLonApps;
	}

	private List<ApplicationInfo> retrieveInfoListFromAppNames(
			final List<String> src) {
		final List<ApplicationInfo> retList = new ArrayList<ApplicationInfo>();
		if (src != null) {
			for (String packageName : src) {
				final ApplicationInfo info = mMainActivities.get(packageName);
				if (info == null) {
					continue;
				}
				Log.d(TAG, "into " + info.mTitle);
				retList.add(info);
			}
		}
		return retList;
	}

	// ///////////////// Private API Helpers //////////////////////////

	private final Comparator<ResolveInfo> INSTALLED_APPS_COMPARATOR = new Comparator<ResolveInfo>() {

		@Override
		public int compare(ResolveInfo lhs, ResolveInfo rhs) {
			int result = 0;
			final int l_flags = lhs.activityInfo.applicationInfo.flags;
			final int r_flags = rhs.activityInfo.applicationInfo.flags;
			final int sys_flag = android.content.pm.ApplicationInfo.FLAG_SYSTEM;
			// if with the same SYSTEM flag then sort by name
			if (((l_flags & sys_flag) ^ (r_flags & sys_flag)) == 0) {
				final String lLabel = lhs.activityInfo.applicationInfo
						.loadLabel(mPackageManager).toString();
				final String rLabel = rhs.activityInfo.applicationInfo
						.loadLabel(mPackageManager).toString();
				// String.trim() is not removing some unseen chars, for example
				// \u00A0, regexp used instead
				result = lLabel
						.replaceAll(NON_ALPHA_NUMERIC_REG_EX, "")
						.toLowerCase()
						.compareTo(
								rLabel.replaceAll(NON_ALPHA_NUMERIC_REG_EX, "")
										.toLowerCase());
			} else {
				if ((l_flags & sys_flag) == 0) {
					result = 1;
				} else {
					result = -1;
				}
			}
			return result;
		}
	};

	/**
	 * Get app package info list from dedicated xml file, such as those apps
	 * which used to add app icons to desktop of app center's main page, or
	 * those excluded apps which we do not want them show out in all app list.
	 * 
	 * @param context
	 *            the context
	 * @param file
	 *            the dedicate xml file to retrieve package info list
	 * @param app_tag
	 *            the xml parser tag
	 * @return A list contains those package info/app list
	 */
	private final List<String> getDedicatedAppListFromXml(Context context,
			int file, String app_tag) {
		List<PackageInfo> packageInfoList = context.getPackageManager()
				.getInstalledPackages(0);
		List<String> installedPackageList = new ArrayList<String>();
		for (PackageInfo info : packageInfoList) {
			installedPackageList.add(info.packageName);
		}
		Resources r = context.getResources();
		XmlResourceParser xrp = r.getXml(file);
		List<String> packageList = new ArrayList<String>();
		String xmlPackageName = null;
		int packagecount = 0;
		try {
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (packagecount >= MAX_COUNT) {
					break;
				}
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String name = xrp.getName();
					if (name.equals(app_tag)) {
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
					Log.d(TAG, "xml end");
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException occurs " + e);
		} catch (IOException e) {
			Log.e(TAG, "packagefilter occurs " + e);
		}
		Log.i(TAG, "getDedicatedAppListFromXml by tag: " + app_tag
				+ ", return: " + packageList.toString());
		return packageList;
	}

}
