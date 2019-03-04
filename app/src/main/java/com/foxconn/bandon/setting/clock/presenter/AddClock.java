package com.foxconn.bandon.setting.clock.presenter;

import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.model.IClockDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class AddClock extends UseCase<AddClock.RequestValues, AddClock.ResponseValues> {

    private ClockRepository mRepository;

    AddClock(ClockRepository repository) {
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
        mRepository.save(requestValues.getClock(), callback);
    }

    final public static class RequestValues implements UseCase.RequestValues {
        private ClockBean clock;

        public RequestValues(ClockBean clock) {
            this.clock = clock;
        }

        public ClockBean getClock() {
            return clock;
        }
    }

    final class ResponseValues implements UseCase.ResponseValue {

    }
}
