package com.foxconn.bandon.messages.presenter;

import android.support.annotation.NonNull;
import com.foxconn.bandon.messages.model.IMemoDataSource;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetMemos extends UseCase<GetMemos.RequestValue, GetMemos.ResponseValue> {
    private MemoRepository mRepository;

    GetMemos(@NonNull MemoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        MemoRepository.LoadMemosCallback callback = new IMemoDataSource.LoadMemosCallback() {
            @Override
            public void onSuccess(List<Memo> memos) {
                ResponseValue value = new ResponseValue(memos);
                getUseCaseCallback().onSuccess(value);
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getMemos(callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private List<Memo> memos;

        public ResponseValue(List<Memo> memos) {
            this.memos = memos;
        }

        public List<Memo> getMemos() {
            return memos;
        }
    }

    static final class RequestValue implements UseCase.RequestValues {

    }
}
