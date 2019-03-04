package com.foxconn.bandon.setting.ice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SingleOptionViewGroup;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

public class IceAutomaticView extends BaseSettingView {

    public static final String TAG = IceAutomaticView.class.getSimpleName();
    private TextView mIceCubeNormal;
    private TextView mIceCubeLarge;
    private View mDurationContainer;
    private TextView mDuration;
    private SingleOptionViewGroup mIceMakerModeOptions;
    private int mCoolerMode;
    private int mCubeSize;
    private int mMakeMode;

    public IceAutomaticView(@NonNull Context context) {
        super(context);
    }

    public IceAutomaticView(@NonNull Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }

    @Override
    protected void setup() {
        initView();
        initData();
        update();
    }

    @Override
    protected void close() {
        mDismissCallback.onDismiss(TAG);
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_ice_automatic, mContentView);
        mTitle.setText("自動製冰");
        mTitleContainer.setBackgroundResource(R.drawable.bg_icemaking);
        mIceMakerModeOptions = findViewById(R.id.ice_make_mode_options);
        mIceMakerModeOptions.setCallback(this::setIceMakerMode);

        mIceCubeNormal = findViewById(R.id.cube_size_normal);
        mIceCubeNormal.setOnClickListener(v -> setIceCubeSize(Constant.ICE_MAKER_CUBE_SIZE_NORMAL));
        mIceCubeLarge = findViewById(R.id.cube_size_large);
        mIceCubeLarge.setOnClickListener(v -> setIceCubeSize(Constant.ICE_MAKER_CUBE_SIZE_LARGE));
        mDurationContainer = findViewById(R.id.duration_container);
        mDuration = mDurationContainer.findViewById(R.id.duration);
    }

    private void initData() {
        mCoolerMode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FROZEN_OPERATION, Constant.FROZEN_NORMAL);
        mCubeSize = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_CUBE_SIZE, Constant.ICE_MAKER_CUBE_SIZE_NORMAL);
        mMakeMode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_MODE, Constant.ICE_MAKER_MODE_OFF);
    }

    private void update() {
        updateIceCubeSizeButtons(mCubeSize);
        updateIceMakerModeOptions(mMakeMode);
        updateDuration(mMakeMode, mCubeSize);
        if (mCoolerMode == Constant.FROZEN_HEAT || mCoolerMode == Constant.FROZEN_EXPRESS) {
            mIceMakerModeOptions.setOptionDisabled(2);
            mIceMakerModeOptions.setOptionTextSize(2, 30);
            if (mCoolerMode == Constant.FROZEN_HEAT) {
                mIceMakerModeOptions.setOptionText(2, R.string.disabled_during_cooling_heat);
            } else {
                mIceMakerModeOptions.setOptionText(2, R.string.disabled_during_cooling_express);
            }
        }
    }

    private void updateIceMakerModeOptions(int makeMode) {
        mIceMakerModeOptions.setSelectedOption(makeMode);
    }

    private void updateIceCubeSizeButtons(int cubeSize) {
        mIceCubeNormal.setSelected(false);
        mIceCubeLarge.setSelected(false);
        switch (cubeSize) {
            case Constant.ICE_MAKER_CUBE_SIZE_LARGE:
                mIceCubeLarge.setSelected(true);
                break;
            case Constant.ICE_MAKER_CUBE_SIZE_NORMAL:
            default:
                mIceCubeNormal.setSelected(true);
                break;
        }
    }

    private void updateDuration(int makeMode, int cubeSize) {
        mDurationContainer.setVisibility(
                makeMode != Constant.ICE_MAKER_MODE_OFF ? VISIBLE : GONE);
        if (makeMode == Constant.ICE_MAKER_MODE_EXPRESS) {
            if (cubeSize == Constant.ICE_MAKER_CUBE_SIZE_LARGE) {
                mDuration.setText(R.string.ice_maker_duration_express_large);
            } else {
                mDuration.setText(R.string.ice_maker_duration_express_normal);
            }
        } else if (makeMode == Constant.ICE_MAKER_MODE_NORMAL) {
            if (cubeSize == Constant.ICE_MAKER_CUBE_SIZE_LARGE) {
                mDuration.setText(R.string.ice_maker_duration_normal_large);
            } else {
                mDuration.setText(R.string.ice_maker_duration_normal_normal);
            }
        }
    }


    private void setIceMakerMode(int makeMode) {
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_MODE, makeMode);
        mMakeMode = makeMode;
        //updateIceMakerModeOptions(mMakeMode);
        //updateDuration(mMakeMode, mCubeSize);
        LogUtils.d(TAG, "setIceMakerMode() makeMode:" + makeMode);
        if (makeMode == Constant.ICE_MAKER_MODE_OFF) {
            UartHelper.getInstance().turnOffIceMaking();
            setIceCubeSize(Constant.ICE_MAKER_CUBE_SIZE_NORMAL);
            mIceCubeNormal.setClickable(false);
            mIceCubeLarge.setClickable(false);
            mIceCubeNormal.setSelected(false);
        } else {
            UartHelper.getInstance().switchMakingMode(mMakeMode);
            mIceCubeNormal.setClickable(true);
            mIceCubeLarge.setClickable(true);
            mIceCubeNormal.setSelected(true);
        }
    }

    private void setIceCubeSize(int cubeSize) {
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_CUBE_SIZE, cubeSize);
        mCubeSize = cubeSize;
        LogUtils.d(TAG, "setIceCubeSize() mCubeSize:" + mCubeSize);
        updateIceCubeSizeButtons(mCubeSize);
        updateDuration(mMakeMode, mCubeSize);
        UartHelper.getInstance().switchIceMode(mMakeMode);
    }
}
