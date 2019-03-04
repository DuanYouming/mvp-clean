package com.foxconn.bandon.setting.clock.model;

import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;
import java.util.List;

public class ClockRepository implements IClockDataSource {
    private static final String TAG = ClockRepository.class.getSimpleName();
    private ClockDao mDao;
    private static ClockRepository instance;
    private AppExecutors mExecutors;

    public ClockRepository(ClockDao dao, AppExecutors executors) {
        this.mDao = dao;
        this.mExecutors = executors;
    }


    public static ClockRepository getInstance(ClockDao dao, AppExecutors executors) {
        if (instance == null) {
            instance = new ClockRepository(dao, executors);
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    @Override
    public void getAll(final LoadClocksCallback callback) {
        Runnable runnable = () -> {
            final List<ClockBean> clocks = mDao.getAll();
            mExecutors.mainThread().execute(() -> callback.onSuccess(clocks));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void save(final ClockBean clock, final GetClockCallback callback) {
        Runnable runnable = () -> {
            mDao.addClock(clock);
            mExecutors.mainThread().execute(() -> callback.onSuccess(null));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteClock(final String id, final GetClockCallback callback) {
        Runnable runnable = () -> {
            mDao.deleteClock(id);
            mExecutors.mainThread().execute(() -> {
                callback.onSuccess(null);
                LogUtils.d(TAG,"deleteClock Thread:"+Thread.currentThread().toString());
            });
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void update(final ClockBean clock, final GetClockCallback callback) {
        Runnable runnable = () -> {
            mDao.update(clock);
            mExecutors.mainThread().execute(() -> callback.onSuccess(null));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getClock(final String id, final GetClockCallback callback) {
        Runnable runnable = () -> {
            final ClockBean clock = mDao.getClock(id);
            mExecutors.mainThread().execute(() -> callback.onSuccess(clock));
        };
        mExecutors.diskIO().execute(runnable);
    }
}
