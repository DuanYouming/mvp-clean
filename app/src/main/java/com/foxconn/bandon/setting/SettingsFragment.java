package com.foxconn.bandon.setting;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.setting.cleaner.CleanSettingsView;
import com.foxconn.bandon.setting.clock.view.ClockSettingsView;
import com.foxconn.bandon.setting.date.DateSettingsView;
import com.foxconn.bandon.setting.frozen.FrozenSettingsView;
import com.foxconn.bandon.setting.ice.IceAutomaticView;
import com.foxconn.bandon.setting.power.PowerSavingView;
import com.foxconn.bandon.setting.sound.SoundSettingsView;
import com.foxconn.bandon.setting.temp.TemperatureViewNew;
import com.foxconn.bandon.setting.upgrade.UpgradeView;
import com.foxconn.bandon.setting.user.UserSettingsView;
import com.foxconn.bandon.setting.wallpaper.WallpaperSettingsView;
import com.foxconn.bandon.setting.wifi.util.WifiUtils;
import com.foxconn.bandon.setting.wifi.view.WifiSettingsView;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SettingsFragment extends BaseFragment {
    public static final String TAG = SettingsFragment.class.getName();
    private SettingIconView mIceAutomatic;
    private SettingIconView mCleanView;
    private SettingIconView mPowerSavingView;
    private SettingIconView mFrozenView;
    private TextView mTextSsid;
    private Map<String, View> showViews = new HashMap<>();

    public SettingsFragment() {

    }

    BaseSettingView.DismissCallback callback = tag -> {
        LogUtils.d(TAG, "tag:" + tag);
        View view = showViews.get(tag);
        mListener.removeView(view);
        if (TextUtils.equals(tag, IceAutomaticView.TAG)) {
            updateIceView();
        } else if (TextUtils.equals(tag, CleanSettingsView.TAG)) {
            updateCleanView();
        } else if (TextUtils.equals(tag, PowerSavingView.TAG)) {
            updatePowerView();
        } else if (TextUtils.equals(tag, FrozenSettingsView.TAG)) {
            updateFrozenView();
        } else if (TextUtils.equals(tag, WifiSettingsView.TAG)) {
            updateWifiView();
        }
    };


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initIceAutomatic(view);
        initTempSettings(view);
        initCleanSettings(view);
        initSavePowerSettings(view);
        initFrozenSettings(view);
        initVoiceSettings(view);

        initWIFISettings(view);
        initClockSettings(view);
        initWallpaperSettings(view);
        initDateSettings(view);
        initUserSettings(view);
        initSystemUpdate(view);

        updateIceView();
        updateCleanView();
        updatePowerView();
        updateFrozenView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Set<String> keys = showViews.keySet();
        if (keys.size() > 0) {
            for (String key : keys) {
                View view = showViews.get(key);
                mListener.removeView(view);
            }
        }
    }

    private void initIceAutomatic(View parent) {
        mIceAutomatic = parent.findViewById(R.id.ice_automatic);
        mIceAutomatic.setOnClickListener(view -> {
            LogUtils.d(TAG, "IceAutomaticView Automatic");
            IceAutomaticView iceView = new IceAutomaticView(getContext(), callback);
            showViews.put(IceAutomaticView.TAG, iceView);
            mListener.addView(iceView);
        });

    }

    private void updateIceView() {
        int makeMode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS,
                Constant.KEY_ICE_MAKER_MODE, Constant.ICE_MAKER_MODE_OFF);
        int cubeSize = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS,
                Constant.KEY_ICE_MAKER_CUBE_SIZE, Constant.ICE_MAKER_CUBE_SIZE_NORMAL);
        int iconRes;

        if (makeMode == Constant.ICE_MAKER_MODE_OFF) {
            iconRes = R.drawable.ic_icemaker_off;
        } else if (makeMode == Constant.ICE_MAKER_MODE_EXPRESS) {
            if (cubeSize == Constant.ICE_MAKER_CUBE_SIZE_LARGE) {
                iconRes = R.drawable.ic_icemaker_big_fast;
            } else {
                iconRes = R.drawable.ic_icemaker_small_fast;
            }
        } else {
            if (cubeSize == Constant.ICE_MAKER_CUBE_SIZE_LARGE) {
                iconRes = R.drawable.ic_icemaker_big_normal;
            } else {
                iconRes = R.drawable.ic_icemaker_small_normal;
            }
        }
        mIceAutomatic.setIcon(iconRes);
    }

    private void initTempSettings(View parent) {
        View view = parent.findViewById(R.id.temperature_setting);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "Temperature Settings");
            TemperatureViewNew tempView = new TemperatureViewNew(getContext(), callback);
            showViews.put(TemperatureViewNew.TAG, tempView);
            mListener.addView(tempView);
        });
    }

    private void initCleanSettings(View parent) {
        mCleanView = parent.findViewById(R.id.clean_deodorant);
        mCleanView.setOnClickListener(view -> {
            LogUtils.d(TAG, "Clean Settings");
            CleanSettingsView cleanView = new CleanSettingsView(getContext(), callback);
            showViews.put(CleanSettingsView.TAG, cleanView);
            mListener.addView(cleanView);
        });
    }

    private void updateCleanView() {
        int deodoriser = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_CLEANER, Constant.CLEANER_OFF);
        int iconRes;
        switch (deodoriser) {
            case Constant.CLEANER_OFF:
                iconRes = R.drawable.ic_clean_off;
                break;
            case Constant.CLEANER_EX:
                iconRes = R.drawable.ic_clean_high;
                break;
            case Constant.CLEANER_NORMAL:
            default:
                iconRes = R.drawable.ic_clean_normal;
                break;
        }
        mCleanView.setIcon(iconRes);
    }

    private void initSavePowerSettings(View parent) {
        mPowerSavingView = parent.findViewById(R.id.save_electricity);
        mPowerSavingView.setOnClickListener(view -> {
            LogUtils.d(TAG, "Save Power Settings");
            PowerSavingView savingView = new PowerSavingView(getContext(), callback);
            showViews.put(PowerSavingView.TAG, savingView);
            mListener.addView(savingView);
        });
    }

    private void updatePowerView() {
        int mode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_POWER_SAVING_MODE, Constant.POWER_SAVING_MODE_OFF);
        if (mode == Constant.POWER_SAVING_MODE_ON) {
            mPowerSavingView.setIcon(R.drawable.ic_eco_high);
        } else {
            mPowerSavingView.setIcon(R.drawable.ic_eco_off);
        }
    }


    private void initFrozenSettings(View parent) {
        mFrozenView = parent.findViewById(R.id.frozen_operation);
        mFrozenView.setOnClickListener(view -> {
            LogUtils.d(TAG, "frozen operation settings");
            FrozenSettingsView frozenView = new FrozenSettingsView(getContext(), callback);
            showViews.put(FrozenSettingsView.TAG, frozenView);
            mListener.addView(frozenView);
        });
    }

    private void updateFrozenView() {
        final int mode = PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, Constant.KEY_FROZEN_OPERATION, Constant.FROZEN_NORMAL);
        int id;
        switch (mode) {
            case Constant.FROZEN_EXPRESS:
                id = R.drawable.ic_freezer_high;
                break;
            case Constant.FROZEN_FRESH:
                id = R.drawable.ic_freezer_fresh;
                break;
            case Constant.FROZEN_HEAT:
                id = R.drawable.ic_freezer_heat;
                break;
            case Constant.FROZEN_NORMAL:
            default:
                id = R.drawable.ic_freezer_normal;
                break;
        }
        mFrozenView.setIcon(id);
    }

    private void initVoiceSettings(View parent) {
        View view = parent.findViewById(R.id.voice_controller);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "voice controller settings");
            SoundSettingsView soundView = new SoundSettingsView(getContext(), callback);
            showViews.put(SoundSettingsView.TAG, soundView);
            mListener.addView(soundView);
        });
    }


    private void initWIFISettings(View parent) {
        FrameLayout mWifiView = parent.findViewById(R.id.wifi_setting);
        mWifiView.setOnClickListener(view -> {
            LogUtils.d(TAG, "wifi settings");
            WifiSettingsView wifiView = new WifiSettingsView(getContext(), callback);
            showViews.put(WifiSettingsView.TAG, wifiView);
            mListener.addView(wifiView);
        });
        mTextSsid = new TextView(getContext());
        mTextSsid.setTextSize(20);
        mTextSsid.setTextColor(Color.GRAY);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = 40;
        mWifiView.addView(mTextSsid, lp);
        updateWifiView();
    }

    private void updateWifiView() {
        ConnectivityManager connectManager = (ConnectivityManager) getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectManager != null;
        NetworkInfo info = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info.isConnected()) {
            android.net.wifi.WifiManager manager = ((android.net.wifi.WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
            if (null != manager) {
                String ssid = WifiUtils.getCurrentSsid(manager);
                mTextSsid.setText(ssid);
            }
        } else {
            mTextSsid.setText(null);
        }
    }

    private void initClockSettings(View parent) {
        View view = parent.findViewById(R.id.clock_setting);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "clock settings");
            ClockSettingsView clockView = new ClockSettingsView(getContext(), callback);
            showViews.put(ClockSettingsView.TAG, clockView);
            mListener.addView(clockView);
        });
    }

    private void initWallpaperSettings(View parent) {
        View view = parent.findViewById(R.id.wall_paper);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "wallpaper settings");
            WallpaperSettingsView wallpaperView = new WallpaperSettingsView(getContext(), callback);
            showViews.put(WallpaperSettingsView.TAG, wallpaperView);
            mListener.addView(wallpaperView);
        });
    }

    private void initDateSettings(View parent) {
        View view = parent.findViewById(R.id.date_setting);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "date settings");
            DateSettingsView dateSettingsView = new DateSettingsView(getContext(), callback);
            showViews.put(DateSettingsView.TAG, dateSettingsView);
            mListener.addView(dateSettingsView);
        });
    }

    private void initUserSettings(View parent) {
        View view = parent.findViewById(R.id.user_setting);
        view.setOnClickListener(view1 -> {
            LogUtils.d(TAG, "user settings");
            UserSettingsView userView = new UserSettingsView(getContext(), callback);
            showViews.put(UserSettingsView.TAG, userView);
            mListener.addView(userView);
        });
    }

    private void initSystemUpdate(View parent) {
        View view = parent.findViewById(R.id.system_upgrade);
        view.setOnClickListener(view1 -> {
            UpgradeView upgradeView = new UpgradeView(getContext(), callback);
            showViews.put(UpgradeView.TAG, upgradeView);
            mListener.addView(upgradeView);
        });
    }

}
