package com.foxconn.bandon.messages;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.messages.model.Memo;

import java.util.List;

public interface MemoPanelContract {

    interface View extends BaseView<Presenter> {

        void updateMemos(List<Memo> memos);

    }

    interface Presenter extends BasePresenter {
        void getMemos();

        void deleteMemo(String id);

        void updateMemo(Memo memo);
    }
}
