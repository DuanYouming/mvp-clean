package com.foxconn.bandon.gtm.presenter;

import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.IGTMDataSource;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class UpdateFoodMessages extends UseCase<UpdateFoodMessages.RequestValue, UpdateFoodMessages.ResponseValue> {
    private GTMRepository repository;

    UpdateFoodMessages(GTMRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(UpdateFoodMessages.RequestValue requestValues) {
        IGTMDataSource.GTMCallback callback = new IGTMDataSource.GTMCallback() {
            @Override
            public void onSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onFailure() {

            }
        };
        repository.updateFoodsMessages(requestValues.getLabels(), callback);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValue implements UseCase.RequestValues {
        private List<FridgeFood.Label> labels;

        public RequestValue(List<FridgeFood.Label> labels) {
            this.labels = labels;
        }

        public List<FridgeFood.Label> getLabels() {
            return labels;
        }
    }

}
