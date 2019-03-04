package com.foxconn.bandon.label.detail.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


public class QuantityOpt {
    private Context mContext;
    private Boolean mIncreaseBtnDown = false;
    private Boolean mDecreaseBtnDown = false;
    private TextView mItemQuantityTxt;
    private ImageView mDecreaseBtn;
    private ImageView mIncreaseBtn;
    private Callback mCallback;

    QuantityOpt(Context context, ImageView increaseBtn, ImageView decreaseBtn, TextView itemQuantityTxt) {
        this.mContext = context;
        this.mIncreaseBtn = increaseBtn;
        this.mDecreaseBtn = decreaseBtn;
        this.mItemQuantityTxt = itemQuantityTxt;
        init();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void update(int number);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        final GestureDetector increaseBtnGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                mIncreaseBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIncreaseBtnDown) {
                            addOne();
                            mIncreaseBtn.postDelayed(this, 200);
                        }
                    }
                });
            }

            @Override
            public boolean onDown(MotionEvent e) {
                mIncreaseBtnDown = true;
                addOne();
                return true;
            }
        });

        mIncreaseBtn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIncreaseBtnDown = false;
                return true;
            } else {
                return increaseBtnGestureDetector.onTouchEvent(event);
            }
        });


        final GestureDetector decreaseBtnGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                mDecreaseBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mDecreaseBtnDown) {
                            minusOne();
                            mDecreaseBtn.postDelayed(this, 200);
                        }
                    }
                });
            }

            @Override
            public boolean onDown(MotionEvent e) {
                mDecreaseBtnDown = true;
                minusOne();
                return true;
            }

        });

        mDecreaseBtn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mDecreaseBtnDown = false;
                return true;
            } else {
                return decreaseBtnGestureDetector.onTouchEvent(event);
            }
        });

    }


    private void addOne() {
        int addOne = Integer.valueOf(mItemQuantityTxt.getText().toString()) + 1;
        if (addOne < 1000 && !mDecreaseBtnDown) {
            mItemQuantityTxt.setText(String.valueOf(addOne));
            if (mCallback != null) {
                mCallback.update(addOne);
            }
        }
    }

    private void minusOne() {
        int minusOne = Integer.valueOf(mItemQuantityTxt.getText().toString()) - 1;
        if (minusOne >= 1 && !mIncreaseBtnDown) {
            mItemQuantityTxt.setText(String.valueOf(minusOne));
            if (mCallback != null) {
                mCallback.update(minusOne);
            }
        }
    }
}
