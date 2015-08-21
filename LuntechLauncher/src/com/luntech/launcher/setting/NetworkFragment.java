/*
 * Copyright 2012 - Jamdeo
 */

package com.luntech.launcher.setting;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

public class NetworkFragment extends GenericFragment {

    // logger variables
    private static final String TAG = "FTE";
    private static boolean DEBUG = false;

    // actions to call appropriate Settings Activities
    private static final String ANDROID_WIFI_SETTINGS = "com.android.net.wifi.SETUP_WIFI_NETWORK";
    private static final String ACTION_ETHERNET_SETTINGS = "android.settings.ETHERNET_SETTINGS";

    // this boolean extra should be set to true as we are going to use Wizard for Wifi Setup
    private static final String EXTRA_IS_FIRST_RUN = "firstRun";

    private Button mButtonConfigureWiFi;
//    private Button mButtonConfigureWired;
    private Button mButtonSwitchToWiFi;
    private Button mButtonSwitchToWired;

    private ImageView mImageWiFi;
    private TextView mTextViewWiFiState;
    private ImageView mImageWired;
    private TextView mTextViewWired;

    private NetworkHelper mNetworkHelper;
    private ConnectionChangeReceiver connectionChangeReceiver;


    private static boolean sIsWired = true;
    /*
     * (non-Javadoc)
     * @see com.jamdeo.tv.fte.GenericFragment#initValues()
     */
    protected void initValues() {
        if (sIsWired) {
            mActionAreaId = R.layout.network_action_area_wired;
        } else {
            mActionAreaId = R.layout.network_action_area_wifi;
        }
        mBackgroundId = R.drawable.bg;
        mTitleStrId = R.string.network_title;
        mTipStrId = R.string.network_tip;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jamdeo.tv.fte.GenericFragment#onCreateView(android.view.LayoutInflater
     * , android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mNetworkHelper = new NetworkHelper(getActivity());
        mView =
                super.onCreateView(inflater, container, savedInstanceState);

        initViews();
        updateUI();

        connectionChangeReceiver = new ConnectionChangeReceiver();
        getActivity().registerReceiver(connectionChangeReceiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        return mView;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(connectionChangeReceiver);
        super.onDestroyView();
    }

    /**
     * Load UI for wired screen
     */
    private void loadWiredLayout() {
        loadUserArea(R.layout.network_action_area_wired);
    }

    /**
     * Load UI for WiFi screen
     */
    private void loadWiFiLayout() {
        loadUserArea(R.layout.network_action_area_wifi);
    }

    /**
     * Find and initialise views of area that are changed programmatically to
     * display network state
     */
    private void initViews() {
        if (sIsWired) {
            initViewsWired();
        } else {
            initViewsWifi();
        }
    }

    /**
     * Find and initialise views of Wired area that are changed programmatically
     * to display network state
     */
    private void initViewsWired() {
//        mButtonConfigureWired = (Button) mView.findViewById(R.id.button_configure_wired);
//        mButtonConfigureWired.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(ACTION_ETHERNET_SETTINGS);
//                startActivity(intent);
//            }
//        });

        mImageWired = (ImageView)
                mView.findViewById(R.id.imageview_wired);
        mTextViewWired = (TextView)
                mView.findViewById(R.id.textview_wired_state);
        mButtonSwitchToWiFi = (Button) mView.findViewById(R.id.button_switch_to_wifi);
        mButtonSwitchToWiFi.requestFocus();
        mButtonSwitchToWiFi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mNetworkHelper.disconnectEthernet();
                sIsWired = false;
                loadWiFiLayout();
                initViews();
                updateUI();
            }
        });
    }

    /**
     * Find and initialise views of WiFi area that are changed programmatically
     * to display network state
     */
    private void initViewsWifi() {
        mButtonConfigureWiFi = (Button) mView.findViewById(R.id.button_configure_wifi);
        mButtonConfigureWiFi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // start Wifi Setup Wizard - WifiSetupActivity in Settings
                Intent intent = new Intent(ANDROID_WIFI_SETTINGS);
                intent.putExtra(EXTRA_IS_FIRST_RUN, true);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Failed to start wifi settings.", e);
                }
            }
        });

        mImageWiFi = (ImageView) mView.findViewById(R.id.imageview_wifi);
        mTextViewWiFiState = (TextView)
                mView.findViewById(R.id.textview_wifi_state);

        mButtonSwitchToWired = (Button) mView.findViewById(R.id.button_switch_to_wired);
        mButtonSwitchToWired.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mNetworkHelper.disconnectWiFi();
                sIsWired = true;
                loadWiredLayout();
                initViews();
                updateUI();
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.jamdeo.tv.fte.GenericFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Show appropriate images and text depending on whether the connection is
     * on or off.
     */
    private void updateUI() {
        boolean isWiFiConnected = mNetworkHelper.isWiFiConnected();
        int isEthernetNetworkConnected = mNetworkHelper.isEthernetNetworkConnected();

        if (sIsWired) {
            updateEthernetControls(isEthernetNetworkConnected);
        } else {
            updateWiFiControls(isWiFiConnected);
            mButtonConfigureWiFi.requestFocus();
        }
    }

    /**
     * Update Ethernet-related UI elements.
     * 
     * @param isEthernetNetworkConnected
     */
    private void updateEthernetControls(int isEthernetNetworkConnected) {
        if (DEBUG) {
            Log.d(TAG, "isEthernetNetworkConnected: " + isEthernetNetworkConnected);
        }
        if (isEthernetNetworkConnected == NetworkHelper.WIRED_CONNECTED) {
            mImageWired.setImageResource(R.drawable.fte_icn_ethernet_connection);
            mTextViewWired.setText(R.string.str_connected);
//            mButtonConfigureWired.setVisibility(View.GONE);
        } else if (isEthernetNetworkConnected == NetworkHelper.WIRED_DISCONNECTED) {
            mImageWired.setImageResource(R.drawable.fte_icn_ethernet_no_connection);
            mTextViewWired.setText(R.string.str_not_detected);
//            mButtonConfigureWired.setVisibility(View.GONE);
        } else if (isEthernetNetworkConnected == NetworkHelper.WIRED_NEEDS_CONFIGURATION) {
            mImageWired.setImageResource(R.drawable.fte_icn_need_configuration);
            mTextViewWired.setText(R.string.str_needs_configuration);
//            mButtonConfigureWired.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update WiFi-related UI elements.
     * 
     * @param isWiFiConnected
     */
    private void updateWiFiControls(boolean isWiFiConnected) {
        if (isWiFiConnected) {
            mImageWiFi.setImageResource(R.drawable.fte_icn_wifi_connection);
            mTextViewWiFiState.setText(mNetworkHelper.getWiFiNetworkId());
        } else {
            mImageWiFi.setImageResource(R.drawable.fte_icn_wifi_no_connection);
            mTextViewWiFiState.setText(R.string.str_not_connected);
        }
    }

    /**
     * @author brltluza
     *
     */
    private class ConnectionChangeReceiver extends BroadcastReceiver
    {
        public ConnectionChangeReceiver() {
            super();
        }

        /* (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    }
    
    public static NetworkFragment newInstance(){
        NetworkFragment fragment = new NetworkFragment();
        return fragment;
    }
}
