package com.foxconn.bandon.setting.user.manager;

import com.foxconn.bandon.setting.user.model.UserInfo;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;

import java.util.List;

public class UserManagerPresenter implements IUserManagerContract.Presenter {

    private IUserManagerContract.View mView;
    private UserInfoRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    UserManagerPresenter(IUserManagerContract.View view, UserInfoRepository repository) {
        mView = view;
        mRepository = repository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void getUser() {
        GetUsers getUsers = new GetUsers(mRepository);
        GetUsers.RequestValue value = new GetUsers.RequestValue();
        mUseCaseHandler.execute(getUsers, value, new UseCase.UseCaseCallback<GetUsers.ResponseValue>() {
            @Override
            public void onSuccess(GetUsers.ResponseValue response) {
                List<UserInfo> users = response.getUsers();
                mView.notifyDataChanged(users);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void deleteUser(String name) {
        DeleteUser deleteUser = new DeleteUser(mRepository);
        DeleteUser.RequestValue value = new DeleteUser.RequestValue(name);
        mUseCaseHandler.execute(deleteUser, value, new UseCase.UseCaseCallback<DeleteUser.ResponseValue>() {
            @Override
            public void onSuccess(DeleteUser.ResponseValue response) {
                getUser();
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
