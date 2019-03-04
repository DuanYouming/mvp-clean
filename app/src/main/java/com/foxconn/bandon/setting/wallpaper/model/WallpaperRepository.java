package com.foxconn.bandon.setting.wallpaper.model;

import android.text.TextUtils;
import android.util.Log;

import com.foxconn.bandon.http.RetrofitUtils;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperRepository implements IWallpaperDataSource {

    private static final String TAG = WallpaperRepository.class.getSimpleName();
    private static final String TAG_LOCAL = "-10";
    private static volatile WallpaperRepository instance;
    private AppExecutors mExecutors;

    private WallpaperRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static WallpaperRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (WallpaperRepository.class) {
                if (null == instance) {
                    instance = new WallpaperRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void getLocalWallpapers(final LocalWallpaperCallback callback) {
        Runnable runnable = () -> {
            final List<WallpaperBean.DataBean> wallpapers = getWallpaperUrls();
            mExecutors.mainThread().execute(() -> callback.onSuccess(wallpapers));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getServerWallpapers(final ServerWallpaperCallback callback) {
        Runnable runnable = () -> {
            String id = DeviceUtils.getDeviceId(BandonApplication.getInstance());
            Call<WallpaperBean> call = RetrofitUtils.getInstance().getRetrofitService().getWallpapers(id);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    private List<WallpaperBean.DataBean> getWallpaperUrls() {
        List<WallpaperBean.DataBean> wallpapers = new ArrayList<>();
        File file = new File(Constant.WALLPAPER);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.getName().toLowerCase().endsWith("jpg") || f.getName().toLowerCase().endsWith("png") || f.getName().toLowerCase().endsWith("jpeg")) {
                    WallpaperBean.DataBean wallpaper = new WallpaperBean.DataBean();
                    wallpaper.setOriginalUrl(f.getPath());
                    wallpaper.setThumnailUrl(f.getPath());
                    wallpaper.setPictureTag(TAG_LOCAL);
                    wallpapers.add(wallpaper);
                }
            }
        }
        return wallpapers;
    }

    @Override
    public void setWallpaper(final WallpaperBean.DataBean wallpaper, final SetWallpaperCallback callback) {
        Runnable runnable = () -> {
            if (TextUtils.equals(wallpaper.getPictureTag(), TAG_LOCAL)) {
                setLocalWallpaper(wallpaper, callback);
            } else {
                setServerWallpaper(wallpaper, callback);
            }
        };
        mExecutors.diskIO().execute(runnable);
    }

    private void setLocalWallpaper(WallpaperBean.DataBean wallpaper, final SetWallpaperCallback callback) {
        final File dest = saveLocalFile(wallpaper.getOriginalUrl());
        mExecutors.mainThread().execute(() -> callback.onSuccess(dest.getPath()));
    }


    private File saveLocalFile(String url) {
        File wallpaperDir = new File(Constant.WALLPAPER_DEFAULT);
        if (!wallpaperDir.exists()) {
            Log.d(TAG, "make dir[" + wallpaperDir.getPath() + "] " + wallpaperDir.mkdirs());
        }
        File src = new File(url);
        File dest = new File(Constant.WALLPAPER_DEFAULT, "wallpaper_default.jpg");
        FileChannel srcChannel = null;
        FileChannel destChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } catch (Exception e) {
            LogUtils.d(TAG, "Exception:" + e.toString());
        } finally {
            try {
                if (null != srcChannel) {
                    srcChannel.close();
                }
                if (null != destChannel) {
                    destChannel.close();
                }
            } catch (IOException e) {
                LogUtils.d(TAG, "Exception:" + e.toString());
            }
        }
        return dest;
    }

    private void setServerWallpaper(final WallpaperBean.DataBean wallpaper, final SetWallpaperCallback callback) {
        Runnable runnable = () -> {
            Call<ResponseBody> call = RetrofitUtils.getInstance().getRetrofitService().downWallpaper(wallpaper.getOriginalUrl());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        File file = saveServerFile(response.body());
                        callback.onSuccess(file.getPath());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        };
        mExecutors.networkIO().execute(runnable);
    }

    private File saveServerFile(ResponseBody body) {
        File wallpaperDir = new File(Constant.WALLPAPER_DEFAULT);
        if (!wallpaperDir.exists()) {
            Log.d(TAG, "make dir[" + wallpaperDir.getPath() + "] " + wallpaperDir.mkdirs());
        }
        File dest = new File(Constant.WALLPAPER_DEFAULT, "wallpaper_default.jpg");
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] fileReader = new byte[4096];
            is = body.byteStream();
            os = new FileOutputStream(dest);
            int read = is.read(fileReader);
            while (read != -1) {
                os.write(fileReader, 0, read);
                read = is.read(fileReader);
            }
            os.flush();
        } catch (IOException e) {
            LogUtils.d(TAG, "IOException:" + e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LogUtils.d(TAG, "IOException:" + e.toString());
            }
        }
        return dest;
    }
}
