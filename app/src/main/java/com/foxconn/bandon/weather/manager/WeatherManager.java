package com.foxconn.bandon.weather.manager;

import android.content.Context;
import android.content.Intent;

import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.weather.WeatherService;
import com.foxconn.bandon.weather.model.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

public class WeatherManager {

    private static volatile WeatherManager instance;
    private List<Callback> callbacks = new ArrayList<>();
    private WeatherInfo info;

    private WeatherManager() {
    }

    public static WeatherManager getInstance() {
        if (null == instance) {
            synchronized (WeatherManager.class) {
                if (null == instance) {
                    instance = new WeatherManager();
                }
            }
        }
        return instance;
    }


    public void updateWeather(WeatherInfo info) {
        this.info = info;
        for (Callback callback : callbacks) {
            callback.updateWeatherInfo(info);
        }

    }

    public WeatherInfo getWeather() {
        if (null == info) {
            getWeather(BandonApplication.getInstance().getApplicationContext());
        }
        return info;
    }

    public void getWeather(Context context) {
        Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(WeatherService.ACTION_GET_WEATHER);
        context.startService(intent);
    }


    public void registCallback(Callback callback) {
        if (null != callback) {
            callbacks.add(callback);
        }
    }

    public void unregistCallback(Callback callback) {
        if (null != callback) {
            callbacks.remove(callback);
        }
    }

    public interface Callback {
        void updateWeatherInfo(WeatherInfo info);
    }
}


