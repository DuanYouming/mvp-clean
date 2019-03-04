package com.foxconn.bandon.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;
public class SettingIconView extends FrameLayout {
    private String mTitle;
    private int mResID;
    private TextView mTvTitle;
    private ImageView mIcon;

    public SettingIconView(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingIconView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingIconView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingIconView);
        mTitle = a.getString(R.styleable.SettingIconView_title);
        mResID = a.getResourceId(R.styleable.SettingIconView_icon, R.drawable.ic_setting_cardtitle_icemake);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_settings_icon, this);
        mIcon = findViewById(R.id.icon);
        mTvTitle = findViewById(R.id.title);
        mIcon.setImageResource(mResID);
        mTvTitle.setText(mTitle);
    }

    public void setTitle(String title) {
        mTitle = title;
        mTvTitle.setText(mTitle);
    }

    public void setIcon(int id) {
        mIcon.setImageResource(id);
    }

}
