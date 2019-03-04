package com.foxconn.bandon.standby.model;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;


public class StandbyRepository implements IStandbyDataSource {

    private static final String TAG = StandbyRepository.class.getSimpleName();
    private static volatile StandbyRepository instance;
    private AppExecutors mExecutors;

    private StandbyRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static StandbyRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (StandbyRepository.class) {
                if (null == instance) {
                    instance = new StandbyRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void getColor(final Bitmap bitmap, final RectF rectF, final GetColorCallback callback) {
        Runnable runnable = () -> {
            final int color = getColor(bitmap, rectF);
            mExecutors.mainThread().execute(() -> callback.onSuccess(color));
        };
        mExecutors.diskIO().execute(runnable);
    }

    private int getColor(Bitmap bitmap, RectF rectF) {
        LogUtils.d(TAG, "getColor:" + rectF.toString() + "bitmap size:" + bitmap.getWidth() + "x" + bitmap.getHeight());

        int startX = (int) rectF.left;
        int endX = (int) rectF.right + startX;

        int startY = (int) rectF.top;
        int endY = (int) rectF.bottom + startY;

        if (startX >= bitmap.getWidth() || startY >= bitmap.getHeight()) {
            return Color.WHITE;
        }

        if (endX > bitmap.getWidth()) {
            endX = bitmap.getWidth();
        }

        if (endY > bitmap.getHeight()) {
            endY = bitmap.getHeight();
        }

        int darkCount = 0;
        int lightCount = 0;

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                int color = bitmap.getPixel(i, j);
                int brightness = getColorBrightness(color);
                if (brightness < 60) {
                    darkCount++;
                } else {
                    lightCount++;
                }
            }
        }
        LogUtils.d(TAG, "darkCount:" + darkCount + "  lightCount:" + lightCount);
        return darkCount > lightCount ? Color.WHITE : Color.BLACK;
    }

    private int getColorBrightness(int color) {
        return (int) ((Math.max(Color.red(color), Math.max(Color.green(color), Color.blue(color))) / 255.0F) * 100);
    }
}
