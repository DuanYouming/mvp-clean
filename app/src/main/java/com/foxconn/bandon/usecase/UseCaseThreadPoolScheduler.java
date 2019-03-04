package com.foxconn.bandon.usecase;

import android.os.Handler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UseCaseThreadPoolScheduler implements UseCaseScheduler {

    private final Handler mHandler = new Handler();
    private static final int POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final int TIMEOUT = 30;
    private ThreadPoolExecutor mThreadPoolExecutor;

    UseCaseThreadPoolScheduler() {
        mThreadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(POOL_SIZE));
        mThreadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> useCaseCallback) {
        mHandler.post(() -> {
            if (null == useCaseCallback) {
                return;
            }
            useCaseCallback.onSuccess(response);
        });
    }

    @Override
    public <V extends UseCase.ResponseValue> void onFailure(final UseCase.UseCaseCallback<V> useCaseCallback) {
        mHandler.post(() -> {
            if (null == useCaseCallback) {
                return;
            }
            useCaseCallback.onFailure();
        });
    }
}
