package com.foxconn.bandon.gtm.presenter;


import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class InsertGTMessage extends UseCase<InsertGTMessage.RequestValue, InsertGTMessage.ResponseValue> {
    private GTMRepository repository;

    InsertGTMessage(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(InsertGTMessage.RequestValue requestValues) {
        IGTMDataSource.GTMCallback callback = new IGTMDataSource.GTMCallback() {
            @Override
            public void onSuccess() {
                getUseCaseCallback().onSuccess(null);
            }

            @Override
            public void onFailure() {

            }
        };
        repository.insertGTMessage(requestValues.getMessage(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private GTMessage message;

        public RequestValue(GTMessage message) {
            this.message = message;
        }

        public GTMessage getMessage() {
            return message;
        }
    }

}
