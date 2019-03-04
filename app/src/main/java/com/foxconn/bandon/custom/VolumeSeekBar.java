package com.foxconn.bandon.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;


public class VolumeSeekBar extends FrameLayout {
    private static final String TAG = VolumeSeekBar.class.getSimpleName();

    private View mThumbView;
    private FrameLayout.LayoutParams mThumbViewFLP;
    private View mColorTrackView;
    private FrameLayout.LayoutParams mColorTrackViewFLP;
    private int mTrackHeight;
    private int mTrackWidth;
    private int mThumbSize;
    private int mMaxLevel;
    private int[] mThumbMarginLeftLevel;
    private boolean onlyTouch;
    private boolean isEnable = true;
    private VolumeSeekBar.LevelListener levelListener;

    public interface LevelListener {
        void onLevel(int level);
    }

    public VolumeSeekBar(@NonNull Context context) {
        super(context);
    }

    public VolumeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // read user define attr
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VolumeSeekBar, 0, 0);
        try {
            mTrackHeight = a.getInteger(R.styleable.VolumeSeekBar_trackHeight, 20);
            mTrackWidth = a.getInteger(R.styleable.VolumeSeekBar_trackWidth, 100);
            mThumbSize = a.getInteger(R.styleable.VolumeSeekBar_thumbSize, 50);
        } finally {
            a.recycle();
        }

        addTrackView();
        addColorTrackView();
        addThumbView();
        setMinimumHeight(mThumbSize * 2);
        setMinimumWidth(mTrackWidth + mThumbSize);
        initScrollLevel();

    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public void scrollByLevel(int level) {
        if (level < 0) {
            level = 0;
        } else if (level > mMaxLevel) {
            level = mMaxLevel;
        }

        updateThumbMarginLeft(mThumbMarginLeftLevel[level]);
        updateColorTrackView(mThumbMarginLeftLevel[level]);
    }

    public void setLevelListener(LevelListener levelListener) {
        this.levelListener = levelListener;
    }

    private void addTrackView() {
        //Define track drawable background
        GradientDrawable trackDrawable = new GradientDrawable();
        trackDrawable.setColor(Color.parseColor("#EFEFEF"));
        trackDrawable.setCornerRadius(45f);

        //set track layout
        FrameLayout.LayoutParams trackViewFLP = new FrameLayout.LayoutParams(mTrackWidth, mTrackHeight);
        trackViewFLP.gravity = Gravity.CENTER;

        //add view
        View trackView = new View(getContext());
        trackView.setLayoutParams(trackViewFLP);
        trackView.setBackground(trackDrawable);
        addView(trackView);
    }

    private void addColorTrackView() {
        //Define track drawable background
        GradientDrawable trackDrawable = new GradientDrawable();
        trackDrawable.setColor(Color.parseColor("#4BC88C"));
        trackDrawable.setCornerRadius(45f);

        //set track layout
        mColorTrackViewFLP = new FrameLayout.LayoutParams(0, mTrackHeight);
        mColorTrackViewFLP.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        mColorTrackViewFLP.leftMargin = mThumbSize / 2;

        //add view
        mColorTrackView = new View(getContext());
        mColorTrackView.setLayoutParams(mColorTrackViewFLP);
        mColorTrackView.setBackground(trackDrawable);
        addView(mColorTrackView);
    }


    private void addThumbView() {

        //set thumb layout
        mThumbViewFLP = new FrameLayout.LayoutParams(mThumbSize, mThumbSize);
        mThumbViewFLP.gravity = Gravity.CENTER_VERTICAL | Gravity.START;

        //add view
        mThumbView = new View(getContext());
        mThumbView.setLayoutParams(mThumbViewFLP);
        //mThumbView.setBackground();
        mThumbView.setBackgroundResource(R.drawable.seekbar_thumb_circle);


        //defining shadows
        mThumbView.setElevation(10);
        addView(mThumbView);
    }

    private void initScrollLevel() {

        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        //get alarm max level
        if (null != mAudioManager) {
            mMaxLevel = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        }

        mThumbMarginLeftLevel = new int[mMaxLevel + 1];

        for (int i = 0; i <= mMaxLevel; i++) {
            mThumbMarginLeftLevel[i] = i * (mTrackWidth / mMaxLevel);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                onlyTouch = true;
                return true;
            case (MotionEvent.ACTION_MOVE):
                onlyTouch = false;

                if (isEnable) {
                    updateThumbPosition(event.getX());
                } else {
                    LogUtils.d(TAG, "volume seek bar is disable");
                }

                return true;
            case (MotionEvent.ACTION_UP):
                if (onlyTouch) {
                    if (isEnable) {
                        updateThumbPosition(event.getX());
                    } else {
                        LogUtils.d(TAG, "volume seek bar is disable");
                    }
                }

                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void updateThumbPosition(float x) {
        //quantify x to level
        int level = quantifyToLevel(x);

        //notify level change
        if (levelListener != null) {
            levelListener.onLevel(level);
        }

        //update thumb margin left by quantify result
        if (level < mThumbMarginLeftLevel.length) {
            updateThumbMarginLeft(mThumbMarginLeftLevel[level]);
            updateColorTrackView(mThumbMarginLeftLevel[level]);
        }
    }


    // quantify x to level
    private int quantifyToLevel(float x) {

        if (x < 0) {
            x = 0;

        } else if (x > mTrackWidth) {
            x = mTrackWidth;
        }

        int level = 0;
        for (int i = 0; i <= mMaxLevel; i++) {
            if (x < mThumbMarginLeftLevel[i]) {
                break;
            } else {
                level = i;
            }
        }
        return level;
    }


    private void updateThumbMarginLeft(int marginLeft) {
        mThumbViewFLP.leftMargin = marginLeft;
        mThumbView.setLayoutParams(mThumbViewFLP);
    }

    private void updateColorTrackView(int width) {
        mColorTrackViewFLP.width = width;
        mColorTrackView.setLayoutParams(mColorTrackViewFLP);
    }
}
