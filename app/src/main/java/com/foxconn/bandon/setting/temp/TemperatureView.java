package com.foxconn.bandon.setting.temp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SeekBar;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class TemperatureView extends BaseSettingView {

    public static final String TAG = TemperatureView.class.getSimpleName();
    private SeekBar mFridgeTemperature;
    private SeekBar mFreezerTemperature;
    private TextView mTextFreezerValue;
    private TextView mTextFridgeValue;
    private RadioGroup mVaryingRoom;


    public TemperatureView(@NonNull Context context) {
        super(context);
    }

    public TemperatureView(Context context, DismissCallback callback) {
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
        inflate(getContext(), R.layout.layout_temperature_view, mContentView);

        mFridgeTemperature = findViewById(R.id.fridge_temperature);
        mFridgeTemperature.setValueRange(Constant.REFRIGERATOR_TEMP_MIN, Constant.REFRIGERATOR_TEMP_MAX);
        mFridgeTemperature.setCallback(new SeekBar.Callback() {
            @Override
            public void onProgressChanged(int progress) {
                setFridgeTemperature(progress);
            }

            @Override
            public void onLevel(int level) {

            }
        });

        mFreezerTemperature = findViewById(R.id.freezer_temperature);
        mFreezerTemperature.setValueRange(Constant.FREEZER_TEMP_MIN, Constant.FREEZER_TEMP_MAX);
        mFreezerTemperature.setCallback(new SeekBar.Callback() {
            @Override
            public void onProgressChanged(int progress) {
                setFreezerTemperature(progress);
            }

            @Override
            public void onLevel(int level) {

            }
        });

        mTextFreezerValue = findViewById(R.id.text_freezer_temp);
        mTextFridgeValue = findViewById(R.id.text_fridge_temp);

        mVaryingRoom = findViewById(R.id.radio_group);
        mVaryingRoom.setOnCheckedChangeListener((radioGroup, id) -> {
            LogUtils.d(TAG, "index:" + id);
            if (id == R.id.radio_unfreeze) {
                PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, Constant.VARYING_UNFREEZE);
            } else {
                PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, Constant.VARYING_REFRIGERATE);
            }
        });
    }

    private void initData() {
        int fridgeTemp = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, 0);
        int freezerTemp = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, 0);
        int varyingStatus = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_VARYING_STATUS, 0);

        mFreezerTemperature.setValue(freezerTemp);
        mFridgeTemperature.setValue(fridgeTemp);

        mTextFreezerValue.setText(getTempText(freezerTemp));
        mTextFridgeValue.setText(getTempText(fridgeTemp));
        ((RadioButton) mVaryingRoom.getChildAt(varyingStatus)).setChecked(true);
    }

    private void setFridgeTemperature(int temperature) {
        LogUtils.d(TAG, "FridgeTemperature:" + temperature);
        mTextFridgeValue.setText(getTempText(temperature));
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, temperature);
    }

    private void setFreezerTemperature(int temperature) {
        LogUtils.d(TAG, "FreezerTemperature:" + temperature);
        mTextFreezerValue.setText(getTempText(temperature));
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, temperature);
    }

    private String getTempText(int temp) {
        String mTempUnit = getContext().getResources().getString(R.string.temp_unit);
        return temp + " " + mTempUnit;
    }

    @Override
    protected void close() {
        if (null != mDismissCallback)
            mDismissCallback.onDismiss(TAG);

    }
}
