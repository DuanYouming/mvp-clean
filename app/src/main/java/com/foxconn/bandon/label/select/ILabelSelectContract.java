package com.foxconn.bandon.label.select;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.label.select.model.Label;

public interface ILabelSelectContract {

    interface Presenter extends BasePresenter {
        void load();
    }

    interface View extends BaseView<Presenter> {

        void update(Label label);
    }
}
