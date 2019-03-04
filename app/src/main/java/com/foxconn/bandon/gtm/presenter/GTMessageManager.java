package com.foxconn.bandon.gtm.presenter;


import android.support.annotation.NonNull;

import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.presenter.GetFoods;
import com.foxconn.bandon.gtm.model.GTMDao;
import com.foxconn.bandon.gtm.model.GTMRepository;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class GTMessageManager {
    private static final String TAG = GTMessageManager.class.getSimpleName();

    private UseCaseHandler mHandler;
    private GTMRepository repository;
    private FridgeFoodRepository foodRepository;
    private List<GTMessage> mGTMessages = new ArrayList<>();
    private List<GTMessageCallback> callbacks = new ArrayList<>();
    private int index;

    private static GTMessageManager instance;

    private GTMessageManager() {
        this.mHandler = UseCaseHandler.getInstance();
        GTMDao gtmDao = BandonDataBase.getInstance(BandonApplication.getInstance()).gtmDao();
        this.repository = GTMRepository.getInstance(gtmDao, new AppExecutors());
        this.foodRepository = FridgeFoodRepository.getInstance(BandonApplication.getInstance(), new AppExecutors());
    }

    public static GTMessageManager getInstance() {
        if (null == instance) {
            synchronized (GTMessageManager.class) {
                if (null == instance) {
                    instance = new GTMessageManager();
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        GTMRepository.destroy();
        instance = null;
    }

    public void registerCallback(GTMessageCallback callback) {
        if (null != callback) {
            callbacks.add(callback);
        }
    }

    public void unregisterCallback(GTMessageCallback callback) {
        if (null != callback) {
            callbacks.remove(callback);
        }
    }

    public List<GTMessage> getCurrentGTMessages() {
        return mGTMessages;
    }

    public void initFoodMessages() {
        LogUtils.d(TAG, "initFoodMessages");
        getFoods(new UseCase.UseCaseCallback<GetFoods.ResponseValue>() {
            @Override
            public void onSuccess(GetFoods.ResponseValue response) {
                FridgeFood fridgeFood = response.getFridgeFood();
                if (null != fridgeFood && null != fridgeFood.data) {
                    List<FridgeFood.Label> labels = fridgeFood.data.listFoodList;
                    insertOrUpdate(labels);
                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    public void insertOrUpdate(List<FridgeFood.Label> labels) {
        LogUtils.d(TAG, "insertOrUpdate");
        UpdateFoodMessages update = new UpdateFoodMessages(repository);
        UpdateFoodMessages.RequestValue value = new UpdateFoodMessages.RequestValue(labels);
        mHandler.execute(update, value, new UseCase.UseCaseCallback<UpdateFoodMessages.ResponseValue>() {
            @Override
            public void onSuccess(UpdateFoodMessages.ResponseValue response) {
                LogUtils.d(TAG, "insertOrUpdate() onSuccess");
                getGTMessages();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getFoods(UseCase.UseCaseCallback<GetFoods.ResponseValue> callback) {
        getStoreAreaFoods(Constant.AREA_FRIDGE, callback);
        getStoreAreaFoods(Constant.AREA_VEGETABLE, callback);
        getStoreAreaFoods(Constant.AREA_FREEZER, callback);
    }

    private void getStoreAreaFoods(int position, UseCase.UseCaseCallback<GetFoods.ResponseValue> callback) {
        GetFoods get = new GetFoods(foodRepository);
        GetFoods.RequestValue value = new GetFoods.RequestValue(position);
        mHandler.execute(get, value, callback);
    }

    private void getGTMessages() {
        LogUtils.d(TAG, "getGTMessages");
        getGTMessages(new UseCase.UseCaseCallback<GetGTMessages.ResponseValue>() {
            @Override
            public void onSuccess(GetGTMessages.ResponseValue response) {
                List<GTMessage> messages = response.getMessages();
                if (null != messages) {
                    mGTMessages = messages;
                    mGTMessages.sort((o1, o2) -> {
                        if (o1.getType() != o2.getType()) {
                            return o1.getType() - o2.getType();
                        } else if (o1.getLevel() != o2.getLevel()) {
                            return o1.getLevel() - o2.getLevel();
                        }
                        return 0;
                    });

                    index = 0;
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getGTMessages(UseCase.UseCaseCallback<GetGTMessages.ResponseValue> callback) {
        GetGTMessages get = new GetGTMessages(repository);
        GetGTMessages.RequestValue value = new GetGTMessages.RequestValue();
        mHandler.execute(get, value, callback);
    }

    public void getFoodMessage(int tid, UseCase.UseCaseCallback<GetFoodMessage.ResponseValue> caseCallback) {
        GetFoodMessage get = new GetFoodMessage(repository);
        GetFoodMessage.RequestValue value = new GetFoodMessage.RequestValue(tid);
        mHandler.execute(get, value, caseCallback);
    }

    public void getGTMessageByType(int type, UseCase.UseCaseCallback<GetGTMessagesByType.ResponseValue> callback) {
        GetGTMessagesByType get = new GetGTMessagesByType(repository);
        GetGTMessagesByType.RequestValue value = new GetGTMessagesByType.RequestValue(type);
        mHandler.execute(get, value, callback);
    }

    public void insertGTMessage(@NonNull GTMessage message) {
        insertGTMessage(message, new UseCase.UseCaseCallback<InsertGTMessage.ResponseValue>() {
            @Override
            public void onSuccess(InsertGTMessage.ResponseValue response) {
                getGTMessages();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void insertGTMessage(@NonNull GTMessage message, UseCase.UseCaseCallback<InsertGTMessage.ResponseValue> caseCallback) {
        LogUtils.d(TAG, "insertGTMessage:" + message.toString());
        InsertGTMessage insert = new InsertGTMessage(repository);
        InsertGTMessage.RequestValue value = new InsertGTMessage.RequestValue(message);
        mHandler.execute(insert, value, caseCallback);
    }

    public void updateGTMessage(@NonNull GTMessage message) {
        LogUtils.d(TAG, "updateGTMessage");
        UpdateGTMessage update = new UpdateGTMessage(repository);
        UpdateGTMessage.RequestValue value = new UpdateGTMessage.RequestValue(message);
        mHandler.execute(update, value, new UseCase.UseCaseCallback<UpdateGTMessage.ResponseValue>() {
            @Override
            public void onSuccess(UpdateGTMessage.ResponseValue response) {
                LogUtils.d(TAG, "update GTMessage success");
                getGTMessages();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void deleteGTMessage(String id, int type) {
        LogUtils.d(TAG, "deleteGTMessage");
        DeleteGTMessage delete = new DeleteGTMessage(repository);
        DeleteGTMessage.RequestValue value = new DeleteGTMessage.RequestValue(id, type);
        mHandler.execute(delete, value, new UseCase.UseCaseCallback<DeleteGTMessage.ResponseValue>() {
            @Override
            public void onSuccess(DeleteGTMessage.ResponseValue response) {
                LogUtils.d(TAG, "delete GTMessage success");
                getGTMessages();
            }

            @Override
            public void onFailure() {

            }
        });
    }


    public void loopMessage() {
        if (mGTMessages.size() == 0 || callbacks.size() == 0) {
            return;
        }
        index = index % mGTMessages.size();
        for (GTMessageCallback callback : callbacks) {
            callback.loop(mGTMessages.get(index));
        }
        index++;
    }


    public interface GTMessageCallback {
        void loop(GTMessage message);
    }


}
