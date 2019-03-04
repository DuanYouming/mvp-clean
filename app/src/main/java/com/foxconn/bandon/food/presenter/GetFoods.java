package com.foxconn.bandon.food.presenter;

import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.model.IFridgeFoodDataSource;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetFoods extends UseCase<GetFoods.RequestValue, GetFoods.ResponseValue> {
    private static final String TAG = GetFoods.class.getSimpleName();
    private FridgeFoodRepository mRepository;

    public GetFoods(FridgeFoodRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(final RequestValue requestValues) {
        FridgeFoodRepository.FridgeFoodsCallback callback = new IFridgeFoodDataSource.FridgeFoodsCallback() {
            @Override
            public void onResponse(Call<FridgeFood> call, Response<FridgeFood> response) {
                FridgeFood fridgeFood = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(fridgeFood, requestValues.getsPosition()));
            }

            @Override
            public void onFailure(Call<FridgeFood> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getFridgeFoods(String.valueOf(requestValues.getsPosition()), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private int sPosition;

        public RequestValue(int sPosition) {
            this.sPosition = sPosition;
        }

        public int getsPosition() {
            return sPosition;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private FridgeFood fridgeFood;
        private int sPosition;

        public ResponseValue(FridgeFood fridgeFood, int sPosition) {
            this.fridgeFood = fridgeFood;
            this.sPosition = sPosition;
        }

        public FridgeFood getFridgeFood() {
            return fridgeFood;
        }

        public int getsPosition() {
            return sPosition;
        }
    }
}
