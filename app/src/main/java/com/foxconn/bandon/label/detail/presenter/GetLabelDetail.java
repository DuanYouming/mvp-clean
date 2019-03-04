package com.foxconn.bandon.label.detail.presenter;

import com.foxconn.bandon.label.detail.model.ILabelDetailDataSource;
import com.foxconn.bandon.label.detail.model.LabelDetail;
import com.foxconn.bandon.label.detail.model.LabelDetailRepository;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetLabelDetail extends UseCase<GetLabelDetail.RequestValue, GetLabelDetail.ResponseValue> {

    private LabelDetailRepository mRepository;

    GetLabelDetail(LabelDetailRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        LabelDetailRepository.GetLabelDetailCallback callback = new ILabelDetailDataSource.GetLabelDetailCallback() {
            @Override
            public void onResponse(Call<LabelDetail> call, Response<LabelDetail> response) {
                LabelDetail detail = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(detail));
            }

            @Override
            public void onFailure(Call<LabelDetail> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getLabelDetail(requestValues.getId(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private int id;

        public RequestValue(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private LabelDetail detail;

        public ResponseValue(LabelDetail detail) {
            this.detail = detail;
        }

        public LabelDetail getDetail() {
            return detail;
        }
    }
}
