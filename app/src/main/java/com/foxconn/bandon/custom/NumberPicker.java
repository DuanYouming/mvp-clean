package com.foxconn.bandon.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;

public class NumberPicker extends FrameLayout {

    public interface OnValueChangedListener {
        void onValueChange(int newVal);
    }

    private int mTextColor = Color.WHITE;
    private int mTextSize = 36;
    private int mMinTextHeight = 60;
    private int mFadingEdgeHeight = 88;
    private int mNumDigits = 2;

    private int mMin = 0;
    private int mMax = 10;
    private int mValue = 0;

    private RecyclerView mList;
    private LinearLayoutManager mLayoutManager;
    private Adapter mAdapter;
    private OnValueChangedListener mListener;
    private final int[] mOutLocation = new int[2];
    private boolean mPrepareAlign;

    public NumberPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    /**
     * Setup
     */
    private void setup() {
        mList = new RecyclerView(getContext());
        mList.setHasFixedSize(true);
        mList.setOverScrollMode(OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(mLayoutManager);
        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    mPrepareAlign = true;
                } else if (newState == 0 && mPrepareAlign) {
                    mPrepareAlign = false;
                    align();
                }
            }
        });

        mAdapter = new Adapter();
        mList.setAdapter(mAdapter);
        FrameLayout.LayoutParams listLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mList, listLp);

        ImageView fadingEdgeTop = new ImageView(getContext());
        fadingEdgeTop.setImageResource(R.drawable.number_picker_fading_edge);
        fadingEdgeTop.setRotation(180);
        FrameLayout.LayoutParams fadingEdgeTopLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mFadingEdgeHeight);
        addView(fadingEdgeTop, fadingEdgeTopLp);

        ImageView fadingEdgeBottom = new ImageView(getContext());
        fadingEdgeBottom.setImageResource(R.drawable.number_picker_fading_edge);
        FrameLayout.LayoutParams fadingEdgeBottomLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mFadingEdgeHeight);
        fadingEdgeBottomLp.gravity = Gravity.BOTTOM;
        addView(fadingEdgeBottom, fadingEdgeBottomLp);
    }

    private int getRealItemCount() {
        if (mMax > mMin) {
            return mMax - mMin + 1;
        } else {
            return mMax;
        }
    }


    private void align() {
        getLocationOnScreen(mOutLocation);
        final int centerY = mOutLocation[1] + getHeight() / 2;

        int theMostMiddleViewHolderCenterY = 0;
        int minDiff = Integer.MAX_VALUE;
        int diff;

        final int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
        final int lastVisiblePos = mLayoutManager.findLastVisibleItemPosition();
        Adapter.ViewHolder theMostMiddleViewHolder = (Adapter.ViewHolder) mList.findViewHolderForLayoutPosition(firstVisiblePos);

        for (int pos = firstVisiblePos; pos <= lastVisiblePos; pos++) {
            Adapter.ViewHolder viewHolder = (Adapter.ViewHolder) mList.findViewHolderForLayoutPosition(pos);
            viewHolder.textView.getLocationOnScreen(mOutLocation);
            int viewHolderCenterY = mOutLocation[1] + viewHolder.textView.getHeight() / 2;
            diff = Math.abs(centerY - viewHolderCenterY);

            if (diff < minDiff) {
                theMostMiddleViewHolderCenterY = viewHolderCenterY;
                minDiff = diff;
                theMostMiddleViewHolder = viewHolder;
            }
        }

        diff = -(centerY - theMostMiddleViewHolderCenterY);

        mValue = Integer.parseInt(theMostMiddleViewHolder.textView.getText().toString());

        if (mListener != null) {
            mListener.onValueChange(mValue);
        }

        mList.smoothScrollBy(0, diff);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setTextColor(mTextColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            textView.setMinHeight(mMinTextHeight);
            textView.setGravity(Gravity.CENTER);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final int realPosition = position % getRealItemCount();

            StringBuilder value = new StringBuilder(String.valueOf(realPosition));
            while (mNumDigits > value.length()) {
                value.insert(0, "0");
            }

            holder.textView.setText(value.toString());
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

    public void setNumDigits(int digits) {
        mNumDigits = digits;

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setFadingEdgeHeight(int height) {
        mFadingEdgeHeight = height;
        requestLayout();
    }


    public void setTextSizePx(int px) {
        mTextSize = px;
        requestLayout();
    }

    public void setMinTextHeight(int height) {
        mMinTextHeight = height;
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        requestLayout();
    }

    public void setValues(int min, int max, int value) {
        mMin = min;
        mMax = max;
        setValue(value);
    }

    public void setValue(final int value) {
        mValue = getRealItemCount() * 100 + value;
        post(new Runnable() {
            @Override
            public void run() {
                int offsetTop = (getHeight() - mMinTextHeight) / 2;
                mLayoutManager.scrollToPositionWithOffset(mValue, offsetTop);
            }
        });
    }

    public int getValue() {
        return mValue % getRealItemCount();
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        mListener = listener;
    }

}
