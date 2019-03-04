package com.foxconn.bandon.gtm.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

public class GTMessage {

    public static final int TYPE_EXCEPTION = 0;
    public static final int TYPE_WEATHER = 1;
    public static final int TYPE_FOOD = 2;

    public static final int LEVEL_HIGH = 0;
    public static final int LEVEL_MIDDLE = 1;
    public static final int LEVEL_LOW = 2;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "level")
    private int level;
    @ColumnInfo(name = "deviceID")
    private String deviceID;
    @ColumnInfo(name = "content")
    private String content;

    public GTMessage(@NonNull String id, int type, int level, String deviceID, String content) {
        this.id = id;
        this.type = type;
        this.level = level;
        this.deviceID = deviceID;
        this.content = content;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "GTMessage{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", level=" + level +
                ", deviceID='" + deviceID + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
