
package com.luntech.launcher.view;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.lang.ref.WeakReference;


public class TvStatusBar extends RelativeLayout implements INetworkStatusListener,
        IUsbMountListener, IWeatherChangeListener ,OnTimeChangedListener, OnFormatChangedListener{
    private static final String TAG = "TvStatusBar";
    private static final boolean DEBUG = true;
    private TextView mTimeView;
    private TextView mTemperatureView;
    private ImageView mWifiStatusView;
    private ImageView mUsbStatusView;
    private ImageView mWeatherStatusView;
    private TimeManager mTimeManager;
    private Context mContext;
    private TvStatus mState;
    private ConnectivityManager mConnectivityManager;
    
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
        mDrawableNetworkOffline = mContext.getResources().getDrawable(R.drawable.ic_statusbar_ununited_network);
        mDrawableNetworkEthernet = mContext.getResources().getDrawable(R.drawable.ic_statusbar_wired_network);
        mDrawableNetworkWifi = mContext.getResources().getDrawable(R.drawable.fullscreen_gp_wifi_signal);
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
        mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTimeManager = TimeManager.getInstance(context);

        mTimeManager.addOnTimeChangedListener(this);
        mTimeManager.addOnFormatChangedListener(this);
        
     // register content observers
        final ContentResolver cr = context.getContentResolver();
        // observe PPPoE be enabled or not
        cr.registerContentObserver(
                Settings.System.getUriFor(PPPOE_ENABLED), false,
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
        cr.registerContentObserver(
                Settings.System.getUriFor(PPPOE_CONNECTED), false,
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
                    final NetworkInfo netInfo = (NetworkInfo)msg.obj;
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
                                // WifiManager uses a handler thread, so we don't want
                                // to keep the reference around forever
                                final WifiManager wifiManager = (WifiManager)
                                        mContext.getSystemService(Context.WIFI_SERVICE);
                                if (wifiManager != null) {
                                    final WifiInfo info = wifiManager.getConnectionInfo();
                                    rssi = info.getRssi();
                                }
                                mState.setNetworkIcon(networkIcon);
                                mState.setNetworkRSSI(rssi);
                            }
                        }
                        if ( mState.hasChanges(TvStatus.NETWORK_INFO)) {
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
                            time = ((Long)msg.obj).longValue();
                        } else {
                            time = System.currentTimeMillis();
                        }

                        synchronized (mState) {
                            mState.setDate(mTimeManager.formatDateTime(time,
                                    TimeManager.DateTimeFormat.DAY_OF_WEEK_MONTH_DAY));
                            mState.setTime(mTimeManager.formatDateTime(time,
                                    TimeManager.DateTimeFormat.TIME_SHORT));
                            if ( mState.hasChanges(TvStatus.DATE_TIME_INFO)) {
                                notifyStateChange();
                            }
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
        mHandler.obtainMessage(NonUIHandler.MSG_UPDATE_DATE_TIME,
                new Long(time)).sendToTarget();
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
           if (DEBUG) Log.d(TAG, "handleMessage msg=" + msg);
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
}
