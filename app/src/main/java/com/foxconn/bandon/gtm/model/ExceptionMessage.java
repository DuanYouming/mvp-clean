package com.foxconn.bandon.gtm.model;

import android.arch.persistence.room.Entity;

@Entity(tableName = "ExceptionMessage")
public class ExceptionMessage extends GTMessage {
    public ExceptionMessage(String id, String deviceID, String content) {
        super(id, GTMessage.TYPE_EXCEPTION, GTMessage.LEVEL_HIGH, deviceID, content);
    }
}
