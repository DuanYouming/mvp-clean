package com.foxconn.bandon.food.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.utils.Constant;


public class FridgeFoodLabelView extends FrameLayout {

    private ColdRoomFood.Label mLabel;

    public FridgeFoodLabelView(@NonNull Context context, ColdRoomFood.Label label) {
        this(context);
        mLabel = label;
    }

    public FridgeFoodLabelView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.fridge_food_label_view, this);
        TextView textName = findViewById(R.id.item_name);
        textName.setText(mLabel.foodTagRename);
        TextView textHint = findViewById(R.id.remaining_days);
        if (TextUtils.equals(mLabel.iconColor, Constant.FOOD_TYPE_EXPIRED)) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_launcher_red_label));
        } else if (TextUtils.equals(mLabel.iconColor, Constant.FOOD_TYPE_SOON_EXPIRE)) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_launcher_yellow_label));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_launcher_green_label));
        }
        textHint.setText(mLabel.iconColorDetail);
    }

}
