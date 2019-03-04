package com.foxconn.bandon.label.detail.model;

import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class LabelDetailRepository implements ILabelDetailDataSource {

    private static final String TAG = LabelDetailRepository.class.getSimpleName();
    private volatile static LabelDetailRepository instance;
    private AppExecutors mExecutors;

    private LabelDetailRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static LabelDetailRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (LabelDetailRepository.class) {
                if (null == instance) {
                    instance = new LabelDetailRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public void saveLabelDetail(final RequestBody body, final LabelDetailCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Call<ResponseStr> call = RetrofitUtils.getInstance().getRetrofitService().saveLabelDetail(body);
                LogUtils.d(TAG, "saveLabelDetail():" + body.toString());
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteLabelDetail(final RequestBody body,final LabelDetailCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Call<ResponseStr> call = RetrofitUtils.getInstance().getRetrofitService().deleteLabelDetail(body);
                LogUtils.d(TAG, "deleteLabelDetail():" + body.toString());
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getLabelItem(final String name, final LabelItemCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JSONObject data = new JSONObject();
                try {
                    data.put("foodTagName", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d(TAG, "post body:" + data.toString());
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), data.toString());
                Call<LabelItem> call = RetrofitUtils.getInstance().getRetrofitService().getLabelItem(body);
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getLabelDetail(final int id, final GetLabelDetailCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Call<LabelDetail> call = RetrofitUtils.getInstance().getRetrofitService().getLabelDetail(id);
                call.enqueue(callback);
            }
        };
        mExecutors.networkIO().execute(runnable);
    }
}
