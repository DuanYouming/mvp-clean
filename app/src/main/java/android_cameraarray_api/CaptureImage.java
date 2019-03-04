package android_cameraarray_api;

public class CaptureImage {

    static {
        System.loadLibrary("capture_image");
    }


    CaptureImage() {

    }

    public native int capture(int port);

    public native int captureAll();

    public native int setSavePath(String path);

}