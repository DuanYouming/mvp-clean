package com.foxconn.bandon.food.model;

import java.util.List;
import java.util.Objects;

public class FridgeFood {

    public Data data;

    public class Data {
        public List<Label> listFoodList;
        public int num;
    }

    public class Label {
        public int id;
        public String deviceId;
        public String foodTagRename;
        public String storageRegion;
        public String numberSurplus;
        public String dueDate;
        public String foodTagBigPicture;
        public String foodTagSmallPicture;
        public String foodNutrition;
        public String iconColor;
        public String iconColorDetail;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Label)) {
                return false;
            }
            Label label = (Label) o;
            return id == label.id &&
                    Objects.equals(deviceId, label.deviceId) &&
                    Objects.equals(foodTagRename, label.foodTagRename) &&
                    Objects.equals(storageRegion, label.storageRegion) &&
                    Objects.equals(numberSurplus, label.numberSurplus) &&
                    Objects.equals(dueDate, label.dueDate) &&
                    Objects.equals(foodTagBigPicture, label.foodTagBigPicture) &&
                    Objects.equals(foodTagSmallPicture, label.foodTagSmallPicture) &&
                    Objects.equals(foodNutrition, label.foodNutrition) &&
                    Objects.equals(iconColor, label.iconColor) &&
                    Objects.equals(iconColorDetail, label.iconColorDetail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, deviceId, foodTagRename, storageRegion, numberSurplus, dueDate, foodTagBigPicture, foodTagSmallPicture, foodNutrition, iconColor, iconColorDetail);
        }

        @Override
        public String toString() {
            return "Label{" +
                    "id=" + id +
                    ", deviceId='" + deviceId + '\'' +
                    ", foodTagRename='" + foodTagRename + '\'' +
                    ", storageRegion='" + storageRegion + '\'' +
                    ", numberSurplus='" + numberSurplus + '\'' +
                    ", dueDate='" + dueDate + '\'' +
                    ", foodTagBigPicture='" + foodTagBigPicture + '\'' +
                    ", foodTagSmallPicture='" + foodTagSmallPicture + '\'' +
                    ", foodNutrition='" + foodNutrition + '\'' +
                    ", iconColor='" + iconColor + '\'' +
                    ", iconColorDetail='" + iconColorDetail + '\'' +
                    '}';
        }
    }
}
