package com.foxconn.bandon.setting.clock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.custom.SpacesItemDecoration;
import com.foxconn.bandon.custom.StatusSwitch;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.setting.clock.ClockContact;
import com.foxconn.bandon.setting.clock.ClockUtils;
import com.foxconn.bandon.setting.clock.presenter.ClockPresenter;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClockSettingsView extends FrameLayout implements ClockContact.View {
    public static final String TAG = ClockSettingsView.class.getSimpleName();
    private BaseSettingView.DismissCallback mDismissCallback;
    private Context mContext;
    private FrameLayout mEditView;
    private View mMainView;
    private List<ClockBean> clocks = new ArrayList<>();
    private boolean mIs24HR;
    private boolean isDeleteType;
    private View mBtnAdd;
    private ClockContact.Presenter mPresenter;
    private Adapter mAdapter;
    private List<String> mDateArray = ClockUtils.getWeeksArray();
    private View mNoRecordsView;


    public ClockSettingsView(@NonNull Context context) {
        super(context);
    }

    public ClockSettingsView(@NonNull Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
        this.mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ClockRepository.destroy();
        mPresenter.removeListener();
    }

    @Override
    public void notifyDataChanged(List<ClockBean> values) {
        if (null != values && values.size() > 0) {
            clocks.clear();
            clocks.addAll(values);
            mAdapter.notifyDataSetChanged();
            mNoRecordsView.setVisibility(INVISIBLE);
        } else {
            mNoRecordsView.setVisibility(VISIBLE);
            mBtnAdd.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setPresenter(ClockContact.Presenter presenter) {
        mPresenter = presenter;
    }

    protected void setup() {
        initView();
        initListView();
        initPresenter();
    }

    private void initPresenter() {
        new ClockPresenter(this, ClockRepository.getInstance(BandonDataBase.getInstance(getContext()).clockDao(), new AppExecutors()));
        mPresenter.addListener();
        mPresenter.getAll();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_clock_setting_view, this);
        mIs24HR = DateFormat.is24HourFormat(getContext());
        mBtnAdd = findViewById(R.id.btn_add);
        View btnClose = findViewById(R.id.btn_close);
        mBtnAdd.setOnClickListener(clickListener);
        btnClose.setOnClickListener(clickListener);
        mMainView = findViewById(R.id.main_view);
        mEditView = findViewById(R.id.clock_edit_view);
        mNoRecordsView = findViewById(R.id.no_records);
    }

    private void initListView() {
        RecyclerView listView = findViewById(R.id.alarm_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.addItemDecoration(new SpacesItemDecoration(30));
        mAdapter = new Adapter(clocks, getContext());
        listView.setAdapter(mAdapter);
    }

    private void add(ClockBean clock) {
        mEditView.setVisibility(VISIBLE);
        mMainView.setVisibility(INVISIBLE);
        ClockEditView view = new ClockEditView(getContext(), clock);
        view.setCallback(editCallback);
        mEditView.addView(view);
    }

    protected void close() {
        if (null != mDismissCallback) {
            mDismissCallback.onDismiss(TAG);
        }
    }

    private void changeToDeleteMode() {
        isDeleteType = true;
        mBtnAdd.setVisibility(View.GONE);
    }

    private void leaveDeleteMode() {
        isDeleteType = false;
        mBtnAdd.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_close) {
                close();
            } else if (id == R.id.btn_add) {
                add(null);
            }
        }
    };

    ClockEditView.ClockEditCallback editCallback = new ClockEditView.ClockEditCallback() {
        @Override
        public void cancel() {
            mEditView.setVisibility(INVISIBLE);
            mMainView.setVisibility(VISIBLE);
            mEditView.removeAllViews();
        }

        @Override
        public void confirm(ClockBean clock, boolean isUpdate) {
            mEditView.setVisibility(INVISIBLE);
            mMainView.setVisibility(VISIBLE);
            LogUtils.d(TAG, "clock:" + clock.toString());
            if (isUpdate) {
                mPresenter.update(clock, true);
            } else {
                mPresenter.add(clock);
            }
            mEditView.removeAllViews();
        }
    };


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<ClockBean> mClocks;
        private LayoutInflater mInflater;

        Adapter(List<ClockBean> clocks, Context context) {
            this.mClocks = clocks;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.alarm_list_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ClockBean clock = mClocks.get(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(clock.getTimezone());
            calendar.set(Calendar.HOUR_OF_DAY, clock.getHour());
            calendar.set(Calendar.MINUTE, clock.getMin());

            if (mIs24HR) {
                SimpleDateFormat format24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                holder.alarmTime.setText(format24.format(calendar.getTime()));
            } else {
                SimpleDateFormat format12 = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                holder.alarmTime.setText(format12.format(calendar.getTime()));
            }

            holder.alarmName.setText(clock.getTag() + " " + getDateString(clock));
            holder.switchBar.setChecked(clock.getEnable());
            holder.switchBar.setOnCheckedChangeListener(changeListener);
            holder.switchBar.setTag(position);

            if (isDeleteType) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
                holder.switchBar.setVisibility(View.GONE);
                holder.deleteBtn.setTag(holder.getAdapterPosition());
                holder.deleteBtn.setOnClickListener(clickListener);
            } else {
                holder.deleteBtn.setVisibility(View.GONE);
                holder.switchBar.setVisibility(View.VISIBLE);
                holder.deleteBtn.setOnClickListener(null);
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDeleteType) {
                        add(clock);
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isDeleteType) {
                        leaveDeleteMode();
                    } else {
                        changeToDeleteMode();
                    }
                    mAdapter.notifyDataSetChanged();
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mClocks == null ? 0 : mClocks.size();
        }

        private String getDateString(ClockBean clock) {
            StringBuilder sb = new StringBuilder();
            List<Integer> list = clock.getPeriodsList();
            if (list.size() == 0) {
                sb.append(getContext().getResources().getString(R.string.alarm_only_one_time));
            } else {
                for (int i = 0; i < list.size(); i++) {
                    sb.append(mDateArray.get(list.get(i)));
                    if (i != list.size() - 1) {
                        sb.append("、");
                    }
                }
            }
            return sb.toString().replaceAll("、$", "");
        }


        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) buttonView.getTag();
                mClocks.get(position).setEnable(isChecked);
                mPresenter.update(mClocks.get(position), false);
            }
        };

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mPresenter.delete(mClocks.get(position));
                mClocks.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        };

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView alarmName;
            private TextView alarmTime;
            private StatusSwitch switchBar;
            private ImageView deleteBtn;
            private View itemView;

            ViewHolder(View view) {
                super(view);
                alarmName = view.findViewById(R.id.alarm_name);
                alarmTime = view.findViewById(R.id.alarm_time);
                switchBar = view.findViewById(R.id.is_alarm_enable);
                deleteBtn = view.findViewById(R.id.delete_btn);
                itemView = view;
            }
        }
    }

}
