package com.foxcomm.bandon.aispeech;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.foxcomm.bandon.aispeech.util.LogUtils;

public class SpeechManager {
    private static final String TAG = SpeechManager.class.getSimpleName();
    private static SpeechManager instance;
    private SpeechService mService;

    private SpeechManager() {
    }

    public static SpeechManager getInstance() {
        if (null == instance) {
            synchronized (SpeechManager.class) {
                if (null == instance) {
                    instance = new SpeechManager();
                }
            }
        }
        return instance;
    }

    public CloudSDS getCloudSDS() {
        if (null != mService) {
            return mService.getCloudSDS();
        }
        return null;
    }

    public void startCloudASR() {
        if (null != mService) {
            mService.startCloudASR();
        }
    }

    public void stopCloudASR() {
        if (null != mService) {
            mService.stopCloudASR();
        }
    }

    public void addAsrCallBack(CloudASR.ASRCallBack asrCallBack) {
        if (null != mService) {
            mService.addAsrCallBack(asrCallBack);
        }
    }

    public void removeAsrCallBack() {
        if (null != mService) {
            mService.removeAsrCallBack();
        }
    }

    public void startSpeak(String text) {
        if (null == mService) {
            LogUtils.d(TAG, "service is dead");
            return;
        }
        mService.startSpeak(text);
    }

    public void stopSpeak() {
        if (null == mService) {
            LogUtils.d(TAG, "service is dead");
            return;
        }
        mService.stopSpeak();
    }

    public void pauseSpeak() {
        if (null == mService) {
            LogUtils.d(TAG, "service is dead");
            return;
        }
        mService.pauseSpeak();
    }

    public void resumeSpeak() {
        if (null == mService) {
            LogUtils.d(TAG, "service is dead");
            return;
        }
        mService.resumeSpeak();
    }


    public void bindService(Context context) {
        LogUtils.d(TAG, "bindService");
        Intent intent = new Intent(context, SpeechService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(Context context) {
        LogUtils.d(TAG, "unBindService");
        context.unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SpeechService.SpeechBinder) service).getService();
            LogUtils.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "onServiceDisconnected");
            mService = null;
        }
    };
}
