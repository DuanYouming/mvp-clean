package com.foxconn.bandon.food.presenter;

import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.model.IFridgeFoodDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class GetCameraPhoto extends UseCase<GetCameraPhoto.RequestValue, GetCameraPhoto.ResponseValue> {
    private FridgeFoodRepository mRepository;

    GetCameraPhoto(FridgeFoodRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(final RequestValue requestValues) {
        FridgeFoodRepository.GetCameraPhotoCallback callback = new IFridgeFoodDataSource.GetCameraPhotoCallback() {
            @Override
            public void onSuccess(String url) {
                getUseCaseCallback().onSuccess(new ResponseValue(url));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getCameraPhoto(requestValues.getIndex(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private int index;

        public RequestValue(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private String url;

        public ResponseValue(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
