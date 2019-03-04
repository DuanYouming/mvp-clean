package com.foxconn.bandon.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

public class StatusSwitch extends SwitchCompat {

    private GradientDrawable mTrackGradientDrawable;
    private GradientDrawable mThumbGradientDrawable;

    public StatusSwitch(Context context) {
        super(context);

        //set thumb no shadow
        setThumbTintMode(PorterDuff.Mode.SRC_IN);

        //load thumb layout
        mThumbGradientDrawable = createThumbGradientDrawable();
        setThumbDrawable(mThumbGradientDrawable);

        //load track layout
        mTrackGradientDrawable = createTrackGradientDrawable();
        setTrackDrawable(mTrackGradientDrawable);
    }

    public StatusSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setThumbTintMode(PorterDuff.Mode.SRC_IN);
        mThumbGradientDrawable = createThumbGradientDrawable();
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        int size = (int) Math.round(Double.valueOf(height.replaceAll("[^\\d.]", "")));
        mThumbGradientDrawable.setSize(size, size);
        setThumbDrawable(mThumbGradientDrawable);

        mTrackGradientDrawable = createTrackGradientDrawable();
        setTrackDrawable(mTrackGradientDrawable);
    }

    //Define track drawable
    private GradientDrawable createThumbGradientDrawable() {
        GradientDrawable thumbDrawable = new GradientDrawable();
        thumbDrawable.setColor(Color.parseColor("#696969"));
        thumbDrawable.setShape(GradientDrawable.OVAL);
        thumbDrawable.setSize(10, 10);
        return thumbDrawable;
    }


    //Define track drawable
    private GradientDrawable createTrackGradientDrawable() {
        GradientDrawable trackDrawable = new GradientDrawable();
        trackDrawable.setColor(Color.parseColor("#EBEBEB"));
        trackDrawable.setCornerRadius(66f);
        return trackDrawable;
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (mTrackGradientDrawable != null) {
            if (checked) {
                mThumbGradientDrawable.setColor(Color.parseColor("#4BB88C"));
            } else {
                mThumbGradientDrawable.setColor(Color.parseColor("#696969"));
            }
        }
    }


}
