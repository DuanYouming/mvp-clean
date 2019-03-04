package com.foxconn.bandon.standby.presenter;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.foxconn.bandon.standby.model.IStandbyDataSource;
import com.foxconn.bandon.standby.model.StandbyRepository;
import com.foxconn.bandon.usecase.UseCase;


public class GetColor extends UseCase<GetColor.RequestValue, GetColor.ResponseValue> {

    private StandbyRepository mRepository;

    GetColor(StandbyRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue values) {
        IStandbyDataSource.GetColorCallback callback = new StandbyRepository.GetColorCallback() {
            @Override
            public void onSuccess(int color) {
                getUseCaseCallback().onSuccess(new ResponseValue(color));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getColor(values.getBitmap(),values.getRectF(), callback);
    }

    public final static class RequestValue implements UseCase.RequestValues {
        private RectF rectF;
        private Bitmap bitmap;

        public RequestValue(Bitmap bitmap,RectF rectF) {
            this.rectF = rectF;
            this.bitmap = bitmap;
        }

        RectF getRectF() {
            return rectF;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }

    public final static class ResponseValue implements UseCase.ResponseValue {
        private int color;

        public ResponseValue(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
