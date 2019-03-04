package com.foxconn.bandon.setting.clock.presenter;

import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.model.IClockDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class DeleteClock extends UseCase<DeleteClock.RequestValues, DeleteClock.ResponseValues> {

    private ClockRepository mRepository;

    DeleteClock(ClockRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ClockRepository.GetClockCallback callback = new IClockDataSource.GetClockCallback() {
            @Override
            public void onSuccess(ClockBean clock) {
                getUseCaseCallback().onSuccess(null);
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.deleteClock(requestValues.getId(), callback);
    }

    final static class RequestValues implements UseCase.RequestValues {
        private String id;

        public RequestValues(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    final class ResponseValues implements UseCase.ResponseValue {

    }
}
