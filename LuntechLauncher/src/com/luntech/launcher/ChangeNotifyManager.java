
package com.luntech.launcher;

import java.util.ArrayList;
import java.util.List;

public class ChangeNotifyManager {

    private static ChangeNotifyManager sChangeNotifyManager = new ChangeNotifyManager();

    private List<IWeatherChangeListener> mWeatherChangeListenerList = new ArrayList<IWeatherChangeListener>();
    private List<IUsbMountListener> mUsbMountListenerList = new ArrayList<IUsbMountListener>();
    private List<INetworkStatusListener> mNetworkStatusListenerList = new ArrayList<INetworkStatusListener>();
    private List<ICityChangeListener> mCityChangeListenerList = new ArrayList<ICityChangeListener>();

    private ChangeNotifyManager() {

    }

    public static ChangeNotifyManager getInstance() {
        if (sChangeNotifyManager == null) {
            return new ChangeNotifyManager();
        } else {
            return sChangeNotifyManager;
        }
    }

    public void registerWeatherChangeListener(IWeatherChangeListener listener) {
        mWeatherChangeListenerList.add(listener);
    }

    public void unregisterWeatherChangeListener(IWeatherChangeListener listener) {
        mWeatherChangeListenerList.remove(listener);
    }

    public void notifyWetherChange(int low, int heigh) {
        for (IWeatherChangeListener listener : mWeatherChangeListenerList) {
            listener.weatherChange(low, heigh);
        }
    }

    public void registerUsbMountListener(IUsbMountListener listener) {
        mUsbMountListenerList.add(listener);
    }

    public void unregisterUsbMountListener(IUsbMountListener listener) {
        mUsbMountListenerList.remove(listener);
    }

    public void notifyMountUsbChange(int status) {
        for (IUsbMountListener listener : mUsbMountListenerList) {
            if (status == 0) {
                listener.mount();
            } else if (status == 1) {
                listener.unMount();
            }
        }
    }

    public void registerNetworkStatusListener(INetworkStatusListener listener) {
        mNetworkStatusListenerList.add(listener);
    }

    public void unregisterNetworkStatusListener(INetworkStatusListener listener) {
        mNetworkStatusListenerList.remove(listener);
    }

    public void notifyNetworkChange(int status) {
        for (INetworkStatusListener listener : mNetworkStatusListenerList) {
            listener.networkChenge(status);
        }
    }

    public void registerCityChangeListener(ICityChangeListener listener) {
        mCityChangeListenerList.add(listener);
    }

    public void unregisterCityChangeListener(ICityChangeListener listener) {
        mCityChangeListenerList.remove(listener);
    }

    public void notifyCityChange(String name) {
        for (ICityChangeListener listener : mCityChangeListenerList) {
            listener.cityChenge(name);
        }
    }
}
