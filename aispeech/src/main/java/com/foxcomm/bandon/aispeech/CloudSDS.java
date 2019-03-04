package com.foxcomm.bandon.aispeech;

import android.content.Context;
import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;
import com.foxcomm.bandon.aispeech.util.Constants;
import com.foxcomm.bandon.aispeech.util.GrammarHelper;
import com.foxcomm.bandon.aispeech.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class CloudSDS {
    private static final String TAG = CloudSDS.class.getSimpleName();
    private static final String WAKE_UP_WORD = "你好小乐";
    private CloudTTS mCloudTTS;
    private Context mContext;
    private AILocalGrammarEngine mGrammarEngine;
    private AIMixASREngine mAsrEngine;
    private AILocalWakeupDnnEngine mWakeupEngine;
    private CloudSDSCallBack sdsCallBack;

    CloudSDS(Context context) {
        this.mContext = context;
    }


    public void start() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }
        mGrammarEngine = AILocalGrammarEngine.createInstance();
        mGrammarEngine.setResFileName(Constants.ebnfc_res);
        mGrammarEngine.init(mContext, new AILocalGrammarListenerImpl(), Constants.KEY, Constants.SECRET_KEY);
    }


    public void onDestroy() {
        if (null != mCloudTTS) {
            mCloudTTS.destroy();
        }
        if (null != mWakeupEngine) {
            mWakeupEngine.destroy();
        }
        if (null != mAsrEngine) {
            mAsrEngine.destroy();
        }
        if (null != mGrammarEngine) {
            mGrammarEngine.destroy();
        }
    }

    public void setSdsCallBack(CloudSDSCallBack sdsCallBack) {
        this.sdsCallBack = sdsCallBack;
    }

    private void initAsrEngine() {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
        }
        mAsrEngine = AIMixASREngine.createInstance();
        mAsrEngine.setResBin(Constants.ebnfr_res);
        mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);
        mAsrEngine.setVadResource(Constants.vad_res);
        mAsrEngine.setServer("ws://s.api.aispeech.com:1028,ws://s.api.aispeech.com:80");
        mAsrEngine.setUseXbnfRec(true);
        mAsrEngine.setRes(Constants.RES_TYPE);
        mAsrEngine.setUseForceout(false);
        mAsrEngine.setAthThreshold(0.6f);
        mAsrEngine.setIsRelyOnLocalConf(true);
        mAsrEngine.setIsPreferCloud(false);
        mAsrEngine.setWaitCloudTimeout(5000);
        mAsrEngine.setPauseTime(200);
        mAsrEngine.setUseConf(true);
        mAsrEngine.setNoSpeechTimeOut(5000);
        mAsrEngine.setDeviceId(Util.getIMEI(mContext));
        mAsrEngine.setCloudVadEnable(false);
        mAsrEngine.init(mContext, new AIASRListenerImpl(), Constants.KEY, Constants.SECRET_KEY);
        mAsrEngine.setUseCloud(true);
        mAsrEngine.setCoreType("cn.sds");
    }

    private void initWakeupEngine() {
        mWakeupEngine = AILocalWakeupDnnEngine.createInstance();
        mWakeupEngine.setResBin(Constants.wakeup_res);
        mWakeupEngine.setOneShotCacheTime(1200);
        mWakeupEngine.init(mContext, new AIWakeupListenerImpl(), Constants.KEY, Constants.SECRET_KEY);
        mWakeupEngine.setStopOnWakeupSuccess(true);
    }

    private void startWakeup() {
        mWakeupEngine.setUseOneShotFunction(true);
        mWakeupEngine.start();
    }

    private void startAsrUsingOneShot() {
        mAsrEngine.notifyWakeup();
        mAsrEngine.setPauseTime(0);
        mAsrEngine.setOneShotIntervalTimeThreshold(600);
        mAsrEngine.setUseOneShotFunction(true);
        mAsrEngine.setWakeupWord(WAKE_UP_WORD);
        mAsrEngine.start();
    }

    private void startAsrNotUsingOneShot() {
        mAsrEngine.setPauseTime(300);
        mAsrEngine.setUseOneShotFunction(false);
        mAsrEngine.setWakeupWord("");
        mAsrEngine.start();
    }

    public class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            LogUtils.d(TAG, "AIError:" + error.toString());
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            initAsrEngine();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                LogUtils.d(TAG, "AI Local Grammar Engine init success!");
                GrammarHelper gh = new GrammarHelper(mContext);
                String str = gh.importAssets("无联系人", "", "asr.xbnf");
                mGrammarEngine.setEbnf(str);
                mGrammarEngine.update();
            } else {
                LogUtils.d(TAG, "AI Local Grammar Engine init fail!");
            }
        }
    }

    public class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onReadyForSpeech() {
        }

        @Override
        public void onRmsChanged(float dB) {
        }

        @Override
        public void onError(AIError error) {
            LogUtils.d(TAG, "AIError:" + error.toString());
            startWakeup();
        }

        @Override
        public void onResults(AIResult results) {
            JSONResultParser parser = new JSONResultParser(results.getResultObject().toString());
            String input = parser.getInput();
            String rec = parser.getRec();
            boolean isWakeupWord = TextUtils.equals(input, WAKE_UP_WORD) || TextUtils.equals(input, "^")
                    || TextUtils.equals(rec, WAKE_UP_WORD);
            if (isWakeupWord) {
                mCloudTTS.speak("有什么可以帮您");
                if (null != sdsCallBack) {
                    sdsCallBack.onWeakUp();
                }
            } else {
                try {
                    String result = new JSONObject(results.getResultObject().toString()).toString(4);
                    LogUtils.d(TAG, "result:" + result);
                    if (null != sdsCallBack) {
                        sdsCallBack.onResults(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startWakeup();
            }
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                LogUtils.d(TAG, "AIMixASR Engine init success!");
                initWakeupEngine();
                mCloudTTS = new CloudTTS(mContext);
                mCloudTTS.setCallBack(ttsCallBack);
            } else {
                LogUtils.d(TAG, "AIMixASR Engine init fail!");
            }
        }

        @Override
        public void onRecorderReleased() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onNotOneShot() {
            LogUtils.d(TAG, "唤醒成功!");
            mCloudTTS.speak("有什么可以帮您");
            if (null != sdsCallBack) {
                sdsCallBack.onWeakUp();
            }
        }
    }

    private class AIWakeupListenerImpl implements AILocalWakeupDnnListener {

        @Override
        public void onError(AIError error) {
            LogUtils.d(TAG, "AIError:" + error.toString());
        }

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                LogUtils.d(TAG, "AILocalWakeupDnn Engine init success!");
                startWakeup();
            } else {
                LogUtils.d(TAG, "AILocalWakeupDnn Engine init fail!");
            }
        }

        @Override
        public void onRmsChanged(float dB) {

        }

        @Override
        public void onWakeup(String recordId, double confidence, String wakeupWord) {
            startAsrUsingOneShot();
        }

        @Override
        public void onReadyForSpeech() {
            if (null != sdsCallBack) {
                sdsCallBack.onReady();
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onRecorderReleased() {
        }

        @Override
        public void onWakeupEngineStopped() {
        }

    }

    private CloudTTS.TTSCallBack ttsCallBack = new CloudTTS.TTSCallBack() {
        @Override
        public void onInit(int status) {

        }

        @Override
        public void onCompletion() {
            startAsrNotUsingOneShot();
        }
    };

    public interface CloudSDSCallBack {
        void onReady();

        void onResults(String result);

        void onWeakUp();
    }

}
