package com.foxconn.bandon.messages.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity(tableName = "Memos")
public final class Memo {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mID;
    @ColumnInfo(name = "path")
    private String mPath;
    @ColumnInfo(name = "draw_actions")
    private String mDrawActions;
    @ColumnInfo(name = "image_actions")
    private String mImageActions;
    @ColumnInfo(name = "audio")
    private String mAudioPath;
    @ColumnInfo(name = "scale")
    private float mScale = 1F;
    @ColumnInfo(name = "angle")
    private float mAngle = 0F;
    @ColumnInfo(name = "x")
    private int mLocationX;
    @ColumnInfo(name = "y")
    private int mLocationY;
    @ColumnInfo(name = "time")
    private long times;

    @Ignore
    private Bitmap bitmap;

    public Memo(String mID, String mPath, String mDrawActions, String mImageActions, String mAudioPath, int mLocationX, int mLocationY, long times) {
        this.mID = mID;
        this.mPath = mPath;
        this.mDrawActions = mDrawActions;
        this.mImageActions = mImageActions;
        this.mAudioPath = mAudioPath;
        this.mLocationX = mLocationX;
        this.mLocationY = mLocationY;
        this.times = times;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getDrawActions() {
        return mDrawActions;
    }

    public void setDrawActions(String mDrawActions) {
        this.mDrawActions = mDrawActions;
    }

    public String getImageActions() {
        return mImageActions;
    }

    public void setImageActions(String mImageActions) {
        this.mImageActions = mImageActions;
    }

    public String getAudioPath() {
        return mAudioPath;
    }

    public void setAudioPath(String mAudioPath) {
        this.mAudioPath = mAudioPath;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float mAngle) {
        this.mAngle = mAngle;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public void setLocation(int x, int y) {
        this.mLocationX = x;
        this.mLocationY = y;
    }

    private int[] getLoaction() {
        return new int[]{mLocationX, mLocationY};
    }

    public int getLocationX() {
        return mLocationX;
    }

    public void setLocationX(int mLocationX) {
        this.mLocationX = mLocationX;
    }

    public int getLocationY() {
        return mLocationY;
    }

    public void setLocationY(int mLocationY) {
        this.mLocationY = mLocationY;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "mID='" + mID + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mDrawActions='" + mDrawActions + '\'' +
                ", mImageActions='" + mImageActions + '\'' +
                ", mAudioPath='" + mAudioPath + '\'' +
                ", mScale=" + mScale +
                ", mAngle=" + mAngle +
                ", mLocationX=" + mLocationX +
                ", mLocationY=" + mLocationY +
                ", times=" + times +
                '}';
    }
}
