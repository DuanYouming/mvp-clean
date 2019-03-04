package com.foxcomm.bandon.aispeech;

import android.content.Context;
import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.foxcomm.bandon.aispeech.util.Constants;
import com.foxcomm.bandon.aispeech.util.FileUtil;
import com.foxcomm.bandon.aispeech.util.LogUtils;

public class CloudASR {
    private static final String TAG = CloudASR.class.getSimpleName();
    private AICloudASREngine mEngine;
    private String mFilePath;
    private ASRCallBack asrCallBack;

    CloudASR(Context context) {
        mEngine = AICloudASREngine.createInstance();
        init(context);
    }

    public void start() {
        if (null != mEngine) {
            mEngine.start();
        }
    }

    public void stop() {
        if (null != mEngine) {
            mEngine.stopRecording();
        }
    }

    public void destroy() {
        if (mEngine != null) {
            mEngine.destroy();
        }
    }

    public void addAsrCallBack(ASRCallBack callBack) {
        this.asrCallBack = callBack;
    }

    public void removeAsrCallBack() {
        this.asrCallBack = null;
    }

    private void init(Context context) {
        mFilePath = Constants.PCM_PATH + System.currentTimeMillis() + Constants.PCM_FILE_TYPE;
        FileUtil.createFile(mFilePath);
        mEngine.setVadResource(Constants.vad_res);
        mEngine.setRes(Constants.RES_TYPE);
        mEngine.setDeviceId(Util.getIMEI(context));
        mEngine.setHttpTransferTimeout(10);
        mEngine.init(context, new AICloudASRListener(), Constants.KEY, Constants.SECRET_KEY);
        mEngine.setNoSpeechTimeOut(0);
    }


    private class AICloudASRListener implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
            LogUtils.d(TAG, "请说话...");
            if (null != asrCallBack) {
                asrCallBack.onReady();
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            LogUtils.d(TAG, "检测到说话...");
            if (null != asrCallBack) {
                asrCallBack.onBeginning();
            }
        }

        @Override
        public void onEndOfSpeech() {
            LogUtils.d(TAG, "语音停止，开始识别...");
            if (null != asrCallBack) {
                asrCallBack.onEnd();
            }
        }

        @Override
        public void onRmsChanged(float dB) {
            if (null != asrCallBack) {
                asrCallBack.onRmsChanged(dB);
            }
        }

        @Override
        public void onError(AIError error) {
            LogUtils.e(TAG, "AIError:" + error.getError());
            if (null != asrCallBack) {
                asrCallBack.onError();
            }
        }

        @Override
        public void onResults(AIResult results) {
            String result = null;
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    JSONResultParser parser = new JSONResultParser(results.getResultObject().toString());
                    result = parser.getRec();
                    LogUtils.e(TAG, "result:" + result);
                }
            }
            if (null != asrCallBack) {
                asrCallBack.onResults(result);
            }

        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.e(TAG, "初始化成功...");
            } else {
                LogUtils.e(TAG, "初始化失敗...");
            }
        }

        @Override
        public void onRecorderReleased() {
            LogUtils.e(TAG, "onRecorderReleased()");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            FileUtil.write(mFilePath, buffer, buffer.length);
        }

        @Override
        public void onNotOneShot() {

        }

    }

    public interface ASRCallBack {
        void onReady();

        void onBeginning();

        void onEnd();

        void onRmsChanged(float db);

        void onError();

        void onResults(String result);

    }

}
