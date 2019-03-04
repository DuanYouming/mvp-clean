package com.foxconn.bandon.setting.clock.presenter;

import com.foxconn.bandon.setting.clock.ClockContact;
import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.clock.model.ClockRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;

import java.util.List;

public class ClockPresenter implements ClockContact.Presenter {

    private ClockContact.View mView;
    private ClockRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    public ClockPresenter(ClockContact.View view, ClockRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        mUseCaseHandler = UseCaseHandler.getInstance();
        mView.setPresenter(this);
    }

    @Override
    public void add(final ClockBean clock) {
        AddClock addClock = new AddClock(mRepository);
        AddClock.RequestValues values = new AddClock.RequestValues(clock);
        mUseCaseHandler.execute(addClock, values, new UseCase.UseCaseCallback<AddClock.ResponseValues>() {
            @Override
            public void onSuccess(AddClock.ResponseValues response) {
                ClockManager.getInstance().newClock(clock);
                getAll();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void delete(final ClockBean clock) {
        DeleteClock deleteClock = new DeleteClock(mRepository);
        DeleteClock.RequestValues values = new DeleteClock.RequestValues(clock.getId());
        mUseCaseHandler.execute(deleteClock, values, new UseCase.UseCaseCallback<DeleteClock.ResponseValues>() {
            @Override
            public void onSuccess(DeleteClock.ResponseValues response) {
                ClockManager.getInstance().deleteClock(clock);
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void update(final ClockBean clock, final boolean isUpdateView) {
        UpdateClock update = new UpdateClock(mRepository);
        UpdateClock.RequestValues values = new UpdateClock.RequestValues(clock);
        mUseCaseHandler.execute(update, values, new UseCase.UseCaseCallback<UpdateClock.ResponseValues>() {
            @Override
            public void onSuccess(UpdateClock.ResponseValues response) {
                if (clock.getEnable()) {
                    ClockManager.getInstance().restartClock(clock);
                } else {
                    ClockManager.getInstance().cancelClock(clock);
                }
                if (isUpdateView) {
                    getAll();
                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void getAll() {
        GetClocks get = new GetClocks(mRepository);
        GetClocks.RequestValues values = new GetClocks.RequestValues();
        mUseCaseHandler.execute(get, values, new UseCase.UseCaseCallback<GetClocks.ResponseValues>() {
            @Override
            public void onSuccess(GetClocks.ResponseValues response) {
                List<ClockBean> clocks = response.getClocks();
                mView.notifyDataChanged(clocks);
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void addListener() {
        ClockManager.getInstance().addStateChangedListener(listener);
    }

    @Override
    public void removeListener() {
        ClockManager.getInstance().removeStateChangedListener(listener);
    }

    private ClockManager.OnStateChangedListener listener = this::getAll;

}
