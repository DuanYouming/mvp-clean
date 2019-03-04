package com.foxconn.bandon.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SingleOptionThumb extends android.support.v7.widget.AppCompatImageView {

    private int mDragLeft = SingleOptionViewGroup.PADDING_LEFT;

    public SingleOptionThumb(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLeft(int left) {
        if (left > ((View) getParent()).getWidth() - getWidth() - SingleOptionViewGroup.PADDING_LEFT) {
            left = ((View) getParent()).getWidth() - getWidth() - SingleOptionViewGroup.PADDING_LEFT;
        }

        if (left < SingleOptionViewGroup.PADDING_LEFT) {
            left = SingleOptionViewGroup.PADDING_LEFT;
        }

        mDragLeft = left;
    }


    public int getDragLeft() {
        return mDragLeft;
    }

}