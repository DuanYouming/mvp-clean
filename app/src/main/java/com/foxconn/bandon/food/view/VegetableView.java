package com.foxconn.bandon.food.view;

import android.content.Context;
import android.support.annotation.NonNull;

import com.foxconn.bandon.utils.LogUtils;


public class VegetableView extends FridgeCommonView {
    private static final String TAG = VegetableView.class.getSimpleName();

    public VegetableView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void attachedToWindow() {
        LogUtils.d(TAG,"attachedToWindow");
    }

    @Override
    protected void detachedFromWindow() {
        LogUtils.d(TAG,"detachedFromWindow");
    }
}
