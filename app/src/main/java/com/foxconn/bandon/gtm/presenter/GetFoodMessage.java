package com.foxconn.bandon.gtm.presenter;


import com.foxconn.bandon.gtm.model.FoodMessage;
import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class GetFoodMessage extends UseCase<GetFoodMessage.RequestValue, GetFoodMessage.ResponseValue> {
    private GTMRepository repository;

    GetFoodMessage(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(GetFoodMessage.RequestValue requestValues) {
        IGTMDataSource.GetFoodMessageCallback callback = new IGTMDataSource.GetFoodMessageCallback() {
            @Override
            public void onSuccess(FoodMessage message) {
                getUseCaseCallback().onSuccess(new ResponseValue(message));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getFoodMessage(requestValues.tid, callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private FoodMessage messages;

        public ResponseValue(FoodMessage messages) {
            this.messages = messages;
        }

        public FoodMessage getMessage() {
            return messages;
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {

        private int tid;

        public RequestValue(int tid) {
            this.tid = tid;
        }

        public int getTid() {
            return tid;
        }
    }

}
