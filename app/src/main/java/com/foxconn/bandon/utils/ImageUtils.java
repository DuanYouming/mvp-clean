package com.foxconn.bandon.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class ImageUtils {
    public static Bitmap resize(Bitmap bitmap, int maxW, int maxH) {
        if (bitmap.getHeight() > maxH || bitmap.getWidth() > maxW) {
            int w, h;
            float scaleRate;
            if (bitmap.getHeight() > bitmap.getWidth()) {
                h = maxH;
                scaleRate = bitmap.getHeight() / (maxH * 1f);
                w = Math.round(bitmap.getWidth() / scaleRate);
            } else {
                w = maxW;
                scaleRate = bitmap.getWidth() / (maxW * 1f);
                h = Math.round(bitmap.getHeight() / scaleRate);
            }
            return Bitmap.createScaledBitmap(bitmap, w, h, false);
        } else {
            return bitmap;
        }
    }

    public static Bitmap getBitmapFromAssets(Context context, String path) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    public static Bitmap scaleBitmap(Bitmap src, float wScale, float hScale) {
        if (null == src) {
            return null;
        }
        float width = src.getWidth();
        float height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale);
        return Bitmap.createBitmap(src, 0, 0, (int) width, (int) height, matrix, true);
    }

    public static String encoderBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(datas);
    }

    public static byte[] decodeBitmap(String base64Str) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(base64Str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
