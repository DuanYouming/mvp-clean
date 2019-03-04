package com.foxconn.bandon.standby;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.main.view.MainFragment;
import com.foxconn.bandon.setting.user.location.DeviceLocation;
import com.foxconn.bandon.setting.user.location.LocationManager;
import com.foxconn.bandon.standby.model.StandbyRepository;
import com.foxconn.bandon.standby.presenter.StandbyPresenter;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.foxconn.bandon.weather.manager.WeatherManager;
import com.foxconn.bandon.weather.manager.WeatherUtils;
import com.foxconn.bandon.weather.model.WeatherInfo;
import com.google.gson.Gson;


public class StandbyFragment extends BaseFragment implements View.OnClickListener, IStandbyContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = StandbyFragment.class.getName();
    private static final float BASE_SCALE = 0.1F;
    private static final int WIDTH = 1080;
    private static final int HEIGHT = 1920;
    private static final int AREA_TOP = 0;
    private static final int AREA_BOTTOM = 1;


    private IStandbyContract.Presenter mPresenter;
    private WeatherInfo mWeatherInfo;
    private ImageView mWeatherIcon;
    private TextView mTextWeatherInfo;
    private TextView mTextTemp;
    private ImageView mImageWallpaper;
    private TextView mTextTimeFlag;
    private TextClock mTextClock;
    private TextView mTextMessage;
    private Bitmap scaleBitmap;
    private TextView mTextTimes;
    private TextView mTextDoorOpenedHint;


    public StandbyFragment() {
    }

    public static StandbyFragment newInstance() {
        return new StandbyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new StandbyPresenter(this, StandbyRepository.getInstance(new AppExecutors()));
        mPresenter.registCallback();
        View view = inflater.inflate(R.layout.fragment_standby, container, false);
        view.setOnClickListener(this);
        PreferenceUtils.registerListener(Constant.SP_SETTINGS, this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWallpaper(view);
        mTextTimeFlag = view.findViewById(R.id.time_flag);
        mTextClock = view.findViewById(R.id.time);
        mTextMessage = view.findViewById(R.id.gtm_message);
        mWeatherIcon = view.findViewById(R.id.icon);
        mTextWeatherInfo = view.findViewById(R.id.weather_text);
        mTextTemp = view.findViewById(R.id.temperature);
        mTextTimes = view.findViewById(R.id.text_door_opened_times);
        mTextDoorOpenedHint = view.findViewById(R.id.text_door_opened_hint);
        int times = PreferenceUtils.getInt(getActivity(), Constant.SP_SETTINGS, Constant.KEY_DOOR_OPENED_TIMES, 0);
        String text = times + " " + getString(R.string.times_unit);
        mTextTimes.setText(text);
        if (null == mWeatherInfo) {
            mWeatherInfo = WeatherManager.getInstance().getWeather();
            if (null != mWeatherInfo) {
                updateWeather();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
        mListener.hideNavigationBar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged:" + hidden);
        if (!hidden) {
            mListener.hideNavigationBar();
            if (PreferenceUtils.getBoolean(getContext(), Constant.SP_SETTINGS, Constant.KEY_WALLPAPER_UPDATE, false)) {
                setWallpaper();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "onDestroyView");
        PreferenceUtils.unregisterListener(Constant.SP_SETTINGS, this);
        mPresenter.unregistCallback();
        StandbyRepository.destroyInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = initBundle(MainFragment.TAG);
        mListener.startFragment(bundle);
        LogUtils.d(TAG, "onClick");
    }

    @Override
    public void updateWeatherInfo(WeatherInfo info) {
        this.mWeatherInfo = info;
        if (null != mWeatherInfo) {
            updateWeather();
        }
    }

    @Override
    public void loopMessage(GTMessage message) {
        mTextMessage.setText(message.getContent());
    }

    @Override
    public void setPresenter(IStandbyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateColor(int area, int color) {

        LogUtils.d(TAG, "updateColor");
        if (area == AREA_TOP) {
            mTextClock.setTextColor(color);
            mTextTimeFlag.setTextColor(color);
            mTextMessage.setTextColor(color);
        } else {
            mWeatherIcon.setColorFilter(color);
            mTextWeatherInfo.setTextColor(color);
            mTextTemp.setTextColor(color);
            mTextTimes.setTextColor(color);
            mTextDoorOpenedHint.setTextColor(color);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (TextUtils.equals(key, Constant.KEY_DOOR_OPENED_TIMES)) {
            int times = sp.getInt(key, 0);
            String text = times + " " + getString(R.string.times_unit);
            mTextTimes.setText(text);
        }
    }

    private void initWallpaper(View view) {
        mImageWallpaper = view.findViewById(R.id.wallpaper);
        setWallpaper();
    }

    private void setWallpaper() {
        String value = PreferenceUtils.getString(getContext(), Constant.SP_SETTINGS, Constant.KEY_WALLPAPER_CACHE, "");
        if (TextUtils.isEmpty(value)) {
            mImageWallpaper.setImageResource(R.drawable.wallpaper);
        } else {
            ObjectKey key = new Gson().fromJson(value, ObjectKey.class);
            LogUtils.d(TAG, "ObjectKey:" + key.toString());
            GlideApp.with(getContext())
                    .asBitmap()
                    .load(Constant.WALLPAPER_FILE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(key)
                    .listener(listener)
                    .fitCenter()
                    .into(mImageWallpaper);
            PreferenceUtils.setBoolean(getContext(), Constant.SP_SETTINGS, Constant.KEY_WALLPAPER_UPDATE, false);
        }
    }

    private void updateWeather() {
        DeviceLocation location = LocationManager.getDeviceLocation(getContext());
        String temp = mWeatherInfo.getData().getTmp() + "℃";
        String weather = mWeatherInfo.getData().getWeather();
        //String wind = mWeatherInfo.getData().getWindDir();
        String info = location.getDistrict() + " / " + weather;
        mWeatherIcon.setImageResource(WeatherUtils.getIcon(weather));
        mTextTemp.setText(temp);
        mTextWeatherInfo.setText(info);
    }

    private void getLocation(final View view, final int area, final float wScale, final float hScale) {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        view.post(() -> {
            RectF rectF = new RectF(params.leftMargin * wScale, params.topMargin * hScale, view.getWidth() * wScale, view.getHeight() * hScale);
            mPresenter.getColor(area, scaleBitmap, rectF);
        });
    }

    private RequestListener<Bitmap> listener = new RequestListener<Bitmap>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            int width = resource.getWidth();
            int height = resource.getHeight();
            float wScale = (WIDTH * BASE_SCALE) / width;
            float hScale = (HEIGHT * BASE_SCALE) / height;
            scaleBitmap = ImageUtils.scaleBitmap(resource, wScale, hScale);
            getLocation(mWeatherIcon, AREA_BOTTOM, wScale, hScale);
            getLocation(mTextClock, AREA_TOP, wScale, hScale);
            return false;
        }
    };

}
