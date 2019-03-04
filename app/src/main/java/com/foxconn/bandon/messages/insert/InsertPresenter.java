package com.foxconn.bandon.messages.insert;

import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

public class InsertPresenter implements InsertContact.Presenter {
    private static final String TAG = InsertPresenter.class.getSimpleName();
    private InsertContact.View mInsertView;
    private UseCaseHandler mUseCaseHandler;
    private InsertImageRepository mRepository;

    InsertPresenter(InsertContact.View insertView, InsertImageRepository repository) {
        LogUtils.d(TAG, "InsertPresenter");
        this.mInsertView = insertView;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mRepository = repository;
        insertView.setPresenter(this);
    }

    @Override
    public void loadDefault(String path, final int type) {
        LogUtils.d(TAG, "loadDefault");
        LoadInsertImageTask task = new LoadInsertImageTask(mRepository);
        LoadInsertImageTask.RequestValues values = new LoadInsertImageTask.RequestValues(path,false);
        mUseCaseHandler.execute(task, values, new UseCase.UseCaseCallback<LoadInsertImageTask.ResponseValue>() {
            @Override
            public void onSuccess(LoadInsertImageTask.ResponseValue response) {
                LogUtils.d(TAG, "data size:" + response.getResult().size());
                if (type == InsertImageView.TYPE_EMOTION) {
                    mInsertView.initDefaultEmotions(response.getResult());
                } else {
                    mInsertView.initDefaultImages(response.getResult());
                }
            }

            @Override
            public void onFailure() {
                LogUtils.d(TAG, "load default image failure");
            }
        });
    }

    @Override
    public void loadLocal(String path, final int type) {
        LogUtils.d(TAG, "loadLocal");
        LoadInsertImageTask task = new LoadInsertImageTask(mRepository);
        LoadInsertImageTask.RequestValues values = new LoadInsertImageTask.RequestValues(path,true);
        mUseCaseHandler.execute(task, values, new UseCase.UseCaseCallback<LoadInsertImageTask.ResponseValue>() {
            @Override
            public void onSuccess(LoadInsertImageTask.ResponseValue response) {
                LogUtils.d(TAG, "data size:" + response.getResult().size());
                if (type == InsertImageView.TYPE_EMOTION) {
                    mInsertView.notifyEmotionDataChanged(response.getResult());
                } else {
                    mInsertView.notifyImageDataChanged(response.getResult());
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

}
