package com.foxconn.bandon.gtm.presenter;


import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class DeleteGTMessage extends UseCase<DeleteGTMessage.RequestValue, DeleteGTMessage.ResponseValue> {
    private GTMRepository repository;

    DeleteGTMessage(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(DeleteGTMessage.RequestValue requestValues) {
        IGTMDataSource.GTMCallback callback = new IGTMDataSource.GTMCallback() {
            @Override
            public void onSuccess() {
                getUseCaseCallback().onSuccess(null);
            }

            @Override
            public void onFailure() {

            }
        };
        repository.deleteGTMessage(requestValues.getId(), requestValues.getType(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String id;
        private int type;

        public RequestValue(String id, int type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public int getType() {
            return type;
        }
    }

}
