package com.foxconn.bandon.setting.clock.model;

import com.foxconn.bandon.setting.clock.model.ClockBean;

import java.util.List;

public interface IClockDataSource {

    interface LoadClocksCallback {
        void onSuccess(List<ClockBean> clocks);

        void onFailure();

    }

    interface GetClockCallback {

        void onSuccess(ClockBean clock);

        void onFailure();
    }

    void getAll(LoadClocksCallback callback);

    void save(ClockBean clock, GetClockCallback callback);

    void deleteClock(String id, GetClockCallback callback);

    void update(ClockBean clock, GetClockCallback callback);

    void getClock(String id, GetClockCallback callback);

}
