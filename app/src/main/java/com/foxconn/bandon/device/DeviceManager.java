package com.foxconn.bandon.device;


import com.foxconn.bandon.setting.user.location.DeviceLocation;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();
    public static final String REGISTER_SUCCESS = "success";
    public static final String DEVICE_EXISTED = "device_existed";
    private volatile static DeviceManager instance;
    private DeviceRepository mRepository;

    private DeviceManager() {
        mRepository = DeviceRepository.getInstance();
    }

    public static DeviceManager getInstance() {
        if (null == instance) {
            synchronized (DeviceManager.class) {
                if (null == instance) {
                    instance = new DeviceManager();
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
        DeviceRepository.destroyInstance();
    }

    public void register(UseCaseHandler useCaseHandler, UseCase.UseCaseCallback<RegisterDevice.ResponseValue> useCaseCallback) {
        RegisterDevice register = new RegisterDevice(mRepository);
        RegisterDevice.RequestValue value = new RegisterDevice.RequestValue(getRegisterRequestBody());
        useCaseHandler.execute(register, value, useCaseCallback);
    }

    public void bind(UseCaseHandler useCaseHandler, UseCase.UseCaseCallback<BindDevice.ResponseValue> useCaseCallback, DeviceLocation location) {
        BindDevice bind = new BindDevice(mRepository);
        BindDevice.RequestValue value = new BindDevice.RequestValue(getBindRequestBody(location));
        useCaseHandler.execute(bind, value, useCaseCallback);
    }

    private RequestBody getRegisterRequestBody() {
        JSONObject data = new JSONObject();
        try {
            data.put("deviceId", DeviceUtils.getDeviceId(BandonApplication.getInstance()));
            data.put("macAddress", DeviceUtils.getMacAddress());
            data.put("model", DeviceUtils.getModal());
        } catch (JSONException e) {
            LogUtils.d(TAG, "JSONException:" + e.toString());
        }
        LogUtils.d(TAG, "Register RequestBody:" + data.toString());
        return RequestBody.create(MediaType.parse("application/json"), data.toString());
    }

    private RequestBody getBindRequestBody(DeviceLocation location) {
        JSONObject data = new JSONObject();
        try {
            data.put("deviceId", DeviceUtils.getDeviceId(BandonApplication.getInstance()));
            data.put("deviceProvince", location.getProvince());
            data.put("deviceCity", location.getCity());
            data.put("deviceArea", location.getDistrict());
            data.put("deviceAdress", "");
        } catch (JSONException e) {
            LogUtils.d(TAG, "JSONException:" + e.toString());
        }
        LogUtils.d(TAG, "Bind RequestBody:" + data.toString());
        return RequestBody.create(MediaType.parse("application/json"), data.toString());
    }


}
