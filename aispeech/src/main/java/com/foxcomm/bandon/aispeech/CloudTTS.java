package com.foxcomm.bandon.aispeech;

import android.content.Context;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.foxcomm.bandon.aispeech.util.Constants;
import com.foxcomm.bandon.aispeech.util.LogUtils;


public class CloudTTS {
    private static final String TAG = CloudTTS.class.getSimpleName();
    private AICloudTTSEngine mEngine;
    private TTSCallBack callBack;

    CloudTTS(Context context) {
        mEngine = AICloudTTSEngine.createInstance();
        init(context);
    }

    public void speak(String text) {
        if (null != mEngine) {
            mEngine.stop();
            mEngine.speak(text, "1024");
        }
    }

    public void stop() {
        if (null != mEngine) {
            mEngine.stop();
        }
    }

    public void pause() {
        if (null != mEngine) {
            mEngine.pause();
        }
    }

    public void resume() {
        if (null != mEngine) {
            mEngine.resume();
        }
    }

    public void destroy() {
        if (null != mEngine) {
            mEngine.destroy();
        }
    }

    public void setCallBack(TTSCallBack callBack) {
        this.callBack = callBack;
    }

    private void init(Context context) {
        mEngine.setRealBack(true);
        mEngine.init(context, new AITTSListenerImpl(), Constants.KEY, Constants.SECRET_KEY);
        mEngine.setLanguage(AIConstant.CN_TTS);
        mEngine.setRes(Constants.CLOUD_TTS_RES);
        mEngine.setSpeechRate(1.2f);
        mEngine.setDeviceId(Util.getIMEI(context));
    }

    private class AITTSListenerImpl implements AITTSListener {
        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.d(TAG, "初始化成功...");
            } else {
                LogUtils.e(TAG, "初始化失敗...");
            }
            if (null != callBack) {
                callBack.onInit(status);
            }
        }

        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {

        }

        @Override
        public void onError(String utteranceId, AIError error) {
            LogUtils.e(TAG, "AIError:" + error.toString());
        }

        @Override
        public void onReady(String utteranceId) {

        }

        @Override
        public void onCompletion(String utteranceId) {
            LogUtils.e(TAG, "onCompletion");
            if (null != callBack) {
                callBack.onCompletion();
            }
        }

    }

    interface TTSCallBack {
        void onInit(int status);

        void onCompletion();
    }
}
