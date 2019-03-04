package com.foxconn.bandon.main.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;

public class TimesUpAlert extends FrameLayout {

    private static final String TAG = TimesUpAlert.class.getSimpleName();

    private static final long DURATION = 500;
    private View mText;
    private MediaPlayer mMediaPlayer;
    private AlphaAnimation mShow;
    private AlphaAnimation mHide;
    private Callback callback;

    public TimesUpAlert(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.dialog_times_up, this);
        mText = findViewById(R.id.text);
        findViewById(R.id.dismiss_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.click();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        playMedia();
        initAnimation();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mText.clearAnimation();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void playMedia() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Uri uri = Uri.parse("content://media/internal/audio/media/7");
            mMediaPlayer.setDataSource(getContext(), uri);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            LogUtils.e(TAG, "exception:" + e.toString());
        }
    }

    private void initAnimation() {
        mShow = new AlphaAnimation(1.0F, 0.0F);
        mShow.setDuration(DURATION);

        mHide = new AlphaAnimation(0.0F, 1.0F);
        mHide.setDuration(DURATION);

        mShow.setAnimationListener(listener);
        mHide.setAnimationListener(listener);
        mText.startAnimation(mShow);
    }

    Animation.AnimationListener listener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mShow) {
                mText.startAnimation(mHide);
            } else {
                mText.startAnimation(mShow);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    interface Callback {
        void click();
    }
}

