package com.foxconn.bandon.setting.wifi.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.utils.LogUtils;

public class WifiConnectDialog extends FrameLayout {
    private static final String TAG = WifiConnectDialog.class.getSimpleName();
    private static final int MIN_LENGTH = 8;
    private EditText mPassword;
    private TextView mMessage;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private ClickCallback callback;
    private InputMethodManager mInputManager;
    private WifiDevice mDevice;

    public WifiConnectDialog(@NonNull Context context) {
        this(context, null);
    }

    public WifiConnectDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    private void setup() {
        LogUtils.d(TAG, "setup");
        inflate(getContext(), R.layout.layout_wifi_connect_dialog, this);
        mBtnCancel = findViewById(R.id.cancel);
        mBtnConfirm = findViewById(R.id.join);
        mBtnCancel.setOnClickListener(ClickListener);
        mBtnConfirm.setOnClickListener(ClickListener);

        mPassword = findViewById(R.id.password_edit);
        mMessage = findViewById(R.id.message);
        mPassword.addTextChangedListener(mTextWatcher);
        mInputManager = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setData(WifiDevice device) {
        mDevice = device;
        TextView textTitle = findViewById(R.id.title);
        textTitle.setText(mDevice.getSSID());
        if (!mDevice.isOpen()) {
            if (null != mDevice.getPassword()) {
                mPassword.setText(mDevice.getPassword());
            }
        } else {
            mPassword.setFocusable(false);
            mPassword.removeTextChangedListener(mTextWatcher);
            mBtnConfirm.setEnabled(true);
        }

    }

    public void setMessage(String message) {
        mMessage.setText(message);
    }

    public void setClickCallback(ClickCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() < MIN_LENGTH) {
                mBtnConfirm.setEnabled(false);
                mMessage.setTextColor(getResources().getColor(R.color.wifi_password_error));
                mMessage.setText(getResources().getString(R.string.wifi_detail_login_eight_password));
            } else {
                mBtnConfirm.setEnabled(true);
                mMessage.setText(null);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };


    private View.OnClickListener ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel:
                    cancel();
                    break;
                case R.id.join:
                    join();
                    break;
                default:
                    break;
            }
        }
    };

    private void join() {
        String password = mPassword.getText().toString().trim();
        mMessage.setTextColor(getResources().getColor(R.color.wifi_password_message));
        mMessage.setText(getResources().getString(R.string.wifi_detail_login_process_password));
        mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        mDevice.setPassword(password);
        callback.join(mDevice);
    }

    private void cancel() {
        mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        callback.cancel();
    }

    public interface ClickCallback {
        void cancel();

        void join(WifiDevice wifiDevice);
    }
}
