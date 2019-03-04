package com.foxconn.bandon.messages.presenter;

import android.support.annotation.NonNull;

import com.foxconn.bandon.messages.model.IMemoDataSource;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;

public class GetMemo extends UseCase<GetMemo.RequestValue, GetMemo.ResponseValue> {
    private MemoRepository mRepository;

    GetMemo(@NonNull MemoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        MemoRepository.GetMemoCallback callback = new IMemoDataSource.GetMemoCallback() {
            @Override
            public void onSuccess(Memo memo) {
                ResponseValue value = new ResponseValue(memo);
                getUseCaseCallback().onSuccess(value);
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getMemoById(requestValues.getID(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Memo memo;

        ResponseValue(Memo memo) {
            this.memo = memo;
        }

        public Memo getMemo() {
            return memo;
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String id;

        public RequestValue(String id) {
            this.id = id;
        }

        String getID() {
            return id;
        }
    }
}
