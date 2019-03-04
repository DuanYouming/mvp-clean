package com.foxconn.bandon.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NumberStringPicker extends FrameLayout {
    private static final String TAG = NumberStringPicker.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private List<String> mRange = new ArrayList<>();
    private int mTextColor = Color.parseColor("#4A4A4A");
    private int mFocusTextColor = Color.parseColor("#000000");
    private int mTextSize;
    private int mFocusTextSize;
    private boolean mUpdateUiWhenFocus;
    private boolean mHideBoundaryLine;
    private int focusUiPosition = 0;
    private int mContentGravity;
    private int mTextViewHeight;
    private int focusPositionNow;
    private boolean isLoop = false;
    private String initValue = "";
    private ScrollListener mScrollListener;
    private MyAdapter myAdapter;
    private int scrollStatus = RecyclerView.SCROLL_STATE_IDLE;

    public NumberStringPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        assert attrs != null;
        String layoutHeight = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        mTextViewHeight = Double.valueOf(layoutHeight.replaceAll("dip", "")).intValue() / 3;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberStringPicker, 0, 0);
        try {
            mTextSize = a.getInteger(R.styleable.NumberStringPicker_textSize, 48);
            mFocusTextSize = a.getInteger(R.styleable.NumberStringPicker_focusTextSize, mTextSize);
            mContentGravity = a.getInteger(R.styleable.NumberStringPicker_contentGravity, Gravity.CENTER_HORIZONTAL);
            mHideBoundaryLine = a.getBoolean(R.styleable.NumberStringPicker_hideBoundaryLine, false);
        } finally {
            a.recycle();
        }
        mUpdateUiWhenFocus = true;
        setup();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void scrollPrev() {
        mRecyclerView.smoothScrollBy(0, -mTextViewHeight);
        if (mScrollListener != null) {
            mScrollListener.scrollComplete(getFocusValue());
        }
    }

    public void scrollNext() {
        mRecyclerView.smoothScrollBy(0, mTextViewHeight);
        if (mScrollListener != null) {
            mScrollListener.scrollComplete(getFocusValue());
        }
    }


    public void setRangeAndLoop(List<String> range, boolean isLoop) {
        this.isLoop = isLoop;
        this.mRange.clear();
        this.mRange.addAll(range);
        if (!isLoop) {
            this.mRange.add(0, "");
            this.mRange.add("");
        }
        myAdapter.notifyDataSetChanged();
    }

    public void setRangeAndLoop(List<String> range, boolean isLoop, String initValue) {
        LogUtils.d(TAG, "initValue:" + initValue);
        if (range.contains(initValue)) {
            this.initValue = initValue;
            setRangeAndLoop(range, isLoop);
            scrollToInitValue();
        } else {
            LogUtils.d(TAG, "init value not in range");
        }
    }

    public interface ScrollListener {
        void onScrolled(Object value);

        void scrollComplete(Object value);
    }

    public void setScrollListener(ScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    public int getScrollStatus() {
        return scrollStatus;
    }

    private void setup() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(layoutManager);
        for (int i = 1; i <= 12; i++) {
            mRange.add(Integer.toString(i));
        }
        myAdapter = new MyAdapter(mRange);
        mRecyclerView.setAdapter(myAdapter);

        addView(mRecyclerView);
        if (!mHideBoundaryLine) {
            setBoundaryLine();
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollDy = 0;
            boolean userScroll = false;
            int throughPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                scrollStatus = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    userScroll = false;
                    if (scrollDy == 0) {
                        if (mScrollListener != null) {
                            mScrollListener.scrollComplete(getFocusValue());
                        }
                        return;
                    }
                    final int offsetDy = Math.abs(scrollDy) % mTextViewHeight;
                    final int direction = scrollDy / Math.abs(scrollDy);
                    if (offsetDy > 0) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        int appendOffset;
                                        if (offsetDy >= mTextViewHeight / 2) {
                                            appendOffset = (mTextViewHeight - offsetDy) * direction;
                                        } else {
                                            appendOffset = -offsetDy * direction;
                                        }
                                        mRecyclerView.smoothScrollBy(0, appendOffset);
                                    }
                                });
                            }
                        }, 100);
                    } else {
                        scrollDy = 0;
                        int firstPosition = layoutManager.findFirstVisibleItemPosition();
                        focusPositionNow = firstPosition + 1;
                        MyAdapter.MyViewHolder viewHolder = (MyAdapter.MyViewHolder) mRecyclerView.findViewHolderForLayoutPosition(focusPositionNow);
                        if (null == viewHolder) {
                            LogUtils.d(TAG, "viewHolder is null");
                            return;
                        }
                        Object focusValue = viewHolder.mTextView.getText();
                        if (mScrollListener != null) {
                            mScrollListener.scrollComplete(focusValue);
                        }
                    }
                } else {
                    userScroll = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollDy += dy;
                throughPosition = focusPositionNow + Math.round(scrollDy / mTextViewHeight);
                if (userScroll) {
                    if (mScrollListener != null) {
                        Object throughValue = mRange.get(throughPosition % mRange.size());
                        mScrollListener.onScrolled(throughValue);
                    }
                }

                updateFocusUi(throughPosition);
            }

            private void updateFocusUi(final int throughPosition) {
                if (!mUpdateUiWhenFocus) {
                    return;
                }
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        focusUiPosition = throughPosition;
                        myAdapter.notifyItemRangeChanged(throughPosition - 1, 4);
                    }
                });
            }
        });
    }


    private void scrollToInitValue() {
        if (isLoop) {
            focusPositionNow = (Integer.MAX_VALUE / 2) + 1;
            while (!initValue.equals(mRange.get(focusPositionNow % mRange.size()))) {
                focusPositionNow++;
            }
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(focusPositionNow - 1, 0);
        } else {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
            focusPositionNow = 1;

            while (!initValue.equals(mRange.get(focusPositionNow % mRange.size()))) {
                focusPositionNow++;
            }
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(focusPositionNow - 1, 0);
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<String> mValues;

        MyAdapter(List<String> values) {
            this.mValues = values;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            MyViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getContext());
            textView.setTextSize(mTextSize);
            textView.setTextColor(mTextColor);
            textView.setMinHeight(mTextViewHeight);
            textView.setGravity(mContentGravity | Gravity.CENTER_VERTICAL);
            textView.setLayoutParams(flp);
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (!isLoop) {
                if (position == 0 || position == mValues.size() - 1) {
                    holder.mTextView.setVisibility(INVISIBLE);
                } else {
                    holder.mTextView.setVisibility(VISIBLE);
                }
            } else {
                holder.mTextView.setVisibility(VISIBLE);
            }

            if (mUpdateUiWhenFocus) {
                if (position == focusUiPosition) {
                    holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFocusTextSize);
                    holder.mTextView.setTextColor(mFocusTextColor);
                    holder.mTextView.setTypeface(null, Typeface.BOLD);
                } else {
                    holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                    holder.mTextView.setTextColor(mTextColor);
                    holder.mTextView.setTypeface(null, Typeface.NORMAL);
                }
            }
            holder.mTextView.setText(String.valueOf(mValues.get(position % mValues.size())));
            holder.mTextView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if (isLoop) {
                return Integer.MAX_VALUE;
            } else {
                return null == mValues ? 0 : mValues.size();
            }
        }
    }


    private void setBoundaryLine() {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundColor(Color.parseColor("#979797"));
        LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        flp.topMargin = mTextViewHeight;
        imageView.setLayoutParams(flp);
        addView(imageView);
        imageView = new ImageView(getContext());
        imageView.setBackgroundColor(Color.parseColor("#979797"));
        flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        flp.topMargin = mTextViewHeight * 2;
        imageView.setLayoutParams(flp);
        addView(imageView);
    }

    public String getFocusValue() {
        return String.valueOf(mRange.get(focusPositionNow % mRange.size()));
    }

}
