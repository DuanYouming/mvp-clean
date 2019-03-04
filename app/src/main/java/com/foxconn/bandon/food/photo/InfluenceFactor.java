package com.foxconn.bandon.food.photo;



public class InfluenceFactor {
    private int brightness;
    private int saturation;
    private int dx;
    private int dy;
    private int dw;
    private int dh;

    InfluenceFactor() {
    }

    InfluenceFactor(int brightness, int saturation, int dx, int dy, int dw, int dh) {
        this.brightness = brightness;
        this.saturation = saturation;
        this.dx = dx;
        this.dy = dy;
        this.dw = dw;
        this.dh = dh;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getDw() {
        return dw;
    }

    public int getDh() {
        return dh;
    }
}
