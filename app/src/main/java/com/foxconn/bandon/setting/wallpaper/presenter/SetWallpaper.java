package com.foxconn.bandon.setting.wallpaper.presenter;

import com.foxconn.bandon.setting.wallpaper.model.IWallpaperDataSource;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperRepository;
import com.foxconn.bandon.usecase.UseCase;

public class SetWallpaper extends UseCase<SetWallpaper.RequestValues, SetWallpaper.ResponseValues> {
    private WallpaperRepository mRepository;

    SetWallpaper(WallpaperRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IWallpaperDataSource.SetWallpaperCallback callback = new IWallpaperDataSource.SetWallpaperCallback() {
            @Override
            public void onSuccess(String url) {
                getUseCaseCallback().onSuccess(new ResponseValues(url));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };


        mRepository.setWallpaper(requestValues.getWallpaper(), callback);
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private WallpaperBean.DataBean wallpaper;

        public RequestValues(WallpaperBean.DataBean wallpaper) {
            this.wallpaper = wallpaper;
        }

        public WallpaperBean.DataBean getWallpaper() {
            return wallpaper;
        }
    }

    public static final class ResponseValues implements UseCase.ResponseValue {
        private String url;
        public ResponseValues(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }
    }
}
