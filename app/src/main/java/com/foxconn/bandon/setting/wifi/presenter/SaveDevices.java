package com.foxconn.bandon.setting.wifi.presenter;


import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.setting.wifi.model.WifiDeviceRepository;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class SaveDevices extends UseCase<SaveDevices.RequestValues, SaveDevices.ResponseValues> {

    private WifiDeviceRepository mRepository;

    SaveDevices(WifiDeviceRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.saveDevices(requestValues.getDevices());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        List<WifiDevice> devices;

        public RequestValues(List<WifiDevice> devices) {
            this.devices = devices;
        }

        List<WifiDevice> getDevices() {
            return devices;
        }
    }

    static final class ResponseValues implements UseCase.ResponseValue {

    }
}
