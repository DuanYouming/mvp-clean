package com.foxconn.bandon.weather;

import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.weather.model.IWeatherDataSource;
import com.foxconn.bandon.weather.model.WeatherInfo;
import com.foxconn.bandon.weather.model.WeatherRepository;

public class GetWeather extends UseCase<GetWeather.RequestValue, GetWeather.ResponseValue> {

    private WeatherRepository repository;

    GetWeather(WeatherRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        IWeatherDataSource.WeatherInfoCallback callback = new WeatherRepository.WeatherInfoCallback() {
            @Override
            public void onSuccess(WeatherInfo info) {
                getUseCaseCallback().onSuccess(new ResponseValue(info));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getWeather(requestValues.getLocation(), callback);

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String location;

        public RequestValue(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private WeatherInfo info;

        public ResponseValue(WeatherInfo info) {
            this.info = info;
        }

        public WeatherInfo getInfo() {
            return info;
        }
    }
}
