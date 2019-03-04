package com.foxconn.bandon.gtm.model;


import android.content.Context;
import android.support.annotation.NonNull;

import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.DateUtils;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GTMRepository implements IGTMDataSource {

    private static final String TAG = GTMRepository.class.getSimpleName();
    private volatile static GTMRepository INSTANCE;
    private AppExecutors mExecutors;
    private GTMDao mDao;


    private GTMRepository(GTMDao dao, AppExecutors executors) {
        mDao = dao;
        mExecutors = executors;
    }

    public static GTMRepository getInstance(GTMDao dao, AppExecutors executors) {
        if (null == INSTANCE) {
            synchronized (GTMRepository.class) {
                if (null == INSTANCE) {
                    INSTANCE = new GTMRepository(dao, executors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
    }

    @Override
    public void getGTMessages(final GetMessagesCallback<GTMessage> callback) {
        Runnable runnable = () -> {
            final List<GTMessage> messages = new ArrayList<>();
            List<FoodMessage> foodMessages = mDao.getFoodMessages();
            List<WeatherMessage> weatherMessages = mDao.getWeatherMessages();
            List<ExceptionMessage> exceptionMessages = mDao.getExceptionMessages();
            messages.addAll(foodMessages);
            messages.addAll(weatherMessages);
            messages.addAll(exceptionMessages);
            mExecutors.mainThread().execute(() -> callback.onSuccess(messages));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getGTMessagesByType(final int type, final GetMessagesCallback<GTMessage> callback) {
        Runnable runnable = () -> {
            final List<GTMessage> messages = new ArrayList<>();
            switch (type) {
                case GTMessage.TYPE_EXCEPTION:
                    messages.addAll(mDao.getExceptionMessages());
                    break;
                case GTMessage.TYPE_FOOD:
                    messages.addAll(mDao.getFoodMessages());
                    break;
                case GTMessage.TYPE_WEATHER:
                    messages.addAll(mDao.getWeatherMessages());
                    break;
                default:
                    break;
            }
            mExecutors.mainThread().execute(() -> callback.onSuccess(messages));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void insertGTMessage(@NonNull final GTMessage message, final GTMCallback callback) {
        Runnable runnable = () -> {
            try {
                if (message instanceof FoodMessage) {
                    mDao.insertFoodMessage((FoodMessage) message);
                } else if (message instanceof WeatherMessage) {
                    mDao.insertWeatherMessage((WeatherMessage) message);
                } else if (message instanceof ExceptionMessage) {
                    mDao.insertExceptionMessage((ExceptionMessage) message);
                }
            } catch (Exception e) {
                LogUtils.d(TAG, "insertGTMessage exception:" + e.toString());
                callback.onFailure();
            }
            callback.onSuccess();
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateGTMessage(final @NonNull GTMessage message, final GTMCallback callback) {
        Runnable runnable = () -> {
            try {
                if (message instanceof FoodMessage) {
                    mDao.updateFoodMessage((FoodMessage) message);
                } else if (message instanceof WeatherMessage) {
                    mDao.updateWeatherMessage((WeatherMessage) message);
                } else if (message instanceof ExceptionMessage) {
                    mDao.updateExceptionMessage((ExceptionMessage) message);
                }
            } catch (Exception e) {
                LogUtils.d(TAG, "updateGTMessage exception:" + e.toString());
                callback.onFailure();
            }
            callback.onSuccess();
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteGTMessage(final String id, final int type, final GTMCallback callback) {
        Runnable runnable = () -> {
            try {
                switch (type) {
                    case GTMessage.TYPE_EXCEPTION:
                        mDao.deleteExceptionMessage(id);
                        break;
                    case GTMessage.TYPE_FOOD:
                        mDao.deleteFoodMessage(id);
                        break;
                    case GTMessage.TYPE_WEATHER:
                        mDao.deleteWeatherMessage(id);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LogUtils.d(TAG, "updateGTMessage exception:" + e.toString());
                callback.onFailure();
            }
            callback.onSuccess();
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getFoodMessage(final int tid, final GetFoodMessageCallback callback) {
        Runnable runnable = () -> {
            final FoodMessage message = mDao.getFoodMessage(tid);
            mExecutors.mainThread().execute(() -> {
                if (null != message) {
                    callback.onSuccess(message);
                } else {
                    callback.onFailure();
                }
            });
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateFoodsMessages(final List<FridgeFood.Label> labels, GTMCallback callback) {
        Runnable runnable = () -> {
            Context context = BandonApplication.getInstance();
            for (FridgeFood.Label label : labels) {
                int tid = label.id;
                int level = Integer.valueOf(label.iconColor);
                int position = Integer.valueOf(label.storageRegion);
                String expiration = label.dueDate;
                String name = label.foodTagRename;
                String deviceID = DeviceUtils.getDeviceId(context);
                String content = DateUtils.getFoodMessageContent(context, position, name, expiration, level);
                FoodMessage foodMessage = mDao.getFoodMessage(tid);
                if (null != foodMessage) {
                    foodMessage.setLevel(level);
                    foodMessage.setPosition(position);
                    foodMessage.setExpirationDate(expiration);
                    foodMessage.setContent(content);
                    mDao.updateFoodMessage(foodMessage);
                    LogUtils.e(TAG, "updateFoodMessage:" + foodMessage.getFoodName());
                } else {
                    final String id = UUID.randomUUID().toString();
                    foodMessage = new FoodMessage(id, level, tid, position, expiration, name, deviceID, content);
                    mDao.insertFoodMessage(foodMessage);
                    LogUtils.e(TAG, "insertFoodMessage:" + foodMessage.getFoodName());
                }
            }
            callback.onSuccess();
        };
        mExecutors.diskIO().execute(runnable);
    }

}
