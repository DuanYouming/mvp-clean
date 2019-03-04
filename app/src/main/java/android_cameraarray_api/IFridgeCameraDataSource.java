package android_cameraarray_api;

public interface IFridgeCameraDataSource {

    interface CaptureCallback {
        void onSuccess(int result);

        void onFailure();
    }

    void capture(int port, CaptureCallback callback);

    void captureAll(CaptureCallback callback);
}
