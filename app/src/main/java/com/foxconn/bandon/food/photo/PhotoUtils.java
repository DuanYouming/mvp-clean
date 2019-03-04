package com.foxconn.bandon.food.photo;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;

import com.foxconn.bandon.cropper.model.CropperFactor;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {

    private static final String TAG = PhotoUtils.class.getSimpleName();

    private static final float H_FACTOR = 0.3F;
    private static final float W_FACTOR = 0.2F;
    private static final float SCALE = 0.5f;
    private static final double ANGLE = Math.toRadians(20);
    private static final int CAMERA_FIRST = 0;
    private static final int CAMERA_SECOND = 1;
    private static final int CAMERA_THIRD = 2;
    private static final int CAMERA_FORTH = 3;
    private static PhotoUtils mInstance;
    private static final String IMAGES_BASE = Constant.FRIDGE_IMAGES_PATH;
    private static final String PHOTOS_BASE = Constant.CAMERA_PHOTOS_PATH;
    private static final String[] urls = {"camera_0.jpg", "camera_1.jpg", "camera_2.jpg", "camera_3.jpg"};


    private PhotoUtils() {

    }

    public static PhotoUtils getInstance() {
        if (null == mInstance) {
            synchronized (PhotoUtils.class) {
                if (null == mInstance) {
                    mInstance = new PhotoUtils();
                }
            }
        }
        return mInstance;
    }


    public String getImageUrl(int index) {
        File file = new File(IMAGES_BASE);
        File image = new File(file, urls[index]);
        return image.exists() ? image.getPath() : null;
    }

    public String getSrcPhoto(int index) {
        return PHOTOS_BASE + "/" + urls[index];
    }

    public String getCameraPhoto(int index) {

        String url = PHOTOS_BASE + "/" + urls[index];
        Bitmap bitmap = getSrcBitmap(url);
        if (null == bitmap) {
            LogUtils.d(TAG, "bitmap is null");
            return null;
        }

        InfluenceFactor factor = initFactor(index);
        CropperFactor cFactor = getCropperFromSP(index);
        if (!checkCropperFactor(cFactor)) {
            cFactor = getCropper(bitmap, factor);
        }
        if(checkCropperFactor(cFactor)) {
            float wScale = bitmap.getWidth() / cFactor.getBitmapWidth();
            float hScale = bitmap.getHeight() / cFactor.getBitmapHeight();
            LogUtils.d(TAG, "CropperFactor:" + cFactor.toString());
            LogUtils.d(TAG, "wScale:" + wScale + "  hScale:" + hScale);
            bitmap = Bitmap.createBitmap(bitmap, (int) (cFactor.getLeft() * wScale), (int) (cFactor.getTop() * hScale), (int) (cFactor.getWidth() * wScale), (int) (cFactor.getHeight() * hScale));
        }
        return save(bitmap, index);
    }

    private int getColorBrightness(int color) {
        return (int) ((Math.max(Color.red(color), Math.max(Color.green(color), Color.blue(color))) / 255.0F) * 100);
    }

    private int getColorSaturation(int color) {
        int max = Math.max(Color.red(color), Math.max(Color.green(color), Color.blue(color)));
        if (max == 0) {
            return max;
        }
        int min = Math.min(Color.red(color), Math.min(Color.green(color), Color.blue(color)));
        return (int) ((1 - (min * 1.0F / max)) * 100);
    }

    private boolean isValidPoint(int color, int brightness, int saturation) {
        int hsbB = getColorBrightness(color);
        int hsbS = getColorSaturation(color);
        return hsbB > brightness && hsbS < saturation;
    }

    private Bitmap getSrcBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (null == bitmap) {
            return null;
        }
        return bitmap;
    }

    private int getLeftPoint(Bitmap bitmap, int brightness, int saturation) {

        Point left = new Point(0, 0);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int fWidth = (int) (width * 0.1F);
        int tWidth = (int) (width * W_FACTOR);
        int fHeight = (int) (height * H_FACTOR);
        int toHeight = (int) (height * (1 - H_FACTOR));

        for (int i = fWidth; i < tWidth; i++) {
            for (int j = fHeight; j < toHeight; j++) {
                int color = bitmap.getPixel(i, j);
                if (isValidPoint(color, brightness, saturation)) {
                    left.x = i;
                    left.y = j;
                    return (int) (left.x / SCALE);
                }
            }
        }
        return left.x;
    }

    private int getRightPoint(Bitmap bitmap, int brightness, int saturation) {
        Point right = new Point(0, 0);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int tWidth = (int) (width * (1 - W_FACTOR));
        int fHeight = (int) (height * H_FACTOR);
        int toHeight = (int) (height * (1 - H_FACTOR));
        int fWidth = (int) (width * 0.9F);
        for (int i = fWidth; i >= tWidth; i--) {
            for (int j = fHeight; j < toHeight; j++) {
                int color = bitmap.getPixel(i, j);
                if (isValidPoint(color, brightness, saturation)) {
                    right.x = i;
                    right.y = j;
                    return (int) (right.x / SCALE);
                }
            }
        }
        return right.x;
    }

    private int getTopPoint(Bitmap bitmap, int brightness, int saturation) {
        Point top = new Point(0, 0);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int fWidth = (int) (width * W_FACTOR);
        int tWidth = (int) (width * (1 - W_FACTOR));
        int fHeight = 0;
        int toHeight = (int) (height * H_FACTOR);
        for (int j = fHeight; j < toHeight; j++) {
            for (int i = fWidth; i < tWidth; i++) {
                int color = bitmap.getPixel(i, j);
                if (isValidPoint(color, brightness, saturation)) {
                    top.x = i;
                    top.y = j;
                    return (int) (top.y / SCALE);
                }
            }
        }
        return top.y;
    }

    private CropperFactor getCropper(Bitmap bitmap, InfluenceFactor factor) {

        Bitmap scale = ImageUtils.scaleBitmap(bitmap, SCALE);
        int brightness = factor.getBrightness();
        int saturation = factor.getSaturation();
        int leftX = getLeftPoint(scale, brightness, saturation);
        int rightX = getRightPoint(scale, brightness, saturation);
        int topY = getTopPoint(scale, brightness, saturation);
        int radius = (rightX - leftX) / 2;
        int cWidth;
        int cHeight;
        if ((leftX + radius * 2) < bitmap.getWidth()) {
            cWidth = radius * 2;
        } else {
            cWidth = bitmap.getWidth() - leftX;
        }
        if ((topY + radius * 2) < bitmap.getHeight()) {
            cHeight = radius * 2;
        } else {
            cHeight = bitmap.getHeight() - topY;
        }

        int dx = factor.getDx();
        int dy = factor.getDy();
        int dw = factor.getDw();
        int dh = factor.getDh();

        int startX = (int) ((1 - Math.cos(ANGLE)) * radius) + dx;
        int startY = (int) ((1 - Math.sin(ANGLE)) * radius) + dy;
        int nWidth = (int) (2 * radius * Math.cos(ANGLE)) - dw;
        int nHeight = (int) (2 * radius * Math.sin(ANGLE)) - dh;

        if ((startX + nWidth) >= cWidth) {
            nWidth = cHeight - startX;
        }
        if ((startY + nHeight) >= cHeight) {
            nHeight = cHeight - startY;
        }
        CropperFactor cFactor = new CropperFactor(startX + leftX, startY + topY, nWidth, nHeight);
        if (!scale.isRecycled()) {
            scale.recycle();
        }
        cFactor.setBitmapWidth(bitmap.getWidth());
        cFactor.setBitmapHeight(bitmap.getHeight());
        return cFactor;
    }

    private CropperFactor getCropperFromSP(int index) {
        Context context = BandonApplication.getInstance().getApplicationContext();
        CropperFactor factor = null;
        String jsonStr = PreferenceUtils.getString(context, Constant.SP_PHOTOS_NAME, String.valueOf(index), "");
        if (!TextUtils.isEmpty(jsonStr)) {
            Gson gson = new Gson();
            factor = gson.fromJson(jsonStr, CropperFactor.class);
        }
        return factor;
    }

    private InfluenceFactor initFactor(int index) {
        InfluenceFactor factor;
        switch (index) {
            case CAMERA_FORTH:
                factor = new InfluenceFactor(50, 10, 100, 120, 150, 0);
                break;
            case CAMERA_THIRD:
                factor = new InfluenceFactor(50, 10, 120, 220, 120, 80);
                break;
            case CAMERA_SECOND:
                factor = new InfluenceFactor(50, 10, 100, 250, 120, 120);
                break;
            case CAMERA_FIRST:
                factor = new InfluenceFactor(50, 10, 100, 180, 120, 0);
                break;
            default:
                factor = new InfluenceFactor();
                break;
        }
        return factor;
    }

    private String save(Bitmap bitmap, int index) {
        File file = new File(IMAGES_BASE);
        if (!file.exists()) {
            LogUtils.d(TAG, "make dir[" + file.getPath() + "] " + file.mkdirs());
        }
        File image = new File(file, urls[index]);
        FileOutputStream fos;
        String result;
        try {
            fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            result = image.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            result =null;
        } finally {
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return result;
    }

    private boolean checkCropperFactor(CropperFactor factor) {
        return null != factor && !(factor.getLeft() < 0 | factor.getTop() < 0 | factor.getHeight() < 0 | factor.getWidth() < 0) && !(factor.getBitmapHeight() < 0 | factor.getBitmapWidth() < 0);
    }
}
