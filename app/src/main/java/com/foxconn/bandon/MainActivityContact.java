package com.foxconn.bandon;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.setting.clock.model.ClockBean;

public interface MainActivityContact {

    interface Presenter extends BasePresenter {

        void addCallback();

        void removeCallback();

        void getClock(String id);

        void update(ClockBean clock);
    }

    interface View extends BaseView<Presenter> {

        void showClockView(ClockBean clock);

        void removeClockView();

    }
}
