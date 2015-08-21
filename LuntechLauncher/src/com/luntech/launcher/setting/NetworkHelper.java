
package com.luntech.launcher.setting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import android.net.ethernet.EthernetManager;

/**
 * Network routines.
 * 
 */
public class NetworkHelper {
    // log variables
    private static final String TAG = "NetworkHelper";
    private static boolean DEBUG = false;

    // WLAN
    private static final String WLAN = "wlan";

    public static final int WIRED_CONNECTED = 0;
    public static final int WIRED_NEEDS_CONFIGURATION = 1;
    public static final int WIRED_DISCONNECTED = 2;

    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private EthernetManager mEthernetManager;

    public NetworkHelper(Context context) {
        mConnectivityManager = (ConnectivityManager) context.
                getSystemService(
                Context.CONNECTIVITY_SERVICE);

        mWifiManager = (WifiManager) context
                .getSystemService(
                Context.WIFI_SERVICE);

        mEthernetManager = EthernetManager.getInstance();
    }

    /**
     * @return local IP address
     */
    public static String getLocalIpAddress() {
        final StringBuilder sb = new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                final NetworkInterface iface = en.nextElement();
                Log.i(TAG, "network interface: " + iface);
                for (Enumeration<InetAddress> enumIpAddr = iface
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        final String ip = inetAddress.getHostAddress().toString();
                        sb.append(iface.getDisplayName()).append(':').append(ip);
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "IP Address", ex);
        }
        return sb.toString();
    }

    /**
     * Disconnect WiFi
     */
    public void disconnectWiFi() {
        if (mWifiManager != null) {
            final List<WifiConfiguration> configs =
                    mWifiManager.getConfiguredNetworks();
            if (configs != null) {
                for (WifiConfiguration config : configs) {
                    if (DEBUG) {
                        Log.d(TAG, String.format("Removing Wi-Fi network \"%s\" (id: %d)",
                                config.SSID, config.networkId));
                    }
                    mWifiManager.removeNetwork(config.networkId);
                }
            }
            mWifiManager.disconnect();
            mWifiManager.setWifiEnabled(false);
            mEthernetManager.setEnabled(true);
        }

        mConnectivityManager.setNetworkPreference(ConnectivityManager.TYPE_ETHERNET);
    }

    public int getNetworkPreference() {
        return mConnectivityManager.getNetworkPreference();
    }

    public void disconnectEthernet() {
        mConnectivityManager.setNetworkPreference(ConnectivityManager.TYPE_WIFI);
        mEthernetManager.setEnabled(false);
        mWifiManager.setWifiEnabled(true);
    }

    /**
     * @return WiFi connection status
     */
    public boolean isWiFiConnected() {
        boolean isConnected = false;
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    /**
     * @return Ethernet connection status
     */
    public int isEthernetNetworkConnected() {
        NetworkInfo ethNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (DEBUG) {
            Log.d(TAG, "ethNetworkInfo: " + ethNetworkInfo + " ip adress: " + getLocalIpAddress());
        }
        String ipAddress = getLocalIpAddress();
        if (ethNetworkInfo != null) {
            if (ethNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return WIRED_CONNECTED;
                // if wireless is connected
            } else if (ethNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED
                    && ipAddress != null && ipAddress.contains(WLAN)) {
                return WIRED_NEEDS_CONFIGURATION;
            }
        }
        return WIRED_DISCONNECTED;
    }

    /**
     * @return WiFi Network ID
     */
    public CharSequence getWiFiNetworkId() {
        return mWifiManager.getConnectionInfo().getSSID();
    }
}
