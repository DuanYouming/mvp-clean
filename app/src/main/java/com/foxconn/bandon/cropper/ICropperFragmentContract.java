package com.foxconn.bandon.cropper;

import android.graphics.Bitmap;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;

public interface ICropperFragmentContract {

    interface Presenter extends BasePresenter {
        void loadCameraPhoto(String url);
    }

    interface View extends BaseView<Presenter> {
        void setBitmap(Bitmap bitmap);

        void close();
    }
}
