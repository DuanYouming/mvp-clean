package com.foxconn.bandon.food.model;


import com.foxconn.bandon.http.ResponseStr;

import retrofit2.Callback;

public interface IFridgeFoodDataSource {


    interface FridgeFoodsCallback extends Callback<FridgeFood> {

    }

    interface ColdRoomFoodsCallback extends Callback<ColdRoomFood> {

    }

    interface UpdateLocationCallback extends Callback<ResponseStr> {

    }


    interface GetCameraPhotoCallback {
        void onSuccess(String url);

        void onFailure();
    }


    void getFridgeFoods(String storagePosition, FridgeFoodsCallback callback);

    void getFridgeFoods(ColdRoomFoodsCallback callback);

    void updateLocation(ColdRoomFood.Label label, UpdateLocationCallback callback);

    void getCameraPhoto(int index, GetCameraPhotoCallback callback);

}
