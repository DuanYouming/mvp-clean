package com.foxconn.bandon.messages.presenter;

import android.support.annotation.NonNull;

import com.foxconn.bandon.messages.model.IMemoDataSource;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;

public class UpdateMemo extends UseCase<UpdateMemo.RequestValue, UpdateMemo.ResponseValue> {

    private MemoRepository mRepository;

    UpdateMemo(@NonNull MemoRepository repository) {
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
        mRepository.updateMemo(requestValues.getMemo(), callback);
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
