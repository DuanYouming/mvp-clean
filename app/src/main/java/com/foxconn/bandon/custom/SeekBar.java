package com.foxconn.bandon.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.DragUtils;
import com.foxconn.bandon.utils.LogUtils;

public class SeekBar extends FrameLayout {

    private static final String TAG = SeekBar.class.getSimpleName();

    public interface Callback {

        void onProgressChanged(int progress);

        void onLevel(int level);

    }

    private static final int MAX = 100;
    private static final int MIN = 0;
    private int maxLeft;
    private View mThumbMovingArea;
    private View mThumb;
    private View mProgressBar;

    private Callback mCallback;
    private int mMin;
    private int mMax;
    private int mQuantifyX;
    private int mProgress;
    private int mMaxProgress;

    public SeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
        mQuantifyX = 0;
        mMin = MIN;
        mMax = MAX;
        mMaxProgress = mMax - mMin;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    protected void inflateLayout(Context context) {
        inflate(context, R.layout.seek_bar, this);
    }

    public void setValueRange(int min, int max) {
        this.mMax = max;
        this.mMin = min;
        mMaxProgress = mMax - mMin;
    }

    private void setup() {
        mThumb = findViewById(R.id.thumb);
        mThumbMovingArea = findViewById(R.id.thumb_moving_area);
        mThumbMovingArea.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int left = (int) (event.getX() - mThumb.getWidth() / 2);
                int progress = (int) ((left * 1.0F / maxLeft) * mMaxProgress);

                setProgress(progress);
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (mCallback != null) {
                        int value = mProgress + mMin;
                        mCallback.onProgressChanged(value);
                    }
                }

                return true;
            }
        });

        DragUtils.enableDrag(mThumb, mThumbMovingArea, mQuantifyX, new DragUtils.Callback() {
            @Override
            public void onDragMove(int x, int y) {
                int progress = (int) (((float) x) / maxLeft * mMaxProgress);
                FrameLayout.LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
                lp.width = mThumbMovingArea.getLeft() + x + mThumb.getWidth() / 2 - lp.leftMargin;
                mProgressBar.setLayoutParams(lp);
                mProgress = progress;
            }

            @Override
            public void onDragEnd() {
                if (mCallback != null) {
                    int value = mProgress + mMin;
                    setProgress(mProgress);
                    mCallback.onProgressChanged(value);
                }
            }

            @Override
            public void onQuantifyLevel(int level) {
                if (mCallback != null) {
                    mCallback.onLevel(level);
                }
            }
        });

        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mThumb.layout(mThumb.getLeft(), 0, mThumb.getLeft() + mThumb.getWidth(), mThumb.getHeight());
        maxLeft = mThumbMovingArea.getWidth() - mThumb.getWidth();
    }


    public void setProgress(int progress) {
        if (progress > mMaxProgress) {
            progress = mMaxProgress;
        } else if (progress < 0) {
            progress = 0;
        }
        mProgress = progress;
        LogUtils.d(TAG, "progress:" + progress);
        post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (LayoutParams) mThumb.getLayoutParams();
                int thumbLeft = getThumbLeft(mProgress);
                lp.leftMargin = thumbLeft;
                mThumb.setLayoutParams(lp);
                lp = (LayoutParams) mProgressBar.getLayoutParams();
                lp.width = mThumbMovingArea.getLeft() + thumbLeft + mThumb.getWidth() / 2 - lp.leftMargin;
                mProgressBar.setLayoutParams(lp);
                requestLayout();
            }
        });
    }

    public void setValue(int value) {
        int progress = value - mMin;
        setProgress(progress);
    }

    private int getThumbLeft(int progress) {
        LogUtils.d(TAG, "maxLeft:" + maxLeft + " mMax-mMin:" + mMaxProgress);
        return (int) (maxLeft * (progress * 1.0F / mMaxProgress));
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

}