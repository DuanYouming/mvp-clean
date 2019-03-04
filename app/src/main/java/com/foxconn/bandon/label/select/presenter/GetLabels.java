package com.foxconn.bandon.label.select.presenter;


import com.foxconn.bandon.label.select.model.ILabelDataSource;
import com.foxconn.bandon.label.select.model.Label;
import com.foxconn.bandon.label.select.model.LabelRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Response;

public class GetLabels extends UseCase<GetLabels.RequestValue, GetLabels.ResponseValue> {
    private static final String TAG = GetLabels.class.getSimpleName();
    private LabelRepository mRepository;

    GetLabels(LabelRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        LabelRepository.GetLabelCallback callback = new ILabelDataSource.GetLabelCallback() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                Label label = response.body();
                LogUtils.d(TAG,"onResponse()");
                getUseCaseCallback().onSuccess(new ResponseValue(label));
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                LogUtils.d(TAG,"onFailure()");
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getLabel(callback);
    }

    public static final class RequestValue implements UseCase.RequestValues {

    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Label label;

        public ResponseValue(Label label) {
            this.label = label;
        }

        public Label getLabel() {
            return label;
        }
    }
}
