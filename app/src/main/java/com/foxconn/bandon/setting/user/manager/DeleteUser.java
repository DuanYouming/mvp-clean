package com.foxconn.bandon.setting.user.manager;


import com.foxconn.bandon.setting.user.model.IUserInfoDataSource;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.usecase.UseCase;


public class DeleteUser extends UseCase<DeleteUser.RequestValue, DeleteUser.ResponseValue> {

    private UserInfoRepository mRepository;

    DeleteUser(UserInfoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        UserInfoRepository.LoadUserCallback callback = new IUserInfoDataSource.LoadUserCallback() {
            @Override
            public void onSuccess(String msg) {
                getUseCaseCallback().onSuccess(null);
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.deleteUser(requestValues.getName(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String name;

        public RequestValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
