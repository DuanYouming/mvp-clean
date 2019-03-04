package com.foxconn.bandon.setting.wallpaper.model;

import java.util.List;

import retrofit2.Callback;

public interface IWallpaperDataSource {

    interface LocalWallpaperCallback {
        void onSuccess(List<WallpaperBean.DataBean> wallpapers);

        void onFailure();
    }

    interface ServerWallpaperCallback extends Callback<WallpaperBean> {

    }

    interface SetWallpaperCallback {
        void onSuccess(String url);

        void onFailure();
    }

    void getLocalWallpapers(LocalWallpaperCallback callback);

    void getServerWallpapers(ServerWallpaperCallback callback);

    void setWallpaper(WallpaperBean.DataBean wallpaper, SetWallpaperCallback callback);

}
