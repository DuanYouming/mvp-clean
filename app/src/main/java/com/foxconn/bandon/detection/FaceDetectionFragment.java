package com.foxconn.bandon.detection;


import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.main.view.MainFragment;
import com.foxconn.bandon.setting.user.face.FaceDB;
import com.foxconn.bandon.setting.user.face.FaceManager;
import com.foxconn.bandon.setting.user.model.UserInfo;
import com.foxconn.bandon.standby.StandbyFragment;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.FragmentsUtils;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import java.util.ArrayList;
import java.util.List;

public class FaceDetectionFragment extends BaseFragment implements CameraSurfaceView.OnCameraListener {
    public static final String TAG = FaceDetectionFragment.class.getName();
    public static final String KEY_IS_BROADCAST = "key_broadcast_weather";
    public static final String KEY_USER_NAME = "key_user_name";
    private static final int DETECTION_COUNTS = 5;
    private static final int TOTAL_TIME = 10000;
    private CameraGLSurfaceView mGLSurfaceView;
    private AFT_FSDKEngine mFaceEngine;
    private AFT_FSDKFace mAFT_FSDKFace = null;
    private List<AFT_FSDKFace> mFaceResult;
    private AppExecutors mExecutors = new AppExecutors();
    private FRAbsLoop mFRAbsLoop;
    private ImageView mUserIconImage;
    private TextView mUserNameText;
    private TextView mTextCueWords;
    private TextView mTextCheckResult;
    private byte[] mImageNV21 = null;
    private int mWidth;
    private int mHeight;
    private int mDetectionTime = DETECTION_COUNTS;
    private CheckTimer mTimer;
    private FragmentsUtils mFragmentsUtils;

    public FaceDetectionFragment() {

    }

    public static FaceDetectionFragment newInstance() {
        return new FaceDetectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_detection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSurfaceView(view);
        initUserView(view);
        initFaceEngine();
        mFragmentsUtils = FragmentsUtils.getInstance(getActivity().getSupportFragmentManager());
        mListener.hideNavigationBar();
        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
        mTextCueWords.setText(R.string.checking);
        initTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.hideNavigationBar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFRAbsLoop.shutdown();
        mFaceEngine.AFT_FSDK_UninitialFaceEngine();
        mTimer.cancel();
    }

    private void initSurfaceView(View view) {
        mWidth = 1920;
        mHeight = 1080;
        CameraSurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        mGLSurfaceView = view.findViewById(R.id.gl_surfaceView);
        surfaceView.setOnCameraListener(this);
        surfaceView.setupGLSurfaceView(mGLSurfaceView, true, false, 90);
        surfaceView.debug_print_fps(false, false);
    }

    private void initFaceEngine() {
        mFaceEngine = new AFT_FSDKEngine();
        mFaceResult = new ArrayList<>();
        AFT_FSDKVersion mFaceVersion = new AFT_FSDKVersion();
        mFaceEngine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        mFaceEngine.AFT_FSDK_GetVersion(mFaceVersion);
    }


    private void initUserView(View view) {
        mUserIconImage = view.findViewById(R.id.user_icon);
        mUserNameText = view.findViewById(R.id.text_user_name);
        mTextCueWords = view.findViewById(R.id.text_cue_words);
        mTextCheckResult = view.findViewById(R.id.check_result);
    }

    private void initTimer() {
        mTimer = new CheckTimer(TOTAL_TIME, 1000);
        mTimer.start();
    }


    @Override
    public Camera setupCamera() {
        int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        int format = ImageFormat.NV21;
        LogUtils.d(TAG, "setupCamera");
        Camera camera = Camera.open(cameraID);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mWidth, mHeight);
        parameters.setPreviewFormat(format);
        camera.setParameters(parameters);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            LogUtils.d(TAG, "SupportedPreviewSizes : " + previewSizes.get(i).width + "x" + previewSizes.get(i).height);
        }
        mWidth = camera.getParameters().getPreviewSize().width;
        mHeight = camera.getParameters().getPreviewSize().height;
        return camera;
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
        mFaceEngine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, mFaceResult);
        if (mImageNV21 == null) {
            if (!mFaceResult.isEmpty()) {
                mAFT_FSDKFace = mFaceResult.get(0).clone();
                mImageNV21 = data.clone();
            }
        }
        Rect[] rects = new Rect[mFaceResult.size()];
        for (int i = 0; i < mFaceResult.size(); i++) {
            rects[i] = new Rect(mFaceResult.get(i).getRect());
        }
        mFaceResult.clear();
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.RED, 2);
    }


    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mRegister = FaceManager.getInstance().getFaceDB().getRegisters();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            LogUtils.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            LogUtils.d(TAG, "FR=" + version.toString() + "," + error.getCode());
        }

        @Override
        public void loop() {
            if (mImageNV21 != null) {
                long time = System.currentTimeMillis();
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                LogUtils.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                LogUtils.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                for (FaceDB.FaceRegist fr : mRegister) {
                    for (AFR_FSDKFace face : fr.mFaceList.values()) {
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        LogUtils.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.name;
                        }
                    }
                }

                if (max > 0.6f) {
                    final float maxScore = max;
                    final String objectName = name;
                    mExecutors.mainThread().execute(() -> {
                        LogUtils.d(TAG, "fit Score:" + maxScore + ", NAME:" + objectName);
                        detectionSuccess(objectName);
                    });

                }
                if (max < 0.6f && mDetectionTime == 0) {
                    mExecutors.mainThread().execute(() -> {
                        LogUtils.d(TAG, getString(R.string.name_new_user));
                        detectionFail();
                    });
                }
                mDetectionTime--;
                mImageNV21 = null;
            }

        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            LogUtils.e(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private void detectionFail() {
        mTimer.cancel();
        mFRAbsLoop.shutdown();
        mDetectionTime = DETECTION_COUNTS;
        //mSurfaceView.stopPreview();
        String name = getString(R.string.user_name) + " " + getString(R.string.name_new_user);
        mUserNameText.setText(name);
        mUserIconImage.setImageResource(R.drawable.ic_default_member);
        mTextCheckResult.setTextColor(Color.RED);
        setCompoundDrawables(mTextCheckResult, R.drawable.ic_check_fail);
        mTextCheckResult.setText(R.string.check_fail);
        mTextCueWords.setText(R.string.cue_words_fail);
        startDelay(StandbyFragment.TAG,null);
    }

    private void detectionSuccess(String objectName) {
        mTimer.cancel();
        mFRAbsLoop.shutdown();
        //mSurfaceView.stopPreview();
        String name = getString(R.string.user_name) + " " + objectName;
        new Thread(){
            @Override
            public void run() {
                super.run();
                UserInfo user = BandonDataBase.getInstance(getActivity()).getUserDao().getUser(objectName);
                mTextCheckResult.post(() -> {
                    mUserNameText.setText(name);
                    String url = user.getUrl();
                    ObjectKey key = new ObjectKey(user.getCreated()+"");
                    GlideApp.with(getContext()).asBitmap().signature(key).load(url).into(mUserIconImage);
                    mTextCheckResult.setTextColor(Color.GREEN);
                    setCompoundDrawables(mTextCheckResult, R.drawable.ic_check_success);
                    mTextCheckResult.setText(R.string.check_success);
                    mTextCueWords.setText(R.string.cue_words_success);
                    startDelay(MainFragment.TAG,objectName);
                });
            }
        }.start();
    }

    private void startDelay(final String fragTag,final String name) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mTextCheckResult.postDelayed(() -> {
                    Bundle bundle = initBundle(fragTag);
                    bundle.putBoolean(KEY_IS_BROADCAST, true);
                    if(!TextUtils.isEmpty(name)){
                        bundle.putString(KEY_USER_NAME,name);
                    }
                    mFragmentsUtils.StartFragmentAndFinish(bundle);
                }, 3000);
            }
        }.start();
    }

    private void setCompoundDrawables(TextView view, int id) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), id);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        view.setCompoundDrawables(drawable, null, null, null);
        view.setCompoundDrawablePadding(10);
    }

    class CheckTimer extends CountDownTimer {

        CheckTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            detectionFail();
        }
    }
}
