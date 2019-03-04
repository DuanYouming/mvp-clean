package com.foxconn.bandon.cropper.util;

import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.foxconn.bandon.R;
public class PaintUtil {

    public static Paint newBorderPaint(@NonNull Resources resources) {

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(resources.getDimension(R.dimen.border_thickness));
        paint.setColor(resources.getColor(R.color.border));

        return paint;
    }


    public static Paint newGuidelinePaint(@NonNull Resources resources) {

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(resources.getDimension(R.dimen.guideline_thickness));
        paint.setColor(resources.getColor(R.color.guideline));

        return paint;
    }

    public static Paint newSurroundingAreaOverlayPaint(@NonNull Resources resources) {

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(resources.getColor(R.color.surrounding_area));

        return paint;
    }

    public static Paint newCornerPaint(@NonNull Resources resources) {

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(resources.getDimension(R.dimen.corner_thickness));
        paint.setColor(resources.getColor(R.color.corner));
        return paint;
    }
}
