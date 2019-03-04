package com.foxconn.bandon.label.detail.model;

import java.util.Objects;

public class LabelDetail {
    public Data data;

    public class Data {
        public int id;
        public String deviceId;
        public String foodTagRename;
        public String storageRegion;
        public String numberSurplus;
        public String dueDate;
        public String foodTagBigPicture;
        public String foodTagSmallPicture;
        public String foodNutrition;
        public String createTime;
        public String due_date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data)) return false;
            Data data = (Data) o;
            return id == data.id &&
                    Objects.equals(deviceId, data.deviceId) &&
                    Objects.equals(foodTagRename, data.foodTagRename) &&
                    Objects.equals(storageRegion, data.storageRegion) &&
                    Objects.equals(numberSurplus, data.numberSurplus) &&
                    Objects.equals(dueDate, data.dueDate) &&
                    Objects.equals(foodTagBigPicture, data.foodTagBigPicture) &&
                    Objects.equals(foodTagSmallPicture, data.foodTagSmallPicture) &&
                    Objects.equals(foodNutrition, data.foodNutrition) &&
                    Objects.equals(createTime, data.createTime) &&
                    Objects.equals(due_date, data.due_date);
        }

        @Override
        public int hashCode() {

            return Objects.hash(id, deviceId, foodTagRename, storageRegion, numberSurplus, dueDate, foodTagBigPicture, foodTagSmallPicture, foodNutrition, createTime, due_date);
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", deviceId='" + deviceId + '\'' +
                    ", foodTagRename='" + foodTagRename + '\'' +
                    ", storageRegion='" + storageRegion + '\'' +
                    ", numberSurplus='" + numberSurplus + '\'' +
                    ", dueDate='" + dueDate + '\'' +
                    ", foodTagBigPicture='" + foodTagBigPicture + '\'' +
                    ", foodTagSmallPicture='" + foodTagSmallPicture + '\'' +
                    ", foodNutrition='" + foodNutrition + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", due_date='" + due_date + '\'' +
                    '}';
        }
    }
}
