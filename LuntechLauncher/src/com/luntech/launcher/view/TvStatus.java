
package com.luntech.launcher.view;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

public class TvStatus {

    public static final int NETWORK_INFO = 0x1;
    public static final int DATE_TIME_INFO = 0x2;
    public static final int WEATHER_INFO = 0x4;
    public static final int ALL_INFO =
            NETWORK_INFO | DATE_TIME_INFO | WEATHER_INFO;
    private int mChanges;

    // Network info
    private int mNetworkType;
    private Drawable mNetworkIcon;
    private String mNetworkName;
    private int mNetworkRSSI;

    // Date Time info
    private String mDate;
    private String mTime;

    // Weather info
    private Drawable mWeatherIcon;
    private String mTemperature;

    public TvStatus() {
        mNetworkType = -1;
        mNetworkIcon = null;
        mNetworkName = null;
        mWeatherIcon = null;
        mNetworkRSSI = 0;
        mDate = null;
        mTime = null;
        mTemperature = null;

    }

    public void setNetworkType(int type) {
        if (mNetworkType != type) {
            mNetworkType = type;
            mChanges |= NETWORK_INFO;
        }
    }

    public int getNetworkType() {
        return mNetworkType;
    }

    public void setWeatherIcon(Drawable icon) {
        if (mWeatherIcon != icon) {
            mWeatherIcon = icon;
            mChanges |= WEATHER_INFO;
        }
    }

    public Drawable getWeatherIcon() {
        return mWeatherIcon;
    }

    public void setNetworkIcon(Drawable icon) {
        if (mNetworkIcon != icon) {
            mNetworkIcon = icon;
            mChanges |= NETWORK_INFO;
        }
    }

    public void setTemperature(String temperature) {
        if (mTemperature != null) {
            if (!mTemperature.equals(temperature)) {
                mTemperature = temperature;
                mChanges |= WEATHER_INFO;
            }
        } else {
            if (temperature != null) {
                mTemperature = temperature;
                mChanges |= WEATHER_INFO;
            }
        }
    }

    public String getTemperature() {
        return mTemperature;
    }

    public Drawable getNetworkIcon() {
        return mNetworkIcon;
    }

    public void setNetworkName(String name) {
        if (mNetworkName != null) {
            if (!mNetworkName.equals(name)) {
                mNetworkName = name;
                mChanges |= NETWORK_INFO;
            }
        } else {
            if (name != null) {
                mNetworkName = name;
                mChanges |= NETWORK_INFO;
            }
        }
    }

    public String getNetworkName() {
        return mNetworkName;
    }

    public void setNetworkRSSI(int rssi) {
        if (mNetworkRSSI != rssi) {
            mNetworkRSSI = rssi;
            mChanges |= NETWORK_INFO;
        }
    }

    public int getNetworkRSSI() {
        return mNetworkRSSI;
    }

    public void setDate(String date) {
        if (mDate != null) {
            if (!mDate.equals(date)) {
                mDate = date;
                mChanges |= DATE_TIME_INFO;
            }
        } else {
            if (date != null) {
                mDate = date;
                mChanges |= DATE_TIME_INFO;
            }
        }
    }

    public String getDate() {
        return mDate;
    }

    public void setTime(String time) {
        if (mTime != null) {
            if (!mTime.equals(time)) {
                mTime = time;
                mChanges |= DATE_TIME_INFO;
            }
        } else {
            if (time != null) {
                mTime = time;
                mChanges |= DATE_TIME_INFO;
            }
        }
    }

    public String getTime() {
        return mTime;
    }

    public boolean hasChanges() {
        return hasChanges(ALL_INFO);
    }

    public boolean hasChanges(int bits) {
        return (mChanges & bits) != 0;
    }

    public int getChanges() {
        return mChanges;
    }

    public void clearChanges() {
        clearChanges(ALL_INFO);
    }

    public void clearChanges(int bits) {
        mChanges &= ~bits;
    }

    public void setChanges() {
        setChanges(ALL_INFO);
    }

    public void setChanges(int bits) {
        mChanges |= bits;
    }

    @Override
    public String toString() {
        return "TvStatus [mChanges=" + mChanges + ", mTemperature=" + mTemperature + "]";
    }

    
}
