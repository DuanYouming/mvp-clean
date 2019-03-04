package com.foxconn.bandon.setting.wifi.model;

import java.util.List;

public interface IWifiDeviceDataSource {

    interface GetDevicesCallback {
        void onSuccess(List<WifiDevice> devices);

        void onFailure();
    }

    void getDevices(GetDevicesCallback callback);

    void saveDevices(List<WifiDevice> devices);

}
