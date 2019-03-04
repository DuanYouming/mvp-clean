package com.foxconn.bandon.messages.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.foxconn.bandon.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends View implements View.OnTouchListener {
    private static final String TAG = DrawingPanel.class.getSimpleName();
    protected static final float DEFAULT_WIDTH = 10F;
    private int mMode = MessageBoardFragment.MODE_PEN;
    private Paint mDrawingPaint;
    private float mSelectedStrokeWidth;
    private int mSelectedColor = Color.BLACK;
    private List<Action> mActions = new ArrayList<>();
    private DrawingPath mWritingPath;
    private Bitmap[] mDrawingBitmap;
    private Canvas[] mDrawingBitmapCanvas;
    private int mDrawingBitmapFlip;
    private PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private Gson gson = new GsonBuilder().create();
    private Callback mCallback;
    private boolean mEnable = true;

    public DrawingPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        mDrawingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawingPaint.setStyle(Paint.Style.STROKE);
        mDrawingPaint.setColor(Color.BLACK);
        mDrawingPaint.setStrokeCap(Paint.Cap.ROUND);
        mSelectedStrokeWidth = DEFAULT_WIDTH;
    }

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    void setMode(int mode) {
        mMode = mode;
    }

    void setPaintColor(int color) {
        mSelectedColor = color;
    }

    void setPaintStrokeWidth(float width) {
        mSelectedStrokeWidth = width;
    }

    void deleteLastPath() {
        if (mActions.isEmpty()) {
            return;
        }
        mActions.remove(mActions.size() - 1);
        postInvalidateOnAnimation();
    }

    Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        LogUtils.d(TAG, "width:" + getWidth() + ",height:" + getHeight());
        draw(canvas);
        return bitmap;
    }

    String getActionData() {
        return gson.toJson(mActions);
    }

    void setActionData(String actionData) {
        mActions = gson.fromJson(actionData, new TypeToken<ArrayList<DrawingPath>>() {
        }.getType());
        postInvalidateOnAnimation();
    }

    public void setDrawable(Boolean enable) {
        mEnable = enable;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mEnable) {
            return false;
        }

        final int action = event.getAction();
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mWritingPath = new DrawingPath();
                mWritingPath.mode = mMode;
                mWritingPath.color = mSelectedColor;
                mWritingPath.width = mSelectedStrokeWidth;
                mWritingPath.moveTo(x, y);

                if (mMode != MessageBoardFragment.MODE_ERASER || !mActions.isEmpty()) {
                    mActions.add(mWritingPath);
                }
                if (mCallback != null) {
                    mCallback.onActionDown();
                }
                postInvalidateOnAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                mWritingPath.lineTo(x, y);
                postInvalidateOnAnimation();
                break;
            case MotionEvent.ACTION_UP:
                mCallback.onActionUp();
                break;
            default:
                break;
        }
        return true;
    }

    void reset() {
        mMode = MessageBoardFragment.MODE_PEN;
        mActions.clear();
        mDrawingBitmap = null;
        mDrawingBitmapCanvas = null;
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawingBitmap == null) {
            final int numBuffer = 2;
            mDrawingBitmap = new Bitmap[numBuffer];
            mDrawingBitmapCanvas = new Canvas[numBuffer];
            for (int i = 0; i < numBuffer; i++) {
                mDrawingBitmap[i] = Bitmap.createBitmap(
                        getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                mDrawingBitmapCanvas[i] = new Canvas(mDrawingBitmap[i]);
            }
            mDrawingBitmapFlip = 0;
        }
        mDrawingBitmapFlip = (mDrawingBitmapFlip + 1) % 2;
        Bitmap drawingBitmap = mDrawingBitmap[mDrawingBitmapFlip];
        Canvas drawingCanvas = mDrawingBitmapCanvas[mDrawingBitmapFlip];
        drawingBitmap.eraseColor(Color.TRANSPARENT);
        for (Action action : mActions) {
            if (action instanceof DrawingPath) {
                DrawingPath path = (DrawingPath) action;
                if (path.mode == MessageBoardFragment.MODE_PEN) {
                    mDrawingPaint.setColor(path.color);
                    mDrawingPaint.setXfermode(null);
                } else if (path.mode == MessageBoardFragment.MODE_ERASER) {
                    mDrawingPaint.setXfermode(mPorterDuffXfermode);
                }
                mDrawingPaint.setStrokeWidth(path.width);
                drawingCanvas.drawPath(path.getPath(), mDrawingPaint);
            }
        }
        canvas.drawBitmap(drawingBitmap, 0, 0, null);
    }

    private static class Action {

    }

    private static class DrawingPath extends Action {
        int mode = MessageBoardFragment.MODE_PEN;
        int color = Color.BLACK;
        float width = 10f;
        List<Location> pathLoc = new ArrayList<>();

        void moveTo(int x, int y) {
            pathLoc.add(new Location(x, y));
        }

        void lineTo(int x, int y) {
            pathLoc.add(new Location(x, y));
        }

        Path getPath() {
            Path path = new Path();
            int size = pathLoc.size();
            if (size > 0) {
                path.moveTo(pathLoc.get(0).x, pathLoc.get(0).y);
                for (int i = 1; i < size; i++) {
                    path.lineTo(pathLoc.get(i).x, pathLoc.get(i).y);
                }
            }
            return path;
        }
    }

    private static class Location {
        int x;
        int y;

        Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public interface Callback {
        void onActionDown();

        void onActionUp();
    }

}
