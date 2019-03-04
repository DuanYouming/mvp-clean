package com.foxconn.bandon.standby.presenter;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.standby.IStandbyContract;
import com.foxconn.bandon.standby.model.StandbyRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.weather.manager.WeatherManager;
import com.foxconn.bandon.weather.model.WeatherInfo;


public class StandbyPresenter implements IStandbyContract.Presenter {

    private static final String TAG = StandbyPresenter.class.getSimpleName();
    private IStandbyContract.View mView;
    private StandbyRepository repository;
    private UseCaseHandler mUseCaseHandler;

    public StandbyPresenter(IStandbyContract.View view, StandbyRepository repository) {
        this.mView = view;
        this.repository = repository;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        this.mView.setPresenter(this);
    }

    @Override
    public void getColor(final int area, Bitmap bitmap, RectF rectF) {
        LogUtils.d(TAG, "getColor");
        GetColor get = new GetColor(repository);
        GetColor.RequestValue value = new GetColor.RequestValue(bitmap, rectF);
        mUseCaseHandler.execute(get, value, new UseCase.UseCaseCallback<GetColor.ResponseValue>() {
            @Override
            public void onSuccess(GetColor.ResponseValue response) {
                int color = response.getColor();
                mView.updateColor(area, color);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void registCallback() {
        WeatherManager.getInstance().registCallback(weatherCallback);
        GTMessageManager.getInstance().registerCallback(gtMessageCallback);
    }

    @Override
    public void unregistCallback() {
        WeatherManager.getInstance().unregistCallback(weatherCallback);
        GTMessageManager.getInstance().unregisterCallback(gtMessageCallback);
    }

    private WeatherManager.Callback weatherCallback = new WeatherManager.Callback() {
        @Override
        public void updateWeatherInfo(WeatherInfo info) {
            mView.updateWeatherInfo(info);
        }
    };

    private GTMessageManager.GTMessageCallback gtMessageCallback = new GTMessageManager.GTMessageCallback() {
        @Override
        public void loop(GTMessage message) {
            mView.loopMessage(message);
        }
    };
}
