package com.foxconn.bandon.uart;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import com.foxconn.bandon.R;
import com.foxconn.bandon.uart.data.receive.ProcessThread;
import com.foxconn.bandon.uart.data.receive.ReceiveThread;
import com.foxconn.bandon.uart.data.receive.UartNotificationData;
import com.foxconn.bandon.uart.data.receive.UartReceiveData;
import com.foxconn.bandon.uart.data.receive.UartDataQueue;
import com.foxconn.bandon.uart.data.receive.UartStatusData;
import com.foxconn.bandon.uart.data.send.UartActionSettingData;
import com.foxconn.bandon.utils.LogUtils;

import java.util.Arrays;

import android_cameraarray_api.CaptureManager;


public class UartService extends Service {
    private static final String TAG = UartService.class.getSimpleName();
    public static final String KEY_BUNDLE_DATA = "key_bundle_data";
    private static final int LOAD_SUCCESS = 0;
    private static final int NO_LOOP = 0;
    private int[] mRecent;
    private UartDataQueue mQueue;
    private ProcessThread mProcessThread;
    private UartActionSettingData actionSettingData = new UartActionSettingData();
    private SoundPool mSoundPool;
    private boolean isLoad = false;
    private int mSampleId;

    class UartBinder extends Binder {
        public UartService getService() {
            return UartService.this;
        }
    }

    public UartService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate");
        System.loadLibrary("uart-lib");
        mQueue = new UartDataQueue();
        initSoundPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, START_STICKY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        mProcessThread = new ProcessThread(mQueue, handler);
        mProcessThread.setLoop(true);
        mProcessThread.start();
        return new UartBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LogUtils.d(TAG, "onRebind");
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        mProcessThread.setLoop(false);
        LogUtils.d(TAG, "unbindService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "unbindService");
        CaptureManager.destroyInstance();
        mSoundPool.release();
    }

    public void receive(int[] data) {
        LogUtils.d(TAG, "receive");
        if (!Arrays.equals(data, mRecent)) {
            mRecent = data;
            ReceiveThread thread = new ReceiveThread(mQueue, data);
            thread.start();
        }
    }

    public UartActionSettingData getActionSettingData() {
        return this.actionSettingData;
    }

    public void updateActionSettingData(UartActionSettingData data) {
        this.actionSettingData = data;
    }

    private void initSoundPool() {
        SoundPool.Builder builder = new SoundPool.Builder();
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        mSoundPool = builder.setMaxStreams(1).setAudioAttributes(attrBuilder.build()).build();
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            LogUtils.d(TAG, "onLoadComplete");
            if (status == LOAD_SUCCESS) {
                LogUtils.d(TAG, "load success");
                isLoad = true;
                mSampleId = sampleId;
            }
        });
        mSoundPool.load(this, R.raw.door_opened, 1);
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (null == bundle) {
                LogUtils.d(TAG, "handleMessage()bundle is null");
                return;
            }
            int[] array = bundle.getIntArray(KEY_BUNDLE_DATA);
            switch (msg.what) {
                case UartReceiveData.COMMAND_STATUS:
                    UartStatusData uartData = new UartStatusData(array);
                    LogUtils.d(TAG, "uartData:" + uartData.toString());
                    updateDoorStatus(uartData);
                    updateActionSettingData(uartData);
                    break;
                case UartReceiveData.COMMAND_NOTIFICATION:
                    LogUtils.d(TAG, "Notification");
                    UartNotificationData data = new UartNotificationData(array);
                    LogUtils.d(TAG, "UartNotificationData:" + data.toString());
                    break;
                default:
                    break;
            }
        }
    };

    private void updateDoorStatus(UartStatusData uartData) {
        if (uartData.rDoorOpen) {
            CaptureManager.getInstance().doorOpen();
            handler.postDelayed(runnable, 55 * 1000);
        } else {
            CaptureManager.getInstance().doorClose();
            handler.removeCallbacks(runnable);
        }
    }

    private void playRemindVoice() {
        if (isLoad) {
            LogUtils.d(TAG, "playRemindVoice");
            mSoundPool.play(mSampleId, 1, 1, 0, NO_LOOP, 1);
        } else {
            LogUtils.d(TAG, "sound pool load fail");
        }
    }

    private void updateActionSettingData(UartStatusData uartData) {
        actionSettingData = new UartActionSettingData(uartData);
    }

    private Runnable runnable = this::playRemindVoice;


}
