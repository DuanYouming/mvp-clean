package com.foxconn.bandon.setting.user.register;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.setting.user.UserSettingsView;
import com.foxconn.bandon.setting.user.manager.UsersListView;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.GlideApp;

import java.security.MessageDigest;

public class UserRegisterView extends FrameLayout implements IUserRegisterContract.View {

    private UserSettingsView.Callback mCallback;
    private IUserRegisterContract.Presenter mPresenter;
    private byte[] mData;

    public UserRegisterView(@NonNull Context context) {
        super(context);
    }

    public UserRegisterView(Context context, UserSettingsView.Callback callback, byte[] data) {
        this(context);
        mCallback = callback;
        mData = data;
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.user_register_view, this);
        ImageView icon = findViewById(R.id.user_icon);
        if (null != mData) {
            GlideApp.with(getContext()).asBitmap().load(mData).override(258, 258).transform(new RotateTransformation(getContext(), 90)).into(icon);
        }

        final EditText editName = findViewById(R.id.edit_user_name);

        findViewById(R.id.btn_cancel).setOnClickListener(v -> mCallback.back());

        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String name = editName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast(BandonApplication.getInstance().getString(R.string.user_name_not_empty));
            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);
                mPresenter.registerUser(name, bitmap);
            }
        });
        new UserRegisterPresenter(this, UserInfoRepository.getInstance(new AppExecutors()));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UserInfoRepository.destroyInstance();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess() {
        mCallback.back();
        UsersListView listView = new UsersListView(getContext(), mCallback);
        mCallback.toOtherView(listView);
    }

    @Override
    public void setPresenter(IUserRegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public class RotateTransformation extends BitmapTransformation {
        private float rotateRotationAngle;

        RotateTransformation(Context context, float rotateRotationAngle) {
            super(context);
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }

    }


}
