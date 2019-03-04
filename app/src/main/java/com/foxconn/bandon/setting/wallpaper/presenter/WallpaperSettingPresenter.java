package com.foxconn.bandon.setting.wallpaper.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.setting.wallpaper.WallpaperContact;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperRepository;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;

import java.util.List;

public class WallpaperSettingPresenter implements WallpaperContact.Presenter {
    private static final String TAG = WallpaperSettingPresenter.class.getSimpleName();
    private WallpaperContact.View mView;
    private UseCaseHandler mUseCaseHandler;
    private WallpaperRepository mRepository;

    public WallpaperSettingPresenter(WallpaperContact.View view, WallpaperRepository repository) {
        this.mView = view;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        this.mRepository = repository;
        this.mView.setPresenter(this);
    }

    @Override
    public void load() {
        getLocalWallpapers();
        getServerWallpapers();
    }

    private void getLocalWallpapers() {
        GetLocalWallpapers get = new GetLocalWallpapers(mRepository);
        mUseCaseHandler.execute(get, new GetLocalWallpapers.RequestValues(), new UseCase.UseCaseCallback<GetLocalWallpapers.ResponseValues>() {
            @Override
            public void onSuccess(GetLocalWallpapers.ResponseValues response) {
                List<WallpaperBean.DataBean> wallpapers = response.getUrls();
                if (null != wallpapers) {
                    LogUtils.d(TAG, "Load wallpaper:" + wallpapers.size());
                    mView.updateLocalWallpapers(wallpapers);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void getServerWallpapers() {
        GetServerWallpapers get = new GetServerWallpapers(mRepository);
        mUseCaseHandler.execute(get, null, new UseCase.UseCaseCallback<GetServerWallpapers.ResponseValues>() {
            @Override
            public void onSuccess(GetServerWallpapers.ResponseValues response) {
                List<WallpaperBean.DataBean> wallpapers = response.getWallpaper().getData();
                if (null != wallpapers) {
                    LogUtils.d(TAG, "Load wallpaper:" + wallpapers.size());
                    mView.updateServerWallpapers(wallpapers);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void setWallpaper(final WallpaperBean.DataBean wallpaper) {
        if (null == wallpaper) {
            mView.finish();
            return;
        }
        mView.showDialog();
        SetWallpaper set = new SetWallpaper(mRepository);
        SetWallpaper.RequestValues values = new SetWallpaper.RequestValues(wallpaper);
        mUseCaseHandler.execute(set, values, new UseCase.UseCaseCallback<SetWallpaper.ResponseValues>() {
            @Override
            public void onSuccess(SetWallpaper.ResponseValues response) {
                String result = response.getUrl();
                LogUtils.d(TAG, "set Wallpaper to :" + result);
                cacheWallpaper(result, wallpaper.getOriginalUrl());
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void cacheWallpaper(String result, String url) {
        final ObjectKey key = new ObjectKey(url);
        final Context context = BandonApplication.getInstance().getApplicationContext();
        GlideApp.with(context)
                .asBitmap()
                .load(result)
                .signature(key)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        PreferenceUtils.setString(context, Constant.SP_SETTINGS, Constant.KEY_WALLPAPER_CACHE, new Gson().toJson(key));
                        PreferenceUtils.setBoolean(context, Constant.SP_SETTINGS, Constant.KEY_WALLPAPER_UPDATE, true);
                        mView.showToast("set wallpaper success");
                        mView.removeDialog();
                        mView.finish();
                        return false;
                    }
                })
                .preload();
    }

}
