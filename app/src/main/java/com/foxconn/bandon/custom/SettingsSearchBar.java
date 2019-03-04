package com.foxconn.bandon.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foxconn.bandon.R;

public class SettingsSearchBar extends FrameLayout {

    private ImageView mClear;
    private EditText mInput;
    private StateListener mStateListener;

    public SettingsSearchBar(@NonNull Context context) {
        super(context);
    }

    public SettingsSearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }


    private void setup() {
        inflate(getContext(), R.layout.settings_search_bar, this);
        mClear = (ImageView) findViewById(R.id.clear);
        mInput = (EditText) findViewById(R.id.input);

        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInput.setText("");
                if (mStateListener != null) {
                    mStateListener.clear();
                }
            }
        });

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStateListener != null) {
                    mStateListener.input(s.toString());
                }
            }
        });

    }

    public interface StateListener {
        void input(String s);

        void clear();
    }

    public void clearInput() {
        mInput.setText("");
    }

    public void setStateListener(StateListener stateListener) {
        this.mStateListener = stateListener;
    }
}
