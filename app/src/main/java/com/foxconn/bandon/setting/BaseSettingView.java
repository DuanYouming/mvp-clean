package com.foxconn.bandon.setting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;

public abstract class BaseSettingView extends FrameLayout {
    private static final String TAG = BaseSettingView.class.getSimpleName();

    protected DismissCallback mDismissCallback;
    private Context mContext;
    protected TextView mTitle;
    protected FrameLayout mTitleContainer;
    protected ViewGroup mContentView;
    protected View mCloseButton;

    public BaseSettingView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    private void init() {
        inflate(mContext, R.layout.layout_base_settings, this);
        mTitle = findViewById(R.id.title);
        mTitleContainer = findViewById(R.id.title_container);
        mContentView = findViewById(R.id.content_view);
        mCloseButton = findViewById(R.id.btn_close);
        mCloseButton.setOnClickListener(v -> {
            LogUtils.d(TAG, "touch close button to dismiss");
            close();
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
        setup();
    }

    protected abstract void setup();

    protected abstract void close();

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDismissCallback = null;
    }

    public interface DismissCallback {
        void onDismiss(String tag);
    }


}
