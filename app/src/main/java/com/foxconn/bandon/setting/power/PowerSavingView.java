package com.foxconn.bandon.setting.power;

import android.content.Context;
import android.support.annotation.NonNull;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SingleOptionViewGroup;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.PreferenceUtils;


public class PowerSavingView extends BaseSettingView {
    public static final String TAG = PowerSavingView.class.getSimpleName();

    public PowerSavingView(@NonNull Context context) {
        super(context);
    }

    public PowerSavingView(@NonNull Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }

    @Override
    protected void setup() {
        mTitle.setText(R.string.power_saving_title);
        mTitleContainer.setBackgroundResource(R.drawable.bg_eco);
        inflate(getContext(), R.layout.layout_power_saving_view, mContentView);
        SingleOptionViewGroup ecoModeOptions = findViewById(R.id.power_saving_options);
        ecoModeOptions.setCallback(this::setPowerSavingMode);
        int mode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_POWER_SAVING_MODE, Constant.POWER_SAVING_MODE_OFF);
        ecoModeOptions.setSelectedOption(mode);
    }

    private void setPowerSavingMode(int id) {
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_POWER_SAVING_MODE, id);
        UartHelper.getInstance().switchEcoMode(id);
    }

    @Override
    protected void close() {
        if (null != mDismissCallback) {
            mDismissCallback.onDismiss(TAG);
        }
    }
}
