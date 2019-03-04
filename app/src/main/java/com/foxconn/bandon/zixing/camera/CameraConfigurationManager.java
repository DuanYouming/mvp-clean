package com.foxconn.bandon.zixing.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;


import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.foxconn.bandon.utils.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";

    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    public void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(null!=manager) {
            Display display = manager.getDefaultDisplay();
            Point theScreenResolution = new Point();
            theScreenResolution = getDisplaySize(display);

            screenResolution = theScreenResolution;
            Log.i(TAG, "Screen resolution: " + screenResolution);

            Point screenResolutionForCamera = new Point();
            screenResolutionForCamera.x = screenResolution.x;
            screenResolutionForCamera.y = screenResolution.y;

            if (screenResolution.x < screenResolution.y) {
                screenResolutionForCamera.x = screenResolution.y;
                screenResolutionForCamera.y = screenResolution.x;
            }

            cameraResolution = findBestPreviewSizeValue(parameters, screenResolutionForCamera);
            LogUtils.i(TAG, "Camera resolution x: " + cameraResolution.x);
            LogUtils.i(TAG, "Camera resolution y: " + cameraResolution.y);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private Point getDisplaySize(final Display display) {
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    public void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters == null) {
            LogUtils.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        LogUtils.i(TAG, "Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            LogUtils.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }

        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        camera.setParameters(parameters);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize
                .height)) {
            Log.w(TAG, "Camera said it supported preview size " + cameraResolution.x + 'x' +
                    cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x'
                    + afterSize.height);
            cameraResolution.x = afterSize.width;
            cameraResolution.y = afterSize.height;
        }

        camera.setDisplayOrientation(270);
    }

    public Point getCameraResolution() {
        return cameraResolution;
    }

    public Point getScreenResolution() {
        return screenResolution;
    }

    private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
        supportedPreviewSizes.sort((a, b) -> {
            int aPixels = a.height * a.width;
            int bPixels = b.height * b.width;
            return Integer.compare(bPixels, aPixels);
        });

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width).append('x').append
                        (supportedPreviewSize.height).append(' ');
            }
            LogUtils.i(TAG, "Supported preview sizes: " + previewSizesString);
        }

        double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;

            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                LogUtils.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            LogUtils.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        Camera.Size defaultPreview = parameters.getPreviewSize();
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        LogUtils.i(TAG, "No suitable preview sizes, using default: " + defaultSize);

        return defaultSize;
    }
}
