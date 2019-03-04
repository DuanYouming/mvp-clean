package com.foxconn.bandon.messages.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;

public class ConfirmView extends FrameLayout {

    private Callback mCallback;

    public ConfirmView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setUp();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void setUp() {
        inflate(getContext(), R.layout.memo_clear_confirm, this);
        findViewById(R.id.cancel_btn).setOnClickListener(dialogListener);
        findViewById(R.id.confirm_btn).setOnClickListener(dialogListener);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.confirm_btn) {
                mCallback.confirm();
            } else {
                mCallback.cancel();
            }
        }
    };

    public interface Callback {

        void confirm();

        void cancel();
    }
}
