package com.luntech.launcher.setting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
//import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luntech.launcher.R;


public class AboutTVDialogFragment extends Fragment {

	private static final String TAG = AboutTVDialogFragment.class
			.getSimpleName();
	private static final boolean DEBUG = false;
	private static final int INDEX_TV_MODEL = 0;
	private static final int INDEX_ANDROID_VERSION = 1;

	private static final int INDEX_BASEBAND_VERSION = 2;
	private static final int INDEX_KERNEL_VERSION = 3;
	private static final int INDEX_BUILD_NUMBER = 4;

	private static final int INDEX_ETHERNET_MAC = 5;
	private static final int INDEX_WIFI_MAC = 6;
	private static final int LIST_SIZE = 7;

	private static final String FILENAME_PROC_VERSION = "/proc/version";
	private static final String LOG_TAG = "DeviceInfoSettings";


	private ArrayList<AboutTVListItem> mItems;

	private static boolean sIsResumed = false;
	private ListView mTvInfoListView;

	public static AboutTVDialogFragment newInstance() {
		AboutTVDialogFragment deviceInfoSettings = new AboutTVDialogFragment();
		return deviceInfoSettings;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.about_tv_layout, null);
		initView(rootView);
		return rootView;
	}


	private void initView(View view) {
		/** first, we must get the Service. **/

		initItems();
		setSummaries();

		AboutTVListAdapter adapter = new AboutTVListAdapter(getActivity()
				.getApplicationContext(), mItems);

		mTvInfoListView = (ListView) view.findViewById(R.id.about_tv_list);
		mTvInfoListView.setAdapter(adapter);
	}



	private void initItems() {
		mItems = new ArrayList<AboutTVListItem>(LIST_SIZE);
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_model_number)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_android_version)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_baseband_version)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_kernel_version)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_build_number)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_mac_address)));
		mItems.add(new AboutTVListItem(
				getString(R.string.settings_system_about_wifi_mac_address)));
	}

	private void setSummaries() {

		mItems.get(INDEX_TV_MODEL).setSummary(Build.MODEL);

		// Android version
		mItems.get(INDEX_ANDROID_VERSION)
				.setSummary(Build.VERSION.RELEASE + "");
		// kernel version
		mItems.get(INDEX_BASEBAND_VERSION).setSummary(Build.HARDWARE);
		// build number and date
		mItems.get(INDEX_KERNEL_VERSION)
				.setSummary(getFormattedKernelVersion());
//		mItems.get(INDEX_BUILD_NUMBER).setSummary(
//				SystemProperties.get("ro.product.version"));
				mItems.get(INDEX_BUILD_NUMBER).setSummary(Build.PRODUCT);
		String ethernetMacAddress = Utilities.getMacAddress();
		if (DEBUG) {
			Log.d(TAG, "updateValuesByTvApi(): got Ethernet MAC address:"
					+ ethernetMacAddress);
		}
		if (ethernetMacAddress != null) {
			mItems.get(INDEX_ETHERNET_MAC).setSummary(
					ethernetMacAddress.toUpperCase());
		} else {
			mItems.get(INDEX_ETHERNET_MAC).setSummary(
					getString(R.string.settings_system_about_unavailiable));
		}
		String wifiMac = null;
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			wifiMac = wifiInfo.getMacAddress();
		}
		if (wifiMac == null || wifiMac.isEmpty()) {
			wifiMac = getString(R.string.settings_system_about_wifi_not_enabled);
		}
		mItems.get(INDEX_WIFI_MAC).setSummary(wifiMac.toUpperCase());
	}


	/**
	 * Reads a line from the specified file.
	 * 
	 * @param filename
	 *            the file to read from
	 * @return the first line, if any.
	 * @throws IOException
	 *             if the file couldn't be read
	 */
	private static String readLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename),
				256);
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}

	public static String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

		} catch (IOException e) {
			Log.e("device info",
					"IO Exception when getting kernel version for Device Info screen",
					e);

			return "Unavailable";
		}
	}

	public static String formatKernelVersion(String rawKernelVersion) {
		// Example (see tests for more):
		// Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
		// (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
		// Thu Jun 28 11:02:39 PDT 2012

		final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /*
																	 * group 1:
																	 * "3.0.31-g6fb96c9"
																	 */
		"\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
		"(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
		"(#\\d+) " + /* group 3: "#1" */
		"(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
		"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /*
											 * group 4:
											 * "Thu Jun 28 11:02:39 PDT 2012"
											 */

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(
				rawKernelVersion);
		if (!m.matches()) {
			Log.e("device info", "Regex did not match on /proc/version: "
					+ rawKernelVersion);
			return "Unavailable";
		} else if (m.groupCount() < 4) {
			Log.e("device info", "Regex match on /proc/version only returned "
					+ m.groupCount() + " groups");
			return "Unavailable";
		}
		return m.group(1) + "\n" + // 3.0.31-g6fb96c9
				//m.group(2)
                                "luntech@luntech.com"+ " " + m.group(3) + "\n" + // x@y.com #1
				m.group(4); // Thu Jun 28 11:02:39 PDT 2012
	}

	class AboutTVListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		private List<AboutTVListItem> mItems;

		public AboutTVListAdapter(Context context, List<AboutTVListItem> items) {
			mInflater = LayoutInflater.from(context);
			mItems = items;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.preference, parent,
						false);
			}
			TextView title = (TextView) convertView
					.findViewById(android.R.id.title);
			title.setTextSize(28);
			TextView summary = (TextView) convertView
					.findViewById(android.R.id.summary);
			summary.setVisibility(View.VISIBLE);
			summary.setTextSize(15);
			AboutTVListItem currentItem = mItems.get(position);
			title.setText(currentItem.getTitle());
			summary.setText(currentItem.getSummary());

			return convertView;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int id) {
			return mItems.get(id);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}
	}

	class AboutTVListItem {
		private String mTitle;
		private String mSummary;

		public AboutTVListItem(String title) {
			setTitle(title);
			mSummary = new String();
		}


		private void setTitle(String title) {
			mTitle = title;
		}

		public void setSummary(String summary) {
			mSummary = summary;
		}

		public String getTitle() {
			return mTitle;
		}

		public String getSummary() {
			return mSummary;
		}
	}
}
