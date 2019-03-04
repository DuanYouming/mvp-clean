package com.foxconn.bandon.weather.model;

import com.foxconn.bandon.weather.model.LocationInfo;
import com.foxconn.bandon.weather.model.WeatherInfo;

public interface IWeatherDataSource {

    interface LocationInfoCallback {

        void onSuccess(LocationInfo info);

        void onFailure();
    }

    interface WeatherInfoCallback {
        void onSuccess(WeatherInfo weatherInfo);

        void onFailure();
    }

    void getWeather(String location, WeatherInfoCallback callback);

    void getLocation(LocationInfoCallback callback);

}
