package com.foxconn.bandon.gtm.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

@Entity(tableName = "FoodMessage")
public class FoodMessage extends GTMessage {
    @ColumnInfo(name = "tid")
    private int tid;
    @ColumnInfo(name = "position")
    private int position;
    @ColumnInfo(name = "expiration_date")
    private String expirationDate;
    @ColumnInfo(name = "food_name")
    private String foodName;

    public FoodMessage(String id, int level, int tid, int position, String expirationDate, String foodName, String deviceID, String content) {
        super(id, GTMessage.TYPE_FOOD, level, deviceID, content);
        this.tid = tid;
        this.position = position;
        this.expirationDate = expirationDate;
        this.foodName = foodName;
    }

    public int getTid() {
        return tid;
    }

    public int getPosition() {
        return position;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    @Override
    public String toString() {
        return "FoodMessage{" +
                "tid=" + tid +
                ", position=" + position +
                ", expirationDate='" + expirationDate + '\'' +
                ", foodName='" + foodName + '\'' +
                "} " + super.toString();
    }
}
