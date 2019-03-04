package com.foxconn.bandon.gtm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

import java.util.Calendar;
import java.util.Date;

public class GTMService extends Service {
    public static final String ACTION_FIRST_LOAD = "com.foxconn.bandon.ACTION_FIRST_LOAD";
    public static final String ACTION_UPDATE_MESSAGE = "com.foxconn.bandon.ACTION_UPDATE_MESSAGE";
    private static final String TAG = GTMService.class.getSimpleName();
    private static final int MSG_WHAT = 101;
    private static final int SECOND = 1000;
    private GTMessageManager manager;
    private Context context;
    private boolean isLoop;
    private AlarmManager mAlarmManager;
    private PendingIntent pIntent;

    public GTMService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pIntent = createIntent();
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, getUpdateTime(), pIntent);
        manager = GTMessageManager.getInstance();
        isLoop = true;
        startLooper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_FIRST_LOAD)) {
                LogUtils.e(TAG,"ACTION_FIRST_LOAD：initFoodMessages");
                manager.initFoodMessages();
            } else if (TextUtils.equals(action, ACTION_UPDATE_MESSAGE)) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, getUpdateTime(), pIntent);
                LogUtils.e(TAG,"ACTION_UPDATE_MESSAGE：initFoodMessages");
                manager.initFoodMessages();
                PreferenceUtils.setInt(this, Constant.SP_SETTINGS, Constant.KEY_DOOR_OPENED_TIMES, 0);
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GTMessageManager.destroyInstance();
        mAlarmManager.cancel(pIntent);
        isLoop = false;
    }


    private PendingIntent createIntent() {
        Intent intent = new Intent(this, GTMService.class);
        intent.setAction(ACTION_UPDATE_MESSAGE);
        return PendingIntent.getService(BandonApplication.getInstance(), 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private long getUpdateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        LogUtils.d(TAG, "getUpdateTime:" + calendar.getTime().toString());
        if (calendar.getTime().getTime() < (new Date()).getTime()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return calendar.getTimeInMillis();
    }

    private void startLooper() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (isLoop) {
                    SystemClock.sleep(10 * SECOND);
                    handler.sendEmptyMessage(MSG_WHAT);
                }
            }
        };
        thread.start();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT:
                    manager.loopMessage();
                    break;
            }
        }
    };


}
