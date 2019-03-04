package com.foxconn.bandon.label.detail.presenter;


import com.foxconn.bandon.label.detail.ILabelDetailFragmentContract;
import com.foxconn.bandon.label.detail.model.LabelDetail;
import com.foxconn.bandon.label.detail.model.LabelDetailRepository;
import com.foxconn.bandon.label.detail.model.LabelItem;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

import okhttp3.RequestBody;

public class LabelDetailPresenter implements ILabelDetailFragmentContract.Presenter {
    private static final String TAG = LabelDetailPresenter.class.getSimpleName();
    private ILabelDetailFragmentContract.View mView;
    private LabelDetailRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    public LabelDetailPresenter(ILabelDetailFragmentContract.View view, LabelDetailRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void getLabelItem(String name) {
        GetLabel getLabelItem = new GetLabel(mRepository);
        GetLabel.RequestValue value = new GetLabel.RequestValue(name);
        mUseCaseHandler.execute(getLabelItem, value, new UseCase.UseCaseCallback<GetLabel.ResponseValue>() {
            @Override
            public void onSuccess(GetLabel.ResponseValue response) {
                LabelItem labelItem = response.getItem();
                if (null != labelItem) {
                    mView.updateLabelItem(labelItem);
                } else {
                    mView.showExceptionView();
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void getLabelDetail(int id) {
        GetLabelDetail getDetail = new GetLabelDetail(mRepository);
        GetLabelDetail.RequestValue value = new GetLabelDetail.RequestValue(id);
        mUseCaseHandler.execute(getDetail, value, new UseCase.UseCaseCallback<GetLabelDetail.ResponseValue>() {
            @Override
            public void onSuccess(GetLabelDetail.ResponseValue response) {
                LabelDetail detail = response.getDetail();
                if (null != detail) {
                    LogUtils.d(TAG, "Label Detail:" + detail.data.toString());
                    mView.updateLabelDetail(detail);
                } else {
                    mView.showExceptionView();
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void save(final RequestBody body) {
        SaveLabelDetail save = new SaveLabelDetail(mRepository);
        SaveLabelDetail.RequestValue value = new SaveLabelDetail.RequestValue(body);
        mUseCaseHandler.execute(save, value, new UseCase.UseCaseCallback<SaveLabelDetail.ResponseValue>() {
            @Override
            public void onSuccess(SaveLabelDetail.ResponseValue response) {
                mView.updateOrSaveGTMessage();
                mView.startToFoodsView();
            }

            @Override
            public void onFailure() {
                mView.showExceptionView();
            }
        });
    }

    @Override
    public void delete(RequestBody body) {
        DeleteLabelDetail delete = new DeleteLabelDetail(mRepository);
        DeleteLabelDetail.RequestValue value = new DeleteLabelDetail.RequestValue(body);
        mUseCaseHandler.execute(delete, value, new UseCase.UseCaseCallback<DeleteLabelDetail.ResponseValue>() {
            @Override
            public void onSuccess(DeleteLabelDetail.ResponseValue response) {
                mView.deleteGTMessage();
                mView.startToFoodsView();
            }

            @Override
            public void onFailure() {
                mView.showExceptionView();
            }
        });
    }
}
