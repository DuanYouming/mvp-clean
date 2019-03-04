package com.foxconn.bandon.main.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.foxconn.bandon.device.DeviceManager;
import com.foxconn.bandon.device.RegisterDevice;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.main.MainFragmentContact;
import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.presenter.GetClocks;
import com.foxconn.bandon.setting.wifi.util.BWifiManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.foxconn.bandon.weather.manager.WeatherManager;
import com.foxconn.bandon.weather.model.WeatherInfo;

import java.util.List;

public class MainFragmentPresenter implements MainFragmentContact.Presenter {
    private static final String TAG = MainFragmentPresenter.class.getSimpleName();

    private MainFragmentContact.View mView;
    private ClockRepository mClockRepository;
    private UseCaseHandler mUseCaseHandler;

    public MainFragmentPresenter(MainFragmentContact.View view, ClockRepository clockRepository) {
        this.mView = view;
        this.mClockRepository = clockRepository;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        view.setPresenter(this);
    }

    @Override
    public void start() {
        getClocks();
        registerDevice();
    }

    @Override
    public void registerDevice() {
        if (PreferenceUtils.getBoolean(BandonApplication.getInstance(), null, Constant.KEY_DEVICE_REGISTER, false)) {
            return;
        }
        DeviceManager.getInstance().register(mUseCaseHandler, new UseCase.UseCaseCallback<RegisterDevice.ResponseValue>() {
            @Override
            public void onSuccess(RegisterDevice.ResponseValue response) {
                ResponseStr str = response.getResponse();
                LogUtils.d(TAG, "str:" + str.getInfo() + "  code:" + str.getCode());
                if (TextUtils.equals(DeviceManager.DEVICE_EXISTED, str.getInfo()) || TextUtils.equals(DeviceManager.REGISTER_SUCCESS, str.getInfo())) {
                    PreferenceUtils.setBoolean(BandonApplication.getInstance(), null, Constant.KEY_DEVICE_REGISTER, true);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void addCallback() {
        BWifiManager.getInstance().addCallback(wifiCallback);
        ClockManager.getInstance().addStateChangedListener(listener);
        WeatherManager.getInstance().registCallback(weatherCallback);
        GTMessageManager.getInstance().registerCallback(gtMessageCallback);
    }

    @Override
    public void removeCallback() {
        BWifiManager.getInstance().removeCallback(wifiCallback);
        ClockManager.getInstance().removeStateChangedListener(listener);
        WeatherManager.getInstance().unregistCallback(weatherCallback);
        GTMessageManager.getInstance().unregisterCallback(gtMessageCallback);
    }

    private BWifiManager.Callback wifiCallback = new BWifiManager.Callback() {
        @Override
        public void onRssiChanged(Intent intent) {
            mView.updateWifiIcon();
        }

        @Override
        public void onScanResults(Intent intent) {

        }

        @Override
        public void onNetworkChanged(Intent intent) {

        }

        @Override
        public void onStateChanged(Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                mView.wifiClose();
            } else {
                mView.updateWifiIcon();
            }
        }

        @Override
        public void onConnectivityChanged(Intent intent) {
            Context context = BandonApplication.getInstance();
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != manager) {
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (null != info && info.isAvailable()) {
                    String name = info.getTypeName();
                    LogUtils.d(TAG, "name:" + name);
                    WeatherManager.getInstance().getWeather(context);
                    registerDevice();
                }
            }
        }
    };

    private ClockManager.OnStateChangedListener listener = new ClockManager.OnStateChangedListener() {
        @Override
        public void OnStateChanged() {
            getClocks();
        }
    };

    private WeatherManager.Callback weatherCallback = new WeatherManager.Callback() {
        @Override
        public void updateWeatherInfo(WeatherInfo info) {
            mView.updateWeatherView(info);
        }
    };

    private void getClocks() {
        GetClocks getClocks = new GetClocks(mClockRepository);
        mUseCaseHandler.execute(getClocks, new GetClocks.RequestValues(), new UseCase.UseCaseCallback<GetClocks.ResponseValues>() {
            @Override
            public void onSuccess(GetClocks.ResponseValues response) {
                List<ClockBean> clocks = response.getClocks();
                if (null != clocks && clocks.size() > 0) {
                    for (ClockBean clock : clocks) {
                        if (clock.getEnable()) {
                            mView.updateClockIcon(true);
                            return;
                        }
                    }
                }
                mView.updateClockIcon(false);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private GTMessageManager.GTMessageCallback gtMessageCallback = new GTMessageManager.GTMessageCallback() {
        @Override
        public void loop(GTMessage message) {
            mView.loopMessage(message);
        }
    };

}
