package com.foxconn.bandon.messages.presenter;

import com.foxconn.bandon.messages.MemoPanelContract;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;
import java.util.List;

public class MemoPanelPresenter implements MemoPanelContract.Presenter {
    private static final String TAG = MemoPanelPresenter.class.getSimpleName();
    private MemoPanelContract.View mView;
    private MemoRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    public MemoPanelPresenter(MemoPanelContract.View view, MemoRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void getMemos() {
        GetMemos getMemos = new GetMemos(mRepository);
        mUseCaseHandler.execute(getMemos, null, new UseCase.UseCaseCallback<GetMemos.ResponseValue>() {
            @Override
            public void onSuccess(GetMemos.ResponseValue response) {
                List<Memo> memos = response.getMemos();
                if (null != memos && memos.size() > 0) {
                    mView.updateMemos(memos);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void deleteMemo(String id) {
        DeleteMemo deleteMemo = new DeleteMemo(mRepository);
        DeleteMemo.RequestValue value = new DeleteMemo.RequestValue(id);
        mUseCaseHandler.execute(deleteMemo, value, new UseCase.UseCaseCallback<DeleteMemo.ResponseValue>() {
            @Override
            public void onSuccess(DeleteMemo.ResponseValue response) {
                getMemos();
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void updateMemo(Memo memo) {
        LogUtils.d(TAG, "memo:" + memo.toString());
        UpdateMemo task = new UpdateMemo(mRepository);
        UpdateMemo.RequestValue value = new UpdateMemo.RequestValue(memo);
        mUseCaseHandler.execute(task, value, new UseCase.UseCaseCallback<UpdateMemo.ResponseValue>() {
            @Override
            public void onSuccess(UpdateMemo.ResponseValue response) {
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
