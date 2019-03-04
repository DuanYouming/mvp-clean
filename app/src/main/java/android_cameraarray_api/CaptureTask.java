package android_cameraarray_api;

import com.foxconn.bandon.usecase.UseCase;

public class CaptureTask extends UseCase<CaptureTask.RequestValues, CaptureTask.ResponseValue> {
    private FridgeCameraRepository repository;

    public CaptureTask(FridgeCameraRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IFridgeCameraDataSource.CaptureCallback captureCallback = new IFridgeCameraDataSource.CaptureCallback() {
            @Override
            public void onSuccess(int result) {
                getUseCaseCallback().onSuccess(new ResponseValue(result));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.capture(requestValues.getPort(), captureCallback);
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int port;

        public RequestValues(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private int result;

        public ResponseValue(int result) {
            this.result = result;
        }

        public int getResult() {
            return result;
        }
    }

}
