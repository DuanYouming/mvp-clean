package com.foxconn.bandon.setting.clock.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.clock.model.ClockBean;
import com.foxconn.bandon.setting.sound.BRingtoneManager;
import com.foxconn.bandon.utils.LogUtils;

public class ClockView extends FrameLayout {

    private static final String TAG = ClockView.class.getSimpleName();
    private Callback callback;
    private TextView mTextContent;
    private ClockBean mClock;

    public ClockView(@NonNull Context context) {
        super(context);
        setup();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BRingtoneManager.getInstance().playAlarm();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BRingtoneManager.getInstance().stopAlarm();
    }

    private void setup() {
        LogUtils.d(TAG, "setup");
        inflate(getContext(), R.layout.clock_view, this);
        View button = findViewById(R.id.dismiss_btn);
        mTextContent = findViewById(R.id.text);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.close(mClock);
                }
            }
        });
    }

    public void setContent(@NonNull ClockBean clock) {
        if (null != mTextContent) {
            mClock = clock;
            mTextContent.setText(mClock.getTag());
        } else {
            LogUtils.d(TAG, "TextContent is null");
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void close(ClockBean clock);
    }
}
