package com.foxconn.bandon.setting.wifi.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.foxconn.bandon.utils.LogUtils;

public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = WifiReceiver.class.getSimpleName();
    private static final String ACTION_RSSI_CHANGED = WifiManager.RSSI_CHANGED_ACTION;
    private static final String ACTION_NETWORK_STATE_CHANGED = WifiManager.NETWORK_STATE_CHANGED_ACTION;
    private static final String ACTION_SCAN_RESULTS_AVAILABLE = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
    private static final String ACTION_WIFI_STATE_CHANGED = WifiManager.WIFI_STATE_CHANGED_ACTION;
    //動態註冊才能收到此廣播
    private static final String ACTION_CONNECTIVITY = ConnectivityManager.CONNECTIVITY_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) {
            LogUtils.d(TAG, "intent is null");
            return;
        }
        String action = intent.getAction();
        if (null == action) {
            LogUtils.d(TAG, "action is null");
            return;
        }
        switch (action) {
            case ACTION_RSSI_CHANGED:
                BWifiManager.getInstance().onRssiChanged(intent);
                break;
            case ACTION_SCAN_RESULTS_AVAILABLE:
                BWifiManager.getInstance().onScanResults(intent);
                break;
            case ACTION_NETWORK_STATE_CHANGED:
                BWifiManager.getInstance().onNetworkChanged(intent);
                break;
            case ACTION_WIFI_STATE_CHANGED:
                BWifiManager.getInstance().onStateChanged(intent);
                break;
            case ACTION_CONNECTIVITY:
                BWifiManager.getInstance().onConnectivityChanged(intent);
                break;
            default:
                break;

        }
    }

}
