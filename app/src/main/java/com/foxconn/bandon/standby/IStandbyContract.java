package com.foxconn.bandon.standby;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.weather.model.WeatherInfo;

public interface IStandbyContract {


    interface Presenter extends BasePresenter {
        void registCallback();

        void unregistCallback();

        void getColor(int area, Bitmap bitmap, RectF rectF);

    }

    interface View extends BaseView<Presenter> {

        void updateWeatherInfo(WeatherInfo info);

        void updateColor(int area, int color);

        void loopMessage(GTMessage message);

    }
}
