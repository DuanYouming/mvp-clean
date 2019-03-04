package com.foxconn.bandon.cropper.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.foxconn.bandon.utils.AppExecutors;


public class CropperRepository implements ICropperDataSource {

    private static final String TAG = CropperRepository.class.getSimpleName();
    private static CropperRepository instance;
    private AppExecutors mExecutors;

    private CropperRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static CropperRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (CropperRepository.class) {
                if (null == instance) {
                    instance = new CropperRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public void loadCameraPhoto(final String url, final LoadBitmapCallback callback) {
        Runnable runnable = () -> {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            if (null != bitmap) {
                callback.onSuccess(bitmap);
            } else {
                callback.onFailure();
            }
        };
        mExecutors.diskIO().execute(runnable);
    }
}
