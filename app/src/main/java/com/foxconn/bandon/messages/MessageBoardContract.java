package com.foxconn.bandon.messages;


import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.messages.model.Memo;

public interface MessageBoardContract {

    interface View extends BaseView<Presenter> {

        void showLoadingView();

        void hideLoadingView();

        void showDialog();

        void startMain();

        void updateMemo(Memo memo);
    }

    interface Presenter extends BasePresenter {
        void save(Memo memo);

        void getMemoById(String id);
    }
}
