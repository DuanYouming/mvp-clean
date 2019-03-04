package com.foxconn.bandon.setting.clock.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;


@Entity(tableName = "Clocks")
public class ClockBean implements Comparable<ClockBean> {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "hour")
    private int hour;
    @ColumnInfo(name = "min")
    private int min;
    @ColumnInfo(name = "tag")
    private String tag;
    @ColumnInfo(name = "ringtone")
    private String ringtone;
    @ColumnInfo(name = "periods")
    private String periods;
    @ColumnInfo(name = "isEnable")
    private Boolean isEnable;
    @ColumnInfo(name = "createTime")
    private Long createTime;
    @ColumnInfo(name = "requestCode")
    private int requestCode;
    @ColumnInfo(name = "timezoneId")
    private String timezoneId;

    public ClockBean() {

    }

    @Ignore
    public ClockBean(@NonNull String id, Long createTime) {
        this.id = id;
        this.createTime = createTime;
        this.requestCode = createTime.intValue();
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }

    public void setTimezone(TimeZone timezone) {
        timezoneId = timezone.getID();
    }

    public List<Integer> getPeriodsList() {
        List<Integer> list = new ArrayList<>();
        String result = periods.replace("[", "").replace("]", "").replace(" ", "");
        if (!TextUtils.isEmpty(result)) {
            String[] results = result.split(",");
            for (String r : results) {
                list.add(Integer.valueOf(r));
            }
        }
        return list;
    }

    public TimeZone getTimezone() {
        if (TextUtils.isEmpty(timezoneId)) {
            return TimeZone.getDefault();
        } else {
            return TimeZone.getTimeZone(timezoneId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClockBean)) return false;
        ClockBean clockBean = (ClockBean) o;
        return getHour() == clockBean.getHour() &&
                getMin() == clockBean.getMin() &&
                getRequestCode() == clockBean.getRequestCode() &&
                Objects.equals(getId(), clockBean.getId()) &&
                Objects.equals(getTag(), clockBean.getTag()) &&
                Objects.equals(getRingtone(), clockBean.getRingtone()) &&
                Objects.equals(getPeriods(), clockBean.getPeriods()) &&
                Objects.equals(isEnable, clockBean.isEnable) &&
                Objects.equals(getCreateTime(), clockBean.getCreateTime()) &&
                Objects.equals(getTimezoneId(), clockBean.getTimezoneId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHour(), getMin(), getTag(), getRingtone(), getPeriods(), isEnable, getCreateTime(), getRequestCode(), getTimezoneId());
    }

    @Override
    public String toString() {
        return "ClockBean{" +
                "id='" + id + '\'' +
                ", hour=" + hour +
                ", min=" + min +
                ", tag='" + tag + '\'' +
                ", ringtone='" + ringtone + '\'' +
                ", periods=" + periods +
                ", isEnable=" + isEnable +
                ", createTime=" + createTime +
                ", intentRequestCode=" + requestCode +
                ", timezoneId='" + timezoneId + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ClockBean o) {
        return this.createTime - o.createTime < 0 ? -1 : 1;
    }
}
