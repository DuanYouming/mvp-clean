package com.foxconn.bandon.gtm.model;


import android.support.annotation.NonNull;

import com.foxconn.bandon.food.model.FridgeFood;

import java.util.List;

public interface IGTMDataSource {

    interface GetMessagesCallback<T> {
        void onSuccess(List<T> messages);

        void onFailure();
    }

    interface GTMCallback {
        void onSuccess();

        void onFailure();
    }

    interface GetFoodMessageCallback {
        void onSuccess(FoodMessage message);

        void onFailure();
    }


    void getGTMessages(GetMessagesCallback<GTMessage> callback);

    void getGTMessagesByType(int type, GetMessagesCallback<GTMessage> callback);

    void insertGTMessage(@NonNull GTMessage message, GTMCallback callback);

    void updateGTMessage(@NonNull GTMessage message, GTMCallback callback);

    void deleteGTMessage(String id, int type, GTMCallback callback);

    void getFoodMessage(int tid, GetFoodMessageCallback callback);

    void updateFoodsMessages(List<FridgeFood.Label> labels, GTMCallback callback);
}
