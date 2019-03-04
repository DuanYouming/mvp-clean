package com.foxconn.bandon.setting.clock;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.setting.clock.model.ClockBean;

import java.util.List;

public interface ClockContact {

    interface Presenter extends BasePresenter {

        void add(ClockBean clock);

        void delete(ClockBean clock);

        void update(ClockBean clock,boolean isUpdateView);

        void getAll();

        void addListener();

        void removeListener();

    }

    interface View extends BaseView<Presenter> {
        void notifyDataChanged(List<ClockBean> values);
    }
}
