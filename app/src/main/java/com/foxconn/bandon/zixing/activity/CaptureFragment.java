package com.foxconn.bandon.zixing.activity;


import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.label.select.LabelSelectFragment;
import com.foxconn.bandon.mall.FreshMallFragment;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.zixing.camera.CameraManager;
import com.foxconn.bandon.zixing.decode.DecodeThread;
import com.foxconn.bandon.zixing.utils.BeepManager;
import com.foxconn.bandon.zixing.utils.CaptureActivityHandler;
import com.foxconn.bandon.zixing.utils.InactivityTimer;
import com.google.zxing.Result;

import java.io.IOException;


public class CaptureFragment extends BaseFragment implements SurfaceHolder.Callback {
    public static final String TAG = CaptureFragment.class.getName();

    public static final int REQUEST_CODE_LABEL = 101;
    public static final int REQUEST_CODE_MALL = 102;
    public static final String KEY_REQUEST_CODE = "key_request_code";
    public static final String KEY_CAPTURE_RESULT = "key_capture_result";

    private SurfaceView scanPreview = null;
    private CameraManager mCameraManager;
    private CaptureActivityHandler mHandler;
    private Rect mCropRect = null;
    private BeepManager mBeepManager;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private boolean isHasSurface;
    private InactivityTimer mInactivityTimer;
    private int mRequestCode;

    public CaptureFragment() {
    }

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(TAG,"onCreateView");
        Bundle bundle = getBundle();
        if(null != bundle) {
            mRequestCode = bundle.getInt(KEY_REQUEST_CODE);
        }
        return inflater.inflate(R.layout.activity_capture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d(TAG,"onViewCreated");
        scanPreview = view.findViewById(R.id.capture_preview);
        scanContainer = view.findViewById(R.id.capture_container);
        scanCropView = view.findViewById(R.id.capture_crop_view);
        mBeepManager = new BeepManager(this);
        mInactivityTimer = new InactivityTimer(this);
        TextView textTitle = view.findViewById(R.id.title);
        if (mRequestCode == REQUEST_CODE_LABEL) {
            textTitle.setText("添加食品標籤");
        } else {
            textTitle.setText("識別購物");
        }
        initScanLine(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG,"onResume");
        mCameraManager = new CameraManager(BandonApplication.getInstance().getApplicationContext());
        mCameraManager.setManualCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        mHandler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }
        mInactivityTimer.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.d(TAG,"onPause");
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mInactivityTimer.onPause();
        mBeepManager.close();
        mCameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        LogUtils.d(TAG,"onDestroyView");
        mInactivityTimer.shutdown();
        super.onDestroyView();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void handleDecode(Result rawResult, Bundle bundle) {
        mInactivityTimer.onActivity();
        mBeepManager.playBeepSoundAndVibrate();
        String result = rawResult.getText();
        LogUtils.d(TAG, "handleDecode:" + result);
        String fragTag;
        if (mRequestCode == REQUEST_CODE_LABEL) {
            fragTag = LabelSelectFragment.TAG;
        } else {
            fragTag = FreshMallFragment.TAG;
        }
        startFragment(fragTag, result);
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    private void initScanLine(View view) {
        ImageView scanLine = view.findViewById(R.id.capture_scan_line);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {

        if (null == surfaceHolder) {
            LogUtils.w(TAG, "No SurfaceHolder provided");
            return;
        }
        if (mCameraManager.isOpen()) {
            LogUtils.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, mCameraManager, DecodeThread.ALL_MODE);
            }
            initCrop();
        } catch (IOException exception) {
            LogUtils.w(TAG, "exception:" + exception.toString());
        } catch (RuntimeException e) {
            LogUtils.w(TAG, "Unexpected error initializing camera " + e.toString());
        }
    }


    private void initCrop() {
        int cameraY = mCameraManager.getCameraResolution().y;
        int cameraX = mCameraManager.getCameraResolution().x;
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1];

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();
        int x = cropLeft * cameraY / containerWidth;
        int y = cropTop * cameraX / containerHeight;
        int width = cropWidth * cameraY / containerWidth;
        int height = cropHeight * cameraX / containerHeight;
        LogUtils.d(TAG, "x:" + x + " y:" + y + " width:" + width + " height:" + height);
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private void startFragment(String fragTag, String result) {
        Bundle bundle = initBundle(fragTag);
        bundle.putString(KEY_CAPTURE_RESULT, result);
        mListener.StartFragmentAndFinish(bundle);
    }
}
