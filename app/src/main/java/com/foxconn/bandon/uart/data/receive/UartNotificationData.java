package com.foxconn.bandon.uart.data.receive;

import android.content.Context;

import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class UartNotificationData extends UartReceiveData {
    private static final String TAG = UartNotificationData.class.getSimpleName();

    private static final int SUB_0 = 0x0;
    private static final int SUB_1 = 0x1;

    private int year;
    private int week;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private boolean isRRTHT;
    private boolean isFRTHT;
    private boolean isIRTHT;
    private boolean isFullIce;
    private boolean isIMDone;
    private boolean isWaterless;

    public UartNotificationData(int[] data) {
        super(data);
        init();
    }

    private void init() {
        int sub = getSubCommand();
        if (sub == SUB_0) {
            initSUB0();
            return;
        }
        if (sub == SUB_1) {
            initSUB1();
        }
    }

    private void initSUB0() {
        initData1(data[3]);
        initData2(data[4]);
        initData3(data[5]);
        initData4(data[6]);
        initData5(data[7]);
        initData6(data[8]);
        initData7(data[9]);
        initData8(data[10]);
    }

    private void initSUB1() {

    }

    private void initData1(int value) {
        isRRTHT = (value & (0x01 << 2)) > 0;
        isFRTHT = (value & (0x01 << 3)) > 0;
        isIRTHT = (value & (0x01 << 4)) > 0;
        isFullIce = (value & (0x01 << 5)) > 0;
        isIMDone = (value & (0x01 << 6)) > 0;
        isWaterless = (value & (0x01 << 7)) > 0;
        if (isIMDone) {
            LogUtils.d(TAG, "Ice making have completed");
            Context context = BandonApplication.getInstance();
            UartHelper.getInstance().turnOffIceMaking();
            PreferenceUtils.setInt(context, Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_MODE, Constant.ICE_MAKER_MODE_OFF);
            PreferenceUtils.setInt(context, Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_CUBE_SIZE, Constant.ICE_MAKER_CUBE_SIZE_NORMAL);
        }
    }

    private void initData2(int value) {
        boolean isDone = (value & 0x01) > 0;
        if (isDone) {
            if ((value & (0x01 << 1)) > 0) {
                LogUtils.d(TAG, "Ice making have completed 10 minutes");
            } else if ((value & (0x01 << 2)) > 0) {
                LogUtils.d(TAG, "Ice making have completed 5 minutes");
            } else if ((value & (0x01 << 3)) > 0) {
                LogUtils.d(TAG, "Ice making have completed");
            }

        } else {
            if ((value & (0x01 << 1)) > 0) {
                LogUtils.d(TAG, "Ice making 5 minutes left");
            } else if ((value & (0x01 << 2)) > 0) {
                LogUtils.d(TAG, "Ice making 10 minutes left");
            } else if ((value & (0x01 << 3)) > 0) {
                LogUtils.d(TAG, "Ice making 15 minutes left");
            }
        }
    }

    private void initData3(int value) {
        this.year = value;
    }

    private void initData4(int value) {
        this.week = ((value & 0xF0) >> 4);
        this.month = ((value & 0x0F));
    }

    private void initData5(int value) {
        this.day = value;
    }

    private void initData6(int value) {
        this.hour = value;
    }

    private void initData7(int value) {
        this.minute = value;
    }

    private void initData8(int value) {
        this.second = value;
    }

    @Override
    public String toString() {
        return "UartNotificationData{" +
                "year=" + year +
                ", week=" + week +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", isRRTHT=" + isRRTHT +
                ", isFRTHT=" + isFRTHT +
                ", isIRTHT=" + isIRTHT +
                ", isFullIce=" + isFullIce +
                ", isIMDone=" + isIMDone +
                ", isWaterless=" + isWaterless +
                "} " + super.toString();
    }
}
