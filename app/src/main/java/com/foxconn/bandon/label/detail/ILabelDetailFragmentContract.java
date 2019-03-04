package com.foxconn.bandon.label.detail;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.label.detail.model.LabelDetail;
import com.foxconn.bandon.label.detail.model.LabelItem;

import okhttp3.RequestBody;

public interface ILabelDetailFragmentContract {

    interface Presenter extends BasePresenter {
        void getLabelItem(String name);

        void getLabelDetail(int id);

        void save(RequestBody body);

        void delete(RequestBody body);

    }

    interface View extends BaseView<Presenter> {

        void updateLabelItem(LabelItem item);

        void updateLabelDetail(LabelDetail detail);

        void updateOrSaveGTMessage();

        void deleteGTMessage();

        void showExceptionView();

        void showToast(String msg);

        void startToFoodsView();
    }
}
