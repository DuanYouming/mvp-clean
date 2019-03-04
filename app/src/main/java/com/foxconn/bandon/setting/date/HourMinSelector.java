package com.foxconn.bandon.setting.date;

import android.app.AlarmManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.NumberStringPicker;
import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class HourMinSelector extends TimeSelector {
    private static final String TAG = HourMinSelector.class.getSimpleName();


    private Boolean mIsFormat24Checked;
    private UpdateListener mUpdateListener;

    HourMinSelector(Context context, UpdateListener updateListener) {
        super(context);
        mUpdateListener = updateListener;
    }

    public void setup(final View timeSelector, boolean isFormat24Checked) {
        this.mIsFormat24Checked = isFormat24Checked;
        final View hour12 = timeSelector.findViewById(R.id.hour_12);
        final View hour24 = timeSelector.findViewById(R.id.hour_24);
        final NumberStringPicker midnightPicker = timeSelector.findViewById(R.id.time_midnight);
        final NumberStringPicker hour24Picker = timeSelector.findViewById(R.id.time_hour_24);
        final NumberStringPicker min24Picker = timeSelector.findViewById(R.id.time_min_24);
        final NumberStringPicker hour12Picker =  timeSelector.findViewById(R.id.time_hour);
        final NumberStringPicker min12Picker = timeSelector.findViewById(R.id.time_min);

        Calendar calendar = Calendar.getInstance();
        setPickerRangeAndIntValue(hour24Picker, true, 0, 23, calendar.get(Calendar.HOUR_OF_DAY));
        setPickerRangeAndIntValue(min24Picker, true, 0, 59, calendar.get(Calendar.MINUTE));

        ArrayList<String> rangeList = new ArrayList<>();
        rangeList.add(getContext().getResources().getString(R.string.time_am));
        rangeList.add(getContext().getResources().getString(R.string.time_pm));
        midnightPicker.setRangeAndLoop(rangeList, false, calendar.get(Calendar.HOUR_OF_DAY) < 12 ? getContext().getResources().getString(R.string.time_am) : getContext().getResources().getString(R.string.time_pm));

        setPickerRangeAndIntValue(hour12Picker, true, 1, 12, calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR));
        setPickerRangeAndIntValue(min12Picker, true, 0, 59, calendar.get(Calendar.MINUTE));

        if (mIsFormat24Checked) {
            hour24.setVisibility(View.VISIBLE);
            hour12.setVisibility(View.GONE);
        } else {
            hour24.setVisibility(View.GONE);
            hour12.setVisibility(View.VISIBLE);
        }
        setAutoHourAccumulate(hour24Picker, min24Picker, String.valueOf(calendar.get(Calendar.MINUTE)));
        setAutoHourAccumulate(hour12Picker, min12Picker, String.valueOf(calendar.get(Calendar.MINUTE)));

        initConfirmBtnOnTimeSelector(timeSelector, midnightPicker, hour24Picker, min24Picker, hour12Picker, min12Picker);
        initCancelBtnOnTimeSelector(timeSelector);
    }


    private void initConfirmBtnOnTimeSelector(final View dateSelector,
                                              final NumberStringPicker midnightPicker,
                                              final NumberStringPicker hour24Picker,
                                              final NumberStringPicker min24Picker,
                                              final NumberStringPicker hour12Picker,
                                              final NumberStringPicker min12Picker) {
        dateSelector.findViewById(R.id.time_edit_btn_confirm).setOnClickListener(v -> {

            //picker is scrolling
            if (midnightPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                    || hour24Picker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                    || min24Picker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                    || hour12Picker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE
                    || min12Picker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            //set user select time
            Calendar c = Calendar.getInstance();
            if (mIsFormat24Checked) {
                c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour24Picker.getFocusValue()));
                c.set(Calendar.MINUTE, Integer.valueOf(min24Picker.getFocusValue()));
            } else {
                if (midnightPicker.getFocusValue().equals(getContext().getResources().getString(R.string.time_am))) {
                    // is after midnight
                    c.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    // is before midnight
                    c.set(Calendar.AM_PM, Calendar.PM);
                }
                c.set(Calendar.HOUR, Integer.valueOf(hour12Picker.getFocusValue()) == 12 ? 0 : Integer.valueOf(hour12Picker.getFocusValue()));
                c.set(Calendar.MINUTE, Integer.valueOf(min12Picker.getFocusValue()));
            }


            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (null == am) {
                return;
            }
            am.setTime(c.getTimeInMillis());
            ClockManager.getInstance().onBootCompleted();
            if (mUpdateListener != null) {
                mUpdateListener.newDate(c);
            }

        });
    }

    private void initCancelBtnOnTimeSelector(final View dateSelector) {
        dateSelector.findViewById(R.id.time_edit_btn_cancel).
                setOnClickListener(v -> {
                    if (mUpdateListener != null) {
                        mUpdateListener.newDate(null);
                    }
                });
    }


    private void setAutoHourAccumulate(final NumberStringPicker hourPicker, final NumberStringPicker minPicker, final String minNow) {
        minPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            String past = minNow;

            @Override
            public void scrollComplete(Object value) {

            }

            @Override
            public void onScrolled(Object value) {
                LogUtils.d(TAG, "past " + past + " value  " + value.toString());
                int diff = Integer.valueOf(value.toString()) - Integer.valueOf(past);
                if (past != null && diff < -20) {
                    //minus one hour
                    hourPicker.scrollNext();
                } else if (past != null && diff > 20) {
                    //add one hour
                    hourPicker.scrollPrev();
                }
                past = value.toString();
            }
        });
    }

    public interface UpdateListener {
        void newDate(Calendar cal);
    }
}
