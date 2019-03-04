package com.foxconn.bandon.setting.user.register;

import android.graphics.Bitmap;
import android.text.TextUtils;
import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;

public class UserRegisterPresenter implements IUserRegisterContract.Presenter {

    private IUserRegisterContract.View mView;
    private UseCaseHandler mUseCaseHandler;
    private UserInfoRepository mRepository;

    UserRegisterPresenter(IUserRegisterContract.View view, UserInfoRepository repository) {
        mView = view;
        mRepository = repository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);

    }

    @Override
    public void registerUser(String name, Bitmap bitmap) {
        final RegisterUser register = new RegisterUser(mRepository);
        RegisterUser.RequestValue value = new RegisterUser.RequestValue(name, bitmap);
        mUseCaseHandler.execute(register, value, new UseCase.UseCaseCallback<RegisterUser.ResponseValue>() {
            @Override
            public void onSuccess(RegisterUser.ResponseValue response) {
                String msg = response.getMsg();
                mView.showToast(response.getMsg());
                if (TextUtils.equals(msg, BandonApplication.getInstance().getString(R.string.register_success))) {
                    mView.onSuccess();
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
