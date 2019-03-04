package com.foxconn.bandon.messages.presenter;

import android.support.annotation.NonNull;

import com.foxconn.bandon.messages.model.IMemoDataSource;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;

public class SaveMemo extends UseCase<SaveMemo.RequestValue, SaveMemo.ResponseValue> {

    private MemoRepository mRepository;

    SaveMemo(@NonNull MemoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        MemoRepository.GetMemoCallback callback = new IMemoDataSource.GetMemoCallback() {
            @Override
            public void onSuccess(Memo memo) {
                getUseCaseCallback().onSuccess(null);
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.insertMemo(requestValues.getMemo(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private Memo memo;

        public RequestValue(Memo memo) {
            this.memo = memo;
        }

        public Memo getMemo() {
            return memo;
        }
    }
}
