package com.foxconn.bandon.food.view;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.R;
import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.DragUtils;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FridgeView extends LinearLayout implements View.OnClickListener {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 1581;
    public static final int IMAGES_NUM = 4;
    private DecimalFormat dfCoordinate = new DecimalFormat("#.##");
    private FrameLayout mLabelsContainer;
    private View mMaskView;
    private Callback callback;
    private List<ImageView> mImageViews;
    private String[] mUrls = new String[IMAGES_NUM];

    public FridgeView(Context context) {
        this(context, null);
    }

    public FridgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View v) {
        int index = mImageViews.indexOf((ImageView) v);
        callback.startToCropper(index);
    }

    private void setup() {
        inflate(getContext(), R.layout.fridge_view, this);
        mLabelsContainer = findViewById(R.id.label_views_container);
        mMaskView = findViewById(R.id.view_mask);
        initImageViews();
    }

    private void initImageViews() {
        mImageViews = new ArrayList<>();
        mImageViews.add((ImageView) findViewById(R.id.image_first));
        mImageViews.add((ImageView) findViewById(R.id.image_second));
        mImageViews.add((ImageView) findViewById(R.id.image_third));
        mImageViews.add((ImageView) findViewById(R.id.image_forth));

        for (int i = 0; i < mImageViews.size(); i++) {
            ImageView view = mImageViews.get(i);
            view.setOnClickListener(this);
            if (!TextUtils.isEmpty(mUrls[i])) {
                setImageUrl(mUrls[i], i);
            }
        }

    }

    public void setImageUrl(String url, int index) {
        mUrls[index] = url;
        String jsonStr = PreferenceUtils.getString(getContext(), Constant.SP_PHOTOS_NAME, Constant.KEY_PHOTOS + String.valueOf(index), "");
        ObjectKey key = new Gson().fromJson(jsonStr, ObjectKey.class);
        if (null == key) {
            GlideApp.with(getContext())
                    .asBitmap()
                    .load(url)
                    .fitCenter()
                    .into(mImageViews.get(index));
            return;
        }
        GlideApp.with(getContext())
                .asBitmap()
                .load(url)
                .signature(key)
                .fitCenter()
                .into(mImageViews.get(index));
    }


    public void setLabels(List<ColdRoomFood.Label> labels) {
        for (final ColdRoomFood.Label label : labels) {
            final FridgeFoodLabelView view = new FridgeFoodLabelView(getContext(), label);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Math.min(parseOffset(label.xCoordinate, WIDTH), WIDTH - getResources().getDimensionPixelSize(R.dimen.cold_room_food_label_size));
            lp.topMargin = Math.min(parseOffset(label.yCoordinate, HEIGHT), HEIGHT - getResources().getDimensionPixelSize(R.dimen.cold_room_food_label_size));
            mLabelsContainer.addView(view, lp);

            DragUtils.enableDrag(view, mLabelsContainer, 0, new DragUtils.Callback() {
                @Override
                public void onDragMove(int x, int y) {
                    super.onDragMove(x, y);
                    onLabelMove(x, y, label);
                }

                @Override
                public void onDragEnd() {
                    super.onDragEnd();
                    onLabelDragEnd(label);
                }

                @Override
                public void onClick() {
                    super.onClick();
                    onLabelClick(label);
                }
            });
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void onLabelDragEnd(ColdRoomFood.Label label) {
        if (mMaskView.getVisibility() != VISIBLE) {
            return;
        }
        mMaskView.animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMaskView.setVisibility(GONE);
                mMaskView.animate().setListener(null);
                mMaskView.setAlpha(1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        callback.updateLocation(label);
    }

    private void onLabelMove(int x, int y, ColdRoomFood.Label label) {

        if (mMaskView.getVisibility() != VISIBLE) {
            mMaskView.setAlpha(0);
            mMaskView.setVisibility(VISIBLE);
            mMaskView.animate().alpha(1);
        }
        label.xCoordinate = transfer(x, WIDTH);
        label.yCoordinate = transfer(y, HEIGHT);

    }

    private void onLabelClick(ColdRoomFood.Label label) {
        if (null != callback) {
            callback.onClick(label.id);
        }
    }

    private int parseOffset(String coordinate, int size) {
        if (TextUtils.isEmpty(coordinate)) {
            return 50;
        } else {
            return (int) Math.round(size * (Double.valueOf(coordinate.replaceAll("%", "")) / 100.0));
        }
    }

    private String transfer(int coordinate, int size) {
        return dfCoordinate.format((1.0 * coordinate / size) * 100) + "%";
    }


    interface Callback extends FridgeCommonView.ClickCallback {
        void updateLocation(ColdRoomFood.Label label);

        void startToCropper(int index);
    }


}
