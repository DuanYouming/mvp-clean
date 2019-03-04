package com.foxconn.bandon.label.detail.presenter;

import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.label.detail.model.ILabelDetailDataSource;
import com.foxconn.bandon.label.detail.model.LabelDetailRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.utils.LogUtils;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class SaveLabelDetail extends UseCase<SaveLabelDetail.RequestValue, SaveLabelDetail.ResponseValue> {
    private static final String TAG = SaveLabelDetail.class.getSimpleName();
    private LabelDetailRepository mRepository;

    SaveLabelDetail(LabelDetailRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        LabelDetailRepository.LabelDetailCallback callback = new ILabelDetailDataSource.LabelDetailCallback() {
            @Override
            public void onResponse(Call<ResponseStr> call, Response<ResponseStr> response) {
                getUseCaseCallback().onSuccess(new ResponseValue(response.body()));
            }

            @Override
            public void onFailure(Call<ResponseStr> call, Throwable t) {
                LogUtils.d(TAG, "Throwable:" + t.toString());
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.saveLabelDetail(requestValues.getBody(), callback);

    }

    public final static class RequestValue implements UseCase.RequestValues {
        private RequestBody body;

        public RequestValue(RequestBody body) {
            this.body = body;
        }

        public RequestBody getBody() {
            return body;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private ResponseStr response;

        public ResponseValue(ResponseStr response) {
            this.response = response;
        }

        public ResponseStr getResponse() {
            return response;
        }
    }
}
