package com.foxconn.bandon.setting.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClockManager {
    private static final String TAG = ClockManager.class.getSimpleName();
    public static final String ACTION_BANDON_CLOCK = "android.intent.action.BANDON_CLOCK";
    public static final String EXTRA_CLOCK_ID = "extra_clock_id";
    private SparseArray<PendingIntent> intentArrays = new SparseArray<>();
    private static volatile ClockManager instance;
    private List<OnReceiveCallback> callbacks = new ArrayList<>();
    private List<OnStateChangedListener> listeners = new ArrayList<>();
    private AlarmManager mAlarmManager;


    private ClockManager() {
        mAlarmManager = ((AlarmManager) BandonApplication.getInstance().getSystemService(Context.ALARM_SERVICE));
    }

    public static ClockManager getInstance() {
        if (null == instance) {
            synchronized (ClockManager.class) {
                if (null == instance) {
                    instance = new ClockManager();
                }
            }
        }
        return instance;
    }

    public void newClock(ClockBean clock) {
        PendingIntent intent = createPendingIntent(clock.getId(), clock.getRequestCode());
        Calendar calendar = ClockUtils.getCalender(clock);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        intentArrays.put(clock.getRequestCode(), intent);
    }


    private PendingIntent createPendingIntent(String id, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(ACTION_BANDON_CLOCK);
        intent.putExtra(EXTRA_CLOCK_ID, id);
        return PendingIntent.getBroadcast(BandonApplication.getInstance(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void restartClock(ClockBean clock) {
        PendingIntent intent = intentArrays.get(clock.getRequestCode());
        if (null != intent) {
            LogUtils.d(TAG, "restartClock:" + clock.getTag());
            Calendar calendar = ClockUtils.getCalender(clock);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        } else {
            newClock(clock);
        }
    }

    public void cancelClock(ClockBean clock) {
        PendingIntent intent = intentArrays.get(clock.getRequestCode());
        if (null != intent) {
            LogUtils.d(TAG, "cancelClock:" + clock.getTag());
            mAlarmManager.cancel(intent);
        }
    }

    public void cancelAll() {
        for (int i = 0; i < intentArrays.size(); i++) {
           PendingIntent intent = intentArrays.valueAt(i);
            if (null != intent) {
                mAlarmManager.cancel(intent);
            }
        }
        intentArrays.clear();
    }


    public void deleteClock(ClockBean clock) {
        PendingIntent intent = intentArrays.get(clock.getRequestCode());
        if (null != intent) {
            LogUtils.d(TAG, "deleteClock:" + clock.getTag());
            mAlarmManager.cancel(intent);
            intentArrays.remove(clock.getRequestCode());
        }
    }

    public void addOnReceiveCallback(@NonNull OnReceiveCallback callback) {
        callbacks.add(callback);
    }

    public void removeOnReceiverCallback(@NonNull OnReceiveCallback callback) {
        callbacks.remove(callback);
    }

    public void addStateChangedListener(@NonNull OnStateChangedListener listener) {
        listeners.add(listener);
    }

    public void removeStateChangedListener(@NonNull OnStateChangedListener listener) {
        listeners.remove(listener);
    }

    public void onSateChanged() {
        for (OnStateChangedListener listener : listeners) {
            listener.OnStateChanged();
        }
    }

    public void onBootCompleted() {
        for (OnReceiveCallback callback : callbacks) {
            callback.onBootCompleted();
        }
    }

    public void onClockRings(Intent intent) {
        LogUtils.d(TAG, "onClockRings");
        for (OnReceiveCallback callback : callbacks) {
            callback.onClockRings(intent);
        }

    }

    public interface OnReceiveCallback {

        void onBootCompleted();

        void onClockRings(Intent intent);

    }


    public interface OnStateChangedListener {
        void OnStateChanged();
    }

}
