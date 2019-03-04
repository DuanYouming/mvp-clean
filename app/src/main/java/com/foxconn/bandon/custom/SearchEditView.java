package com.foxconn.bandon.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;


public class SearchEditView extends FrameLayout {
    private static final String TAG = SearchEditView.class.getSimpleName();

    private EditText mInput;
    private SearchListener mSearchListener;


    public SearchEditView(@NonNull Context context) {
        super(context);
    }

    public SearchEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public interface SearchListener {
        void go(String keyword);

        void voiceInput();
    }

    public void setSearchListener(SearchListener mSearchListener) {
        this.mSearchListener = mSearchListener;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }


    private void setup() {
        View view = inflate(getContext(), R.layout.search_edit_view, this);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "click view");
                mInput.requestFocus();
                mInput.setFocusableInTouchMode(true);
                mInput.setSelection(mInput.getText().length());
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null == imm) {
                    return;
                }
                imm.showSoftInput(mInput, InputMethodManager.SHOW_IMPLICIT);
                mInput.setHint(" ");
                mInput.setCursorVisible(true);
            }
        });


        mInput = view.findViewById(R.id.input);

        mInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchListener.go(v.getText().toString().replaceAll("\n", ""));
                    return true;
                }
                return false;
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
                mSearchListener.go(s.toString().replaceAll("\n", ""));
            }
        });


        view.findViewById(R.id.voice_input).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchListener.voiceInput();
            }
        });


    }

    public void clearInput() {
        mInput.setText(null);
    }

    public void clearFocus() {
        LogUtils.d(TAG, "clearFocus");
        mInput.clearFocus();
        mInput.setCursorVisible(false);
    }

    // set hint string
    public void setHintString(final String hintString) {
        post(new Runnable() {
            @Override
            public void run() {
                mInput.setHint(hintString);

            }
        });
    }

    public void setInputString(final String inputString) {
        post(new Runnable() {
            @Override
            public void run() {
                mInput.setText(inputString);
            }
        });
    }
}
