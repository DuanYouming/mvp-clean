package com.foxconn.bandon.cropper;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.cropper.model.CropperFactor;
import com.foxconn.bandon.cropper.model.CropperRepository;
import com.foxconn.bandon.cropper.presenter.CropperPresenter;
import com.foxconn.bandon.food.photo.PhotoUtils;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;
import java.io.File;

public class CropperFragment extends BaseFragment implements View.OnClickListener, ICropperFragmentContract.View {
    public static final String TAG = CropperFragment.class.getName();
    public static final String KEY_PHOTO_INDEX = "key_photo_index";
    private ICropperFragmentContract.Presenter mPresenter;
    private CropImageView mCropImageView;
    private Bitmap mCameraPhoto;
    private int mIndex;
    private Bitmap mDrawingCache;

    public CropperFragment() {

    }

    public static CropperFragment newInstance() {
        return new CropperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new CropperPresenter(this, CropperRepository.getInstance(new AppExecutors()));
        return inflater.inflate(R.layout.fragment_cropper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getBundle();
        if(null!=bundle){
            mIndex = getBundle().getInt(KEY_PHOTO_INDEX);
        }
        LogUtils.d(TAG, "index:" + mIndex);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        mCropImageView = view.findViewById(R.id.CropImageView);
        mCropImageView.setGuidelines(CropImageView.GUIDELINES_ON);
        String url = PhotoUtils.getInstance().getSrcPhoto(mIndex);
        mPresenter.loadCameraPhoto(url);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "onDestroyView");
        CropperRepository.destroyInstance();
        if (null != mCameraPhoto && !mCameraPhoto.isRecycled()) {
            mCameraPhoto.recycle();
            mCameraPhoto = null;
        }
        if (null != mDrawingCache && !mDrawingCache.isRecycled()) {
            mDrawingCache.recycle();
            mDrawingCache = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            confirm();
        } else {
            close();
        }
    }

    private void confirm() {
        CropperFactor factor = mCropImageView.getCropperFactor();
        mCropImageView.setDrawingCacheEnabled(true);
        mDrawingCache = mCropImageView.getDrawingCache();
        mCropImageView.setDrawingCacheEnabled(false);
        factor.setBitmapWidth(mDrawingCache.getWidth());
        factor.setBitmapHeight(mDrawingCache.getHeight());
        saveCropperFactor(factor);
        deleteFridgeImage();
        close();
    }

    @Override
    public void setPresenter(ICropperFragmentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        mCameraPhoto = bitmap;
        GlideApp.with(BandonApplication.getInstance()).asBitmap().load(mCameraPhoto).into(mCropImageView);
    }

    @Override
    public void close() {
        finish();
    }

    private void saveCropperFactor(CropperFactor factor) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(factor);
        LogUtils.d(TAG, "saveCropperFactor:" + jsonStr);
        PreferenceUtils.setString(getContext(), Constant.SP_PHOTOS_NAME, String.valueOf(mIndex), jsonStr);
    }

    private void deleteFridgeImage() {
        String imageUrl = PhotoUtils.getInstance().getImageUrl(mIndex);
        LogUtils.d(TAG, "imageUrl:" + imageUrl);
        if (null == imageUrl) {
            return;
        }
        File file = new File(imageUrl);
        if (file.exists()) {
            LogUtils.d(TAG, "delete:" + imageUrl + " " + file.delete());
        }
    }
}
