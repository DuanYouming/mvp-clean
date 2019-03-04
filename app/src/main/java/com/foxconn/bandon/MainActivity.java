package com.foxconn.bandon;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.foxcomm.bandon.aispeech.SpeechManager;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.base.BaseActivity;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.detection.FaceDetectionFragment;
import com.foxconn.bandon.gtm.GTMService;
import com.foxconn.bandon.main.view.MainFragment;
import com.foxconn.bandon.main.view.TimesUpAlert;
import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.view.ClockView;
import com.foxconn.bandon.setting.wifi.util.BWifiManager;
import com.foxconn.bandon.speech.SpeechFragment;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.uart.UartHelper;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.FragmentsUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PowerUtils;
import com.foxconn.bandon.weather.model.WeatherRepository;
import com.foxconn.bandon.weather.WeatherService;

import android.os.Process;


public class MainActivity extends BaseActivity implements BaseFragment.Callback, View.OnClickListener, MainActivityContact.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private static final int MINUTE = 58000; // system sleep time 1 minute
    private static final int SECOND = 1000;
    private MainActivityContact.Presenter mPresenter;
    private NavigationCallback navigationCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private View mNavigation;
    private FragmentsUtils mFragmentsUtils;
    private FrameLayout mViewContainer;
    private ClockView mClockView;
    private SleepTimer mTimer;
    private boolean isCanSleep = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver();
        BWifiManager.getInstance().registerReceiver(this);
        initFragmentUtils();
        initNavigation();
        initMainFragment();
        initSleepTimer();
        mViewContainer = findViewById(R.id.view_container);
        new MainPresenter(this, ClockRepository.getInstance(BandonDataBase.getInstance(this).clockDao(), new AppExecutors()));
        mPresenter.addCallback();
        startWeatherService();
        startGTMService();
        SpeechManager.getInstance().bindService(this);
        UartHelper.getInstance().start();
        ClockManager.getInstance().onBootCompleted();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!PowerUtils.getInstance(this).isWakeUpBySelf()) {
            startFaceDetection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSleepTimer();
        Toast.makeText(this,"import so",Toast.LENGTH_LONG).show();
        LogUtils.d(TAG,"result:"+Jni.stringFromJNI(2));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanSleep) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP) {
                startSleepTimer();
            } else {
                mTimer.cancel();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void canSleep() {
        LogUtils.d(TAG, "canSleep");
        startSleepTimer();
        isCanSleep = true;
    }

    @Override
    public void cancelSleep() {
        LogUtils.d(TAG, "cancelSleep");
        mTimer.cancel();
        isCanSleep = false;
    }

    private void startWeatherService() {
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    private void startGTMService() {
        Intent intent = new Intent(this, GTMService.class);
        intent.setAction(GTMService.ACTION_FIRST_LOAD);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.removeCallback();
        }
        if (null != mTimer) {
            mTimer = null;
        }
        SpeechManager.getInstance().unBindService(this);
        UartHelper.getInstance().stop();
        BWifiManager.getInstance().unregisterReceiver(this);
        unregisterReceiver(receiver);
        ClockRepository.destroy();
        WeatherRepository.destroy();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back_btn:
                back();
                break;
            case R.id.home_btn:
                home();
                break;
            case R.id.voice_btn:
                voiceClick();
                break;
        }
    }

    @Override
    public void startFragment(Bundle bundle) {
        mFragmentsUtils.startFragment(bundle);
    }

    @Override
    public void StartFragmentAndFinish(Bundle bundle) {
        mFragmentsUtils.StartFragmentAndFinish(bundle);
    }

    @Override
    public void showNavigationBar() {
        LogUtils.d(TAG, "showNavigationBar");
        mNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNavigationBar() {
        LogUtils.d(TAG, "hideNavigationBar");
        mNavigation.setVisibility(View.GONE);
    }

    @Override
    public void registNavigationCallback(@NonNull MainActivity.NavigationCallback callback) {
        LogUtils.d(TAG, "registNavigationCallback:" + callback.toString());
        navigationCallback = callback;
    }

    @Override
    public void unregistNavigationCallback(@NonNull MainActivity.NavigationCallback callback) {
        if (navigationCallback == callback) {
            LogUtils.d(TAG, "unregistNavigationCallback:" + callback.toString());
            navigationCallback = null;
        }
    }

    @Override
    public void addView(View view) {
        if (view instanceof TimesUpAlert || view instanceof ClockView) {
            PowerUtils.getInstance(this).wakeup();
        }
        mViewContainer.setFocusable(true);
        mViewContainer.setClickable(true);
        mViewContainer.removeAllViews();
        mViewContainer.addView(view);
    }

    @Override
    public void removeView(View view) {
        mViewContainer.removeView(view);
        mViewContainer.setFocusable(false);
        mViewContainer.setClickable(false);
    }

    @Override
    public void destroyView() {
        mViewContainer.removeAllViews();
        mViewContainer.setFocusable(false);
        mViewContainer.setClickable(false);
    }

    @Override
    public void showClockView(ClockBean clock) {
        mClockView = new ClockView(this);
        mClockView.setCallback(clockCallback);
        mClockView.setContent(clock);
        mViewContainer.addView(mClockView);
    }

    @Override
    public void removeClockView() {
        mViewContainer.removeView(mClockView);
        mClockView = null;
    }

    @Override
    public void setPresenter(MainActivityContact.Presenter presenter) {
        mPresenter = presenter;
    }

    private void initSleepTimer() {
        mTimer = new SleepTimer(MINUTE, SECOND);
    }

    private void startSleepTimer() {
        handler.post(() -> mTimer.start());
    }

    private void initFragmentUtils() {
        FragmentManager manager = getSupportFragmentManager();
        mFragmentsUtils = FragmentsUtils.getInstance(manager);
    }

    private void initNavigation() {
        mNavigation = findViewById(R.id.navigation);
        ImageView back = mNavigation.findViewById(R.id.back_btn);
        ImageView home = mNavigation.findViewById(R.id.home_btn);
        ImageView voice = mNavigation.findViewById(R.id.voice_btn);

        back.setOnClickListener(this);
        home.setOnClickListener(this);
        voice.setOnClickListener(this);
    }

    private void initMainFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.KEY_FRAG_TAG, MainFragment.TAG);
        mFragmentsUtils.initMain(bundle);
    }

    private void back() {
        if (null != navigationCallback) {
            if (navigationCallback.onBackClick()) {
                return;
            }
        }
        mFragmentsUtils.back();
    }

    @Override
    public void home() {
        mFragmentsUtils.home();
    }

    private void voiceClick() {
        if (null != navigationCallback) {
            if (navigationCallback.onVoiceClick()) {
                return;
            }
        }
        String msg = "voice button clicked";
        showToast(msg);
        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.KEY_FRAG_TAG, SpeechFragment.TAG);
        mFragmentsUtils.startFragment(bundle);
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    ClockView.Callback clockCallback = new ClockView.Callback() {
        @Override
        public void close(ClockBean clock) {
            mViewContainer.removeView(mClockView);
        }
    };

    public interface NavigationCallback {

        boolean onBackClick();

        boolean onVoiceClick();
    }

    private void registerReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (TextUtils.equals(action, ACTION_SCREEN_ON)) {
                    LogUtils.d(TAG, "SCREEN ON");
                } else if (TextUtils.equals(action, ACTION_SCREEN_OFF)) {
                    LogUtils.d(TAG, "SCREEN OFF");
                }
            }
        }

    };


    private void startFaceDetection() {
        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.KEY_FRAG_TAG, FaceDetectionFragment.TAG);
        startFragment(bundle);
    }


    class SleepTimer extends CountDownTimer {

        SleepTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            home();
            PowerUtils.getInstance(BandonApplication.getInstance().getApplicationContext()).goToSleep();
        }
    }


}
