package com.foxconn.bandon.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.foxconn.bandon.R;


public class EmotionItemDecoration extends RecyclerView.ItemDecoration {
    private int mBorderSize;
    private final Drawable mDivider;

    public EmotionItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.bg_insert_emotion_divider);
        mBorderSize = context.getResources().getDimensionPixelSize(R.dimen.emotion_item_border);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontalDivider(c, parent);
        drawVerticalDivider(c, parent);
    }

    private void drawVerticalDivider(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int top = child.getTop();
            int bottom = child.getBottom() + mBorderSize;
            int left;
            int right;

            if (i / spanCount == 0) {
                top = child.getTop() - mBorderSize;
            }

            if ((i % spanCount) == 0) {
                left = child.getLeft() - mBorderSize;
                right = child.getLeft();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);


            }
            left = child.getRight();
            right = left + mBorderSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int left = child.getLeft() - mBorderSize;
            int right = child.getRight() + mBorderSize;
            int top;
            int bottom;
            if (i % spanCount == 0) {
                left = child.getLeft();
            }
            if ((i / spanCount) == 0) {
                top = child.getTop()-mBorderSize;
                bottom = top + mBorderSize;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            top = child.getBottom();
            bottom = top + mBorderSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }
}
