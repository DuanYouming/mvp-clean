package com.foxconn.bandon.device;

import com.foxconn.bandon.http.ResponseStr;

import okhttp3.RequestBody;
import retrofit2.Callback;

public interface IDeviceDataSource {

    interface DeviceCallback extends Callback<ResponseStr> {

    }


    void register(RequestBody body, DeviceCallback callback);


    void bind(RequestBody body, DeviceCallback callback);


}
