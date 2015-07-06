
package com.luntech.launcher.view;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.ChangeNotifyManager;
import com.luntech.launcher.INetworkStatusListener;
import com.luntech.launcher.IUsbMountListener;
import com.luntech.launcher.IWeatherChangeListener;
import com.luntech.launcher.R;
import com.luntech.launcher.TimeManager;
import com.luntech.launcher.TimeManager.OnFormatChangedListener;
import com.luntech.launcher.TimeManager.OnTimeChangedListener;

import org.jsoup.Jsoup;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TvStatusBar extends RelativeLayout implements INetworkStatusListener,
        IUsbMountListener, IWeatherChangeListener, OnTimeChangedListener, OnFormatChangedListener {
    private static final String TAG = "TvStatusBar";
    private static final boolean DEBUG = true;
    private TextView mTimeView;
    private TextView mTemperatureView;
    private ImageView mWifiStatusView;
    private ImageView mUsbStatusView;
    private ImageView mWeatherStatusView;

    private String mCity;
    private String mWeatherDetail;
    private String mTemperature;
    private TimeManager mTimeManager;
    private Context mContext;
    private TvStatus mState;
    private ConnectivityManager mConnectivityManager;

    private Resources mResources;

    private long mDelayTime = 5*1000;// 3 * 60 * 1000; // 3min
    // keep in sync with System Settings -> Network Settings
    private static final String PPPOE_ENABLED = "pppoe_enabled";
    private static final String PPPOE_CONNECTED = "pppoe_connected";
    private Drawable mDrawableNetworkOffline;
    private Drawable mDrawableNetworkEthernet;
    private Drawable mDrawableNetworkWifi;

    public TvStatusBar(Context context) {
        super(context);
        initView(context);
    }

    public TvStatusBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public TvStatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mUiHandler = new UiHandler(this);
        mResources = mContext.getResources();
        mDrawableNetworkOffline = mResources.getDrawable(R.drawable.ic_statusbar_ununited_network);
        mDrawableNetworkEthernet = mResources.getDrawable(R.drawable.ic_statusbar_wired_network);
        mDrawableNetworkWifi = mResources.getDrawable(R.drawable.fullscreen_gp_wifi_signal);
        mState = new TvStatus();
        LayoutInflater.from(context).inflate(R.layout.status_bar, this);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mTemperatureView = (TextView) findViewById(R.id.temperature_view);
        mWifiStatusView = (ImageView) findViewById(R.id.wifi_status);
        mUsbStatusView = (ImageView) findViewById(R.id.usb_status);
        mWeatherStatusView = (ImageView) findViewById(R.id.weather_status);
        registerListener();
        // get the shared handler thread on which we will do all our work
        final HandlerThread thread = new HandlerThread("update");
        thread.start();
        mHandler = new NonUIHandler(thread.getLooper());

        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mTimeManager = TimeManager.getInstance(context);

        mTimeManager.addOnTimeChangedListener(this);
        mTimeManager.addOnFormatChangedListener(this);

        // register content observers
        final ContentResolver cr = context.getContentResolver();
        // observe PPPoE be enabled or not
        cr.registerContentObserver(Settings.System.getUriFor(PPPOE_ENABLED), false,
                new ContentObserver(mHandler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        if (DEBUG) {
                            Log.d(TAG, "value of PPPOE_ENABLED changed");
                        }
                        if (mConnectivityManager != null) {
                            final NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                            onNetworkInfoChanged(netInfo);
                        }
                    }
                });

        // observe PPPoE connection change
        cr.registerContentObserver(Settings.System.getUriFor(PPPOE_CONNECTED), false,
                new ContentObserver(mHandler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        if (DEBUG) {
                            Log.d(TAG, "value of PPPOE_CONNECTED changed");
                        }
                        if (mConnectivityManager != null) {
                            final NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                            onNetworkInfoChanged(netInfo);
                        }
                    }
                });

        // initialize state data
        if (!mHandler.hasMessages(NonUIHandler.MSG_UPDATE_DATE_TIME)) {
            mHandler.obtainMessage(NonUIHandler.MSG_UPDATE_DATE_TIME,
                    new Long(System.currentTimeMillis())).sendToTarget();
        }

        // initialize weather info
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                captureWeatherFromInternet();
            }
        }, mDelayTime);
        // update the network info
        if (mConnectivityManager != null) {
            final NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            onNetworkInfoChanged(netInfo);
        }
        // register for broadcasts we want
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    private void registerListener() {
        final ChangeNotifyManager changeNotifyManager = ChangeNotifyManager.getInstance();
        changeNotifyManager.registerNetworkStatusListener(this);
        changeNotifyManager.registerUsbMountListener(this);
        changeNotifyManager.registerWeatherChangeListener(this);
    }

    @Override
    public void mount() {

    }

    @Override
    public void unMount() {

    }

    @Override
    public void weatherChange(int low, int heigh) {

    }

    @Override
    public void networkChenge(int status) {

    }

    private class NonUIHandler extends Handler {
        private static final boolean DEBUG_MSG = true;

        public static final int MSG_ON_NETWORK_INFO_CHANGED = 1;
        public static final int MSG_UPDATE_DATE_TIME = 2;
        public static final int MSG_ON_WEATHER_INFO_CHANGED = 3;

        public NonUIHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_NETWORK_INFO_CHANGED:
                    if (DEBUG_MSG) {
                        Log.d(TAG, "handleMessage(Non UI): MSG_ON_NETWORK_INFO_CHANGED");
                    }
                    final NetworkInfo netInfo = (NetworkInfo) msg.obj;
                    synchronized (mState) {
                        if (netInfo == null || !netInfo.isConnected()) {
                            mState.setNetworkType(-1);
                            mState.setNetworkIcon(mDrawableNetworkOffline);
                            mState.setNetworkRSSI(Integer.MIN_VALUE);
                        } else {
                            final int networkType = netInfo.getType();
                            Drawable networkIcon = mDrawableNetworkOffline;
                            int rssi = Integer.MIN_VALUE;
                            mState.setNetworkType(networkType);
                            if (networkType == ConnectivityManager.TYPE_ETHERNET) {
                                networkIcon = mDrawableNetworkEthernet;
                                mState.setNetworkIcon(networkIcon);
                                mState.setNetworkRSSI(rssi);
                            } else if (networkType == ConnectivityManager.TYPE_WIFI) {
                                networkIcon = mDrawableNetworkWifi;
                                // WifiManager uses a handler thread, so we
                                // don't want
                                // to keep the reference around forever
                                final WifiManager wifiManager = (WifiManager) mContext
                                        .getSystemService(Context.WIFI_SERVICE);
                                if (wifiManager != null) {
                                    final WifiInfo info = wifiManager.getConnectionInfo();
                                    rssi = info.getRssi();
                                }
                                mState.setNetworkIcon(networkIcon);
                                mState.setNetworkRSSI(rssi);
                            }
                        }
                        if (mState.hasChanges(TvStatus.NETWORK_INFO)) {
                            notifyStateChange();
                        }
                    }
                break;

                case MSG_UPDATE_DATE_TIME:
                    if (DEBUG_MSG) {
                        Log.d(TAG, "handleMessage(Non UI): MSG_UPDATE_DATE_TIME");
                    }
                    if (mTimeManager != null) {
                        final long time;
                        if (msg.obj instanceof Long) {
                            time = ((Long) msg.obj).longValue();
                        } else {
                            time = System.currentTimeMillis();
                        }

                        synchronized (mState) {
                            mState.setDate(mTimeManager.formatDateTime(time,
                                    TimeManager.DateTimeFormat.DAY_OF_WEEK_MONTH_DAY));
                            mState.setTime(mTimeManager.formatDateTime(time,
                                    TimeManager.DateTimeFormat.TIME_SHORT));
                            if (mState.hasChanges(TvStatus.DATE_TIME_INFO)) {
                                notifyStateChange();
                            }
                        }
                    }
                break;
                case MSG_ON_WEATHER_INFO_CHANGED:
                    if (DEBUG_MSG) {
                        Log.d(TAG, "handleMessage(Non UI): MSG_UPDATE_WEATHER_INFO");
                    }
                    final String[] weatherInfo;
                    if (msg.obj instanceof String[]) {
                        weatherInfo = (String[]) msg.obj;
                    } else {
                        weatherInfo = new String[] {
                                "", ""
                        };
                    }
                    Log.d(TAG, "weather info "+weatherInfo);
                    synchronized (mState) {
                        Drawable weatherIcon = getWeatherIcon(weatherInfo[0]);
                        mState.setWeatherIcon(weatherIcon);
                        mState.setTemperature(weatherInfo[1]);
                        if (mState.hasChanges(TvStatus.DATE_TIME_INFO)) {
                            notifyStateChange();
                        }
                    }
                break;

                default:
                    if (DEBUG_MSG) {
                        Log.d(TAG, "handleMessage(Non UI): unhandled msg=" + msg);
                    }
                    super.handleMessage(msg);
                break;
            }
        }
    }

    private Handler mHandler;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                    || action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    || action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                    || action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                if (mConnectivityManager != null) {
                    final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
                    onNetworkInfoChanged(networkInfo);
                }
            }
        }
    };

    private void onNetworkInfoChanged(NetworkInfo netInfo) {
        mHandler.obtainMessage(NonUIHandler.MSG_ON_NETWORK_INFO_CHANGED, netInfo).sendToTarget();
    }

    @Override
    public void onFormatChanged() {
        // only send if no message is already enqueued
        if (!mHandler.hasMessages(NonUIHandler.MSG_UPDATE_DATE_TIME)) {
            mHandler.obtainMessage(NonUIHandler.MSG_UPDATE_DATE_TIME,
                    new Long(System.currentTimeMillis())).sendToTarget();
        }
    }

    @Override
    public void onTimeChanged(long time) {
        // always send this so that we don't miss any time updates
        mHandler.obtainMessage(NonUIHandler.MSG_UPDATE_DATE_TIME, new Long(time)).sendToTarget();
    }

    private void processState(boolean refreshAll) {
        if (mState != null) {
            synchronized (mState) {
                Log.d(TAG, "processState: " + mState);

                // Network info
                if (refreshAll || mState.hasChanges(TvStatus.NETWORK_INFO)) {
                    mWifiStatusView.setImageDrawable(mState.getNetworkIcon());
                    mWifiStatusView.setImageLevel(WifiManager.calculateSignalLevel(
                            mState.getNetworkRSSI(), 4));
                    mState.clearChanges(TvStatus.NETWORK_INFO);
                }

                // Date Time info
                if (refreshAll || mState.hasChanges(TvStatus.DATE_TIME_INFO)) {
                    mTimeView.setText(mState.getTime());
                    mState.clearChanges(TvStatus.DATE_TIME_INFO);
                }
                // Weather info
                if (refreshAll || mState.hasChanges(TvStatus.WEATHER_INFO)) {
                    mWeatherStatusView.setImageDrawable(mState.getWeatherIcon());
                    mTemperatureView.setText(mState.getTemperature());
                    mState.clearChanges(TvStatus.DATE_TIME_INFO);
                }
            }
        }
    }

    private void notifyStateChange() {
        if (mUiHandler != null) {
            if (!mUiHandler.hasMessages(UiHandler.MSG_STATE_CHANGE)) {
                mUiHandler.obtainMessage(UiHandler.MSG_STATE_CHANGE).sendToTarget();
            }
        }
    }

    private static class UiHandler extends Handler {
        public static final int MSG_STATE_CHANGE = 1;

        private final WeakReference<TvStatusBar> mLayout;

        public UiHandler(TvStatusBar obj) {
            super(Looper.getMainLooper());
            mLayout = new WeakReference<TvStatusBar>(obj);
        }

        @Override
        public void handleMessage(Message msg) {
            if (DEBUG)
                Log.d(TAG, "handleMessage msg=" + msg);
            TvStatusBar layout = mLayout.get();
            if (layout == null) {
                super.handleMessage(msg);
                return;
            }

            switch (msg.what) {
                case MSG_STATE_CHANGE:
                    layout.processState(false);
                break;

                default:
                    super.handleMessage(msg);
                break;
            }
        }
    }

    private UiHandler mUiHandler;

    public String getCityIP() {
        URL url;
        URLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = "";
        String localIPString = null;
        try {
            url = new URL("http://iframe.ip138.com/ic.asp");
            conn = url.openConnection();
            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String input = "";
            org.jsoup.nodes.Document doc;
            while ((input = br.readLine()) != null) {
                str += input;
            }
            doc = Jsoup.parse(str);

            String ip1 = doc.body().toString();
            int start = ip1.indexOf("[");
            int end = ip1.indexOf("]");

            localIPString = ip1.substring(start + 1, end);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localIPString;

    }

    public String getCityByIp(String ipString) {
        String cityString = null;
        try {
            URL url = new URL("http://whois.pconline.com.cn/ip.jsp?ip=" + ipString);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream is = connect.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buff = new byte[256];
            int rc = 0;
            while ((rc = is.read(buff, 0, 256)) > 0) {
                outStream.write(buff, 0, rc);

            }

            byte[] b = outStream.toByteArray();

            // 关闭
            outStream.close();
            is.close();
            connect.disconnect();
            String address = new String(b, "GBK");

            if (address.startsWith("北") || address.startsWith("上") || address.startsWith("重")) {
                cityString = (address.substring(0, address.indexOf("市")));
            }
            if (address.startsWith("香")) {
                cityString = (address.substring(0, address.indexOf("港")));
            }
            if (address.startsWith("澳")) {
                cityString = (address.substring(0, address.indexOf("门")));
            }
            if (address.indexOf("省") != -1) {
                cityString = (address.substring(address.indexOf("省") + 1, address.indexOf("市")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityString;
    }

    public int getWeatherByCity(String cityString) {
        int r = 0;
        String today_templow = null, today_temphigh = null;
        String today_conditon = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        synchronized (mState) {
            try {
                // today forecast
                URL url = new URL("http://php.weather.sina.com.cn/xml.php?city="
                        + URLEncoder.encode(cityString, "gb2312")
                        + "&password=DJOYnieT8234jlsK&day=0");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                is = connection.getInputStream();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dombuilder = factory.newDocumentBuilder();
                Document doc = null;
                doc = dombuilder.parse(is);
                Element element = doc.getDocumentElement();

                NodeList Profiles = element.getChildNodes();

                if ((Profiles != null) && Profiles.getLength() > 1) {
                    for (int i = 0; i < Profiles.getLength(); i++) {
                        Node weather = Profiles.item(i);
                        if (weather.getNodeType() == Node.ELEMENT_NODE) {
                            for (Node node = weather.getFirstChild(); node != null; node = node
                                    .getNextSibling()) {
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    if (node.getNodeName().equals("figure1")) {
                                        today_conditon = node.getFirstChild().getNodeValue();
                                        String chString = changeWeatherToChinese(today_conditon);
                                        mWeatherDetail = chString;
                                    }
                                    if (node.getNodeName().equals("temperature1")) {
                                        today_temphigh = node.getFirstChild().getNodeValue();
                                    }
                                    if (node.getNodeName().equals("temperature2")) {
                                        today_templow = node.getFirstChild().getNodeValue();
                                    }

                                    if (today_conditon != null && today_temphigh != null
                                            && today_templow != null) {
                                        // TODO
                                        mTemperature = today_templow
                                                + mResources.getString(R.string.temp_degree) + "~"
                                                + today_temphigh
                                                + mResources.getString(R.string.temp_degree);
                                        break;
                                    } else {
                                    }
                                }
                            }
                        }
                    }
                } else {
                }

            } catch (Exception e) {
                r = -1;
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (is != null)
                        is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return r;
    }

    public String changeWeatherToChinese(String engString) {
        if (engString.equals("qing"))
            return mResources.getString(R.string.qing);
        if (engString.equals("duoyun")) {
            return mResources.getString(R.string.duoyun);
        }
        if (engString.equals("dafeng"))
            return mResources.getString(R.string.dafeng);
        if (engString.equals("ying")) {
            return mResources.getString(R.string.ying);
        }
        if (engString.equals("zhenyu")) {
            return mResources.getString(R.string.zhenyu);
        }
        if (engString.equals("leizhenyu")) {
            return mResources.getString(R.string.leizhenyu);
        }
        if (engString.equals("binbao_leizhenyu")) {
            return mResources.getString(R.string.binbao_leizhenyu);
        }
        if (engString.equals("yujiaxue")) {
            return mResources.getString(R.string.yujiaxue);
        }
        if (engString.equals("xiaoyu")) {
            return mResources.getString(R.string.xiaoyu);
        }
        if (engString.equals("zhongyu")) {
            return mResources.getString(R.string.zhongyu);
        }
        if (engString.equals("dayu")) {
            return mResources.getString(R.string.dayu);
        }
        if (engString.equals("dongyu")) {
            return mResources.getString(R.string.dongyu);
        }
        if (engString.equals("baoyu")) {
            return mResources.getString(R.string.baoyu);
        }
        if (engString.equals("dabaoyu")) {
            return mResources.getString(R.string.dabaoyu);
        }
        if (engString.equals("te_dabaoyu")) {
            return mResources.getString(R.string.te_dabaoyu);
        }
        if (engString.equals("xiaoxue")) {
            return mResources.getString(R.string.xiaoxue);
        }
        if (engString.equals("zhongxue")) {
            return mResources.getString(R.string.zhongxue);
        }
        if (engString.equals("daxue")) {
            return mResources.getString(R.string.daxue);
        }
        if (engString.equals("baoxue")) {
            return mResources.getString(R.string.baoxue);
        }
        if (engString.equals("shachengbao")) {
            return mResources.getString(R.string.shachengbao);
        }
        if (engString.equals("qiang_shachengbao")) {
            return mResources.getString(R.string.qiang_shachengbao);
        }
        if (engString.equals("wu")) {
            return mResources.getString(R.string.wu);
        }
        if (engString.equals("fuchen")) {
            return mResources.getString(R.string.fuchen);
        }
        if (engString.equals("yangsha")) {
            return mResources.getString(R.string.yangsha);
        }
        if (engString.equals("mai")) {
            return mResources.getString(R.string.mai);
        }
        if (engString.equals("zhenxue")) {
            return mResources.getString(R.string.zhenxue);
        }

        return "";
    }

    private String filterSuffix(String cityName) {
        if (cityName.endsWith(mResources.getString(R.string.string_shi))) {
            if (cityName.subSequence(0, cityName.length() - 1).equals(
                    mResources.getString(R.string.string_sha))) {
                return cityName; // 沙市
            } else {
                return cityName.substring(0, cityName.length() - 1);
            }
        } else if (cityName.endsWith(mResources.getString(R.string.string_sheng))) {
            return cityName.substring(0, cityName.length() - 1);
        } else if (cityName.endsWith(mResources.getString(R.string.string_qu))) {
            return cityName.substring(0, cityName.length() - 1);
        } else if (cityName.endsWith(mResources.getString(R.string.string_xian))) {
            if (cityName.length() == 2) {
                return cityName;
            } else {
                return cityName.substring(0, cityName.length() - 1);
            }
        } else {
            return cityName;
        }
    }

    private void searchWeather(String cityString) {
        String weatherCity = null;
        String today_templow = null, today_temphigh = null;
        String today_conditon = null;
        HttpURLConnection connection = null;
        InputStream isInputStream = null;
        int r = 0;
        synchronized (mState) {
            try {
                weatherCity = filterSuffix(cityString);
                mCity = weatherCity;

                if (mCity == "") {
                    mWeatherDetail = "";
                    mTemperature = "";
                    return;
                }

                URL url = new URL("http://php.weather.sina.com.cn/xml.php?city="
                        + URLEncoder.encode(weatherCity, "gb2312")
                        + "&password=DJOYnieT8234jlsK&day=0");
                Log.d(TAG, "weather search: " + url.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                isInputStream = connection.getInputStream();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder domBuilder = factory.newDocumentBuilder();
                Document doc = null;
                doc = domBuilder.parse(isInputStream);
                org.w3c.dom.Element element = doc.getDocumentElement();

                NodeList profilesList = element.getChildNodes();
                if ((profilesList != null) && (profilesList.getLength() > 1)) {

                    for (int i = 0; i < profilesList.getLength(); i++) {
                        Node weather = profilesList.item(i);

                        if (weather.getNodeType() == Node.ELEMENT_NODE) {
                            for (Node node = weather.getFirstChild(); node != null; node = node
                                    .getNextSibling()) {
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    if (node.getNodeName().equals("figure1")) {
                                        today_conditon = node.getFirstChild().getNodeValue();
                                        String chString = changeWeatherToChinese(today_conditon);
                                        mWeatherDetail = chString;
                                    }
                                    if (node.getNodeName().equals("temperature1")) {
                                        today_temphigh = node.getFirstChild().getNodeValue();
                                    }
                                    if (node.getNodeName().equals("temperature2")) {
                                        today_templow = node.getFirstChild().getNodeValue();
                                    }

                                    if (today_conditon != null && today_temphigh != null
                                            && today_templow != null) {
                                        // TODO
                                        mTemperature = today_templow
                                                + mResources.getString(R.string.temp_degree) + "~"
                                                + today_temphigh
                                                + mResources.getString(R.string.temp_degree);
                                        break;
                                    } else {
                                    }
                                }
                            }
                        }
                    }
                } else {
                    mWeatherDetail = "";
                    mTemperature = mResources.getString(R.string.city_weather_error);
                }
            } catch (Exception e) {
                // TODO: handle exception
                r = -1;
                mWeatherDetail = "";
                mTemperature = mResources.getString(R.string.city_weather_error);
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (isInputStream != null) {
                        isInputStream.close();
                    }
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }
        }
    }

    private String getUserCity() {
        String cityString = null;
        try {
            cityString = Settings.System.getString(mContext.getContentResolver(), "city");
        } catch (Exception e) {
            Log.e(TAG, "Can't get the use set city");
        }
        return cityString;
    }

    private void captureWeatherFromInternet() {
        String userCityString = getUserCity();
        if ((userCityString == null)) {
            Log.d(TAG, "#### set city by ip ####");
            String localIPString = getCityIP();
            String cityString = getCityByIp(localIPString);
            mCity = cityString;
            Settings.System.putString(mContext.getContentResolver(), "city", mCity);
            getWeatherByCity(cityString);

        } else {
            searchWeather(userCityString);
        }

        if (!mHandler.hasMessages(NonUIHandler.MSG_ON_WEATHER_INFO_CHANGED)) {
            mHandler.obtainMessage(NonUIHandler.MSG_ON_WEATHER_INFO_CHANGED, new String[] {
                    mWeatherDetail, mTemperature
            }).sendToTarget();
        }
    }
    
    private Drawable getWeatherIcon(String key){
        return null;
    }
}
