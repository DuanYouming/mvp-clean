package com.foxconn.bandon.messages.insert;


import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import java.util.List;

interface InsertContact {
    interface View extends BaseView<Presenter> {
        void notifyEmotionDataChanged(List<InsertImage> data);

        void notifyImageDataChanged(List<InsertImage> data);

        void initDefaultEmotions(List<InsertImage> data);

        void initDefaultImages(List<InsertImage> data);
    }

    interface Presenter extends BasePresenter {
        void loadDefault(String path,int type);

        void loadLocal(String path,int type);
    }
}
