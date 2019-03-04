package com.foxconn.bandon.setting.date;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.StatusSwitch;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateSettingsView extends RelativeLayout {
    public static final String TAG = DateSettingsView.class.getSimpleName();
    private BaseSettingView.DismissCallback mCallback;
    private StatusSwitch mIsFormat24;
    private Boolean mIsFormat24Checked;
    private StatusSwitch mIsAutoTime;
    private Boolean mIsAutoTimeChecked;
    private FrameLayout mTimezoneContainer;
    private RelativeLayout mTimezoneEditContainer;
    private TextView mTimeZone;
    private TextView mDate;
    private TextView mTime;
    private TextView mTimezoneConfirmBtn;
    private FrameLayout mDateContainer;
    private FrameLayout mHourMinContainer;
    private View mTimeZoneMask;
    private View mDateMask;
    private View mTimeMask;
    private View mDateEditView;
    private DateSelector mDateSelector;
    private View mTimeEditView;
    private HourMinSelector mTimeSelector;
    private TimezoneAdapter mAdapter;
    private List<Map> mZoneList;
    public final static String KEY_TIME = "time";
    public final static String KEY_BASE_YEAR_ENTRY = "base_year";

    public DateSettingsView(Context context) {
        super(context);
    }

    public DateSettingsView(Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mCallback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void setup() {
        inflate(getContext(), R.layout.layout_date_settings_view, this);
        mDate = findViewById(R.id.date);
        mIsFormat24 = findViewById(R.id.is_format_24);
        mIsAutoTime = findViewById(R.id.is_auto_time);
        mTimezoneContainer = findViewById(R.id.timezone_container);
        mTimezoneEditContainer = findViewById(R.id.timezone_edit_container);
        mTimezoneConfirmBtn = findViewById(R.id.timezone_edit_btn_confirm);
        mTimeZone = findViewById(R.id.timezone);
        mDateContainer = findViewById(R.id.date_container);
        mHourMinContainer = findViewById(R.id.hour_min_container);
        mTime = findViewById(R.id.time);
        mTimeZoneMask = findViewById(R.id.timezone_mask);
        mDateMask = findViewById(R.id.date_mask);
        mTimeMask = findViewById(R.id.time_mask);

        loadSettings();
        mZoneList = new ZoneList(getContext()).getTimezoneSortedList(ZoneList.KEY_OFFSET);
        mDate.setText(getCurrentDateString());
        mTimeZone.setText(getTimezoneDisplayName());

        ImageView closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(view -> mCallback.onDismiss(TAG));

        mDateEditView = findViewById(R.id.date_edit_container);
        mDateSelector = new DateSelector(getContext(), date -> {
            if (date != null) {
                mDate.setText(getDateString(date.getTime()));
            }
            mDateEditView.setVisibility(View.GONE);
            mDateContainer.setVisibility(View.VISIBLE);
        });
        mDateSelector.setup(mDateEditView);

        mTimeEditView = findViewById(R.id.time_edit_container);
        mTimeSelector = new HourMinSelector(getContext(), cal -> {
            if (cal != null) {
                updateTime();
            }
            mTimeEditView.setVisibility(View.GONE);
            mHourMinContainer.setVisibility(View.VISIBLE);

        });
        mTimeSelector.setup(mTimeEditView, mIsFormat24Checked);
        RecyclerView recyclerView = findViewById(R.id.timezone_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new TimezoneAdapter();
        recyclerView.setAdapter(mAdapter);
        String defaultId = TimeZone.getDefault().getID();

        for (int pos = 0; pos < mZoneList.size(); pos++) {
            Map hashMap = mZoneList.get(pos);
            if (hashMap.get(ZoneList.KEY_ID).equals(defaultId)) {
                recyclerView.scrollToPosition(pos);
                break;
            }
        }
        initIsFormat24Container();
        initIsAutoTimeContainer();
        initTimezoneContainer();
        initDateContainer();
        initHourMinContainer();
    }

    private void loadSettings() {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(getContext());
        mIsFormat24.setChecked(is24HourFormat);
        mIsFormat24Checked = is24HourFormat;
        try {
            int result = Settings.System.getInt(getContext().getContentResolver(), Settings.Global.AUTO_TIME);
            if (result > 0) {
                setBaseYear();
            }
            mIsAutoTime.setChecked(result == 1);
            mIsAutoTimeChecked = (result == 1);
            setDateTimeStatus();
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e(TAG, "get Settings.Global.AUTO_TIME failure " + e);
        }
    }

    private void setDateTimeStatus() {
        int color;
        if (mIsAutoTimeChecked) {
            color = getResources().getColor(R.color.setting_time_disable);
            mTimeZoneMask.setVisibility(View.VISIBLE);
            mTimeMask.setVisibility(View.VISIBLE);
            mDateMask.setVisibility(View.VISIBLE);
        } else {
            color = getResources().getColor(R.color.setting_time_enable);
            mTimeZoneMask.setVisibility(View.GONE);
            mTimeMask.setVisibility(View.GONE);
            mDateMask.setVisibility(View.GONE);
        }
        mTimeZone.setTextColor(color);
        mDate.setTextColor(color);
        mTime.setTextColor(color);
    }

    private void initIsFormat24Container() {
        mIsFormat24.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String result = "24";
            if (!isChecked) {
                result = "12";
            }
            mIsFormat24Checked = isChecked;
            Settings.System.putString(getContext().getContentResolver(), Settings.System.TIME_12_24, result);
            updateTime();
            if (mHourMinContainer.getVisibility() == View.GONE) {
                mTimeSelector.setup(mTimeEditView, mIsFormat24Checked);
            }
        });
    }

    private void initIsAutoTimeContainer() {
        mIsAutoTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mIsAutoTimeChecked = isChecked;
            Settings.Global.putInt(getContext().getContentResolver(), Settings.Global.AUTO_TIME, isChecked ? 1 : 0);
            if (isChecked) {
                postDelayed(() -> {
                    mDate.setText(getCurrentDateString());
                    updateTime();
                    mAdapter.notifyDataSetChanged();
                    mTimeZone.setText(getTimezoneDisplayName());
                }, 200);
                setBaseYear();
                if (mHourMinContainer.getVisibility() == View.GONE) {
                    mHourMinContainer.setVisibility(View.VISIBLE);
                    mTimeEditView.setVisibility(View.GONE);
                }

                if (mTimezoneContainer.getVisibility() == View.GONE) {
                    mTimezoneContainer.setVisibility(View.VISIBLE);
                    mTimezoneEditContainer.setVisibility(View.GONE);
                }

                if (mDateContainer.getVisibility() == View.GONE) {
                    mDateContainer.setVisibility(View.VISIBLE);
                    mDateEditView.setVisibility(View.GONE);
                }
            }
            setDateTimeStatus();
        });
    }


    private void initTimezoneContainer() {

        mTimezoneContainer.setOnClickListener(view -> {
            mTimezoneContainer.setVisibility(View.GONE);
            mTimezoneEditContainer.setVisibility(View.VISIBLE);
        });

        mTimezoneConfirmBtn.setOnClickListener(view -> {
            mTimezoneContainer.setVisibility(View.VISIBLE);
            mTimezoneEditContainer.setVisibility(View.GONE);
            mTimeZone.setText(getTimezoneDisplayName());
            mDate.setText(getCurrentDateString());
            updateTime();
        });
    }

    private void initDateContainer() {
        mDateContainer.setOnClickListener(v -> {
            if (mIsAutoTimeChecked) {
                return;
            }

            mDateSelector.setup(mDateEditView);
            mDateEditView.setVisibility(View.VISIBLE);
            mDateContainer.setVisibility(View.GONE);
        });
    }


    private void initHourMinContainer() {
        mHourMinContainer.setOnClickListener(v -> {
            if (mIsAutoTimeChecked) {
                return;
            }
            mTimeSelector.setup(mTimeEditView, mIsFormat24Checked);
            mHourMinContainer.setVisibility(View.GONE);
            mTimeEditView.setVisibility(View.VISIBLE);
        });
        updateTime();
    }

    private void setBaseYear() {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        PreferenceUtils.setString(getContext(), KEY_TIME, KEY_BASE_YEAR_ENTRY, year);
    }


    private String getTimezoneDisplayName() {
        return getTimezoneDisplayName(TimeZone.getDefault().getID());
    }

    private String getTimezoneDisplayName(String tId) {
        for (Map hashMap : mZoneList) {
            if (hashMap.get(ZoneList.KEY_ID).toString().equals(tId)) {
                return hashMap.get(ZoneList.KEY_DISPLAY_NAME).toString();
            }
        }
        return null;
    }

    private void updateTime() {
        String format;
        if (mIsFormat24Checked) {
            format = "HH:mm";
        } else {
            format = "aa hh:mm";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        mTime.setText(simpleDateFormat.format(calendar.getTime()));
    }


    class TimezoneAdapter extends RecyclerView.Adapter<TimezoneAdapter.TimezoneViewHolder> {

        @Override
        public TimezoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.settings_timezone_item, parent, false);
            return new TimezoneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TimezoneViewHolder holder, int position) {
            Map hashMap = mZoneList.get(position);
            holder.mTimezoneName.setText(hashMap.get(ZoneList.KEY_DISPLAY_NAME).toString());
            String defaultId = TimeZone.getDefault().getID();

            if (hashMap.get(ZoneList.KEY_ID).toString().equals(defaultId)) {
                holder.mTimezoneName.setTextColor(getResources().getColor(R.color.setting_time_disable));
            } else {
                holder.mTimezoneName.setTextColor(getResources().getColor(R.color.wifi_detail_login_password_hint));
            }

            holder.itemView.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                Map hashMap1 = mZoneList.get(pos);
                setTimezone(hashMap1.get(ZoneList.KEY_ID).toString());
                notifyDataSetChanged();
            });

        }

        private void setTimezone(String tId) {
            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if(null!=am) {
                am.setTimeZone(tId);
            }
        }

        @Override
        public int getItemCount() {
            return mZoneList != null ? mZoneList.size() : 0;
        }

        class TimezoneViewHolder extends RecyclerView.ViewHolder {
            TextView mTimezoneName;

            TimezoneViewHolder(View itemView) {
                super(itemView);
                mTimezoneName = itemView.findViewById(R.id.timezone_name);
            }
        }
    }

    private String getCurrentDateString() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return format.format(Calendar.getInstance().getTime());

    }

    private String getDateString(Date time) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return format.format(time);
    }

}
