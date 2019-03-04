package com.foxconn.bandon.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SeparateGridItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = SeparateGridItemDecoration.class.getSimpleName();


    public SeparateGridItemDecoration(Context context) {
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        draw(c, parent);
    }


    private void draw(Canvas c, RecyclerView parent) {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int parentWidth = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();

        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }

        int childMaxWidth = parentWidth / spanCount;
        int childWidth = view.getLayoutParams().width;
        int childPosition = parent.getChildAdapterPosition(view);
        int space = childMaxWidth - childWidth;
        switch (childPosition % spanCount) {
            case 0:
                outRect.set(0, 0, space, 0);
                break;
            case 1:
                outRect.set(space / 2, 0, space / 2, 0);
                break;
            case 2:
                outRect.set(space, 0, 0, 0);
                break;
        }
    }

}
