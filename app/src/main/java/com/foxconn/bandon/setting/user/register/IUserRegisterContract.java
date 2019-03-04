package com.foxconn.bandon.setting.user.register;

import android.graphics.Bitmap;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;

public interface IUserRegisterContract {

    interface Presenter extends BasePresenter {
        void registerUser(String name ,Bitmap bitmap);
    }

    interface View extends BaseView<Presenter> {
        void showToast(String msg);

        void onSuccess();
    }
}
