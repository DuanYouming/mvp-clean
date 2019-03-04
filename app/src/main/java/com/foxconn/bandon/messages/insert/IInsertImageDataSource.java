package com.foxconn.bandon.messages.insert;

import java.util.List;

public interface IInsertImageDataSource {

    interface Callback {
        void onSuccess(List<InsertImage> data);

        void onFailure();
    }

    void loadDefaultImage(String path, Callback callback);

    void loadLocalImage(String path, Callback callback);
}
