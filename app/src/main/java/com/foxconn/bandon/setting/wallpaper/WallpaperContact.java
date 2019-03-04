package com.foxconn.bandon.setting.wallpaper;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;

import java.util.List;

public interface WallpaperContact {

    interface Presenter extends BasePresenter {

        void load();

        void setWallpaper(WallpaperBean.DataBean wallpaper);
    }

    interface View extends BaseView<Presenter> {

        void showDialog();

        void removeDialog();

        void showToast(String message);

        void updateLocalWallpapers(List<WallpaperBean.DataBean> wallpapers);

        void updateServerWallpapers(List<WallpaperBean.DataBean> wallpapers);

        void finish();

    }

}
