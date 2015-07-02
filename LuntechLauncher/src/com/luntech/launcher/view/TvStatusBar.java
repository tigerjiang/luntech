
package com.luntech.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luntech.launcher.ChangeNotifyManager;
import com.luntech.launcher.INetworkStatusListener;
import com.luntech.launcher.IUsbMountListener;
import com.luntech.launcher.IWeatherChangeListener;
import com.luntech.launcher.R;

public class TvStatusBar extends RelativeLayout implements INetworkStatusListener,
        IUsbMountListener, IWeatherChangeListener {
    private TextView mTimeView;
    private TextView mTemperatureView;
    private ImageView mWifiStatusView;
    private ImageView mUsbStatusView;
    private ImageView mWeatherStatusView;

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
        LayoutInflater.from(context).inflate(R.layout.status_bar, this);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mTemperatureView = (TextView) findViewById(R.id.temperature_view);
        mWifiStatusView = (ImageView) findViewById(R.id.wifi_status);
        mUsbStatusView = (ImageView) findViewById(R.id.usb_status);
        mWeatherStatusView = (ImageView) findViewById(R.id.weather_status);
        registerListener();
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

}
