package com.foxconn.bandon.setting.clock.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.NumberStringPicker;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ClockEditView extends RelativeLayout {
    private static final String TAG = ClockEditView.class.getSimpleName();
    private int[] btnIDs = {R.id.sun_day, R.id.mon_day, R.id.tue_day, R.id.wed_day, R.id.thu_day, R.id.fri_day, R.id.sat_day};
    private NumberStringPicker mHourPicker;
    private NumberStringPicker mMinPicker;
    private NumberStringPicker mMidnightPicker;
    private NumberStringPicker mHour24Picker;
    private NumberStringPicker mMin24Picker;
    private ClockEditCallback callback;
    private boolean mIs24HR;
    private boolean mRepeat;
    private List<Integer> mChoosePeriods;
    private List<Integer> mButtonIDs = new ArrayList<>();
    private EditText mEditText;
    private ClockBean mClockBean;
    private View mTwentyFourHR;
    private View mTwelveHR;

    public ClockEditView(Context context, ClockBean clock) {
        super(context);
        this.mClockBean = clock;
    }

    public ClockEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView(mClockBean);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void initView(ClockBean clock) {
        LogUtils.d(TAG, "initView()");
        inflate(getContext(), R.layout.layout_clock_edit_view, this);
        mClockBean = clock;
        initButtonsList();
        initRepeatView();
        initNumPickerView();
        initPeriodButtons();
        setTimePickerInitValue();

        mEditText = findViewById(R.id.edit_text);
        if (null != mClockBean) {
            mEditText.setText(mClockBean.getTag());
        }

        View btnCancel = findViewById(R.id.btn_cancel);
        View btnConfirm = findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(v -> callback.cancel());

        btnConfirm.setOnClickListener(v -> {
            if (mHourPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE || mMinPicker.getScrollStatus() != RecyclerView.SCROLL_STATE_IDLE) {
                LogUtils.d(TAG, "min or hour picker is scrolling");
                return;
            }
            boolean isUpdate = true;
            if (null == mClockBean) {
                mClockBean = new ClockBean(UUID.randomUUID().toString(), System.currentTimeMillis());
                isUpdate = false;
            }
            mClockBean.setEnable(true);
            Calendar calendar = Calendar.getInstance();
            LogUtils.d(TAG,"calendar day:"+calendar.get(Calendar.DAY_OF_MONTH));
            if (!mIs24HR) {
                calendar.set(Calendar.AM_PM, mMidnightPicker.getFocusValue().equals(getContext().getResources().getString(R.string.alarm_am)) ? Calendar.AM : Calendar.PM);
                calendar.set(Calendar.HOUR, Integer.valueOf(mHourPicker.getFocusValue()) == 12 ? 0 : Integer.valueOf(mHourPicker.getFocusValue()));
                calendar.set(Calendar.MINUTE, Integer.valueOf(mMinPicker.getFocusValue()));
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHour24Picker.getFocusValue()));
                calendar.set(Calendar.MINUTE, Integer.valueOf(mMin24Picker.getFocusValue()));
            }
            mClockBean.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            mClockBean.setMin(calendar.get(Calendar.MINUTE));
            Collections.sort(mChoosePeriods);
            mClockBean.setPeriods(mChoosePeriods.toString());
            mClockBean.setTag(mEditText.getText().toString());
            callback.confirm(mClockBean, isUpdate);
        });
    }

    private void initButtonsList() {
        for (int id : btnIDs) {
            mButtonIDs.add(id);
        }
    }

    private void initRepeatView() {
        RadioGroup group = findViewById(R.id.radio_group);
        RadioButton repeatButton = findViewById(R.id.text_repeat);
        RadioButton btn = findViewById(R.id.text_not_repeat);
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            mRepeat = (checkedId == R.id.text_repeat);
            updatePeriodButtons();
        });

        if (null != mClockBean) {
            mChoosePeriods = mClockBean.getPeriodsList();
            repeatButton.setChecked((mChoosePeriods.size() > 0));
            btn.setChecked(!(mChoosePeriods.size() > 0));
        } else {
            mChoosePeriods = new ArrayList<>();
            repeatButton.setChecked(true);
            btn.setChecked(false);
        }
    }

    private void initPeriodButtons() {
        for (int i = 0; i < mButtonIDs.size(); i++) {
            findViewById(mButtonIDs.get(i)).setOnClickListener(clickListener);
        }
    }

    private void updatePeriodButtons() {
        for (int i = 0; i < mButtonIDs.size(); i++) {
            int id = mButtonIDs.get(i);
            TextView view = findViewById(id);
            if (mRepeat) {
                view.setAlpha(1.0f);
                if (mChoosePeriods.contains(mButtonIDs.indexOf(id))) {
                    view.setTextColor(ContextCompat.getColor(getContext(), R.color.clock_repeat_active));
                    view.setBackgroundResource(R.drawable.alarm_repeat_select_background);
                } else {
                    view.setTextColor(ContextCompat.getColor(getContext(), R.color.clock_repeat_not_active));
                    view.setBackgroundResource(R.drawable.alarm_repeat_not_select_background);
                }
            } else {
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.clock_repeat_not_active));
                view.setBackgroundResource(R.drawable.alarm_repeat_not_select_background);
                view.setAlpha(0.2f);
            }
        }
    }

    private void initNumPickerView() {
        mTwentyFourHR = findViewById(R.id.view_24_hr);
        mTwelveHR = findViewById(R.id.view_12_hr);
        mHour24Picker = findViewById(R.id.alarm_hour_24);
        mMin24Picker = findViewById(R.id.alarm_min_24);
        mHourPicker = findViewById(R.id.alarm_hour);
        mMinPicker = findViewById(R.id.alarm_min);
        mMidnightPicker = findViewById(R.id.alarm_midnight);
    }

    private void setTimePickerInitValue() {
        mIs24HR = is24HR();
        Calendar calendar = Calendar.getInstance();
        if (null != mClockBean) {
            calendar.set(Calendar.HOUR_OF_DAY, mClockBean.getHour());
            calendar.set(Calendar.MINUTE, mClockBean.getMin());
            calendar.setTimeZone(mClockBean.getTimezone());
        }
        if (mIs24HR) {
            mTwentyFourHR.setVisibility(VISIBLE);
            mTwelveHR.setVisibility(INVISIBLE);
            initHour24Picker(calendar.get(Calendar.HOUR_OF_DAY));
            initMin24Picker(calendar.get(Calendar.MINUTE));
            setAutoHour24Accumulate(String.valueOf(calendar.get(Calendar.MINUTE)));
        } else {
            mTwentyFourHR.setVisibility(INVISIBLE);
            mTwelveHR.setVisibility(VISIBLE);
            initHour12Picker(calendar.get(Calendar.HOUR));
            initMin12Picker(calendar.get(Calendar.MINUTE));
            initMidnightPicker(calendar.get(Calendar.AM_PM));
            setAutoHourAccumulate(String.valueOf(calendar.get(Calendar.MINUTE)));
        }
    }

    private void initHour12Picker(int hour) {
        ArrayList<String> hourList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        if (hour == 0) {
            hour = 12;
        }
        mHourPicker.setRangeAndLoop(hourList, true, String.format(Locale.getDefault(), "%02d", hour));
    }

    private void initMin12Picker(int minute) {
        List<String> minList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        mMinPicker.setRangeAndLoop(minList, true, String.format(Locale.getDefault(), "%02d", minute));
    }

    private void initMin24Picker(int minute) {
        List<String> minList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        mMin24Picker.setRangeAndLoop(minList, true, String.format(Locale.getDefault(), "%02d", minute));
    }

    private void initMidnightPicker(int amOrPm) {
        List<String> midnightList = new ArrayList<>();
        midnightList.add(getContext().getResources().getString(R.string.alarm_am));
        midnightList.add(getContext().getResources().getString(R.string.alarm_pm));
        if (amOrPm == Calendar.AM) {
            mMidnightPicker.setRangeAndLoop(midnightList, false, getContext().getResources().getString(R.string.alarm_am));
        } else {
            mMidnightPicker.setRangeAndLoop(midnightList, false, getContext().getResources().getString(R.string.alarm_pm));
        }
    }


    private void initHour24Picker(int hour) {
        ArrayList<String> hourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        mHour24Picker.setRangeAndLoop(hourList, true, String.format(Locale.getDefault(), "%02d", hour));
    }


    private void setAutoHourAccumulate(final String minNow) {
        mMinPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            String past = minNow;

            @Override
            public void scrollComplete(Object value) {
            }

            @Override
            public void onScrolled(Object value) {
                int diff = Integer.valueOf(value.toString()) - Integer.valueOf(past);
                if (past != null && diff < -20) {
                    mHourPicker.scrollNext();
                } else if (past != null && diff > 20) {
                    mHourPicker.scrollPrev();
                }
                past = value.toString();
            }
        });
    }

    private void setAutoHour24Accumulate(final String minNow) {
        mMin24Picker.setScrollListener(new NumberStringPicker.ScrollListener() {
            String past = minNow;

            @Override
            public void scrollComplete(Object value) {
            }

            @Override
            public void onScrolled(Object value) {
                int diff = Integer.valueOf(value.toString()) - Integer.valueOf(past);
                if (past != null && diff < -20) {
                    mHour24Picker.scrollNext();
                } else if (past != null && diff > 20) {
                    mHour24Picker.scrollPrev();
                }
                past = value.toString();
            }
        });
    }

    private boolean is24HR() {
        return DateFormat.is24HourFormat(getContext());
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mRepeat) {
                if (mChoosePeriods.contains(mButtonIDs.indexOf(view.getId()))) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.clock_repeat_not_active));
                    view.setBackgroundResource(R.drawable.alarm_repeat_not_select_background);
                    mChoosePeriods.remove(Integer.valueOf(mButtonIDs.indexOf(view.getId())));
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.clock_repeat_active));
                    view.setBackgroundResource(R.drawable.alarm_repeat_select_background);
                    mChoosePeriods.add(mButtonIDs.indexOf(view.getId()));
                }
            }
        }
    };


    public interface ClockEditCallback {

        void cancel();

        void confirm(ClockBean clock, boolean isUpdate);
    }

    public void setCallback(ClockEditCallback callback) {
        this.callback = callback;
    }

}
