package com.foxconn.bandon.label.detail.presenter;

import com.foxconn.bandon.label.detail.model.ILabelDetailDataSource;
import com.foxconn.bandon.label.detail.model.LabelDetailRepository;
import com.foxconn.bandon.label.detail.model.LabelItem;
import com.foxconn.bandon.usecase.UseCase;
import retrofit2.Call;
import retrofit2.Response;

public class GetLabel extends UseCase<GetLabel.RequestValue, GetLabel.ResponseValue> {

    private LabelDetailRepository mRepository;

    GetLabel(LabelDetailRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        LabelDetailRepository.LabelItemCallback callback = new ILabelDetailDataSource.LabelItemCallback() {
            @Override
            public void onResponse(Call<LabelItem> call, Response<LabelItem> response) {
                LabelItem item = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(item));
            }

            @Override
            public void onFailure(Call<LabelItem> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getLabelItem(requestValues.getName(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private String name;

        public RequestValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private LabelItem item;

        public ResponseValue(LabelItem item) {
            this.item = item;
        }

        public LabelItem getItem() {
            return item;
        }
    }
}
