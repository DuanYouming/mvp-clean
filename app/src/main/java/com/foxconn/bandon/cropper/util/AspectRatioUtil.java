package com.foxconn.bandon.cropper.util;
import android.graphics.RectF;
import android.support.annotation.NonNull;


public class AspectRatioUtil {


    public static float calculateAspectRatio(float left, float top, float right, float bottom) {
        final float width = right - left;
        final float height = bottom - top;
        return width / height;
    }


    public static float calculateAspectRatio(@NonNull RectF rect) {
        return rect.width() / rect.height();
    }


    public static float calculateLeft(float top, float right, float bottom, float targetAspectRatio) {

        final float height = bottom - top;
        return right - (targetAspectRatio * height);
    }

    public static float calculateTop(float left, float right, float bottom, float targetAspectRatio) {

        final float width = right - left;
        return bottom - (width / targetAspectRatio);
    }

    public static float calculateRight(float left, float top, float bottom, float targetAspectRatio) {

        final float height = bottom - top;
        return (targetAspectRatio * height) + left;
    }

    public static float calculateBottom(float left, float top, float right, float targetAspectRatio) {

        final float width = right - left;
        return (width / targetAspectRatio) + top;
    }


    public static float calculateWidth(float height, float targetAspectRatio) {
        return targetAspectRatio * height;
    }

    public static float calculateHeight(float width, float targetAspectRatio) {
        return width / targetAspectRatio;
    }
}
