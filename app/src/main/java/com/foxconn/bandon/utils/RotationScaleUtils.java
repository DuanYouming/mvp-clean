package com.foxconn.bandon.utils;

public class RotationScaleUtils {

    public static double optimalWidth(float angle, float scale, int width, int height) {
        return Math.abs(width * scale * Math.cos(Math.toRadians(angle))) + Math.abs(height * scale * Math.cos(Math.toRadians(90 - angle)));
    }

    public static double optimalHeight(float angle, float scale, int width, int height) {
        return Math.abs(width * scale * Math.sin(Math.toRadians(angle))) + Math.abs(height * scale * Math.sin(Math.toRadians(90 - angle)));
    }
}
