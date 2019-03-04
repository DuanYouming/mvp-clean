package com.foxconn.bandon.setting.user.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user")
public class UserInfo {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "created")
    private long created;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "icon")
    private String icon;

    UserInfo(@NonNull String name, String url, String icon, long created) {
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.created = created;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
