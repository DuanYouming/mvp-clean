package com.foxconn.bandon.setting.wifi.presenter;

import com.foxconn.bandon.setting.wifi.model.IWifiDeviceDataSource;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.setting.wifi.model.WifiDeviceRepository;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetDevices extends UseCase<GetDevices.RequestValues, GetDevices.ResponseValues> {

    private WifiDeviceRepository mRepository;

    GetDevices(WifiDeviceRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        WifiDeviceRepository.GetDevicesCallback callback = new IWifiDeviceDataSource.GetDevicesCallback() {
            @Override
            public void onSuccess(List<WifiDevice> devices) {
                getUseCaseCallback().onSuccess(new ResponseValues(devices));
            }

            @Override
            public void onFailure() {

            }
        };
        mRepository.getDevices(callback);
    }

    public static final class RequestValues implements UseCase.RequestValues {
        public RequestValues() {

        }
    }

    public static final class ResponseValues implements UseCase.ResponseValue {
        private List<WifiDevice> devices;

        ResponseValues(List<WifiDevice> devices) {
            this.devices = devices;
        }

        public List<WifiDevice> getDevices() {
            return devices;
        }
    }
}
