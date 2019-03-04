package com.foxconn.bandon.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.foxconn.bandon.R;

public class TempSettingBar extends View {

    private static final int SCALE = 4;
    private Rect rectBounds;
    private Paint mPaint;
    private int mHeight;
    private int mWidth;
    private int size = 16;
    private int mDefaultColor = Color.GRAY;
    private int mColor;
    private int mProgress = 0;
    private ProgressChangedCallback callback;

    public TempSettingBar(Context context) {
        super(context, null);
    }

    public TempSettingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        rectBounds = new Rect();
        mColor = Color.parseColor("#4AABFE");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < size; i++) {
            drawSegment(canvas, i);
        }
    }

    private void drawSegment(Canvas canvas, int index) {
        if (index < mProgress) {
            mPaint.setColor(mColor);
        } else {
            mPaint.setColor(mDefaultColor);
        }
        int left = index * (SCALE + 1) * getSpaceWidth();
        int right = left + (SCALE * getSpaceWidth());
        rectBounds.set(left, 0, right, mHeight);
        canvas.drawRect(rectBounds, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getResources().getDimensionPixelSize(R.dimen.progress_bar_width);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getResources().getDimensionPixelSize(R.dimen.progress_bar_height);
        }
        mWidth = width;
        mHeight = height;
        setMeasuredDimension(width, height);
    }

    private int getSpaceWidth() {
        int spaceNum = (size - 1) + size * SCALE;
        return (int) (mWidth / (spaceNum * 1.0F));
    }

    public void setSize(int size) {
        this.size = size;
        invalidate();
    }

    public void setDefaultColor(int defaultColor) {
        this.mDefaultColor = defaultColor;
        invalidate();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            mProgress = 0;
        } else if (progress <= size) {
            this.mProgress = progress;
        } else {
            this.mProgress = size;
        }
        invalidate();
        invokeCallback();
    }

    public void setCallback(ProgressChangedCallback callback) {
        this.callback = callback;
    }

    public void down() {
        if (mProgress > 0) {
            mProgress--;
            Log.d("FEYOND", "mProgress:" + mProgress);
            invalidate();
            invokeCallback();
        }
    }

    public void up() {
        if (mProgress < size) {
            mProgress++;
            Log.d("FEYOND", "mProgress:" + mProgress);
            invalidate();
            invokeCallback();
        }
    }

    private void invokeCallback() {
        if(null !=callback) {
            callback.progressChanged(mProgress);
        }
    }

    public interface ProgressChangedCallback {

        void progressChanged(int progress);
    }
}
