package com.foxconn.bandon.food;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFood;

import java.util.List;

public interface IFoodContract {

    interface Presenter extends BasePresenter {

        void load(int sPosition);

        void getFridgeFoods();

        void updateLocation(ColdRoomFood.Label label);

        void getFridgeImage(int index);

        void getCameraPhoto(int index);
    }

    interface View extends BaseView<Presenter> {

        void updateVegetableView(List<FridgeFood.Label> labels);

        void updateFridgeView(List<ColdRoomFood.Label> labels);

        void updateFreezerView(List<FridgeFood.Label> labels);

        void setFridgeImage(String url, int index);

    }

}
