package com.foxconn.bandon.main.view;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxcomm.bandon.aispeech.SpeechManager;
import com.foxconn.bandon.custom.NumberPicker;
import com.foxconn.bandon.detection.FaceDetectionFragment;
import com.foxconn.bandon.gtm.model.FoodMessage;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.gtm.view.GTMItemView;
import com.foxconn.bandon.gtm.view.GTMView;
import com.foxconn.bandon.label.add.AddLabelFragment;
import com.foxconn.bandon.food.view.FoodsFragment;
import com.foxconn.bandon.label.detail.view.LabelDetailFragment;
import com.foxconn.bandon.mall.FreshMallFragment;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.main.MainFragmentContact;
import com.foxconn.bandon.main.model.AppButton;
import com.foxconn.bandon.main.presenter.MainFragmentPresenter;
import com.foxconn.bandon.messages.view.MemoPanel;
import com.foxconn.bandon.messages.view.MessageBoardFragment;
import com.foxconn.bandon.R;
import com.foxconn.bandon.recipe.view.RecipeFragment;
import com.foxconn.bandon.setting.SettingsFragment;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.user.location.DeviceLocation;
import com.foxconn.bandon.setting.user.location.LocationManager;
import com.foxconn.bandon.setting.wifi.util.WifiUtils;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.DateUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.TimerTask;
import com.foxconn.bandon.weather.manager.WeatherUtils;
import com.foxconn.bandon.weather.model.WeatherInfo;
import com.foxconn.bandon.zixing.activity.CaptureFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment implements MainFragmentContact.View {

    public static final String TAG = MainFragment.class.getName();
    private int[] wifiImages = {R.drawable.ic_wifi_0s, R.drawable.ic_wifi_1s, R.drawable.ic_wifi_2s, R.drawable.ic_wifi_fulls};
    private MainFragmentContact.Presenter mPresenter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageView mWifiIcon;
    private ImageView mClockIcon;
    private NumberPicker hPicker;
    private NumberPicker mPicker;
    private NumberPicker sPicker;
    private View mPickerContainer;
    private View mTimeContainer;
    private TextView mTextTimer;
    private WeatherInfo weatherInfo;
    private ImageView mWeatherIcon;
    private TextView mTextTemp;
    private TextView mTextInfo;
    private CookTimer mTimer;
    private MemoPanel memoPanel;
    private TimesUpAlert mTimerView;
    private TextView mTextGTM;
    private ImageView mImageGTM;
    private GTMView gtmView;
    private GTMItemView itemView;
    private boolean isGtmClickable;
    private boolean isRunning;
    private boolean isUpdateView;
    private long mTime;

    public MainFragment() {

    }


    public static BaseFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainFragmentPresenter(this, ClockRepository.getInstance(BandonDataBase.getInstance(getContext()).clockDao(), new AppExecutors()));
        mPresenter.addCallback();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPresenter.start();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        isUpdateView = true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAppButtons(view);
        initMessageBoard(view);
        initStatusBar(view);
        initToolbars(view);
        initTimerView(view);
        initJDBanner(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
        mListener.showNavigationBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
        isUpdateView = false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "onDestroyView");
        mListener.removeView(mTimerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
        mPresenter.removeCallback();
        cancelTimer();
    }

    @Override
    public void updateWifiIcon() {
        WifiManager manager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != manager) {
            int level = WifiUtils.getCurrentWifiLevel(manager);
            if (level >= 0 && level < 4)
                mWifiIcon.setImageResource(wifiImages[level]);
        }
    }

    @Override
    public void wifiClose() {
        mWifiIcon.setImageResource(R.drawable.signal_wifi_off);
    }

    @Override
    public void updateClockIcon(boolean isShow) {
        if (isShow) {
            mClockIcon.setVisibility(View.VISIBLE);
        } else {
            mClockIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateWeatherView(WeatherInfo info) {
        this.weatherInfo = info;
        updateWeather();
    }

    @Override
    public void loopMessage(GTMessage message) {
        mTextGTM.setText(message.getContent());
        if (message.getLevel() == GTMessage.LEVEL_HIGH) {
            mImageGTM.setImageResource(R.drawable.ic_notification_red);
        } else if (message.getLevel() == GTMessage.LEVEL_MIDDLE) {
            mImageGTM.setImageResource(R.drawable.ic_notification_yellow);
        } else {
            mImageGTM.setImageResource(R.drawable.ic_notification_green);
        }
        isGtmClickable = true;
    }

    @Override
    public void setPresenter(@NonNull MainFragmentContact.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged");
        if (!hidden) {
            Bundle bundle = getBundle();
            if (null != bundle) {
                if (bundle.getBoolean(MessageBoardFragment.KEY_UPDATE_MEMO, false)) {
                    memoPanel.update();
                }
                if (bundle.getBoolean(FaceDetectionFragment.KEY_IS_BROADCAST, false)) {
                    String name = bundle.getString(FaceDetectionFragment.KEY_USER_NAME, "");
                    broadcastWeather(name);
                }
            }
            if (null != mPresenter) {
                mPresenter.start();
            }
            mListener.showNavigationBar();
        } else {
            mListener.removeView(gtmView);
            mListener.removeView(itemView);
        }
    }

    private void broadcastWeather(String name) {
        String remind = getRemindFromFoodMessages();
        String sb = "您好，" + name + "!";
        if (null != remind) {
            sb = sb + remind;
            SpeechManager.getInstance().startSpeak(sb);
            return;
        }
        if (null != weatherInfo) {
            sb = sb + getContext().getString(R.string.weather_info_title) +
                    weatherInfo.getData().getWeather() +
                    "，" +
                    getContext().getString(R.string.weather_info_sub) +
                    weatherInfo.getData().getTmp() +
                    getContext().getString(R.string.weather_info_unit) +
                    "!" +
                    weatherInfo.getData().getSport();
            SpeechManager.getInstance().startSpeak(sb);
        }
    }

    private String getRemindFromFoodMessages() {
        StringBuilder sb = new StringBuilder();
        List<GTMessage> messages = GTMessageManager.getInstance().getCurrentGTMessages();
        for (GTMessage message : messages) {
            if (message instanceof FoodMessage) {
                if (message.getLevel() == GTMessage.LEVEL_HIGH) {
                    sb.append("冰箱內有食品已經過期，請點擊庫內食材查看");
                    return sb.toString();
                } else if (message.getLevel() == GTMessage.LEVEL_MIDDLE) {
                    sb.append("冰箱內有食品即將過期，請點擊庫內食材查看");
                    return sb.toString();
                }
            }
        }
        return null;
    }

    private void initStatusBar(View view) {
        View statusBar = view.findViewById(R.id.status_bar);
        mWifiIcon = statusBar.findViewById(R.id.wifi_indicator);
        mWifiIcon.setColorFilter(Color.WHITE);
        mClockIcon = statusBar.findViewById(R.id.alarm_indicator);
        updateWifiIcon();
    }

    private void initMessageBoard(View view) {
        memoPanel = view.findViewById(R.id.message_board);
        memoPanel.setCallback(id -> {
            Bundle bundle = initBundle(MessageBoardFragment.TAG);
            bundle.putString(MessageBoardFragment.KEY_MEMO_ID, id);
            mListener.startFragment(bundle);
        });

    }

    private void initAppButtons(View view) {
        View apps = view.findViewById(R.id.apps);
        RecyclerView appsListView = apps.findViewById(R.id.main_apps_list_views);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        appsListView.setLayoutManager(layoutManager);
        final List<AppButton> appButtons = initAppsData();
        AppsAdapter adapter = new AppsAdapter(getContext(), appButtons);
        appsListView.setAdapter(adapter);
        adapter.setOnClickListener(index -> {
            String fragTag = appButtons.get(index).getFragTag();
            Bundle bundle = initBundle(fragTag);
            if (TextUtils.equals(fragTag, CaptureFragment.TAG)) {
                bundle.putInt(CaptureFragment.KEY_REQUEST_CODE, CaptureFragment.REQUEST_CODE_MALL);
            }
            mListener.startFragment(bundle);
        });
    }

    private List<AppButton> initAppsData() {
        List<AppButton> appButtons = new ArrayList<>();
        appButtons.add(new AppButton("添加食品", AddLabelFragment.TAG, R.drawable.bg_launcher_add_food, R.drawable.ic_launcher_add_food));
        appButtons.add(new AppButton("生鮮商城", FreshMallFragment.TAG, R.drawable.bg_launcher_shopping, R.drawable.ic_launcher_shopping));
        appButtons.add(new AppButton("菜譜", RecipeFragment.TAG, R.drawable.bg_launcher_recipe, R.drawable.ic_launcher_recipe));
        appButtons.add(new AppButton("便捷購物", CaptureFragment.TAG, R.drawable.bg_launcher_scan, R.drawable.ic_launcher_scan));
        appButtons.add(new AppButton("庫內食材", FoodsFragment.TAG, R.drawable.bg_launcher_inside_section, R.drawable.ic_launcher_inside_section));
        appButtons.add(new AppButton("設置", SettingsFragment.TAG, R.drawable.bg_launcher_setting, R.drawable.ic_launcher_setting));
        return appButtons;
    }

    private void initToolbars(final View view) {
        View toolbar = view.findViewById(R.id.tools_bar);
        //TextClock mTextClock = toolbar.findViewById(R.id.clock);
        mWeatherIcon = toolbar.findViewById(R.id.weather_icon);
        mWeatherIcon.setColorFilter(Color.WHITE);
        mTextTemp = toolbar.findViewById(R.id.temperature);
        mTextInfo = toolbar.findViewById(R.id.weather_text);

        mTextGTM = toolbar.findViewById(R.id.text_gtm);
        mImageGTM = toolbar.findViewById(R.id.image_gtm);
        View gtmContainer = toolbar.findViewById(R.id.container_gtm);
        final GTMItemView.ClickCallback callback = () -> mListener.removeView(itemView);

        final GTMView.ClickCallback clickCallback = new GTMView.ClickCallback() {
            @Override
            public void close() {
                mListener.removeView(gtmView);
            }

            @Override
            public void startLabelDetailFragment(int id) {
                mListener.removeView(gtmView);
                Bundle bundle = initBundle(LabelDetailFragment.TAG);
                bundle.putInt(LabelDetailFragment.KEY_ITEM_ID, id);
                mListener.startFragment(bundle);
            }

            @Override
            public void viewGTMessage(GTMessage message) {
                mListener.removeView(gtmView);
                itemView = new GTMItemView(getContext(), message, callback);
                mListener.addView(itemView);
            }
        };

        gtmContainer.setOnClickListener(v -> {
            if (isGtmClickable) {
                LogUtils.d(TAG, "gtmContainer onClick");
                gtmView = new GTMView(getContext());
                gtmView.setCallback(clickCallback);
                mListener.addView(gtmView);
            }
        });
    }

    private void updateWeather() {
        DeviceLocation location = LocationManager.getDeviceLocation(getContext());
        String temp = weatherInfo.getData().getTmp() + "℃";
        String weather = weatherInfo.getData().getWeather();
        //String wind = weatherInfo.getData().getWindDir();
        String info = location.getDistrict() + " / " + weather;
        mWeatherIcon.setImageResource(WeatherUtils.getIcon(weather));
        mTextTemp.setText(temp);
        mTextInfo.setText(info);
    }


    private void initTimerView(View view) {
        View timerView = view.findViewById(R.id.timer);
        mPickerContainer = timerView.findViewById(R.id.picker_container);
        mTimeContainer = timerView.findViewById(R.id.running);
        mTextTimer = timerView.findViewById(R.id.running_time);
        hPicker = timerView.findViewById(R.id.hr_picker);
        mPicker = timerView.findViewById(R.id.min_picker);
        sPicker = timerView.findViewById(R.id.sec_picker);
        resetPickers();
        timerView.findViewById(R.id.cancel_btn).setOnClickListener(v -> cancelTimer());
        timerView.findViewById(R.id.start_btn).setOnClickListener(v -> startTimer());
        if (isRunning) {
            displayTimer();
        }
    }

    private void resetPickers() {
        hPicker.setNumDigits(1);
        hPicker.setValues(0, 9, 0);
        mPicker.setNumDigits(2);
        mPicker.setValues(0, 59, 0);
        sPicker.setNumDigits(2);
        sPicker.setValues(0, 59, 0);
    }

    private void initJDBanner(View view) {
        View bannerView = view.findViewById(R.id.jd_banner);
        bannerView.setOnClickListener(v -> {
        });
    }

    private void startTimer() {
        LogUtils.d(TAG, "startTimer");
        if (!canStartTimer()) return;
        displayTimer();
        mTimer = new CookTimer(mTime, 1000);
        handler.post(() -> {
            mTimer.start();
            isRunning = true;
        });
    }

    private boolean canStartTimer() {
        if (isRunning) {
            return false;
        }
        mTime = DateUtils.getTime(hPicker.getValue(), mPicker.getValue(), sPicker.getValue());
        return !(mTime == 0);
    }


    private void displayTimer() {
        mPickerContainer.setVisibility(View.GONE);
        mTimeContainer.setVisibility(View.VISIBLE);
        mTextTimer.setText(DateUtils.getTimeString(mTime));
    }

    private void hideTimer() {
        mPickerContainer.setVisibility(View.VISIBLE);
        mTimeContainer.setVisibility(View.GONE);
        mTextTimer.setText("");
    }

    private void cancelTimer() {
        if (!isRunning) {
            return;
        }
        resetPickers();
        hideTimer();
        mTimer.cancel();
        mTimer = null;
        isRunning = false;
    }

    private void showDialog() {
        mTimerView = new TimesUpAlert(getContext());
        mTimerView.setCallback(() -> mListener.removeView(mTimerView));
        mListener.addView(mTimerView);
    }

    class CookTimer extends TimerTask {

        CookTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (isUpdateView) {
                mTextTimer.setText(DateUtils.getTimeString(mTime));
            }
            mTime = mTime - 1000;
            LogUtils.d(TAG, "mTime:" + mTime);
        }

        @Override
        public void onFinish() {
            mTextTimer.setText(DateUtils.getTimeString(mTime));
            showDialog();
            cancelTimer();
        }


    }

}

