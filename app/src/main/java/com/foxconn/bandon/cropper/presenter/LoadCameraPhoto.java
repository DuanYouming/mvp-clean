package com.foxconn.bandon.cropper.presenter;

import android.graphics.Bitmap;

import com.foxconn.bandon.cropper.model.CropperRepository;
import com.foxconn.bandon.cropper.model.ICropperDataSource;

import com.foxconn.bandon.usecase.UseCase;

public class LoadCameraPhoto extends UseCase<LoadCameraPhoto.RequestValue, LoadCameraPhoto.ResponseValue> {
    private static final String TAG = LoadCameraPhoto.class.getSimpleName();
    private CropperRepository mRepository;

    public LoadCameraPhoto(CropperRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(final RequestValue value) {
        ICropperDataSource.LoadBitmapCallback callback = new CropperRepository.LoadBitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                getUseCaseCallback().onSuccess(new ResponseValue(bitmap));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.loadCameraPhoto(value.getUrl(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {

        private String url;

        public RequestValue(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private Bitmap bitmap;

        public ResponseValue(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
