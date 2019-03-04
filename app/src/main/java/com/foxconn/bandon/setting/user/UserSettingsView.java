package com.foxconn.bandon.setting.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.setting.user.manager.UsersListView;

public class UserSettingsView extends FrameLayout {
    public static final String TAG = UserSettingsView.class.getSimpleName();
    private BaseSettingView.DismissCallback mCallback;
    private FrameLayout mChildContainer;
    private View mMainView;
    private Callback mClickCallback;

    public UserSettingsView(@NonNull Context context) {
        super(context);
    }

    public UserSettingsView(@NonNull Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mCallback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void setup() {

        inflate(getContext(), R.layout.layout_user_setting_view, this);
        findViewById(R.id.member_manage_container).setOnClickListener(v -> {
            UsersListView userSettingsView = new UsersListView(getContext(), mClickCallback);
            addChildView(userSettingsView);
        });

        findViewById(R.id.btn_close).setOnClickListener(v -> mCallback.onDismiss(TAG));
        mMainView = findViewById(R.id.main_view);
        mChildContainer = findViewById(R.id.child_container);

        mClickCallback = new Callback() {
            @Override
            public void back() {
                removeChildView();
            }

            @Override
            public void toOtherView(View to) {
                addChildView(to);
            }
        };

    }

    private void addChildView(View child) {
        int index = mChildContainer.getChildCount();
        if (index > 0) {
            mChildContainer.getChildAt(index - 1).setVisibility(INVISIBLE);
        } else {
            mMainView.setVisibility(INVISIBLE);
        }
        mChildContainer.addView(child, index);
    }

    private void removeChildView() {
        int index = mChildContainer.getChildCount();
        if (index > 0) {
            mChildContainer.removeViewAt(mChildContainer.getChildCount() - 1);
            if (index > 1) {
                mChildContainer.getChildAt(index - 2).setVisibility(VISIBLE);
            }
        }
        if (mChildContainer.getChildCount() == 0) {
            mMainView.setVisibility(VISIBLE);
        }
    }

    public interface Callback {
        void back();

        void toOtherView(View to);
    }
}
