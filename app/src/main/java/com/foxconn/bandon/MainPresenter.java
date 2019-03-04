package com.foxconn.bandon;

import android.content.Intent;

import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.presenter.GetClock;
import com.foxconn.bandon.setting.clock.presenter.GetClocks;
import com.foxconn.bandon.setting.clock.presenter.UpdateClock;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

import java.util.List;

public class MainPresenter implements MainActivityContact.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();

    private MainActivityContact.View mView;
    private ClockRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    public MainPresenter(MainActivityContact.View view, ClockRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void getClock(String id) {
        GetClock get = new GetClock(mRepository);
        GetClock.RequestValues values = new GetClock.RequestValues(id);
        mUseCaseHandler.execute(get, values, new UseCase.UseCaseCallback<GetClock.ResponseValues>() {
            @Override
            public void onSuccess(GetClock.ResponseValues response) {
                ClockBean clock = response.getClock();
                if (null != clock) {
                    mView.showClockView(clock);
                    List<Integer> periods = clock.getPeriodsList();
                    if (null != periods && periods.size() > 0) {
                        ClockManager.getInstance().restartClock(clock);
                    } else {
                        clock.setEnable(false);
                        update(clock);
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void update(ClockBean clock) {
        UpdateClock update = new UpdateClock(mRepository);
        UpdateClock.RequestValues values = new UpdateClock.RequestValues(clock);
        mUseCaseHandler.execute(update, values, new UseCase.UseCaseCallback<UpdateClock.ResponseValues>() {
            @Override
            public void onSuccess(UpdateClock.ResponseValues response) {
                ClockManager.getInstance().onSateChanged();
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void addCallback() {
        ClockManager.getInstance().addOnReceiveCallback(callback);
        LogUtils.d(TAG, "addOnReceiveCallback");
    }

    @Override
    public void removeCallback() {
        ClockManager.getInstance().removeOnReceiverCallback(callback);
    }

    private ClockManager.OnReceiveCallback callback = new ClockManager.OnReceiveCallback() {
        @Override
        public void onBootCompleted() {
            GetClocks get = new GetClocks(mRepository);
            GetClocks.RequestValues values = new GetClocks.RequestValues();
            mUseCaseHandler.execute(get, values, new UseCase.UseCaseCallback<GetClocks.ResponseValues>() {
                @Override
                public void onSuccess(GetClocks.ResponseValues response) {
                    List<ClockBean> clocks = response.getClocks();
                    if (null != clocks && clocks.size() > 0) {
                        for (ClockBean clock : clocks) {
                            if (clock.getEnable()) {
                                ClockManager.getInstance().restartClock(clock);
                            }
                        }
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        }

        @Override
        public void onClockRings(Intent intent) {
            String id = intent.getStringExtra(ClockManager.EXTRA_CLOCK_ID);
            getClock(id);
        }
    };
}
