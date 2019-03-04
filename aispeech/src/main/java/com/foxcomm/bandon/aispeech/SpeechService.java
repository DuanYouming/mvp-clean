package com.foxcomm.bandon.aispeech;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.aispeech.export.listeners.AIAuthListener;
import com.foxcomm.bandon.aispeech.util.LogUtils;

public class SpeechService extends Service implements AIAuthListener {
    private static final String TAG = SpeechService.class.getSimpleName();

    private SpeechHelper mHelper;
    private boolean isAuthorized;


    class SpeechBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }

    public SpeechService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHelper = SpeechHelper.getInstance(this);
        mHelper.auth(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, START_STICKY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        return new SpeechBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpeechHelper.destroy();
    }

    @Override
    public void onAuthSuccess() {
        LogUtils.d(TAG, "onAuthSuccess");
        isAuthorized = true;
        mHelper.destroyAIAuthEngine();
    }

    @Override
    public void onAuthFailed(String s) {
        LogUtils.d(TAG, "onAuthFailed");
        isAuthorized = false;
        mHelper.destroyAIAuthEngine();
    }

    public CloudSDS getCloudSDS() {
        return mHelper.getCloudSDS(this);
    }

    public void startCloudASR() {
        if (!isAuthorized) {
            LogUtils.d(TAG, "AI Auth Engine not be authorized");
            return;
        }
        mHelper.startCloudASR();
    }

    public void stopCloudASR() {
        mHelper.stopCloudASR();
    }

    public void addAsrCallBack(CloudASR.ASRCallBack asrCallBack) {
        mHelper.addAsrCallBack(asrCallBack);
    }

    public void removeAsrCallBack() {
        mHelper.removeAsrCallBack();
    }

    public void startSpeak(String text) {
        if (!isAuthorized) {
            LogUtils.d(TAG, "AI Auth Engine not be authorized");
            return;
        }
        mHelper.startSpeak(text);
    }

    public void stopSpeak() {
        if (!isAuthorized) {
            LogUtils.d(TAG, "AI Auth Engine not be authorized");
            return;
        }
        mHelper.stopSpeak();
    }

    public void pauseSpeak() {
        if (!isAuthorized) {
            LogUtils.d(TAG, "AI Auth Engine not be authorized");
            return;
        }
        mHelper.pauseSpeak();
    }

    public void resumeSpeak() {
        if (!isAuthorized) {
            LogUtils.d(TAG, "AI Auth Engine not be authorized");
            return;
        }
        mHelper.resumeSpeak();
    }
}
