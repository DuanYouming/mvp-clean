package com.foxconn.bandon.weather;

import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.weather.model.IWeatherDataSource;
import com.foxconn.bandon.weather.model.LocationInfo;
import com.foxconn.bandon.weather.model.WeatherRepository;

public class GetLocation extends UseCase<GetLocation.RequestValue, GetLocation.ResponseValue> {

    private WeatherRepository repository;

    GetLocation(WeatherRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        IWeatherDataSource.LocationInfoCallback callback = new WeatherRepository.LocationInfoCallback() {
            @Override
            public void onSuccess(LocationInfo info) {
                getUseCaseCallback().onSuccess(new ResponseValue(info));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getLocation(callback);

    }

    public static final class RequestValue implements UseCase.RequestValues {

    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private LocationInfo info;

        public ResponseValue(LocationInfo info) {
            this.info = info;
        }

        public LocationInfo getInfo() {
            return info;
        }
    }
}
