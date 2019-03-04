package com.foxconn.bandon.setting.user.manager;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.setting.user.model.UserInfo;

import java.util.List;

public interface IUserManagerContract {

    interface Presenter extends BasePresenter {
        void getUser();

        void deleteUser(String name);
    }

    interface View extends BaseView<Presenter> {

        void notifyDataChanged(List<UserInfo> users);

    }
}
