package com.foxconn.bandon.weather.model;


import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository implements IWeatherDataSource {

    private AppExecutors mExecutors;
    private static volatile WeatherRepository INSTANCE;

    private WeatherRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static WeatherRepository getInstance(AppExecutors executors) {
        if (INSTANCE == null) {
            INSTANCE = new WeatherRepository(executors);
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
    }


    @Override
    public void getWeather(final String location, final WeatherInfoCallback callback) {
        Runnable runnable = () -> {
            Call<WeatherInfo> call = RetrofitUtils.getInstance().getRetrofitService().getWeather(Constant.WEATHER_KEY, location);
            call.enqueue(new Callback<WeatherInfo>() {
                @Override
                public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                    WeatherInfo info = response.body();
                    callback.onSuccess(info);
                }

                @Override
                public void onFailure(Call<WeatherInfo> call, Throwable t) {
                    callback.onFailure();
                }
            });
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getLocation(final LocationInfoCallback callback) {
        Runnable runnable = () -> {
            Call<LocationInfo> call = RetrofitUtils.getInstance().getRetrofitService().getLocation(Constant.IP_URL);
            call.enqueue(new Callback<LocationInfo>() {
                @Override
                public void onResponse(Call<LocationInfo> call, Response<LocationInfo> response) {
                    LocationInfo info = response.body();
                    callback.onSuccess(info);
                }

                @Override
                public void onFailure(Call<LocationInfo> call, Throwable t) {
                    callback.onFailure();
                }
            });
        };
        mExecutors.networkIO().execute(runnable);
    }
}
