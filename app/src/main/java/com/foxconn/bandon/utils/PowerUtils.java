package com.foxconn.bandon.utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;

import java.lang.reflect.Method;

public class PowerUtils {
    private static final String TAG = PowerUtils.class.getSimpleName();

    private static PowerUtils instance;
    private PowerManager mPM;
    private boolean isWakeUpBySelf;

    private PowerUtils(Context context) {
        mPM = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        isWakeUpBySelf = true;
    }


    public static PowerUtils getInstance(Context context) {
        if (null == instance) {
            synchronized (PowerUtils.class) {
                if (null == instance) {
                    instance = new PowerUtils(context);
                }
            }
        }
        return instance;
    }

    private boolean isScreenOn() {
        return mPM.isInteractive();
    }

    public void wakeup() {
        if (isScreenOn()) {
            return;
        }
        LogUtils.d(TAG, "wakeup");
        Class<?> clazz = mPM.getClass();
        try {
            Class[] params = new Class[]{long.class};
            String name = "wakeUp";
            Method method = clazz.getMethod(name, params);
            method.invoke(mPM, SystemClock.uptimeMillis());
            setWakeUpBySelf(true);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "wakeup  Exception：" + e.toString());
        }
    }

    public boolean isWakeUpBySelf() {
        return isWakeUpBySelf;
    }

    /*
     * when receive broadcast android.intent.action.SCREEN_OFF
     * set isWakeUpBySelf false;
     */
    public void setWakeUpBySelf(boolean wakeUpBySelf) {
        LogUtils.d(TAG, "setWakeUpBySelf:" + wakeUpBySelf);
        isWakeUpBySelf = wakeUpBySelf;
    }

    public void goToSleep() {
        if (!isScreenOn()) {
            return;
        }
        LogUtils.d(TAG, "goToSleep");
        Class<?> clazz = mPM.getClass();
        try {
            Class[] params = new Class[]{long.class};
            String name = "goToSleep";
            Method method = clazz.getMethod(name, params);
            method.invoke(mPM, SystemClock.uptimeMillis());
            setWakeUpBySelf(false);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "goToSleep  Exception：" + e.toString());
        }
    }


}
