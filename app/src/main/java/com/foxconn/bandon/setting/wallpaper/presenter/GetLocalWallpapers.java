package com.foxconn.bandon.setting.wallpaper.presenter;

import com.foxconn.bandon.setting.wallpaper.model.IWallpaperDataSource;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperRepository;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetLocalWallpapers extends UseCase<GetLocalWallpapers.RequestValues, GetLocalWallpapers.ResponseValues> {
    private WallpaperRepository mRepository;

    GetLocalWallpapers(WallpaperRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IWallpaperDataSource.LocalWallpaperCallback callback = new IWallpaperDataSource.LocalWallpaperCallback() {

            @Override
            public void onSuccess(List<WallpaperBean.DataBean> wallpapers) {
                getUseCaseCallback().onSuccess(new ResponseValues(wallpapers));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getLocalWallpapers(callback);
    }

    public static final class RequestValues implements UseCase.RequestValues {

    }

    public static final class ResponseValues implements UseCase.ResponseValue {
        private List<WallpaperBean.DataBean> urls;

        public ResponseValues(List<WallpaperBean.DataBean> urls) {
            this.urls = urls;
        }

        public List<WallpaperBean.DataBean> getUrls() {
            return urls;
        }
    }
}
