package com.foxconn.bandon.food.presenter;

import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.model.IFridgeFoodDataSource;
import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.usecase.UseCase;
import retrofit2.Call;
import retrofit2.Response;

public class UpdateLocation extends UseCase<UpdateLocation.RequestValue, UpdateLocation.ResponseValue> {
    private static final String TAG = UpdateLocation.class.getSimpleName();
    private FridgeFoodRepository mRepository;

    public UpdateLocation(FridgeFoodRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(final RequestValue requestValues) {
        FridgeFoodRepository.UpdateLocationCallback callback = new IFridgeFoodDataSource.UpdateLocationCallback() {
            @Override
            public void onResponse(Call<ResponseStr> call, Response<ResponseStr> response) {
                getUseCaseCallback().onSuccess(new ResponseValue(response.body()));
            }

            @Override
            public void onFailure(Call<ResponseStr> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.updateLocation(requestValues.getLabel(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private ColdRoomFood.Label label;

        public RequestValue(ColdRoomFood.Label label) {
            this.label = label;
        }

        public ColdRoomFood.Label getLabel() {
            return label;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private ResponseStr response;

        public ResponseValue(ResponseStr response) {
            this.response = response;
        }

        public ResponseStr getResponse() {
            return response;
        }
    }
}
