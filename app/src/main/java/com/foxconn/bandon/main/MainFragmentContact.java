package com.foxconn.bandon.main;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.weather.model.WeatherInfo;

public interface MainFragmentContact {

    interface Presenter extends BasePresenter {

        void addCallback();

        void removeCallback();

        void start();

        void registerDevice();

    }

    interface View extends BaseView<Presenter> {

        void updateWifiIcon();

        void wifiClose();

        void updateClockIcon(boolean isShow);

        void updateWeatherView(WeatherInfo info);

        void loopMessage(GTMessage message);
    }
}
