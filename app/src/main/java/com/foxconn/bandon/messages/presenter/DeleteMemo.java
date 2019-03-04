package com.foxconn.bandon.messages.presenter;

import android.support.annotation.NonNull;

import com.foxconn.bandon.messages.model.IMemoDataSource;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;

public class DeleteMemo extends UseCase<DeleteMemo.RequestValue,DeleteMemo.ResponseValue> {

    private MemoRepository mRepository;

    DeleteMemo(@NonNull MemoRepository repository) {
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
        mRepository.deleteMemoById(requestValues.getID(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

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
