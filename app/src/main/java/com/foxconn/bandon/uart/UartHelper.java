package com.foxconn.bandon.uart;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.foxconn.bandon.setting.temp.TemperatureViewNew;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.uart.data.send.UartActionSettingData;
import com.foxconn.bandon.uart.data.send.UartDataFactory;
import com.foxconn.bandon.uart.data.send.UartSendData;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class UartHelper {
    private static final String TAG = UartHelper.class.getSimpleName();
    private static final char DATA_SETTING_SUB = 0X0;
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final int FROZEN_MODE_NORMAL = 0;
    private static final int FROZEN_MODE_FRESH = 1;
    private static final int FROZEN_MODE_QUICK = 2;
    private static final int ICE_MODE_LARGE = 1;
    private static UartHelper instance;
    private UartService mService;

    private UartHelper() {

    }

    public static UartHelper getInstance() {
        if (null == instance) {
            synchronized (UartHelper.class) {
                if (null == instance) {
                    instance = new UartHelper();
                }
            }
        }
        return instance;
    }

    public void receiveUartData(byte[] data) {
        LogUtils.d(TAG, "receiveUartData");
        int[] formatData = UartUtils.format(data);
        if (null != formatData) {
            UartUtils.print(formatData);
            if (null != mService) {
                mService.receive(formatData);
            }
        }
    }

    public void start() {
        LogUtils.d(TAG, "start");
        bindService();
    }

    public void stop() {
        LogUtils.d(TAG, "stop");
        if (null != mService) {
            unBindService();
        }
    }

    public void setDate() {
        char[] data = UartUtils.getDateData();
        char command = UartSendData.DATA_TYPE_DATE_SETTINGS;
        char sub = 0x0;
        sendCommand(sub, data, command);
    }

    public void setVolume(int level) {
        char[] data = new char[]{(char) (0x0F & level)};
        char command = UartSendData.DATA_TYPE_VOLUME_SETTINGS;
        char sub = 0x0;
        sendCommand(sub, data, command);
    }

    public void setTemp(char sub, int temp) {
        char[] datas = new char[]{(char) (temp & 0xFF)};
        char command = UartSendData.DATA_TYPE_TEMP_SETTINGS;
        sendCommand(sub, datas, command);
    }

    public void switchVaryingStatus(int status){
        UartActionSettingData data = mService.getActionSettingData();
        data.sAttemperation = status;
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void switchIceClean(int status) {
        UartActionSettingData data = mService.getActionSettingData();
        data.deo = status;
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void switchEcoMode(int status) {
        UartActionSettingData data = mService.getActionSettingData();
        data.eco = status;
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void switchFrozenStatus(int status) {
        UartActionSettingData data = mService.getActionSettingData();
        setFrozenMode(status, data);
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void turnOffIceMaking() {
        UartActionSettingData data = mService.getActionSettingData();
        data.iceStop = ON;
        data.qIce = OFF;
        data.iceLarge = OFF;
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void switchIceMode(int iceMode) {
        UartActionSettingData data = mService.getActionSettingData();
        if (iceMode == ICE_MODE_LARGE) {
            data.iceLarge = ON;
        } else {
            data.iceLarge = OFF;
        }
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    public void switchMakingMode(int makingMode) {
        UartActionSettingData data = mService.getActionSettingData();
        data.iceStop = OFF;
        if (makingMode == Constant.ICE_MAKER_MODE_NORMAL) {
            data.qIce = OFF;
        } else {
            data.qIce = ON;
        }
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    private void initActionSettingData(Context context) {

        int fridgeTemp = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, 0);
        setTemp(TemperatureViewNew.SUB_FRIDGE, fridgeTemp);
        int freezerTemp = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, 0);
        setTemp(TemperatureViewNew.SUB_FREEZER, freezerTemp);

        int deodoriser = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_CLEANER, Constant.CLEANER_OFF);
        int coolerMode = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_FROZEN_OPERATION, Constant.FROZEN_NORMAL);
        int iceMode = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_CUBE_SIZE, Constant.ICE_MAKER_CUBE_SIZE_NORMAL);
        int makeMode = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_MODE, Constant.ICE_MAKER_MODE_OFF);
        int ecoMode = PreferenceUtils.getInt(context, Constant.SP_SETTINGS, Constant.KEY_POWER_SAVING_MODE, Constant.POWER_SAVING_MODE_OFF);
        UartActionSettingData data = mService.getActionSettingData();
        data.deo = deodoriser;
        data.eco = ecoMode;
        data = setFrozenMode(coolerMode, data);
        data = setIceMakingMode(iceMode, makeMode, data);
        char[] datas = data.getDatas();
        char command = UartSendData.DATA_TYPE_MODE_SETTINGS;
        sendCommand(DATA_SETTING_SUB, datas, command);
        mService.updateActionSettingData(data);
    }

    private UartActionSettingData setIceMakingMode(int iceMode, int makeMode, UartActionSettingData data) {
        if (makeMode == Constant.ICE_MAKER_MODE_OFF) {
            data.iceStop = ON;
            data.qIce = OFF;
            data.iceLarge = OFF;
        } else {
            data.iceStop = OFF;
            if (makeMode == Constant.ICE_MAKER_MODE_NORMAL) {
                data.qIce = OFF;
            } else {
                data.qIce = ON;
            }
            if (iceMode == ICE_MODE_LARGE) {
                data.iceLarge = ON;
            } else {
                data.iceLarge = OFF;
            }
        }
        return data;
    }

    private UartActionSettingData setFrozenMode(int status, UartActionSettingData data) {
        if (status == FROZEN_MODE_NORMAL) {
            data.fFrozen = OFF;
            data.qFrozen = OFF;
            data.hFrozen = OFF;
        } else if (status == FROZEN_MODE_FRESH) {
            data.fFrozen = ON;
            data.qFrozen = OFF;
            data.hFrozen = OFF;
        } else if (status == FROZEN_MODE_QUICK) {
            data.fFrozen = OFF;
            data.qFrozen = ON;
            data.hFrozen = OFF;
        } else {
            data.fFrozen = OFF;
            data.qFrozen = OFF;
            data.hFrozen = ON;
        }
        return data;
    }

    private void bindService() {
        LogUtils.d(TAG, "bindService");
        Context context = BandonApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(context, UartService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {
        LogUtils.d(TAG, "unBindService");
        Context context = BandonApplication.getInstance().getApplicationContext();
        context.unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((UartService.UartBinder) service).getService();
            LogUtils.d(TAG, "onServiceConnected");
            boolean isConnected = connect();
            if (isConnected) {
                initActionSettingData(mService);
                setDate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "onServiceDisconnected");
            mService = null;
            disconnect();
        }
    };


    private void sendCommand(char sub, char[] datas, char command) {
        UartDataFactory factory = UartDataFactory.getInstance();
        UartSendData sendData = factory.getUartSendData(command, sub, datas);
        char[] arrays = factory.uartSendDataConvertToArray(sendData);
        char[] values = UartUtils.sendDataFormat(arrays);
        UartUtils.print(values);
        sendCommand(values, values.length);
    }

    private native void sendCommand(char[] data, int size);

    private native boolean connect();

    private native void disconnect();


}
