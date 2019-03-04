package com.foxconn.bandon.gtm.presenter;


import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetGTMessagesByType extends UseCase<GetGTMessagesByType.RequestValue, GetGTMessagesByType.ResponseValue> {
    private GTMRepository repository;

    GetGTMessagesByType(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(GetGTMessagesByType.RequestValue requestValues) {
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
        repository.getGTMessagesByType(requestValues.type, callback);
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
        private int type;

        public RequestValue(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

}
