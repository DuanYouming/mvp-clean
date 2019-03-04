package com.foxconn.bandon.recipe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;

import java.util.Formatter;
import java.util.Locale;


public class VideoController extends MediaController {

    private static final String TAG = VideoController.class.getSimpleName();
    private static final int sDefaultTimeout = 10000;
    private final Context mContext;
    private MediaPlayerControl mPlayer;
    private OnStateChangedListener stateChangedListener;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private ImageButton mPauseButton;
    private ImageButton mFfwdButton;
    private ImageButton mRewButton;
    private View mRoot;
    private CharSequence mPlayDescription;
    private CharSequence mPauseDescription;
    private boolean mDragging;
    private boolean mShowing;

    public VideoController(Context context) {
        this(context, null);
    }

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setup();
    }

    private void setup() {
        mRoot = inflate(mContext, R.layout.layout_video_controller, this);
        initControllerView(mRoot);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null) {
            initControllerView(mRoot);
        }
    }


    public void setMediaPlayer(MediaPlayerControl player) {
        if (player instanceof View) {
            ((View) player).setOnTouchListener(mTouchListener);
        }
        mPlayer = player;
        updatePausePlay();
    }

    public void setAnchorView(View view) {
        LogUtils.d(TAG, "setAnchorView");
        mRoot.setVisibility(INVISIBLE);
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.stateChangedListener = listener;
    }

    private void initControllerView(View v) {
        mPlayDescription = "播放";
        mPauseDescription = "暫停";
        mPauseButton = v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mFfwdButton = v.findViewById(R.id.ffwd);
        if (mFfwdButton != null) {
            mFfwdButton.setOnClickListener(mFfwdListener);
            mFfwdButton.setVisibility(View.VISIBLE);
        }

        mRewButton = v.findViewById(R.id.rew);
        if (mRewButton != null) {
            mRewButton.setOnClickListener(mRewListener);
            mRewButton.setVisibility(View.VISIBLE);
        }

        mProgress = v.findViewById(R.id.controller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }
        mEndTime = v.findViewById(R.id.time);
        mCurrentTime = v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mShowing) {
                    hide();
                } else {
                    show();
                }
            }
            return false;
        }
    };


    public void hide() {
        if (mShowing) {
            try {
                mRoot.setVisibility(INVISIBLE);
                removeCallbacks(mShowProgress);
            } catch (IllegalArgumentException ex) {
                LogUtils.w("MediaController", "already removed");
            }
            mShowing = false;
        }
    }

    public void show() {
        show(sDefaultTimeout);
    }


    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && !mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }
            if (mRewButton != null && !mPlayer.canSeekBackward()) {
                mRewButton.setEnabled(false);
            }
            if (mFfwdButton != null && !mPlayer.canSeekForward()) {
                mFfwdButton.setEnabled(false);
            }
            if (mProgress != null && !mPlayer.canSeekBackward() && !mPlayer.canSeekForward()) {
                mProgress.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            LogUtils.d(TAG, "IncompatibleClassChangeError:" + ex.toString());
        }
    }

    public void show(int timeout) {
        if (!mShowing) {
            mRoot.setVisibility(VISIBLE);
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();
            mShowing = true;
        }
        updatePausePlay();
        post(mShowProgress);
        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private final Runnable mFadeOut = this::hide;
    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    private final View.OnClickListener mPauseListener = v -> {
        doPauseResume();
        show(sDefaultTimeout);
    };

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_media_pause);
            mPauseButton.setContentDescription(mPauseDescription);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_media_play);
            mPauseButton.setContentDescription(mPlayDescription);
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            if (null != stateChangedListener) {
                stateChangedListener.onPause();
            }
        } else {
            mPlayer.start();
            if (null != stateChangedListener) {
                stateChangedListener.onStart();
            }
        }
        updatePausePlay();
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            mDragging = true;
            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }
            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newPosition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime((int) newPosition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);
            post(mShowProgress);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mFfwdButton != null) {
            mFfwdButton.setEnabled(enabled);
        }
        if (mRewButton != null) {
            mRewButton.setEnabled(enabled);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return MediaController.class.getName();
    }

    private final View.OnClickListener mRewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mPlayer.getCurrentPosition();
            pos -= 5000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            show(sDefaultTimeout);
        }
    };

    private final View.OnClickListener mFfwdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = mPlayer.getCurrentPosition();
            pos += 15000;
            mPlayer.seekTo(pos);
            setProgress();
            show(sDefaultTimeout);
        }
    };

    public interface OnStateChangedListener {

        void onStart();

        void onPause();

    }

}
