package com.foxconn.bandon.setting.upgrade;

import android.os.Handler;
import com.foxconn.bandon.utils.Constant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OtaAPI {

    private static final String TAG = OtaAPI.class.getSimpleName();

    public interface Callback {
        void onResult(String result);

        void onError(Throwable t);
    }

    public static void getInfo(final Handler handler, final Callback callback) {
        final String url = Constant.BASE_URL + "/ecfood/OTAInfo/";

        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();
                    final String result = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(result);
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        }.start();
    }

}