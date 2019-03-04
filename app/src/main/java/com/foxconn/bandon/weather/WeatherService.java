package com.foxconn.bandon.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.model.WeatherMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.gtm.presenter.GetGTMessagesByType;
import com.foxconn.bandon.setting.user.location.DeviceLocation;
import com.foxconn.bandon.setting.user.location.LocationManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.weather.manager.WeatherManager;
import com.foxconn.bandon.weather.model.LocationInfo;
import com.foxconn.bandon.weather.model.WeatherInfo;
import com.foxconn.bandon.weather.model.WeatherRepository;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class WeatherService extends Service {

    private static final String TAG = WeatherService.class.getSimpleName();
    public static final String ACTION_GET_WEATHER = "com.bandon.foxconn.GET_WEATHER";
    private static final String ERROR_LOCATION = "0.0,0.0";
    private static final int UPDATE_TIME = 3;
    private static final int REQUEST_CODE = 101;
    private static final double DEFAULT_LON = 22.547;
    private static final double DEFAULT_LAT = 114.085947;
    private AlarmManager mAlarmManager;
    private WeatherRepository mRepository;
    private UseCaseHandler mHandler;
    private PendingIntent mGetWeatherIntent;
    private boolean isOver = true;
    private Context context;

    public WeatherService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = this;
        mRepository = WeatherRepository.getInstance(new AppExecutors());
        mHandler = UseCaseHandler.getInstance();
        mGetWeatherIntent = createIntent();
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, getUpdateTime(), mGetWeatherIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent || !TextUtils.equals(intent.getAction(), ACTION_GET_WEATHER)) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, getUpdateTime(), mGetWeatherIntent);
            LogUtils.d(TAG, "set Alarm to update weather");
        }
        loadWeatherInfo();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
        WeatherRepository.destroy();
        mAlarmManager.cancel(mGetWeatherIntent);
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(this, WeatherService.class);
        intent.setAction(ACTION_GET_WEATHER);
        return PendingIntent.getService(BandonApplication.getInstance(), REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private long getUpdateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = ((hour / UPDATE_TIME) + 1) * UPDATE_TIME;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        LogUtils.d(TAG, "getUpdateTime:" + calendar.getTime().toString());
        return calendar.getTimeInMillis();
    }


    private void loadWeatherInfo() {
        String location = getLocationFromSP();
        if (TextUtils.equals(location, ERROR_LOCATION)) {
            getLocationFromIP();
        } else {
            getWeather(location);
        }
    }

    private void getWeather(String location) {
        LogUtils.d(TAG, "getWeather:" + location);
        GetWeather get = new GetWeather(mRepository);
        GetWeather.RequestValue value = new GetWeather.RequestValue(location);
        mHandler.execute(get, value, new UseCase.UseCaseCallback<GetWeather.ResponseValue>() {
            @Override
            public void onSuccess(GetWeather.ResponseValue response) {
                WeatherInfo info = response.getInfo();
                LogUtils.d(TAG, "info:" + info.toString());
                WeatherManager.getInstance().updateWeather(info);
                if(isOver) {
                    saveOrUpdateWeatherMessage(info);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private String getLocationFromSP() {
        DeviceLocation location = LocationManager.getDeviceLocation(getApplicationContext());
        if (null == location) {
            return null;
        }
        double lat = location.getLat();
        double lon = location.getLon();
        return String.valueOf(lat) + "," + lon;
    }

    private void getLocationFromIP() {
        GetLocation get = new GetLocation(mRepository);
        mHandler.execute(get, null, new UseCase.UseCaseCallback<GetLocation.ResponseValue>() {
            @Override
            public void onSuccess(GetLocation.ResponseValue response) {
                LocationInfo info = response.getInfo();
                LogUtils.d(TAG, "LocationInfo:" + info.toString());
                String location = String.valueOf(info.getLat()) + "," + info.getLon();
                getWeather(location);
            }

            @Override
            public void onFailure() {
                String location = String.valueOf(DEFAULT_LAT) + "," + DEFAULT_LON;
                getWeather(location);
            }
        });
    }


    private synchronized void saveOrUpdateWeatherMessage(final WeatherInfo info) {
        isOver = false;
        GTMessageManager.getInstance().getGTMessageByType(GTMessage.TYPE_WEATHER, new UseCase.UseCaseCallback<GetGTMessagesByType.ResponseValue>() {
            @Override
            public void onSuccess(GetGTMessagesByType.ResponseValue response) {
                List<GTMessage> messages = response.getMessages();
                if (null != messages && messages.size() > 0) {
                    WeatherMessage message = (WeatherMessage) messages.get(0);
                    message.setContent(info.getData().getDrsg());
                    GTMessageManager.getInstance().updateGTMessage(message);
                } else {
                    String id = UUID.randomUUID().toString();
                    int level = GTMessage.LEVEL_LOW;
                    String deviceID = DeviceUtils.getDeviceId(context);
                    String content = info.getData().getDrsg();
                    WeatherMessage message = new WeatherMessage(id, level, deviceID, content);
                    GTMessageManager.getInstance().insertGTMessage(message);
                }
                isOver = true;
            }

            @Override
            public void onFailure() {
                isOver = true;
            }
        });

    }
}
