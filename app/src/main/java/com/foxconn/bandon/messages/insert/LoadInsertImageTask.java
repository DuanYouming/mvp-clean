package com.foxconn.bandon.messages.insert;

import android.support.annotation.NonNull;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.utils.LogUtils;
import java.util.List;

public class LoadInsertImageTask extends UseCase<LoadInsertImageTask.RequestValues, LoadInsertImageTask.ResponseValue> {
    private static final String TAG = LoadInsertImageTask.class.getSimpleName();
    private InsertImageRepository mRepository;
    LoadInsertImageTask(@NonNull InsertImageRepository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IInsertImageDataSource.Callback callback = new IInsertImageDataSource.Callback() {
            @Override
            public void onSuccess(List<InsertImage> data) {
                ResponseValue value = new ResponseValue(data);
                LogUtils.d(TAG, "data size:" + data.size());
                getUseCaseCallback().onSuccess(value);
            }
            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };

        if (requestValues.isLocal()) {
            mRepository.loadLocalImage(requestValues.getPath(), callback);
        } else {
            mRepository.loadDefaultImage(requestValues.getPath(), callback);
        }

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private String path;
        private boolean isLocal;

        RequestValues(@NonNull String path, boolean isLocal) {
            this.path = path;
            this.isLocal = isLocal;
        }

        public String getPath() {
            return path;
        }

        public boolean isLocal() {
            return isLocal;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private List<InsertImage> data;

        public ResponseValue(List<InsertImage> data) {
            this.data = data;
        }

        public List<InsertImage> getResult() {
            return data;
        }
    }

}
