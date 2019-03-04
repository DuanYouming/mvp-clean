

package com.foxconn.bandon.cropper.cropwindow.handle;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import com.foxconn.bandon.cropper.cropwindow.edge.Edge;
import com.foxconn.bandon.cropper.cropwindow.edge.EdgePair;
import com.foxconn.bandon.cropper.util.AspectRatioUtil;


abstract class HandleHelper {
    private static final float UNFIXED_ASPECT_RATIO_CONSTANT = 1;
    private Edge mHorizontalEdge;
    private Edge mVerticalEdge;
    private EdgePair mActiveEdges;

    HandleHelper(Edge horizontalEdge, Edge verticalEdge) {
        mHorizontalEdge = horizontalEdge;
        mVerticalEdge = verticalEdge;
        mActiveEdges = new EdgePair(mHorizontalEdge, mVerticalEdge);
    }

    void updateCropWindow(float x, float y, @NonNull RectF imageRect, float snapRadius) {

        final EdgePair activeEdges = getActiveEdges();
        final Edge primaryEdge = activeEdges.primary;
        final Edge secondaryEdge = activeEdges.secondary;

        if (primaryEdge != null) {
            primaryEdge.adjustCoordinate(x, y, imageRect, snapRadius, UNFIXED_ASPECT_RATIO_CONSTANT);
        }

        if (secondaryEdge != null) {
            secondaryEdge.adjustCoordinate(x, y, imageRect, snapRadius, UNFIXED_ASPECT_RATIO_CONSTANT);
        }
    }

    abstract void updateCropWindow(float x, float y, float targetAspectRatio, @NonNull RectF imageRect, float snapRadius);

    EdgePair getActiveEdges() {
        return mActiveEdges;
    }

    EdgePair getActiveEdges(float x, float y, float targetAspectRatio) {

        // Calculate the aspect ratio if this handle were dragged to the given x-y coordinate.
        final float potentialAspectRatio = getAspectRatio(x, y);

        // If the touched point is wider than the aspect ratio, then x is the determining side. Else, y is the determining side.
        if (potentialAspectRatio > targetAspectRatio) {
            mActiveEdges.primary = mVerticalEdge;
            mActiveEdges.secondary = mHorizontalEdge;
        } else {
            mActiveEdges.primary = mHorizontalEdge;
            mActiveEdges.secondary = mVerticalEdge;
        }
        return mActiveEdges;
    }

    private float getAspectRatio(float x, float y) {

        // Replace the active edge coordinate with the given touch coordinate.
        final float left = (mVerticalEdge == Edge.LEFT) ? x : Edge.LEFT.getCoordinate();
        final float top = (mHorizontalEdge == Edge.TOP) ? y : Edge.TOP.getCoordinate();
        final float right = (mVerticalEdge == Edge.RIGHT) ? x : Edge.RIGHT.getCoordinate();
        final float bottom = (mHorizontalEdge == Edge.BOTTOM) ? y : Edge.BOTTOM.getCoordinate();

        return AspectRatioUtil.calculateAspectRatio(left, top, right, bottom);
    }
}
