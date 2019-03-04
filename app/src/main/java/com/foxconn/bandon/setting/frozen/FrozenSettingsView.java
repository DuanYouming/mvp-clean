package com.foxconn.bandon.setting.frozen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class FrozenSettingsView extends BaseSettingView {

    public static final String TAG = FrozenSettingsView.class.getSimpleName();
    private List<Integer> mIDs = new ArrayList<>();


    public FrozenSettingsView(@NonNull Context context) {
        super(context);
    }

    public FrozenSettingsView(@NonNull Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }


    @Override
    protected void setup() {
        mTitle.setText(R.string.frozen_title);
        mTitleContainer.setBackgroundResource(R.drawable.bg_freezer);
        inflate(getContext(), R.layout.layout_frozen_view, mContentView);
        mIDs.add(R.id.frozen_normal);
        mIDs.add(R.id.frozen_fresh);
        mIDs.add(R.id.frozen_express);
        mIDs.add(R.id.frozen_heat);

        for (int id : mIDs) {
            findViewById(id).setOnClickListener(clickListener);
        }

        TextView textExpress = findViewById(R.id.frozen_express);
        TextView textHeat = findViewById(R.id.frozen_heat);
        int iceMakeMode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_ICE_MAKER_MODE, Constant.ICE_MAKER_MODE_OFF);
        if (iceMakeMode == Constant.ICE_MAKER_MODE_EXPRESS) {
            textExpress.setEnabled(false);
            textExpress.setText(R.string.disabled_during_ice_make_express);
            textExpress.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            textHeat.setEnabled(false);
            textHeat.setText(R.string.disabled_during_ice_make_express);
            textHeat.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        }

        int coolerMode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FROZEN_OPERATION, Constant.FROZEN_NORMAL);
        initButtons(mIDs.get(coolerMode));
    }

    @Override
    protected void close() {
        if (null != mDismissCallback) {
            mDismissCallback.onDismiss(TAG);
        }
    }

    private void setFrozenMode(int mode) {
        LogUtils.d(TAG, "Frozen Mode：" + mode);
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FROZEN_OPERATION, mode);
        UartHelper.getInstance().switchFrozenStatus(mode);
    }

    View.OnClickListener clickListener = view -> {
        setFrozenMode(mIDs.indexOf(view.getId()));
        initButtons(view.getId());
    };

    private void initButtons(int resID) {
        for (int i = 0; i < mIDs.size(); i++) {
            int id = mIDs.get(i);
            if (resID == id) {
                findViewById(id).setSelected(true);
            } else {
                findViewById(id).setSelected(false);
            }
        }
    }
}
