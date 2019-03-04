package com.foxconn.bandon.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.foxconn.bandon.R;
public class SingleOptionView extends android.support.v7.widget.AppCompatTextView {

    private int mTextColorNormal;
    private int mTextColorSelected;
    private int mTextColorDisabled;

    public SingleOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTextColorNormal = ContextCompat.getColor(context, R.color.single_option_view_text_normal);
        mTextColorDisabled = ContextCompat.getColor(context, R.color.single_option_view_text_disabled);
        mTextColorSelected = getCurrentTextColor();

        if (isEnabled()) {
            setTextColor(mTextColorNormal);
        } else {
            setTextColor(mTextColorDisabled);
        }
    }

    /**
     * Invoked when this option is selected
     */
    public void onSelected() {
        if (isEnabled()) {
            setTextColor(mTextColorSelected);
        } else {
            setTextColor(mTextColorDisabled);
        }
    }

    /**
     * Invoked when this option is deselected
     */
    public void onDeselected() {
        if (isEnabled()) {
            setTextColor(mTextColorNormal);
        } else {
            setTextColor(mTextColorDisabled);
        }
    }

}