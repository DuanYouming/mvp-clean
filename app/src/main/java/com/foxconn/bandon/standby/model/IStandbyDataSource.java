package com.foxconn.bandon.standby.model;

import android.graphics.Bitmap;
import android.graphics.RectF;

public interface IStandbyDataSource {

    interface GetColorCallback {
        void onSuccess(int color);

        void onFailure();
    }

    void getColor(Bitmap bitmap,RectF rectF, GetColorCallback callback);
}
