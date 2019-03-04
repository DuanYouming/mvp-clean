package com.foxconn.bandon.label.detail.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerView extends FrameLayout {
    private static final String TAG = DatePickerView.class.getSimpleName();
    private Date mCreateDate;
    private Date mExpirationDate;
    private Callback callback;
    private MaterialCalendarView mCalendarView;

    public DatePickerView(@NonNull Context context, @NonNull Date createdDate, @NonNull Date expirationDate) {
        this(context);
        mCreateDate = createdDate;
        mExpirationDate = expirationDate;
    }

    private DatePickerView(@NonNull Context context) {
        this(context, null);
    }

    private DatePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.food_label_update_expired, this);
        initCalendarView();
        initButtons();
    }

    private void initCalendarView() {
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
                return dateFormat.format(day.getDate());
            }
        });

        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    LogUtils.d(TAG, "date:" + date.toString());
                    mExpirationDate = date.getDate();
                }
            }
        });

        mCalendarView.addDecorator(new EnableItemDecorator());

        mCalendarView.setCurrentDate(mCreateDate);
        if (null != mExpirationDate) {
            mCalendarView.setSelectedDate(mExpirationDate);
        }
        mCalendarView.setSelectionColor(getContext().getColor(R.color.colorAccent));

    }

    public void setCreateDate(Date createDate) {
        mCreateDate = createDate;
        mCalendarView.setCurrentDate(mCreateDate);
    }

    public void setExpirationDate(Date expirationDate) {
        mExpirationDate = expirationDate;
        mCalendarView.setSelectedDate(mExpirationDate);
    }

    private void initButtons() {

        findViewById(R.id.cancel_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.cancel();
                }
            }
        });

        findViewById(R.id.confirm_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.confirm(mExpirationDate);
                }
            }
        });
    }

    private class EnableItemDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.getDate().getTime() <= Calendar.getInstance().getTime().getTime();
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.GRAY));
            view.setDaysDisabled(true);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    interface Callback {
        void cancel();

        void confirm(Date date);
    }
}
