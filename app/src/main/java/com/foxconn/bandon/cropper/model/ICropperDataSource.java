package com.foxconn.bandon.cropper.model;

import android.graphics.Bitmap;

public interface ICropperDataSource {

    interface LoadBitmapCallback {
        void onSuccess(Bitmap bitmap);

        void onFailure();
    }

    void loadCameraPhoto(String url, LoadBitmapCallback callback);

}
