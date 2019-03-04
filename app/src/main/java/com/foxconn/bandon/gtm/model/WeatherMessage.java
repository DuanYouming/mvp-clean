package com.foxconn.bandon.gtm.model;

import android.arch.persistence.room.Entity;

@Entity(tableName = "WeatherMessage")
public class WeatherMessage extends GTMessage {

    public WeatherMessage(String id, int level, String deviceID, String content) {
        super(id, GTMessage.TYPE_WEATHER, level, deviceID, content);
    }
}
