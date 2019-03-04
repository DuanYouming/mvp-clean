package com.foxconn.bandon.cropper.presenter;

import android.graphics.Bitmap;

import com.foxconn.bandon.cropper.ICropperFragmentContract;
import com.foxconn.bandon.cropper.model.CropperRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;

public class CropperPresenter implements ICropperFragmentContract.Presenter {

    private CropperRepository mRepository;
    private UseCaseHandler mUseCaseHandler;
    private ICropperFragmentContract.View mView;

    public CropperPresenter(ICropperFragmentContract.View view, CropperRepository repository) {
        this.mRepository = repository;
        this.mView = view;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void loadCameraPhoto(String url) {
        LoadCameraPhoto load = new LoadCameraPhoto(mRepository);
        LoadCameraPhoto.RequestValue value = new LoadCameraPhoto.RequestValue(url);
        mUseCaseHandler.execute(load, value, new UseCase.UseCaseCallback<LoadCameraPhoto.ResponseValue>() {
            @Override
            public void onSuccess(LoadCameraPhoto.ResponseValue response) {
                Bitmap bitmap = response.getBitmap();
                mView.setBitmap(bitmap);
            }
            @Override
            public void onFailure() {

            }
        });
    }

}
