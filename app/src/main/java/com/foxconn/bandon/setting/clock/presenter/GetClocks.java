package com.foxconn.bandon.setting.clock.presenter;

import android.support.annotation.NonNull;

import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.setting.clock.model.IClockDataSource;
import com.foxconn.bandon.usecase.UseCase;

import java.util.List;

public class GetClocks extends UseCase<GetClocks.RequestValues, GetClocks.ResponseValues> {
    private ClockRepository mRepository;

    public GetClocks(@NonNull ClockRepository repository) {
        this.mRepository = repository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ClockRepository.LoadClocksCallback callback = new IClockDataSource.LoadClocksCallback() {
            @Override
            public void onSuccess(List<ClockBean> clocks) {
                getUseCaseCallback().onSuccess(new ResponseValues(clocks));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        mRepository.getAll(callback);
    }

    public final  static class RequestValues implements UseCase.RequestValues {

    }

    final public class ResponseValues implements UseCase.ResponseValue {
        private List<ClockBean> clocks;

         ResponseValues(List<ClockBean> clocks) {
            this.clocks = clocks;
        }

        public List<ClockBean> getClocks() {
            return clocks;
        }
    }
}
