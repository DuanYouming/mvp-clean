package com.foxconn.bandon.setting.wifi.presenter;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.foxconn.bandon.setting.wifi.WifiContract;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.setting.wifi.model.WifiDeviceRepository;
import com.foxconn.bandon.setting.wifi.util.BWifiManager;
import com.foxconn.bandon.setting.wifi.util.WifiUtils;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

import java.util.List;

public class WifiPresenter implements WifiContract.Presenter {
    private static final String TAG = WifiPresenter.class.getSimpleName();
    private static final int RECONNECTING_TIME = 6;
    private static final int ERROR = -1;
    private WifiManager mWifiManager;
    private UseCaseHandler mUseCaseHandler;
    private WifiContract.View mView;
    private WifiDeviceRepository mRepository;
    private WifiDevice mCurrent;
    private boolean isConnecting;
    private int mConnectedTime;

    public WifiPresenter(WifiManager mWifiManager, WifiContract.View view, WifiDeviceRepository repository) {
        this.mWifiManager = mWifiManager;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        this.mView = view;
        this.mRepository = repository;
        mView.setPresenter(this);
    }

    @Override
    public void startScan() {
        if (null != mWifiManager && mWifiManager.isWifiEnabled()) {
            mView.setSwitchBarState(true);
            mWifiManager.startScan();
        } else {
            mView.setSwitchBarState(false);
        }
    }

    @Override
    public void getWifiDevices() {
        GetDevices getDevices = new GetDevices(mRepository);
        mUseCaseHandler.execute(getDevices, null, new UseCase.UseCaseCallback<GetDevices.ResponseValues>() {
            @Override
            public void onSuccess(GetDevices.ResponseValues response) {
                List<WifiDevice> devices = response.getDevices();
                if (null != devices && devices.size() > 0) {
                    mView.notifyDataChanged(devices);
                }
            }
            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void saveWifiDevices(List<WifiDevice> devices) {
        SaveDevices save = new SaveDevices(mRepository);
        SaveDevices.RequestValues values = new SaveDevices.RequestValues(devices);
        mUseCaseHandler.execute(save, values, null);
    }

    @Override
    public void setWifiState(boolean state) {
        if (null != mWifiManager) {
            mWifiManager.setWifiEnabled(state);
            if (state) {
                getWifiDevices();
            }
        }
    }

    @Override
    public void startConnecting(@NonNull WifiDevice device) {
        mCurrent = device;
        isConnecting = true;
        mConnectedTime = RECONNECTING_TIME;

        boolean b = WifiUtils.disConnectCurrent(mWifiManager);
        LogUtils.d(TAG, "disConnectCurrent:" + b);
        final String security = WifiUtils.ConfigSec.getScanResultSecurity(device.getCapabilities());
        final WifiConfiguration config = WifiUtils.getWifiConfiguration(mWifiManager, device, security);
        boolean success;
        if (config == null) {
            success = WifiUtils.connectToNewNetwork(mWifiManager, device);
        } else {
            success = WifiUtils.connectToConfiguredNetwork(mWifiManager, config);
        }
        LogUtils.d(TAG, "startConnecting:" + success);
    }

    @Override
    public void addCallback() {
        BWifiManager.getInstance().addCallback(callback);
    }

    @Override
    public void removeCallback() {
        BWifiManager.getInstance().removeCallback(callback);
    }

    private BWifiManager.Callback callback = new BWifiManager.Callback() {
        @Override
        public void onRssiChanged(Intent intent) {

        }

        @Override
        public void onNetworkChanged(Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = info.getState();
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
            if (state.equals(NetworkInfo.State.CONNECTED)) {
                if (null != bssid && null != mCurrent && TextUtils.equals(mCurrent.getBSSID(), bssid)) {
                    LogUtils.d(TAG, "Connected ssid:" + wifiInfo.getSSID());
                    mWifiManager.startScan();
                    mView.hideDialog();
                }
            } else if (state.equals(NetworkInfo.State.DISCONNECTED)) {
                if (isConnecting) {
                    mConnectedTime--;
                    if (mConnectedTime == 0) {
                        LogUtils.d(TAG, "password is error");
                        mView.setDialogMessage("密碼錯誤！");
                        isConnecting = false;
                        final String security = WifiUtils.ConfigSec.getScanResultSecurity(mCurrent.getCapabilities());
                        final WifiConfiguration config = WifiUtils.getWifiConfiguration(mWifiManager, mCurrent, security);
                        if (null != config) {
                            mWifiManager.removeNetwork(config.networkId);
                        }
                    }
                }
            }
        }

        @Override
        public void onScanResults(Intent intent) {
            List<ScanResult> results = mWifiManager.getScanResults();
            List<WifiDevice> newResults = WifiUtils.filtrate(mWifiManager, results);
            if (newResults.size() > 0) {
                mView.notifyDataChanged(newResults);
                saveWifiDevices(newResults);
            }
        }

        @Override
        public void onStateChanged(Intent intent) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, ERROR);
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED:
                    mView.hideDevicesList();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    mView.showDevicesList();
                    WifiUtils.autoConnect(mWifiManager);
                    break;
            }
        }

        @Override
        public void onConnectivityChanged(Intent intent) {

        }
    };

}
