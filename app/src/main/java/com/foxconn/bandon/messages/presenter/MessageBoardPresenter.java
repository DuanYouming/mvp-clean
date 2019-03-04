package com.foxconn.bandon.messages.presenter;


import com.foxconn.bandon.messages.MessageBoardContract;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

public class MessageBoardPresenter implements MessageBoardContract.Presenter {
    private static final String TAG = MessageBoardPresenter.class.getSimpleName();

    private MessageBoardContract.View mView;
    private UseCaseHandler mUseCaseHandler;
    private MemoRepository mRepository;

    public MessageBoardPresenter(MessageBoardContract.View view, MemoRepository mRepository) {
        this.mView = view;
        this.mRepository = mRepository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void save(Memo memo) {
        LogUtils.d(TAG, "save() memo:" + memo.toString());
        SaveMemo task = new SaveMemo(mRepository);
        SaveMemo.RequestValue value = new SaveMemo.RequestValue(memo);
        mUseCaseHandler.execute(task, value, new UseCase.UseCaseCallback<SaveMemo.ResponseValue>() {
            @Override
            public void onSuccess(SaveMemo.ResponseValue response) {
                mView.hideLoadingView();
                mView.startMain();
            }

            @Override
            public void onFailure() {
                mView.hideLoadingView();
                mView.showDialog();
            }
        });
    }

    @Override
    public void getMemoById(String id) {
        GetMemo getMemo = new GetMemo(mRepository);
        GetMemo.RequestValue value = new GetMemo.RequestValue(id);
        mUseCaseHandler.execute(getMemo, value, new UseCase.UseCaseCallback<GetMemo.ResponseValue>() {
            @Override
            public void onSuccess(GetMemo.ResponseValue response) {
                mView.updateMemo(response.getMemo());
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
