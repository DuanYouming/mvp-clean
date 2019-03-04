package com.foxconn.bandon.device;

import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;

import okhttp3.RequestBody;
import retrofit2.Call;

public class DeviceRepository implements IDeviceDataSource {

    private static final String TAG = DeviceRepository.class.getSimpleName();
    private volatile static DeviceRepository instance;
    private AppExecutors mExecutors;

    private DeviceRepository() {
        this.mExecutors = new AppExecutors();
    }

    public static DeviceRepository getInstance() {
        if (null == instance) {
            synchronized (DeviceRepository.class) {
                if (null == instance) {
                    instance = new DeviceRepository();
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void register(final RequestBody body, final DeviceCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Call<ResponseStr> call = RetrofitUtils.getInstance().getRetrofitService().register(body);
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void bind(final RequestBody body, final DeviceCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Call<ResponseStr> call = RetrofitUtils.getInstance().getRetrofitService().bindDevice(body);
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }


}
