package com.foxconn.bandon.device;

import com.foxconn.bandon.http.ResponseStr;
import com.foxconn.bandon.usecase.UseCase;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterDevice extends UseCase<RegisterDevice.RequestValue, RegisterDevice.ResponseValue> {

    private DeviceRepository mRepository;

    public RegisterDevice(DeviceRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        IDeviceDataSource.DeviceCallback callback = new DeviceRepository.DeviceCallback() {
            @Override
            public void onResponse(Call<ResponseStr> call, Response<ResponseStr> response) {
                ResponseStr responseStr = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(responseStr));
            }

            @Override
            public void onFailure(Call<ResponseStr> call, Throwable t) {

            }
        };
        mRepository.register(requestValues.getBody(), callback);
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private RequestBody body;

        public RequestValue(RequestBody body) {
            this.body = body;
        }

        public RequestBody getBody() {
            return body;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private ResponseStr response;

        public ResponseValue(ResponseStr response) {
            this.response = response;
        }

        public ResponseStr getResponse() {
            return response;
        }
    }
}
