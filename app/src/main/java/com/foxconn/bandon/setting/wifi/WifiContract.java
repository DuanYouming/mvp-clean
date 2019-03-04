package com.foxconn.bandon.setting.wifi;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;

import java.util.List;

public interface WifiContract {

    interface View extends BaseView<Presenter> {

        void notifyDataChanged(List<WifiDevice> devices);

        void setSwitchBarState(boolean isChecked);

        void showDevicesList();

        void hideDevicesList();

        void showDialog();

        void hideDialog();

        void setDialogMessage(String message);
    }

    interface Presenter extends BasePresenter {
        void getWifiDevices();

        void saveWifiDevices(List<WifiDevice> devices);

        void startScan();

        void setWifiState(boolean state);

        void addCallback();

        void removeCallback();

        void startConnecting(WifiDevice device);
    }
}
