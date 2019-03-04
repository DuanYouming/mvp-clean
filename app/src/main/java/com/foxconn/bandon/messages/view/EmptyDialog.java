package com.foxconn.bandon.messages.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.foxconn.bandon.R;

public class EmptyDialog extends LinearLayout {


    private Callback callback;


    public EmptyDialog(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.layout_empty_dialog, this);
        findViewById(R.id.confirm_btn).setOnClickListener(v -> callback.confirm());
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    interface Callback {
        void confirm();
    }

}
