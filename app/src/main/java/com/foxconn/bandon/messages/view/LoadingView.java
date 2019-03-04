package com.foxconn.bandon.messages.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;



public class LoadingView extends FrameLayout {
    private static final String TAG = LoadingView.class.getSimpleName();
    private ImageView mLoading;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }
    private void setup() {
        inflate(getContext(), R.layout.memo_loading, this);
        //ImageView blurry = (ImageView) findViewById(R.id.blur);
        mLoading = findViewById(R.id.loading);
        rotationLoading();
    }
    private void rotationLoading() {
        LogUtils.d(TAG, "rotationLoading");
        mLoading.setBackgroundResource(R.drawable.memo_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) mLoading.getBackground();
        frameAnimation.start();
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d(TAG, "onDetachedFromWindow");
        mLoading.clearAnimation();
        removeAllViews();
    }
}
