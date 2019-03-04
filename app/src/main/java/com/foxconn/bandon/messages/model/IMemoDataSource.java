package com.foxconn.bandon.messages.model;

import java.util.List;

public interface IMemoDataSource {

    interface LoadMemosCallback {
        void onSuccess(List<Memo> memos);

        void onFailure();

    }

    interface GetMemoCallback {

        void onSuccess(Memo memo);

        void onFailure();
    }

    void getMemos(LoadMemosCallback callback);

    void getMemoById(String id, GetMemoCallback callback);

    void insertMemo(Memo memo,GetMemoCallback callback);

    void deleteMemoById(String id,GetMemoCallback callback);

    void updateMemo(Memo memo,GetMemoCallback callback);

    void deleteAllMemos();
}
