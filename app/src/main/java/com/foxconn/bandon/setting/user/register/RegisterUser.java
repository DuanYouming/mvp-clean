package com.foxconn.bandon.setting.user.register;

import android.graphics.Bitmap;

import com.foxconn.bandon.setting.user.model.IUserInfoDataSource;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.usecase.UseCase;

public class RegisterUser extends UseCase<RegisterUser.RequestValue, RegisterUser.ResponseValue> {

    private UserInfoRepository mRepository;

    RegisterUser(UserInfoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        UserInfoRepository.LoadUserCallback callback = new IUserInfoDataSource.LoadUserCallback() {
            @Override
            public void onSuccess(String msg) {
                getUseCaseCallback().onSuccess(new ResponseValue(msg));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.registerUser(requestValues.getName(), requestValues.getBitmap(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private String msg;

        public ResponseValue(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String name;
        private Bitmap bitmap;

        public RequestValue(String name, Bitmap bitmap) {
            this.name = name;
            this.bitmap = bitmap;
        }

        public String getName() {
            return name;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
