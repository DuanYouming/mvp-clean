package android_cameraarray_api;


import android.support.annotation.NonNull;

import com.foxconn.bandon.food.photo.PhotoUtils;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;

import java.io.File;


public class CaptureManager {
    private static final String TAG = CaptureManager.class.getSimpleName();
    private static final int CAPTURE_SUCCESS = 0;
    private static final int CAMERA_PORT_FIR = 0;
    private static final int CAMERA_PORT_SEC = 1;
    private static final int CAMERA_PORT_THR = 2;
    private static final int CAMERA_PORT_FOU = 3;
    private final FridgeCameraRepository repository;
    private UseCaseHandler mUseCaseHandler;
    private static CaptureManager instance;
    private CaptureCallback captureCallback;
    private boolean isDoorOpen;
    private boolean isCaptureEnd = true;


    private CaptureManager() {
        this.repository = FridgeCameraRepository.getInstance(new AppExecutors());
        this.mUseCaseHandler = UseCaseHandler.getInstance();
    }

    public static CaptureManager getInstance() {
        if (null == instance) {
            synchronized (CaptureManager.class) {
                if (null == instance) {
                    instance = new CaptureManager();
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        FridgeCameraRepository.destroyInstance();
        instance = null;
    }

    public void registerCaptureCallback(@NonNull CaptureCallback callback) {
        this.captureCallback = callback;
    }

    public void unregisterCaptureCallback() {
        this.captureCallback = null;
    }


    public void doorOpen() {
        LogUtils.d(TAG, "doorOpen");
        if (isDoorOpen) {
            return;
        }
        isDoorOpen = true;
    }

    public void doorClose() {
        LogUtils.d(TAG, "doorClose");
        if (!isDoorOpen) {
            return;
        }
        isDoorOpen = false;
        if (isCaptureEnd) {
            isCaptureEnd = false;
        }
        captureAll();
    }

    private void capture(final int port) {
        LogUtils.d(TAG, "camera[" + port + "] capture");
        CaptureTask capture = new CaptureTask(repository);
        CaptureTask.RequestValues values = new CaptureTask.RequestValues(port);
        mUseCaseHandler.execute(capture, values, new UseCase.UseCaseCallback<CaptureTask.ResponseValue>() {
            @Override
            public void onSuccess(CaptureTask.ResponseValue response) {
                if (response.getResult() == CAPTURE_SUCCESS) {
                    LogUtils.d(TAG, "camera[" + port + "] capture success");
                } else {
                    LogUtils.d(TAG, "camera[" + port + "] capture failure");
                }
            }

            @Override
            public void onFailure() {
                LogUtils.d(TAG, "camera[" + port + "] capture failure");
            }
        });
    }

    private void captureAll() {
        CaptureAllTask captureAllTask = new CaptureAllTask(repository);
        mUseCaseHandler.execute(captureAllTask, null, new UseCase.UseCaseCallback<CaptureAllTask.ResponseValue>() {
            @Override
            public void onSuccess(CaptureAllTask.ResponseValue response) {
                int result = response.getResult();
                isCaptureEnd = true;
                completed(result);
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void completed(int result) {
        LogUtils.d(TAG, "completed");
        boolean fir = ((result & (0x01)) == 0);
        boolean sec = ((result & (0x01 << 1)) == 0);
        boolean third = ((result & (0x01 << 2)) == 0);
        boolean four = ((result & (0x01 << 3)) == 0);
        LogUtils.d(TAG, "captureAll result:first[" + fir + "] sec[" + sec + "] third[" + third + "] four[" + four + "]");
        if (isDoorOpen) {
            return;
        }
        if (fir) {
            deleteFridgeImage(CAMERA_PORT_FIR);
        }
        if (sec) {
            deleteFridgeImage(CAMERA_PORT_SEC);
        }
        if (third) {
            deleteFridgeImage(CAMERA_PORT_THR);
        }
        if (four) {
            deleteFridgeImage(CAMERA_PORT_FOU);
        }
        if (null != captureCallback) {
            captureCallback.onCompleted();
        }
    }

    private void deleteFridgeImage(int index) {
        String imageUrl = PhotoUtils.getInstance().getImageUrl(index);
        LogUtils.d(TAG, "imageUrl:" + imageUrl);
        if (null == imageUrl) {
            return;
        }
        File file = new File(imageUrl);
        if (file.exists()) {
            LogUtils.d(TAG, "delete:" + imageUrl + " " + file.delete());
        }
    }


    public interface CaptureCallback {
        void onCompleted();
    }
}
