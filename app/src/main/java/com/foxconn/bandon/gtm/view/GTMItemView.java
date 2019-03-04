package com.foxconn.bandon.gtm.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.utils.LogUtils;

public class GTMItemView extends FrameLayout {
    private static final String TAG = GTMItemView.class.getSimpleName();

    private GTMessage message;
    private ClickCallback callback;

    public GTMItemView(@NonNull Context context) {
        super(context);
    }

    public GTMItemView(@NonNull Context context, GTMessage message, ClickCallback callback) {
        this(context);
        this.message = message;
        this.callback = callback;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
        LogUtils.d(TAG,"onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d(TAG,"onDetachedFromWindow");
    }

    private void setup() {
        LogUtils.d(TAG,"setup");
        inflate(getContext(), R.layout.gtm_item_detail_view, this);
        TextView headTxt = findViewById(R.id.head);
        TextView titleTxt = findViewById(R.id.title);
        View confirm = findViewById(R.id.confirm_btn);
        headTxt.setText("消息提醒");
        titleTxt.setText(message.getContent());
        confirm.setOnClickListener(v -> {
            if (null != callback) {
                callback.confirm();
            }
        });
    }

    public interface ClickCallback {
        void confirm();
    }

}
