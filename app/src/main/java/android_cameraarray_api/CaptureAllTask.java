package android_cameraarray_api;

import com.foxconn.bandon.usecase.UseCase;

public class CaptureAllTask extends UseCase<CaptureAllTask.RequestValues, CaptureAllTask.ResponseValue> {
    private FridgeCameraRepository repository;

    public CaptureAllTask(FridgeCameraRepository repository) {
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
        repository.captureAll(captureCallback);
    }

    public static final class RequestValues implements UseCase.RequestValues {


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
