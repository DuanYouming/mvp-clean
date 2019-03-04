package com.foxconn.bandon.setting.wifi.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import com.foxconn.bandon.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class BWifiManager {
    private static final String TAG = BWifiManager.class.getSimpleName();
    private static volatile BWifiManager INSTANCE;
    private List<Callback> mCallbacks = new ArrayList<>();
    private WifiReceiver receiver;

    private BWifiManager() {
        receiver = new WifiReceiver();
    }

    public static BWifiManager getInstance() {
        if (null == INSTANCE) {
            synchronized (BWifiManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new BWifiManager();
                }
            }
        }
        return INSTANCE;
    }


    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(receiver);
    }

    public void addCallback(Callback callback) {
        LogUtils.d(TAG, "addOnReceiveCallback:" + callback.toString());
        mCallbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        LogUtils.d(TAG, "removeOnReceiverCallback:" + callback.toString());
        mCallbacks.remove(callback);
    }

    protected void onRssiChanged(Intent intent) {
        if (null != mCallbacks && mCallbacks.size() > 0) {
            for (Callback callback : mCallbacks) {
                callback.onRssiChanged(intent);
            }
        }
    }

    protected void onScanResults(Intent intent) {
        if (null != mCallbacks && mCallbacks.size() > 0) {
            for (Callback callback : mCallbacks) {
                callback.onScanResults(intent);
            }
        }
    }

    protected void onNetworkChanged(Intent intent) {
        if (null != mCallbacks && mCallbacks.size() > 0) {
            for (Callback callback : mCallbacks) {
                callback.onNetworkChanged(intent);
            }
        }
    }


    protected void onStateChanged(Intent intent) {
        if (null != mCallbacks && mCallbacks.size() > 0) {
            for (Callback callback : mCallbacks) {
                callback.onStateChanged(intent);
            }
        }
    }

    protected void onConnectivityChanged(Intent intent) {
        if (null != mCallbacks && mCallbacks.size() > 0) {
            for (Callback callback : mCallbacks) {
                callback.onConnectivityChanged(intent);
            }
        }
    }

    public interface Callback {

        void onRssiChanged(Intent intent);

        void onScanResults(Intent intent);

        void onNetworkChanged(Intent intent);

        void onStateChanged(Intent intent);

        void onConnectivityChanged(Intent intent);
    }

}
