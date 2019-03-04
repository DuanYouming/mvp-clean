package com.foxconn.bandon.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public abstract class TimerTask {

    private static final int MSG = 1;
    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mStopTimeInFuture;
    private boolean mCancelled = false;

    public TimerTask(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    public synchronized final TimerTask start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            synchronized (TimerTask.this) {
                if (mCancelled) {
                    return;
                }
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0) {
                    onFinish();
                } else {
/*                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);
                    long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                    long delay;
                    if (millisLeft < mCountdownInterval) {
                        delay = millisLeft - lastTickDuration;
                        if (delay < 0) delay = 0;
                    } else {
                        delay = mCountdownInterval - lastTickDuration;
                        while (delay < 0) delay += mCountdownInterval;
                    }*/
                    onTick(millisLeft);
                    sendMessageDelayed(obtainMessage(MSG), mCountdownInterval);
                }
            }
        }
    };
}
