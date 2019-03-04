package com.foxconn.bandon.setting.clock;

import android.content.Context;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClockUtils {
    private static final String TAG = ClockUtils.class.getSimpleName();

    public static List<String> getWeeksArray() {
        List<String> weekArray = new ArrayList<>();
        Context context = BandonApplication.getInstance();
        weekArray.add(context.getResources().getString(R.string.alarm_period_sun));
        weekArray.add(context.getResources().getString(R.string.alarm_period_mon));
        weekArray.add(context.getResources().getString(R.string.alarm_period_tue));
        weekArray.add(context.getResources().getString(R.string.alarm_period_wed));
        weekArray.add(context.getResources().getString(R.string.alarm_period_thu));
        weekArray.add(context.getResources().getString(R.string.alarm_period_fri));
        weekArray.add(context.getResources().getString(R.string.alarm_period_sat));
        return weekArray;
    }

    private static Calendar getClockWeekday(List<Integer> periods, Calendar calendar) {
        int curWeekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        LogUtils.d(TAG, "curWeekday：" + curWeekday);
        for (int index : periods) {
            if (curWeekday < index) {
                calendar.set(Calendar.DAY_OF_WEEK, index + 1);
                return calendar;
            } else if (curWeekday == index) {
                if (calendar.getTime().getTime() <= (new Date()).getTime()) {
                    break;
                } else {
                    calendar.set(Calendar.DAY_OF_WEEK, index + 1);
                    return calendar;
                }
            }
        }
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, periods.get(0) + 1);
        return calendar;
    }

    public static Calendar getCalender(ClockBean clock) {
        List<Integer> periods = clock.getPeriodsList();
        Calendar calendar = Calendar.getInstance();
        LogUtils.d(TAG, "calendar:" + calendar.getTime());
        calendar.setTimeZone(clock.getTimezone());
        calendar.set(Calendar.HOUR_OF_DAY, clock.getHour());
        calendar.set(Calendar.MINUTE, clock.getMin());
        if (null != periods && periods.size() > 0) {
            calendar = ClockUtils.getClockWeekday(periods, calendar);
        } else {
            if (calendar.getTime().getTime() < (new Date()).getTime()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }
        }
        LogUtils.d(TAG, "calendar:" + calendar.getTime());
        return calendar;
    }
}
