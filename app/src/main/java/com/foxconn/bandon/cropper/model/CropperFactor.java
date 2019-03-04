package com.foxconn.bandon.cropper.model;

public class CropperFactor {
    private float left;
    private float top;
    private float width;
    private float height;
    private float bitmapWidth;
    private float bitmapHeight;

    public CropperFactor(float left, float top, float width, float height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(float bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public float getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(float bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    @Override
    public String toString() {
        return "CropperFactor{" +
                "left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", bitmapWidth=" + bitmapWidth +
                ", bitmapHeight=" + bitmapHeight +
                '}';
    }
}
