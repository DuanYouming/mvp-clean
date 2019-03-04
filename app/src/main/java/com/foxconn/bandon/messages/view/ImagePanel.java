package com.foxconn.bandon.messages.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foxconn.bandon.utils.RotationScaleUtils;
import com.foxconn.bandon.messages.insert.InsertImage;
import com.foxconn.bandon.utils.DragUtils;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ImagePanel extends FrameLayout {
    private static final String TAG = ImagePanel.class.getSimpleName();
    private static final float MAX_SCALE = 2.0f;
    private static final float MIN_SCALE = 0.5f;
    private HashMap<String, Integer> mImageWidthMap;
    private HashMap<String, Integer> mImageHeightMap;
    private Map<String, ImageAction> mImageActionMap;
    private Map<String, Bitmap> mBitmapMap;
    private Boolean mDraggable;
    private Callback mCallback;
    private Gson mGson = new GsonBuilder().create();

    public ImagePanel(@NonNull Context context) {
        super(context);
    }

    public ImagePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mImageWidthMap = new HashMap<>();
        mImageHeightMap = new HashMap<>();
        mImageActionMap = new HashMap<>();
        mBitmapMap = new HashMap<>();
        mDraggable = false;
    }

    public interface Callback {
        void onMove();
    }

    public void setActionData(String actionData) {
        List<ImageAction> iActionList = mGson.fromJson(actionData, new TypeToken<ArrayList<ImageAction>>() {
        }.getType());
        LogUtils.d(TAG, "actionData:" + actionData);
        for (ImageAction imageAction : iActionList) {
            addImage(imageAction);
        }

    }

    public void add(InsertImage image) {
        boolean isEmotion = image.isEmotion();
        ImageAction action = new ImageAction(image);
        if (isEmotion) {
            addEmotion(action);
        } else {
            addImage(action);
        }
    }

    public void addImage(ImageAction imageAction) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap;
        if (imageAction.image.isLocal()) {
            bitmap = BitmapFactory.decodeFile(imageAction.image.getPath(), options);
        } else {
            bitmap = ImageUtils.getBitmapFromAssets(getContext(), imageAction.image.getPath());
        }
        bitmap = ImageUtils.resize(bitmap, getWidth(), getHeight());
        addImage(bitmap, imageAction, imageAction.x, imageAction.y, imageAction.angle, imageAction.scale);
    }


    public void addEmotion(ImageAction imageAction) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap;
        if (imageAction.image.isLocal()) {
            bitmap = BitmapFactory.decodeFile(imageAction.image.getPath(), options);
        } else {
            bitmap = ImageUtils.getBitmapFromAssets(getContext(), imageAction.image.getPath());
        }
        bitmap = ImageUtils.resize(bitmap, getWidth(), getHeight());
        addImage(bitmap, imageAction, imageAction.x, imageAction.y, imageAction.angle, imageAction.scale);
    }


    public void addImage(Bitmap bitmap, ImageAction imageAction, int x, int y, float angle, float scale) {
        ImageView imgView = new ImageView(getContext());
        imgView.setImageBitmap(bitmap);
        FrameLayout imageContainer = new FrameLayout(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        imageContainer.addView(imgView, lp);

        String uuid = UUID.randomUUID().toString();
        imageContainer.setTag(uuid);
        mImageHeightMap.put(uuid, bitmap.getHeight());
        mImageWidthMap.put(uuid, bitmap.getWidth());

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        imageAction.setX(lp.leftMargin);
        imageAction.setY(lp.topMargin);
        addView(imageContainer, lp);
        mBitmapMap.put(uuid, bitmap);
        mImageActionMap.put(uuid, imageAction);
        bindDragEvent(imageContainer);
        rotationAndScale(imageContainer, angle, scale);
    }

    public void reset() {
        removeAllViews();
        mImageActionMap.clear();
        mImageHeightMap.clear();
        mImageWidthMap.clear();
        for (String uuid : mBitmapMap.keySet()) {
            mBitmapMap.get(uuid).recycle();
        }
        mBitmapMap.clear();
    }

    public void setDraggable(boolean draggable) {
        int childCount = getChildCount();
        if (mDraggable != draggable) {
            if (draggable) {
                for (int i = 0; i < childCount; i++) {
                    bindDragEvent((ViewGroup) getChildAt(i));
                }
            } else {
                for (int i = 0; i < childCount; i++) {
                    DragUtils.disableDrag(getChildAt(i));
                }
            }
            mDraggable = draggable;
        }
    }

    public String getImageActionData() {
        List<ImageAction> iActionList = new ArrayList<>(mImageActionMap.values());
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            iActionList.sort(Comparator.comparingLong(o -> o.update));
        } else {
            Collections.sort(iActionList, (o1, o2) -> o1.update >= o2.update ? 0 : 1);
        }*/
        Collections.sort(iActionList, (o1, o2) -> o1.update >= o2.update ? 0 : 1);
        return mGson.toJson(iActionList);
    }

    public static class ImageAction {
        private int x;
        private int y;
        private float scale;
        private float angle;
        private long update;
        private InsertImage image;

        ImageAction(InsertImage image) {
            this.x = 0;
            this.y = 0;
            this.scale = 1.0f;
            this.angle = 0.0f;
            this.update = 0;
            this.image = image;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        private void setScale(float scale) {
            this.scale = scale;
        }

        private void setAngle(float angle) {
            this.angle = angle;
        }
    }

    Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int childCount = getChildCount();
        ViewGroup viewGroup;
        String uuid;
        int xOffset, yOffset;
        for (int i = 0; i < childCount; i++) {
            canvas.save();
            viewGroup = (ViewGroup) getChildAt(i);
            uuid = (String) viewGroup.getTag();

            Matrix matrix = new Matrix();
            matrix.postRotate(mImageActionMap.get(uuid).angle, mBitmapMap.get(uuid).getWidth() / 2, mBitmapMap.get(uuid).getHeight() / 2);
            matrix.postScale(mImageActionMap.get(uuid).scale, mImageActionMap.get(uuid).scale);

            xOffset = viewGroup.getChildAt(0).getLeft();
            yOffset = viewGroup.getChildAt(0).getTop();
            canvas.translate(mImageActionMap.get(uuid).x + xOffset, mImageActionMap.get(uuid).y + yOffset);
            canvas.drawBitmap(mBitmapMap.get(uuid), matrix, null);
            canvas.restore();

        }
        return bitmap;
    }

    private void bindDragEvent(final ViewGroup imageContainer) {
        DragUtils.enableDrag(imageContainer, this, 0, new DragUtils.Callback() {
            private float initRotateAngle = Float.MAX_VALUE;
            private float initScale = Float.MAX_VALUE;
            String uuid = ((String) imageContainer.getTag());

            @Override
            public void onDragMove(int x, int y) {
                mImageActionMap.get(uuid).setX(x);
                mImageActionMap.get(uuid).setY(y);
                if (mCallback != null) {
                    mCallback.onMove();
                }
            }

            @Override
            public void onDragEnd() {
                initRotateAngle = Float.MAX_VALUE;
                initScale = Float.MAX_VALUE;
                mImageActionMap.get(uuid).update = System.currentTimeMillis();
            }

            @Override
            public void onLongPress() {
            }

            @Override
            public void onHold() {

            }

            @Override
            public void onRotationAndScale(float angle, float scale) {

                if (initRotateAngle == Float.MAX_VALUE) {
                    initRotateAngle = imageContainer.getChildAt(0).getRotation();
                }

                float adjustAngle = initRotateAngle + angle;
                if (initScale == Float.MAX_VALUE) {
                    initScale = imageContainer.getChildAt(0).getWidth() / (mImageWidthMap.get(uuid) * 1f);
                }

                float adjustScale = initScale * scale;
                if (adjustScale < MIN_SCALE) {
                    adjustScale = MIN_SCALE;
                }

                if (adjustScale > MAX_SCALE) {
                    adjustScale = MAX_SCALE;
                }
                rotationAndScale(imageContainer, adjustAngle, adjustScale);
            }

        });
    }

    private void rotationAndScale(final ViewGroup imageContainer, float adjustAngle, float adjustScale) {

        String uuid = (String) imageContainer.getTag();
        int containerW = (int) RotationScaleUtils.optimalWidth(adjustAngle, adjustScale, mImageWidthMap.get(uuid), mImageHeightMap.get(uuid));
        int containerH = (int) RotationScaleUtils.optimalHeight(adjustAngle, adjustScale, mImageWidthMap.get(uuid), mImageHeightMap.get(uuid));

        if (containerW <= getWidth() && containerH <= getHeight()) {
            //check image still in boundary

            mImageActionMap.get(uuid).setAngle(adjustAngle);
            mImageActionMap.get(uuid).setScale(adjustScale);

            //rotation
            imageContainer.getChildAt(0).setRotation(adjustAngle);

            //update image view width & height
            imageContainer.getChildAt(0).getLayoutParams().width = (int) (adjustScale * mImageWidthMap.get(uuid));
            imageContainer.getChildAt(0).getLayoutParams().height = (int) (adjustScale * mImageHeightMap.get(uuid));
            imageContainer.getChildAt(0).requestLayout();

            //update container width & height
            imageContainer.getLayoutParams().width = containerW;
            imageContainer.getLayoutParams().height = containerH;
            imageContainer.requestLayout();
        }
    }

}
