package android_cameraarray_api;

import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

import java.io.File;

public class FridgeCameraRepository implements IFridgeCameraDataSource {
    private static final String TAG = FridgeFoodRepository.class.getSimpleName();
    private static FridgeCameraRepository instance;
    private AppExecutors mExecutors;
    private CaptureImage captureImage;


    private FridgeCameraRepository(AppExecutors executors) {
        this.mExecutors = executors;
        this.captureImage = new CaptureImage();
        boolean valid = checkFolderPath(Constant.CAMERA_PHOTOS_PATH);
        LogUtils.d(TAG, "checkFolderPath:" + valid);
        this.captureImage.setSavePath(Constant.CAMERA_PHOTOS_PATH);

    }

    public static FridgeCameraRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (FridgeFoodRepository.class) {
                if (null == instance) {
                    instance = new FridgeCameraRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void capture(int port, CaptureCallback callback) {
        LogUtils.d(TAG, "camera[" + port + "] capture");
        Runnable runnable = () -> {
            final int result = captureImage.capture(port);
            mExecutors.mainThread().execute(() -> callback.onSuccess(result));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void captureAll(CaptureCallback callback) {
        Runnable runnable = () -> {
            final int result = captureImage.captureAll();
            mExecutors.mainThread().execute(() -> callback.onSuccess(result));
        };
        mExecutors.diskIO().execute(runnable);
    }

    private boolean checkFolderPath(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }
}
