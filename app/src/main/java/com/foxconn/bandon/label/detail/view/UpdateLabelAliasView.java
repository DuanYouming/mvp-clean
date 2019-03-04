package com.foxconn.bandon.label.detail.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.KeyboardUtils;


public class UpdateLabelAliasView extends FrameLayout {
    private static final int MAX_LENGTH = 4;
    private EditText mAliasInput;
    private Callback mCallback;
    public UpdateLabelAliasView(@NonNull Context context) {
        this(context, null);
    }

    public UpdateLabelAliasView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void cancel();

        void confirm(String alias);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    private void setup() {
        final View view = inflate(getContext(), R.layout.label_edit_alias, this);
        mAliasInput = findViewById(R.id.alias_input);
        findViewById(R.id.editContainer).setOnClickListener(v -> KeyboardUtils.hide(view, getContext()));

        //cancel
        findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            KeyboardUtils.hide(view, getContext());
            if (mCallback != null) {
                mCallback.cancel();
            }
        });

        //confirm
        findViewById(R.id.confirm_btn).findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            KeyboardUtils.hide(view, getContext());
            if (mCallback != null) {
                String str = mAliasInput.getText().toString();
                if (str.length() <= MAX_LENGTH) {
                    mCallback.confirm(mAliasInput.getText().toString());
                } else {
                    Toast.makeText(getContext(), "請輸入4個字以內名稱", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
