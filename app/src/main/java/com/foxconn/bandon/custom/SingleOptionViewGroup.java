package com.foxconn.bandon.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SingleOptionViewGroup extends FrameLayout implements View.OnTouchListener {

    public static final boolean ENABLE_DRAG = false;
    public static final int PADDING_VERTICAL = 6;
    public static final int PADDING_LEFT = 6;
    public static final int PADDING_RIGHT = 6;
    public static final int OPTION_GAP = 0;
    private int mSelectedOption = 0;
    private int mOptionWidth;
    private int mOptionHeight;
    private SingleOptionThumb mThumb;

    public interface Callback {
        void onOptionSelected(int id);
    }

    public SingleOptionViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mThumb = (SingleOptionThumb) getChildAt(0);
    }

    @Override
    public void onMeasure(int wSpec, int hSpec) {
        super.onMeasure(wSpec, hSpec);
        final int childCount = getChildCount();
        final int optionCount = childCount - 1;
        if (optionCount == 0) {
            return;
        }
        if (mOptionWidth == 0) {
            mOptionHeight = getMeasuredHeight() - PADDING_VERTICAL * 2;
            mOptionWidth = (getMeasuredWidth() - PADDING_LEFT - PADDING_RIGHT - (optionCount - 1) * OPTION_GAP) / optionCount;
        }
        int thumbWSpec = MeasureSpec.makeMeasureSpec(206, MeasureSpec.EXACTLY);
        int thumbHSpec = MeasureSpec.makeMeasureSpec(148, MeasureSpec.EXACTLY);
        int cwSpec = MeasureSpec.makeMeasureSpec(mOptionWidth, MeasureSpec.EXACTLY);
        int chSpec = MeasureSpec.makeMeasureSpec(mOptionHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; i++) {
            if (i == 0) {
                getChildAt(i).measure(thumbWSpec, thumbHSpec);
            } else {
                getChildAt(i).measure(cwSpec, chSpec);
            }
        }
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        final int optionCount = childCount - 1;
        if (optionCount == 0) {
            return;
        }
        int optionViewCount = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof SingleOptionView) {
                int cl = PADDING_LEFT + optionViewCount * (mOptionWidth + OPTION_GAP);
                int ct = PADDING_VERTICAL;
                child.layout(cl, ct, cl + mOptionWidth, ct + mOptionHeight);
                optionViewCount++;
            } else if (child == mThumb) {
                int cl = mThumb.getDragLeft();
                int ct = PADDING_VERTICAL;
                child.layout(cl, ct, cl + mOptionWidth, ct + mOptionHeight);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int left = (int) event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (ENABLE_DRAG) {
                    SingleOptionView optionView = getOptionView(left);
                    if (optionView != null && !optionView.isEnabled()) {
                        return true;
                    }
                    if (optionView != null) {
                        mThumb.setDragLeft(left - mThumb.getWidth() / 2);
                        requestLayout();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int optionId = getOptionId(left);
                int targetLeft = getOptionLeft(optionId);
                SingleOptionView selected = getOptionView(targetLeft);
                if (null != selected && !selected.isEnabled()) {
                    targetLeft = getOptionLeft(mSelectedOption);
                    animateThumbTo(targetLeft);
                    return true;
                }
                setSelectedOption(optionId);
                break;
        }

        return true;
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setSelectedOption(final int optionId) {
        mSelectedOption = optionId;
        if (null != mCallback) {
            mCallback.onOptionSelected(optionId);
        }
        post(() -> {
            SingleOptionView selected = getOptionView(getOptionLeft(optionId));
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof SingleOptionView) {
                    if (child == selected) {
                        ((SingleOptionView) child).onSelected();
                    } else {
                        ((SingleOptionView) child).onDeselected();
                    }
                }
            }

            int targetLeft = getOptionLeft(optionId);

            if (mThumb != null) {
                mThumb.setDragLeft(targetLeft);
            }

            requestLayout();
        });
    }

    public void setOptionDisabled(final int optionId) {
        post(() -> {
            SingleOptionView optionView = getOptionView(getOptionLeft(optionId));
            if (null != optionView) {
                optionView.setEnabled(false);
            }
        });
    }

    public void setOptionEnabled(final int optionId) {
        post(() -> {
            SingleOptionView optionView = getOptionView(getOptionLeft(optionId));
            if (null != optionView) {
                optionView.setEnabled(true);
            }
        });
    }

    public void setOptionText(final int optionId, final int resId) {
        post(() -> {
            SingleOptionView optionView = getOptionView(getOptionLeft(optionId));
            if (null != optionView) {
                optionView.setText(resId);
            }
        });
    }

    public void setOptionTextSize(final int optionId, final int size) {
        post(() -> {
            SingleOptionView optionView = getOptionView(getOptionLeft(optionId));
            if (null != optionView) {
                optionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
        });
    }

    private int getOptionId(int left) {
        final int childCount = getChildCount();
        final int optionCount = childCount - 1;

        if (optionCount <= 1) {
            return 0;
        }

        if (left < 0) {
            return 0;
        }

        if (left >= getWidth()) {
            return optionCount - 1;
        }

        int lBound = PADDING_LEFT;
        int rBound;

        for (int i = 0; i < optionCount; i++) {
            rBound = lBound + mOptionWidth;
            if (left >= lBound && left <= rBound) {
                return i;
            }
            lBound = rBound + OPTION_GAP;
        }

        return 0;
    }

    private int getOptionLeft(int optionId) {
        return PADDING_LEFT + optionId * (mOptionWidth + OPTION_GAP);
    }

    private SingleOptionView getOptionView(int left) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof SingleOptionView) {
                if (left >= child.getLeft() && left < child.getRight()) {
                    return (SingleOptionView) child;
                }
            }
        }
        return null;
    }

    private ValueAnimator mAnimator;

    private void animateThumbTo(int left) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ValueAnimator.ofInt(mThumb.getDragLeft(), left);
        mAnimator.setDuration(200);
        mAnimator.addUpdateListener(animation -> {
            int animatedLeft = (Integer) animation.getAnimatedValue();
            mThumb.setDragLeft(animatedLeft);
            requestLayout();
        });
        mAnimator.start();
    }

}