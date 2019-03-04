package com.foxconn.bandon.setting.date;

import android.app.AlarmManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.NumberStringPicker;
import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import java.util.Calendar;


public class DateSelector extends TimeSelector {
    private static final String TAG = DateSelector.class.getSimpleName();

    private UpdateListener mUpdateListener;

    DateSelector(Context context, UpdateListener updateListener) {
        super(context);
        this.mUpdateListener = updateListener;
    }

    public void setup(final View dateSelector) {
        final NumberStringPicker yearPicker = dateSelector.findViewById(R.id.date_year);
        final NumberStringPicker monthPicker = dateSelector.findViewById(R.id.date_month);
        final NumberStringPicker dayPicker = dateSelector.findViewById(R.id.date_day);

        //set picker range and init value
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setPickerRangeAndIntValue(yearPicker, false, 1970, getBaseYear() + 20, year);
        setPickerRangeAndIntValue(monthPicker, true, 1, 12, month + 1);
        setPickerRangeAndIntValue(dayPicker, true, 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), day);

        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        yearPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                if (yearPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && monthPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && dayPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE) {
                    updateDayRange(dateSelector);
                    updateSelectDate(dateSelector);
                } else {
                    LogUtils.d(TAG, "year picker is completed, others is scrolling");
                }
            }
        });


        monthPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                if (yearPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && monthPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && dayPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE) {
                    //when all picker scroll completed, update day number and select date info
                    updateDayRange(dateSelector);
                    updateSelectDate(dateSelector);
                } else {
                    LogUtils.d(TAG, "month picker is completed, others is scrolling");
                }
            }
        });


        dayPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                if (yearPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && monthPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE && dayPicker.getScrollStatus() == RecyclerView.SCROLL_STATE_IDLE) {
                    //when all picker scroll completed, update day number and select date info
                    updateDayRange(dateSelector);
                    updateSelectDate(dateSelector);
                } else {
                    LogUtils.d(TAG, "day picker is completed, others is scrolling");
                }
            }
        });

        initCancelBtnOnDateSelector(dateSelector);
        initConfirmBtnOnDateSelector(dateSelector, yearPicker, monthPicker, dayPicker);
    }

    private void updateSelectDate(final View timeDateSelector) {

        NumberStringPicker yearPicker = timeDateSelector.findViewById(R.id.date_year);
        String year = yearPicker.getFocusValue();
        NumberStringPicker monthPicker = timeDateSelector.findViewById(R.id.date_month);
        String month = monthPicker.getFocusValue();
        NumberStringPicker dayPicker = timeDateSelector.findViewById(R.id.date_day);
        String day = dayPicker.getFocusValue();

    }

    private void updateDayRange(final View timeDateSelector) {
        NumberStringPicker yearPicker = timeDateSelector.findViewById(R.id.date_year);
        String year = yearPicker.getFocusValue();
        NumberStringPicker monthPicker = timeDateSelector.findViewById(R.id.date_month);
        String month = monthPicker.getFocusValue();
        NumberStringPicker dayPicker = timeDateSelector.findViewById(R.id.date_day);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(year));
        calendar.set(Calendar.MONTH, Integer.valueOf(month) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int intValue = Integer.valueOf(dayPicker.getFocusValue());
        if (intValue > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            intValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        setPickerRangeAndIntValue(dayPicker, true, 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), intValue);
    }

    private void initConfirmBtnOnDateSelector(final View dateSelector, final NumberStringPicker yearPicker, final NumberStringPicker monthPicker, final NumberStringPicker dayPicker) {
        dateSelector.findViewById(R.id.date_edit_btn_confirm).
                setOnClickListener(v -> {
                    if (yearPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                            || monthPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                            || dayPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE) {
                        return;
                    }
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, Integer.valueOf(yearPicker.getFocusValue()));
                    c.set(Calendar.MONTH, Integer.valueOf(monthPicker.getFocusValue()) - 1);
                    c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayPicker.getFocusValue()));
                    if (mUpdateListener != null) {
                        mUpdateListener.newDate(c);
                    }
                    ClockManager.getInstance().cancelAll();
                    AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    if (null != am) {
                        am.setTime(c.getTimeInMillis());
                    }
                    ClockManager.getInstance().onBootCompleted();
                });
    }

    private void initCancelBtnOnDateSelector(final View dateSelector) {
        dateSelector.findViewById(R.id.date_edit_btn_cancel).
                setOnClickListener(v -> {
                    if (mUpdateListener != null) {
                        mUpdateListener.newDate(null);
                    }
                });
    }

    private int getBaseYear() {
        return Integer.valueOf(PreferenceUtils.getString(getContext(),
                DateSettingsView.KEY_TIME,
                DateSettingsView.KEY_BASE_YEAR_ENTRY,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
    }

    public interface UpdateListener {
        void newDate(Calendar date);
    }
}
