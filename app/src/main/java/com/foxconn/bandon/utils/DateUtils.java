package com.foxconn.bandon.utils;

import android.content.Context;

import com.foxconn.bandon.R;
import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.gtm.model.GTMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    private static final long ONE_DAY = 24 * 3600 * 1000L;//ms
    private static final int UNIT_MILLION = 1000;
    private static final int UNIT_HOUR = 60;

    public static Date parseCreated(String source)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.parse(source);
    }


    public static String memoDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("M/d", Locale.getDefault());
        return sdf.format(date);
    }

    public static String createQrcodeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder qDate = new StringBuilder();
        qDate.append(year);
        if (month < 10) {
            qDate.append(0).append(month);
        } else {
            qDate.append(month);
        }
        if (day < 10) {
            qDate.append(0).append(day);
        } else {
            qDate.append(day);
        }
        return qDate.toString();

    }

    public static String expirationDateFormat(Context context, Date expiration) {
        StringBuilder sb = new StringBuilder();
        Calendar ex = Calendar.getInstance();
        ex.setTime(expiration);
        ex.set(Calendar.HOUR_OF_DAY, 23);
        ex.set(Calendar.MINUTE, 59);
        ex.set(Calendar.SECOND, 59);
        expiration = ex.getTime();

        Calendar now = Calendar.getInstance();
        boolean isOutOfDate = (expiration.getTime() - now.getTime().getTime()) < 0;
        int days = (int) Math.ceil((expiration.getTime() - now.getTime().getTime()) / ONE_DAY);
        LogUtils.d(TAG, "isOutOfDate:" + isOutOfDate + " days:" + days);
        if (days == 0) {
            sb.append(context.getString(R.string.expiration_today));
            return sb.toString();
        }
        int year = (int) Math.abs(Math.floor(days / 365f));
        int month = (int) Math.abs(Math.floor(days / 30f));
        int day = Math.abs(days);
        if (isOutOfDate) {
            sb.append(context.getString(R.string.out_of_date)).append(" ");
        } else {
            sb.append(context.getString(R.string.remain)).append(" ");
        }
        if (year > 0) {
            sb.append(year).append(" ").append(context.getString(R.string.expiration_year));
            return sb.toString();
        }
        if (month > 0) {
            sb.append(month).append(" ").append(context.getString(R.string.expiration_month));
            return sb.toString();
        }
        sb.append(day).append(" ").append(context.getString(R.string.expiration_day));
        return sb.toString();
    }

    public static String getFoodMessageContent(Context context, int position, String name, String expirationDate, int level) {
        StringBuilder sb = new StringBuilder();
        if (position == Constant.AREA_FRIDGE) {
            sb.append(context.getString(R.string.store_area_fridge));
        } else if (position == Constant.AREA_VEGETABLE) {
            sb.append(context.getString(R.string.store_area_vegetable));
        } else {
            sb.append(context.getString(R.string.store_area_freezer));
        }
        sb.append(context.getString(R.string.de)).append(name);
        sb.append(expirationDate);
        if (level == GTMessage.LEVEL_MIDDLE) {
            sb.append(context.getString(R.string.stalled_in_days)).append(context.getString(R.string.comma)).append(context.getString(R.string.hint_food));
        } else if (level == GTMessage.LEVEL_LOW) {
            sb.append(context.getString(R.string.stalled_in_days));
        }
        return sb.toString();
    }

    public static String dateFormat(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date getDate(String format) {
        String[] strings = format.replace(" ", "").split("-");
        int year = Integer.valueOf(strings[0]);
        int month = Integer.valueOf(strings[1]);
        int day = Integer.valueOf(strings[2]);
        LogUtils.d(TAG, "year:" + year + " month:" + month + " day:" + day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static long getTime(int hour, int min, int second) {
        return ((hour * UNIT_HOUR + min) * UNIT_HOUR + second) * UNIT_MILLION;
    }

    public static String getTimeString(long time) {
        int hour = (int) (time / (UNIT_HOUR * UNIT_HOUR * UNIT_MILLION));
        int min = (int) (time % (UNIT_HOUR * UNIT_HOUR * UNIT_MILLION)) / (UNIT_HOUR * UNIT_MILLION);
        int second = (int) ((time / UNIT_MILLION - (hour * UNIT_HOUR + min) * UNIT_HOUR));
        StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append("0").append(hour);
        } else {
            sb.append(hour);
        }
        sb.append(":");
        if (min < 10) {
            sb.append("0").append(min);
        } else {
            sb.append(min);
        }
        sb.append(":");
        if (second < 10) {
            sb.append("0").append(second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

}
