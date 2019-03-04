package com.foxconn.bandon.gtm.presenter;


import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetGTMessages extends UseCase<GetGTMessages.RequestValue, GetGTMessages.ResponseValue> {
    private GTMRepository repository;

    GetGTMessages(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(GetGTMessages.RequestValue requestValues) {
        IGTMDataSource.GetMessagesCallback<GTMessage> callback = new IGTMDataSource.GetMessagesCallback<GTMessage>() {
            @Override
            public void onSuccess(List<GTMessage> messages) {
                getUseCaseCallback().onSuccess(new ResponseValue(messages));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getGTMessages(callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private List<GTMessage> messages;

        public ResponseValue(List<GTMessage> messages) {
            this.messages = messages;
        }

        public List<GTMessage> getMessages() {
            return messages;
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {

    }

}
