package com.foxconn.bandon.setting.user.manager;


import com.foxconn.bandon.setting.user.model.IUserInfoDataSource;
import com.foxconn.bandon.setting.user.model.UserInfo;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

class GetUsers extends UseCase<GetUsers.RequestValue, GetUsers.ResponseValue> {

    private UserInfoRepository mRepository;

    GetUsers(UserInfoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        UserInfoRepository.GetUsersCallback callback = new IUserInfoDataSource.GetUsersCallback() {
            @Override
            public void onSuccess(List<UserInfo> users) {
                getUseCaseCallback().onSuccess(new ResponseValue(users));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getUsers(callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private List<UserInfo> users;

        public ResponseValue(List<UserInfo> users) {
            this.users = users;
        }

        public List<UserInfo> getUsers() {
            return users;
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {

    }
}
