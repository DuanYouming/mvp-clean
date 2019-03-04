package com.foxconn.bandon.label.select.model;

import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelRepository implements ILabelDataSource {

    private volatile static LabelRepository instance;
    private AppExecutors mExecutors;

    private LabelRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static LabelRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (LabelRepository.class) {
                if (null == instance) {
                    instance = new LabelRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public void getLabel(final GetLabelCallback callback) {
        Runnable runnable = () -> {
            Call<LabelVersion> call = RetrofitUtils.getInstance().getRetrofitService().getLabelUrl();
            call.enqueue(new Callback<LabelVersion>() {
                @Override
                public void onResponse(Call<LabelVersion> call, Response<LabelVersion> response) {
                    LabelVersion version = response.body();
                    if (null != version) {
                        final String url = version.getData().getUrl();
                        mExecutors.mainThread().execute(() -> getLabel(url, callback));
                    }
                }


                @Override
                public void onFailure(Call<LabelVersion> call, Throwable t) {
                    callback.onFailure(null, t);
                }
            });
        };
        mExecutors.networkIO().execute(runnable);
    }

    private void getLabel(final String url, final GetLabelCallback callback) {
        Call<Label> call = RetrofitUtils.getInstance().getRetrofitService().getLabel(url);
        call.enqueue(callback);
    }


}
