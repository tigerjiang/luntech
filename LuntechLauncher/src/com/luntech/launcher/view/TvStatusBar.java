
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
import android.net.Uri;
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
import android.view.View;
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
import com.luntech.launcher.WeatherForm;

import org.json.JSONException;
import org.json.JSONObject;
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
    private int mTryCount = 10;
    private boolean mIsGetWeather;
    private boolean mIsInternetConnected;

    private String mCity;
    private String mWeatherDetail;
    private String mTemperature;
    private TimeManager mTimeManager;
    private Context mContext;
    private TvStatus mState;
    private ConnectivityManager mConnectivityManager;

    private Resources mResources;

    private long mDelayTime = 5 * 1000;// 3 * 60 * 1000; // 3min
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
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
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
                            mIsInternetConnected = true;
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
                    Log.d(TAG, "weather info " + weatherInfo.toString());
                    synchronized (mState) {
                        Drawable weatherIcon = changeWeatherToIcon(weatherInfo[0]);
                        mState.setWeatherIcon(weatherIcon);
                        mState.setTemperature(weatherInfo[1]);
                        if (mState.hasChanges(TvStatus.WEATHER_INFO)) {
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
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Uri uri = intent.getData();
                String path = uri == null ? "" : uri.getPath();
                mUsbStatusView.setVisibility(View.VISIBLE);
                if (DEBUG)
                    Log.d(TAG, "action: " + action + " path: " + path);
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                mUsbStatusView.setVisibility(View.GONE);
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

    public Drawable changeWeatherToIcon(String engString) {
        if (engString.equals(mResources.getString(R.string.qing))) {
            return mResources.getDrawable(R.drawable.ic_weather_qing);
        }
        else if (engString.equals(mResources.getString(R.string.duoyun))) {
            return mResources.getDrawable(R.drawable.ic_weather_duoyun);
        }
        else if (engString.equals(mResources.getString(R.string.ying))) {
            return mResources.getDrawable(R.drawable.ic_weather_ying);
        }
        if (engString.equals(mResources.getString(R.string.zhenyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.leizhenyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_leizhenyu);
        }
        else if (engString.equals(mResources.getString(R.string.yujiaxue))) {
            return mResources.getDrawable(R.drawable.ic_weather_yujiaxue);
        }
        else if (engString.equals(mResources.getString(R.string.xiaoyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_xiaoyu);
        }
        else if (engString.equals(mResources.getString(R.string.zhongyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.dayu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.dongyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.baoyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.dabaoyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.te_dabaoyu))) {
            return mResources.getDrawable(R.drawable.ic_weather_dayu);
        }
        else if (engString.equals(mResources.getString(R.string.xiaoxue))
                || engString.equals(mResources.getString(R.string.zhongxue))
                || engString.equals(mResources.getString(R.string.daxue))
                || engString.equals(mResources.getString(R.string.baoxue))
                || engString.equals(mResources.getString(R.string.zhenxue))
                || engString.equals(mResources.getString(R.string.baoxue))
                || engString.equals(mResources.getString(R.string.xue))) {
            return mResources.getDrawable(R.drawable.ic_weather_xue);

        }
        else if (engString.equals(mResources.getString(R.string.qiang_shachengbao))
                || engString.equals(mResources.getString(R.string.wu))
                || engString.equals(mResources.getString(R.string.fuchen))
                || engString.equals(mResources.getString(R.string.yangsha))) {
            return mResources.getDrawable(R.drawable.ic_weather_wu);
        }
        return null;
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
        int r = 0;
        synchronized (mState) {
            weatherCity = filterSuffix(cityString);
            mCity = weatherCity;
            if (mCity == "") {
                mWeatherDetail = "";
                mTemperature = "";
                return;
            }
            Log.d(TAG, "mCity" + mCity + "   " + cityString);
            String url = "http://apis.baidu.com/apistore/weatherservice/cityname";
            String httpArg = "cityname=" + mCity;
            String jasonResult = requestWeather(url, httpArg);
            try {
                JSONObject jb = new JSONObject(jasonResult);
                Log.d(TAG, "weather " + jasonResult);
                String errNum = jb.getString("errNum");
                String errMessage = jb.getString("errMsg");
                JSONObject jb1 = jb.getJSONObject("retData");
                WeatherForm weather = new WeatherForm();
                weather.setTemp(jb1.getString("l_tmp") + "-" + jb1.getString("h_tmp"));
                weather.setWeather(jb1.getString("weather"));
                weather.setDdate(jb1.getString("date"));
                weather.setName(jb1.getString("city"));
                weather.setId(jb1.getString("citycode"));
                mIsGetWeather = true;
                mWeatherDetail = weather.getWeather();
                mTemperature = weather.getTemp();
                Log.d(TAG, "weather " + weather.toString());
                if (!mHandler.hasMessages(NonUIHandler.MSG_ON_WEATHER_INFO_CHANGED)) {
                    mHandler.obtainMessage(NonUIHandler.MSG_ON_WEATHER_INFO_CHANGED, new String[] {
                            mWeatherDetail, mTemperature
                    }).sendToTarget();
                }
            } catch (JSONException e) {

                mIsGetWeather = false;
                e.printStackTrace();
            } catch (Exception e) {
                mIsGetWeather = false;
            }
            if (!mIsGetWeather) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        captureWeatherFromInternet();
                    }
                }, mDelayTime);
            }

        }
    }

    private String getUserCity() {
        String cityString = "青岛";
        try {
            cityString = Settings.System.getString(mContext.getContentResolver(), "city");
        } catch (Exception e) {
            Log.e(TAG, "Can't get the use set city");
        }
        return cityString;
    }

    private void captureWeatherFromInternet() {
        if (mIsInternetConnected && !mIsGetWeather && mTryCount <= 10) {
            mTryCount++;
            String userCityString = getUserCity();
            searchWeather(userCityString);
        } else {
            Log.d(TAG, "cant get weather due to " + "mIsInternetConnected " + mIsInternetConnected
                    + "mIsGetWeather  " + mIsGetWeather + " mTryCount " + mTryCount);
        }
    }

    /**
     * @param urlAll :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String requestWeather(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "ad30c50fa431bbcacdd06c03ad3e9542");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Drawable getWeatherIcon(String key) {
        return null;
    }
}
