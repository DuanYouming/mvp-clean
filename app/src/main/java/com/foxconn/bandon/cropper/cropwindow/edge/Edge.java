package com.foxconn.bandon.cropper.cropwindow.edge;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.foxconn.bandon.cropper.util.AspectRatioUtil;

public enum Edge {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    public static final int MIN_CROP_LENGTH_PX = 40;

    private float mCoordinate;

    public void setCoordinate(float coordinate) {
        mCoordinate = coordinate;
    }

    public void offset(float distance) {
        mCoordinate += distance;
    }

    public float getCoordinate() {
        return mCoordinate;
    }

    public void adjustCoordinate(float x, float y, @NonNull RectF imageRect, float imageSnapRadius, float aspectRatio) {

        switch (this) {
            case LEFT:
                mCoordinate = adjustLeft(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case TOP:
                mCoordinate = adjustTop(y, imageRect, imageSnapRadius, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = adjustRight(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = adjustBottom(y, imageRect, imageSnapRadius, aspectRatio);
                break;
        }
    }


    /**
     * Adjusts this Edge position such that the resulting window will have the given aspect ratio.
     *
     * @param aspectRatio the aspect ratio to achieve
     */
    public void adjustCoordinate(float aspectRatio) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        switch (this) {
            case LEFT:
                mCoordinate = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);
                break;
            case TOP:
                mCoordinate = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);
                break;
        }
    }

    public boolean isNewRectangleOutOfBounds(@NonNull Edge edge, @NonNull RectF imageRect, float aspectRatio) {

        final float offset = edge.snapOffset(imageRect);

        switch (this) {

            case LEFT:

                if (edge.equals(Edge.TOP)) {

                    final float top = imageRect.top;
                    final float bottom = Edge.BOTTOM.getCoordinate() - offset;
                    final float right = Edge.RIGHT.getCoordinate();
                    final float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.BOTTOM)) {

                    final float bottom = imageRect.bottom;
                    final float top = Edge.TOP.getCoordinate() - offset;
                    final float right = Edge.RIGHT.getCoordinate();
                    final float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;

            case TOP:

                if (edge.equals(Edge.LEFT)) {

                    final float left = imageRect.left;
                    final float right = Edge.RIGHT.getCoordinate() - offset;
                    final float bottom = Edge.BOTTOM.getCoordinate();
                    final float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.RIGHT)) {

                    final float right = imageRect.right;
                    final float left = Edge.LEFT.getCoordinate() - offset;
                    final float bottom = Edge.BOTTOM.getCoordinate();
                    final float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;

            case RIGHT:

                if (edge.equals(Edge.TOP)) {

                    final float top = imageRect.top;
                    final float bottom = Edge.BOTTOM.getCoordinate() - offset;
                    final float left = Edge.LEFT.getCoordinate();
                    final float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.BOTTOM)) {

                    final float bottom = imageRect.bottom;
                    final float top = Edge.TOP.getCoordinate() - offset;
                    final float left = Edge.LEFT.getCoordinate();
                    final float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;

            case BOTTOM:

                if (edge.equals(Edge.LEFT)) {

                    final float left = imageRect.left;
                    final float right = Edge.RIGHT.getCoordinate() - offset;
                    final float top = Edge.TOP.getCoordinate();
                    final float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.RIGHT)) {

                    final float right = imageRect.right;
                    final float left = Edge.LEFT.getCoordinate() - offset;
                    final float top = Edge.TOP.getCoordinate();
                    final float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                }
                break;
        }
        return true;
    }

    private boolean isOutOfBounds(float top, float left, float bottom, float right, @NonNull RectF imageRect) {
        return (top < imageRect.top || left < imageRect.left || bottom > imageRect.bottom || right > imageRect.right);
    }

    public float snapToRect(@NonNull RectF imageRect) {

        final float oldCoordinate = mCoordinate;

        switch (this) {
            case LEFT:
                mCoordinate = imageRect.left;
                break;
            case TOP:
                mCoordinate = imageRect.top;
                break;
            case RIGHT:
                mCoordinate = imageRect.right;
                break;
            case BOTTOM:
                mCoordinate = imageRect.bottom;
                break;
        }

        return mCoordinate - oldCoordinate;
    }

    public float snapOffset(@NonNull RectF imageRect) {

        final float oldCoordinate = mCoordinate;
        final float newCoordinate;

        switch (this) {
            case LEFT:
                newCoordinate = imageRect.left;
                break;
            case TOP:
                newCoordinate = imageRect.top;
                break;
            case RIGHT:
                newCoordinate = imageRect.right;
                break;
            default: // BOTTOM
                newCoordinate = imageRect.bottom;
                break;
        }

        return newCoordinate - oldCoordinate;
    }

    public static float getWidth() {
        return Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate();
    }

    public static float getHeight() {
        return Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate();
    }

    public boolean isOutsideMargin(@NonNull RectF rect, float margin) {

        final boolean result;

        switch (this) {
            case LEFT:
                result = mCoordinate - rect.left < margin;
                break;
            case TOP:
                result = mCoordinate - rect.top < margin;
                break;
            case RIGHT:
                result = rect.right - mCoordinate < margin;
                break;
            default: // BOTTOM
                result = rect.bottom - mCoordinate < margin;
                break;
        }
        return result;
    }

    private static float adjustLeft(float x, @NonNull RectF imageRect, float imageSnapRadius, float aspectRatio) {

        final float resultX;

        if (x - imageRect.left < imageSnapRadius) {

            resultX = imageRect.left;

        } else {

            // Select the minimum of the three possible values to use
            float resultXHoriz = Float.POSITIVE_INFINITY;
            float resultXVert = Float.POSITIVE_INFINITY;

            // Checks if the window is too small horizontally
            if (x >= Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX) {
                resultXHoriz = Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX;
            }
            // Checks if the window is too small vertically
            if (((Edge.RIGHT.getCoordinate() - x) / aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultXVert = Edge.RIGHT.getCoordinate() - (MIN_CROP_LENGTH_PX * aspectRatio);
            }
            resultX = Math.min(x, Math.min(resultXHoriz, resultXVert));
        }
        return resultX;
    }

    private static float adjustRight(float x, @NonNull RectF imageRect, float imageSnapRadius, float aspectRatio) {

        final float resultX;

        // If close to the edge...
        if (imageRect.right - x < imageSnapRadius) {

            resultX = imageRect.right;

        } else {

            // Select the maximum of the three possible values to use
            float resultXHoriz = Float.NEGATIVE_INFINITY;
            float resultXVert = Float.NEGATIVE_INFINITY;

            // Checks if the window is too small horizontally
            if (x <= Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX) {
                resultXHoriz = Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX;
            }
            // Checks if the window is too small vertically
            if (((x - Edge.LEFT.getCoordinate()) / aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultXVert = Edge.LEFT.getCoordinate() + (MIN_CROP_LENGTH_PX * aspectRatio);
            }
            resultX = Math.max(x, Math.max(resultXHoriz, resultXVert));
        }
        return resultX;
    }

    private static float adjustTop(float y, @NonNull RectF imageRect, float imageSnapRadius, float aspectRatio) {

        final float resultY;

        if (y - imageRect.top < imageSnapRadius) {

            resultY = imageRect.top;

        } else {

            // Select the minimum of the three possible values to use
            float resultYVert = Float.POSITIVE_INFINITY;
            float resultYHoriz = Float.POSITIVE_INFINITY;

            // Checks if the window is too small vertically
            if (y >= Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX) {
                resultYHoriz = Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX;
            }

            // Checks if the window is too small horizontally
            if (((Edge.BOTTOM.getCoordinate() - y) * aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultYVert = Edge.BOTTOM.getCoordinate() - (MIN_CROP_LENGTH_PX / aspectRatio);
            }

            resultY = Math.min(y, Math.min(resultYHoriz, resultYVert));
        }
        return resultY;
    }

    private static float adjustBottom(float y, @NonNull RectF imageRect, float imageSnapRadius, float aspectRatio) {

        final float resultY;

        if (imageRect.bottom - y < imageSnapRadius) {

            resultY = imageRect.bottom;

        } else {

            // Select the maximum of the three possible values to use
            float resultYVert = Float.NEGATIVE_INFINITY;
            float resultYHoriz = Float.NEGATIVE_INFINITY;

            // Checks if the window is too small vertically
            if (y <= Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_PX) {
                resultYVert = Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_PX;
            }
            // Checks if the window is too small horizontally
            if (((y - Edge.TOP.getCoordinate()) * aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultYHoriz = Edge.TOP.getCoordinate() + (MIN_CROP_LENGTH_PX / aspectRatio);
            }
            resultY = Math.max(y, Math.max(resultYHoriz, resultYVert));
        }
        return resultY;
    }
}
