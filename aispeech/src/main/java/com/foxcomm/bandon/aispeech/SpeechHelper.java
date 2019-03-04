package com.foxcomm.bandon.aispeech;

import android.content.Context;

import com.aispeech.export.listeners.AIAuthListener;
import com.aispeech.speech.AIAuthEngine;
import com.foxcomm.bandon.aispeech.util.Constants;
import com.foxcomm.bandon.aispeech.util.LogUtils;

class SpeechHelper {
    private static final String TAG = SpeechHelper.class.getSimpleName();
    private static SpeechHelper instance;
    private AIAuthEngine mAuthEngine;
    private CloudASR mCloudASR;
    private CloudTTS mCloudTTS;

    private SpeechHelper(Context context) {
        mAuthEngine = AIAuthEngine.getInstance(context);
        mCloudASR = new CloudASR(context);
        mCloudTTS = new CloudTTS(context);
    }

    protected static SpeechHelper getInstance(Context context) {
        if (null == instance) {
            synchronized (SpeechHelper.class) {
                if (null == instance) {
                    instance = new SpeechHelper(context);
                }
            }
        }
        return instance;
    }

    public void auth(AIAuthListener listener) {
        LogUtils.d(TAG, "auth");
        if (null == mAuthEngine) {
            LogUtils.d(TAG, "AuthEngine is null");
            return;
        }
        try {
            mAuthEngine.init(Constants.KEY, Constants.SECRET_KEY, "");
            mAuthEngine.setOnAuthListener(listener);
            mAuthEngine.doAuth();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "auth exception:" + e.toString());
        }
    }

    public void destroyAIAuthEngine() {
        LogUtils.d(TAG, "destroyAIAuthEngine");
        if (null != mAuthEngine) {
            mAuthEngine.destroy();
            mAuthEngine = null;
        }
    }

    public CloudSDS getCloudSDS(Context context) {
        return new CloudSDS(context);
    }

    public void startCloudASR() {
        if (null != mCloudASR) {
            mCloudASR.start();
        }
    }

    public void stopCloudASR() {
        if (null != mCloudASR) {
            mCloudASR.stop();
        }
    }

    public void addAsrCallBack(CloudASR.ASRCallBack asrCallBack) {
        if (null != mCloudASR) {
            mCloudASR.addAsrCallBack(asrCallBack);
        }
    }

    public void removeAsrCallBack() {
        if (null != mCloudASR) {
            mCloudASR.removeAsrCallBack();
        }
    }

    public void startSpeak(String text) {
        if (null != mCloudTTS) {
            mCloudTTS.speak(text);
        }
    }

    public void stopSpeak() {
        if (null != mCloudTTS) {
            mCloudTTS.stop();
        }
    }

    public void pauseSpeak() {
        if (null != mCloudTTS) {
            mCloudTTS.pause();
        }
    }

    public void resumeSpeak() {
        if (null != mCloudTTS) {
            mCloudTTS.resume();
        }
    }

    public static void destroy() {
        instance.mCloudASR.destroy();
        instance.mCloudTTS.destroy();
        if (null != instance) {
            instance = null;
        }
    }

}
