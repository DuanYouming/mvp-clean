package com.foxconn.bandon.label.select.presenter;

import com.foxconn.bandon.label.select.ILabelSelectContract;
import com.foxconn.bandon.label.select.model.Label;
import com.foxconn.bandon.label.select.model.LabelRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;


public class LabelSelectPresenter implements ILabelSelectContract.Presenter {
    private static final String TAG = LabelSelectPresenter.class.getName();
    private ILabelSelectContract.View mView;
    private UseCaseHandler mUseCaseHandler;
    private LabelRepository mRepository;

    public LabelSelectPresenter(ILabelSelectContract.View view, LabelRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void load() {
        GetLabels getLabels = new GetLabels(mRepository);
        GetLabels.RequestValue value = new GetLabels.RequestValue();
        mUseCaseHandler.execute(getLabels, value, new UseCase.UseCaseCallback<GetLabels.ResponseValue>() {
            @Override
            public void onSuccess(GetLabels.ResponseValue response) {
                Label label = response.getLabel();
                LogUtils.d(TAG, "onSuccess()");
                mView.update(label);
            }

            @Override
            public void onFailure() {
                LogUtils.d(TAG, "onFailure()");
            }
        });
    }
}
