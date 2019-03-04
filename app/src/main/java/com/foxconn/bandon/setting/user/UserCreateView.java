package com.foxconn.bandon.setting.user;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.user.face.FaceDB;
import com.foxconn.bandon.setting.user.register.UserRegisterView;
import com.foxconn.bandon.utils.LogUtils;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;


public class UserCreateView extends FrameLayout implements OnCameraListener {

    private static final String TAG = UserCreateView.class.getSimpleName();
    private UserSettingsView.Callback mCallback;
    private CameraGLSurfaceView mGLSurfaceView;
    private AFT_FSDKEngine engine = new AFT_FSDKEngine();
    private AFT_FSDKVersion version = new AFT_FSDKVersion();
    private List<AFT_FSDKFace> result = new ArrayList<>();
    private Camera mCamera;
    private ValueAnimator mCounterValueAnimator;
    private TextView mCountDownTxt;
    private CircularProgressBar mProgressBar;
    private boolean hasFace;
    private boolean isCancel;
    private int mWidth;
    private int mHeight;

    public UserCreateView(@NonNull Context context) {
        super(context, null);
    }

    public UserCreateView(@NonNull Context context, @NonNull UserSettingsView.Callback callback) {
        this(context);
        this.mCallback = callback;
        setup();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setup() {
        inflate(getContext(), R.layout.user_create_view, this);
        mWidth = 640;
        mHeight = 480;

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        LogUtils.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        LogUtils.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        findViewById(R.id.back).setOnClickListener(v -> {
            isCancel =true;
            mCallback.back();
        });
        mProgressBar = findViewById(R.id.progress_bar);
        mCountDownTxt = findViewById(R.id.count_down);

        mGLSurfaceView = findViewById(R.id.gl_surfaceView);
        //mGLSurfaceView.setOnTouchListener(this);

        CameraSurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setOnCameraListener(this);
        surfaceView.setupGLSurfaceView(mGLSurfaceView, true, false, 90);
        surfaceView.debug_print_fps(false, false);
        startCameraCounter();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d(TAG, "onDetachedFromWindow");
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        LogUtils.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());
        isCancel = true;
        cancelCameraCounter();
        if (null != mCamera) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public Camera setupCamera() {
        int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        int format = ImageFormat.NV21;
        LogUtils.d(TAG, "setupCamera");
        mCamera = Camera.open(cameraID);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mWidth, mHeight);
        parameters.setPreviewFormat(format);
        mCamera.setParameters(parameters);

        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            LogUtils.d(TAG, "SupportedPreviewSizes : " + previewSizes.get(i).width + "x" + previewSizes.get(i).height);
        }
        if (null != mCamera) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewImmediately() {
        return true;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        hasFace = result.size() > 0;
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        result.clear();
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.RED, 2);
    }

    private void takePhoto() {
        mCamera.takePicture(null, null, (data, camera) -> {
            UserRegisterView registerView = new UserRegisterView(getContext(), mCallback, data);
            mCallback.back();
            mCallback.toOtherView(registerView);
        });
    }

    private void startCameraCounter() {
        mCounterValueAnimator = ValueAnimator.ofInt(0, 100);
        mCounterValueAnimator.setDuration(4000);//in millis
        mCounterValueAnimator.addUpdateListener(animation -> {
            mProgressBar.setProgress((int) animation.getAnimatedValue());
            int count = 4 - (int) Math.floor((int) animation.getAnimatedValue() / 25.0);
            mCountDownTxt.setText(String.valueOf(count));
        });
        mCounterValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isCancel) {
                    return;
                }
                if (hasFace) {
                    LogUtils.d(TAG, "take photo");
                    takePhoto();
                } else {
                    LogUtils.d(TAG, "No face found");
                    startCameraCounter();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCounterValueAnimator.start();
    }

    private void cancelCameraCounter() {
        if (mCounterValueAnimator.isRunning()) {
            LogUtils.d(TAG, "cancelCameraCounter");
            mCounterValueAnimator.cancel();
            isCancel = true;
        }
    }

}
