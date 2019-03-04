package com.foxconn.bandon.setting.clock.presenter;

import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.model.IClockDataSource;
import com.foxconn.bandon.usecase.UseCase;

public class GetClock extends UseCase<GetClock.RequestValues, GetClock.ResponseValues> {

    private ClockRepository mRepository;

    public GetClock(ClockRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ClockRepository.GetClockCallback callback = new IClockDataSource.GetClockCallback() {
            @Override
            public void onSuccess(ClockBean clock) {
                getUseCaseCallback().onSuccess(new ResponseValues(clock));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getClock(requestValues.getId(), callback);
    }

    final public static class RequestValues implements UseCase.RequestValues {
        private String id;

        public RequestValues(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    final public static class ResponseValues implements UseCase.ResponseValue {
        private ClockBean clock;

        ResponseValues(ClockBean clock) {
            this.clock = clock;
        }

        public ClockBean getClock() {
            return clock;
        }
    }
}
