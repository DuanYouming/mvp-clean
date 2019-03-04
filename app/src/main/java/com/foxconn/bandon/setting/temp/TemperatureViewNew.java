package com.foxconn.bandon.setting.temp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.TempSettingBar;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class TemperatureViewNew extends BaseSettingView {

    public static final String TAG = TemperatureViewNew.class.getSimpleName();
    public static final char SUB_FRIDGE = 0x0;
    public static final char SUB_FREEZER = 0x2;
    private TempSettingBar mFridgeBar;
    private TempSettingBar mFreezerBar;
    private TextView mFridgeTempValue;
    private TextView mFreezerTempValue;
    private RadioGroup mVaryingRoom;

    public TemperatureViewNew(@NonNull Context context) {
        super(context);
    }

    public TemperatureViewNew(Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }

    @Override
    protected void setup() {
        initView();
        initData();
    }

    private void initView() {
        mTitle.setText(R.string.temperature_title);
        mTitleContainer.setBackgroundResource(R.drawable.bg_temp);
        inflate(getContext(), R.layout.layout_new_temperature_view, mContentView);
        mFridgeBar = findViewById(R.id.fridge_temperature);
        mFridgeBar.setCallback(progress -> {
            LogUtils.d(TAG, "fridge temperature:" + progress);
            setFridgeTemperature(progress);
        });

        mFreezerBar = findViewById(R.id.freezer_temperature);
        mFreezerBar.setCallback(progress -> {
            LogUtils.d(TAG, "freezer temperature:" + progress);
            setFreezerTemperature(progress);
        });

        findViewById(R.id.fridge_temp_down).setOnClickListener(v -> mFridgeBar.down());
        findViewById(R.id.fridge_temp_up).setOnClickListener(v -> mFridgeBar.up());
        findViewById(R.id.freezer_temp_down).setOnClickListener(v -> mFreezerBar.down());
        findViewById(R.id.freezer_temp_up).setOnClickListener(v -> mFreezerBar.up());

        mFridgeTempValue = findViewById(R.id.fridge_temp_value);
        mFreezerTempValue = findViewById(R.id.freezer_temp_value);

        mVaryingRoom = findViewById(R.id.radio_group);
        mVaryingRoom.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.radio_unfreeze) {
                LogUtils.d(TAG, "VARYING_UNFREEZE:");
                UartHelper.getInstance().switchVaryingStatus(Constant.VARYING_UNFREEZE);
                PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, Constant.VARYING_UNFREEZE);
            } else {
                LogUtils.d(TAG, "VARYING_REFRIGERATE:");
                UartHelper.getInstance().switchVaryingStatus(Constant.VARYING_REFRIGERATE);
                PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, Constant.VARYING_REFRIGERATE);
            }
        });
    }

    private void initData() {
        int fridgeTemp = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, 0);
        int freezerTemp = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, 0);
        int varyingStatus = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, 0);
        mFreezerBar.setProgress(freezerTemp);
        mFridgeBar.setProgress(fridgeTemp);
        ((RadioButton) mVaryingRoom.getChildAt(varyingStatus)).setChecked(true);
    }

    private void setFridgeTemperature(int temperature) {
        LogUtils.d(TAG, "FridgeTemperature:" + temperature);
        mFridgeTempValue.setText(getTempText(temperature));
        UartHelper.getInstance().setTemp(SUB_FRIDGE, temperature);
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, temperature);
    }

    private void setFreezerTemperature(int temperature) {
        LogUtils.d(TAG, "FreezerTemperature:" + temperature);
        mFreezerTempValue.setText(getTempText(temperature));
        UartHelper.getInstance().setTemp(SUB_FREEZER, temperature);
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, temperature);
    }


    private String getTempText(int temp) {
        return "< " + temp + " >";
    }


    @Override
    protected void close() {
        if (null != mDismissCallback)
            mDismissCallback.onDismiss(TAG);

    }
}
