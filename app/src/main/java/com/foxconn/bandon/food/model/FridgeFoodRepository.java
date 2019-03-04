package com.foxconn.bandon.food.model;

import android.content.Context;
import android.support.annotation.NonNull;
import com.foxconn.bandon.food.photo.PhotoUtils;
import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.LogUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;



public class FridgeFoodRepository implements IFridgeFoodDataSource {

    private static final String TAG = FridgeFoodRepository.class.getSimpleName();
    private volatile static FridgeFoodRepository instance;
    private AppExecutors mExecutors;
    private String mDeviceID;

    private FridgeFoodRepository(Context context, AppExecutors executors) {
        this.mExecutors = executors;
        this.mDeviceID = DeviceUtils.getDeviceId(context);
    }

    public static FridgeFoodRepository getInstance(Context context, AppExecutors executors) {
        if (null == instance) {
            synchronized (FridgeFoodRepository.class) {
                if (null == instance) {
                    instance = new FridgeFoodRepository(context, executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void getFridgeFoods(final String storagePosition, final FridgeFoodsCallback callback) {
        Runnable runnable = () -> {
            Call<FridgeFood> call = RetrofitUtils.getInstance().getRetrofitService().getFridgeFoods(mDeviceID, 0, storagePosition);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getFridgeFoods(final ColdRoomFoodsCallback callback) {
        Runnable runnable = () -> {
            Call<ColdRoomFood> call = RetrofitUtils.getInstance().getRetrofitService().getFridgeFoods(mDeviceID);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void updateLocation(final ColdRoomFood.Label label, final UpdateLocationCallback callback) {
        Runnable runnable = () -> {
            RequestBody body = getJsonObject(label);
            Call<ResponseStr> call = RetrofitUtils.getInstance().getRetrofitService().updateLocation(body);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    @NonNull
    private RequestBody getJsonObject(ColdRoomFood.Label label) {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            object.put("id", label.id);
            object.put("xCoordinate", label.xCoordinate);
            object.put("yCoordinate", label.yCoordinate);
            array.put(object);
            data.put("listLocation_XY", array);
        } catch (JSONException e) {
            LogUtils.e(TAG, "JSONException:" + e.toString());
        }
        return RequestBody.create(MediaType.parse("application/json"), data.toString());
    }

    @Override
    public void getCameraPhoto(final int index, final GetCameraPhotoCallback callback) {
        Runnable runnable = () -> {
            final String url = PhotoUtils.getInstance().getCameraPhoto(index);
            mExecutors.mainThread().execute(() -> {
                if (null != url) {
                    callback.onSuccess(url);
                } else {
                    callback.onFailure();
                }
            });
        };
        mExecutors.diskIO().execute(runnable);
    }

}
