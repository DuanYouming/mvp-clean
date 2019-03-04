package com.foxconn.bandon.setting.wallpaper.presenter;

import com.foxconn.bandon.setting.wallpaper.model.IWallpaperDataSource;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperRepository;
import com.foxconn.bandon.usecase.UseCase;
import retrofit2.Call;
import retrofit2.Response;


public class GetServerWallpapers extends UseCase<GetServerWallpapers.RequestValues, GetServerWallpapers.ResponseValues> {
    private WallpaperRepository mRepository;

    GetServerWallpapers(WallpaperRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IWallpaperDataSource.ServerWallpaperCallback callback = new IWallpaperDataSource.ServerWallpaperCallback() {
            @Override
            public void onResponse(Call<WallpaperBean> call, Response<WallpaperBean> response) {
                WallpaperBean wallpaper = response.body();
                getUseCaseCallback().onSuccess(new ResponseValues(wallpaper));
            }

            @Override
            public void onFailure(Call<WallpaperBean> call, Throwable t) {

            }
        };
        mRepository.getServerWallpapers(callback);
    }

    public static final class RequestValues implements UseCase.RequestValues {

    }

    public static final class ResponseValues implements UseCase.ResponseValue {
        private WallpaperBean wallpaper;

        public ResponseValues(WallpaperBean wallpaper) {
            this.wallpaper = wallpaper;
        }

        public WallpaperBean getWallpaper() {
            return wallpaper;
        }
    }
}
