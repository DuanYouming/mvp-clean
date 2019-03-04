package com.foxconn.bandon.food.presenter;

import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.model.IFridgeFoodDataSource;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetFridgeFoods extends UseCase<GetFridgeFoods.RequestValue, GetFridgeFoods.ResponseValue> {
    private static final String TAG = GetFridgeFoods.class.getSimpleName();
    private FridgeFoodRepository mRepository;

    public GetFridgeFoods(FridgeFoodRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(final RequestValue requestValues) {
        FridgeFoodRepository.ColdRoomFoodsCallback callback = new IFridgeFoodDataSource.ColdRoomFoodsCallback() {
            @Override
            public void onResponse(Call<ColdRoomFood> call, Response<ColdRoomFood> response) {
                ColdRoomFood fridgeFood = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(fridgeFood));
            }

            @Override
            public void onFailure(Call<ColdRoomFood> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getFridgeFoods(callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private ColdRoomFood fridgeFood;

        public ResponseValue(ColdRoomFood fridgeFood) {
            this.fridgeFood = fridgeFood;
        }

        public ColdRoomFood getFridgeFood() {
            return fridgeFood;
        }

    }
}
