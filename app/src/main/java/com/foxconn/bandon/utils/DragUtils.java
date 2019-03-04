package com.foxconn.bandon.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


public class DragUtils {
    private static final String TAG = DragUtils.class.getSimpleName();

    public static class Callback {

        public void onDragMove(int x, int y) {
        }

        public void onFingerMove(int x, int y) {
        }

        public void onDragEnd() {
        }

        public void onClick() {
        }

        public void onLongPress() {
        }

        public void onHold() {
        }

        public void onRotationAndScale(float angle, float scale) {
        }

        public void onQuantifyLevel(int level) {

        }

    }


    public static void enableDrag(final View view, final View container, final int quantifyX, final Callback callback) {
        container.post(new Runnable() {
            @Override
            public void run() {
                enableDrag(view, 0, 0, container.getWidth(), container.getHeight(),
                        quantifyX, callback);
            }
        });
    }

    private static void enableDrag(final View view, final int l, final int t, final int r, final int b, final int quantifyX,
                                   final Callback callback) {

        view.setOnTouchListener(new View.OnTouchListener() {

            private boolean mLongPressed;
            private Runnable mLongPressRunnable;
            private boolean mHold;
            private Runnable mHoldRunnable;
            private float mTouchDownX;
            private float mTouchDownY;
            private final int[] mOutLocation = new int[2];
            private int mOriginX;
            private int mOriginY;
            private boolean mMoved;
            private static final int INVALID_POINTER_ID = -1;
            private float fX, fY, sX, sY;
            private int ptrID1 = INVALID_POINTER_ID, ptrID2 = INVALID_POINTER_ID;
            private float mAngle, mScale;

            private int mQLevel = 0;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                final float x = event.getRawX();
                final float y = event.getRawY();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mMoved = false;
                        mLongPressed = false;
                        mHold = false;

                        if (mLongPressRunnable != null) {
                            view.getHandler().removeCallbacks(mLongPressRunnable);
                        }

                        mLongPressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mLongPressed = true;

                                if (callback != null) {
                                    callback.onLongPress();
                                }

                                mLongPressRunnable = null;
                            }
                        };

                        view.getHandler().postDelayed(mLongPressRunnable,
                                ViewConfiguration.getLongPressTimeout());

                        mHoldRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mHold = true;

                                if (callback != null) {
                                    callback.onHold();
                                }
                            }
                        };

                        view.getHandler().postDelayed(mHoldRunnable,
                                ViewConfiguration.getLongPressTimeout());

                        float r = view.getRotation();
                        view.setRotation(0);
                        view.getLocationOnScreen(mOutLocation);
                        view.setRotation(r);

                        mOriginX = mOutLocation[0];
                        mOriginY = mOutLocation[1];
                        mTouchDownX = x;
                        mTouchDownY = y;


                        ptrID1 = event.getPointerId(event.getActionIndex());
                        LogUtils.d(TAG, "ACTION_DOWN");

                        view.bringToFront();

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        ptrID2 = event.getPointerId(event.getActionIndex());
                        sX = event.getX(event.findPointerIndex(ptrID1));
                        sY = event.getY(event.findPointerIndex(ptrID1));
                        fX = event.getX(event.findPointerIndex(ptrID2));
                        fY = event.getY(event.findPointerIndex(ptrID2));
                        LogUtils.d(TAG, "ACTION_POINTER_DOWN");

                        break;

                    case MotionEvent.ACTION_MOVE:
                        mMoved = true;

                        if (mLongPressRunnable != null) {
                            view.getHandler().removeCallbacks(mLongPressRunnable);
                        }

                        if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                            float nfX, nfY, nsX, nsY;
                            nsX = event.getX(event.findPointerIndex(ptrID1));
                            nsY = event.getY(event.findPointerIndex(ptrID1));
                            nfX = event.getX(event.findPointerIndex(ptrID2));
                            nfY = event.getY(event.findPointerIndex(ptrID2));

                            mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);
                            mScale = scale(fX, fY, sX, sY, nfX, nfY, nsX, nsY);
                            //LogUtils.d(TAG, "ROTATION:" + mAngle);
                            //LogUtils.d(TAG, "SCALE:" + mScale);
                            if (callback != null) {
                                callback.onRotationAndScale(mAngle, mScale);
                            }

                        }

                        if (quantifyX > 0) {
                            callback.onQuantifyLevel(mQLevel);
                        }
                        move(x, y);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ptrID1 = INVALID_POINTER_ID;
                        ptrID2 = INVALID_POINTER_ID;
                    case MotionEvent.ACTION_UP:

                        if (mLongPressRunnable != null) {
                            view.getHandler().removeCallbacks(mLongPressRunnable);
                        }

                        if (mHoldRunnable != null) {
                            view.getHandler().removeCallbacks(mHoldRunnable);
                        }

                        if (callback != null) {
                            callback.onDragEnd();

                            if (!mMoved && !mLongPressed && !mHold) {
                                callback.onClick();
                            }
                        }

                        ptrID1 = INVALID_POINTER_ID;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        LogUtils.d(TAG, "ACTION_POINTER_UP");
                        ptrID2 = INVALID_POINTER_ID;
                        break;
                }

                return true;
            }

            private void move(float x, float y) {
                //LogUtils.d(TAG, "x:" + x + ",y:" + y);
                if (callback != null) {
                    callback.onFingerMove((int) x, (int) y);
                }

                ((View) view.getParent()).getLocationOnScreen(mOutLocation);
                x -= mOutLocation[0];
                y -= mOutLocation[1];
                x = mOriginX + x - mTouchDownX;
                y = mOriginY + y - mTouchDownY;
                if (x < l) {
                    x = l;
                } else if (x > r - view.getWidth()) {
                    x = r - view.getWidth();
                }
                if (y < t) {
                    y = t;
                } else if (y > b - view.getHeight()) {
                    y = b - view.getHeight();
                }
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                x = quantifyXPosition((int) x);
                lp.leftMargin = (int) x;
                lp.topMargin = (int) y;
                if (callback != null) {
                    callback.onDragMove((int) x, (int) y);
                }
                view.postOnAnimation(new Runnable() {
                    @Override
                    public void run() {
                        view.setLayoutParams(lp);
                    }
                });
            }

            private int quantifyXPosition(int x) {

                if (quantifyX == 0) {
                    return x;
                } else {
                    int interval = ((r - view.getWidth() - l) / quantifyX);
                    mQLevel = Math.round(1f * x / interval);

                    LogUtils.d(TAG, "mQLevel:" + mQLevel);
                    if (mQLevel == 0) {
                        return l;
                    } else if (mQLevel == quantifyX) {
                        return r - view.getWidth();
                    } else {
                        return mQLevel * interval;
                    }
                }
            }

            private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
                float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
                float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

                float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
                if (angle < -180.f) angle += 360.0f;
                if (angle > 180.f) angle -= 360.0f;
                return -1 * angle;
            }

            private float scale(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
                double distance1 = Math.sqrt(Math.pow(sX - fX, 2) + Math.pow(sY - fY, 2));
                if (distance1 == 0) {
                    distance1 = 1;
                }
                double distance2 = Math.sqrt(Math.pow(nsX - nfX, 2) + Math.pow(nsY - nfY, 2));
                return (float) (distance2 / distance1);
            }


        });
    }

    public static void disableDrag(View view) {
        view.setOnTouchListener(null);
    }

}