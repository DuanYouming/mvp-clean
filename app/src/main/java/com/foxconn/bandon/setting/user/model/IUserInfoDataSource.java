package com.foxconn.bandon.setting.user.model;

import android.graphics.Bitmap;

import com.foxconn.bandon.setting.user.model.UserInfo;

import java.util.List;

public interface IUserInfoDataSource {

    interface GetUsersCallback {

        void onSuccess(List<UserInfo> users);

        void onFailure();
    }

    interface LoadUserCallback {

        void onSuccess(String msg);

        void onFailure();
    }

    void registerUser(String name, Bitmap bitmap, LoadUserCallback callback);

    void deleteUser(String name ,LoadUserCallback callback);

    void getUsers(GetUsersCallback callback);

}
