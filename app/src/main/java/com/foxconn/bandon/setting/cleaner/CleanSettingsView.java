package com.foxconn.bandon.setting.cleaner;

import android.content.Context;
import android.support.annotation.NonNull;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SingleOptionViewGroup;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class CleanSettingsView extends BaseSettingView {

    public static final String TAG = CleanSettingsView.class.getSimpleName();
    public static final int CLEAN_MODE_OFF = 0;
    public static final int CLEAN_MODE_ON = 1;
    private SingleOptionViewGroup mDeodoriserOptions;

    public CleanSettingsView(@NonNull Context context) {
        super(context);
    }

    public CleanSettingsView(@NonNull Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }

    @Override
    protected void setup() {
        initView();
        initData();
    }

    private void initView() {
        mTitle.setText(R.string.cleaner_title);
        mTitleContainer.setBackgroundResource(R.drawable.bg_clean);
        inflate(getContext(), R.layout.layout_cleaner_view, mContentView);
        mDeodoriserOptions = findViewById(R.id.cleaner_options);
        mDeodoriserOptions.setCallback(this::setDeodoriserMode);
    }

    private void initData() {
        int deodoriser = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_CLEANER, Constant.CLEANER_OFF);
        mDeodoriserOptions.setSelectedOption(deodoriser);
    }

    private void setDeodoriserMode(int mode) {
        LogUtils.d(TAG, "DeodoriserMode:" + mode);
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_CLEANER, mode);
        UartHelper.getInstance().switchIceClean(mode);
    }

    @Override
    protected void close() {
        if (null != mDismissCallback)
            mDismissCallback.onDismiss(TAG);

    }
}
