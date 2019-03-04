package com.foxconn.bandon.cropper.cropwindow.handle;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import com.foxconn.bandon.cropper.cropwindow.edge.Edge;

public enum Handle {

    TOP_LEFT(new CornerHandleHelper(Edge.TOP, Edge.LEFT)),
    TOP_RIGHT(new CornerHandleHelper(Edge.TOP, Edge.RIGHT)),
    BOTTOM_LEFT(new CornerHandleHelper(Edge.BOTTOM, Edge.LEFT)),
    BOTTOM_RIGHT(new CornerHandleHelper(Edge.BOTTOM, Edge.RIGHT)),
    LEFT(new VerticalHandleHelper(Edge.LEFT)),
    TOP(new HorizontalHandleHelper(Edge.TOP)),
    RIGHT(new VerticalHandleHelper(Edge.RIGHT)),
    BOTTOM(new HorizontalHandleHelper(Edge.BOTTOM)),
    CENTER(new CenterHandleHelper());

    private HandleHelper mHelper;

    Handle(HandleHelper helper) {
        mHelper = helper;
    }

    public void updateCropWindow(float x, float y, @NonNull RectF imageRect, float snapRadius) {
        mHelper.updateCropWindow(x, y, imageRect, snapRadius);
    }

    public void updateCropWindow(float x, float y, float targetAspectRatio, @NonNull RectF imageRect, float snapRadius) {
        mHelper.updateCropWindow(x, y, targetAspectRatio, imageRect, snapRadius);
    }
}
