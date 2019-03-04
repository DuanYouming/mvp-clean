package com.foxconn.bandon.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    private static final String API_BASE_URL = "http://smart-blink.xyz:8060/B-Link/";
    private volatile static RetrofitUtils INSTANCE;
    private RetrofitService mRetrofitService;


    private RetrofitUtils() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    public static RetrofitUtils getInstance() {
        if (null == INSTANCE) {
            synchronized (RetrofitUtils.class) {
                if (null == INSTANCE) {
                    INSTANCE = new RetrofitUtils();
                }
            }
        }
        return INSTANCE;
    }

    public RetrofitService getRetrofitService() {
        return mRetrofitService;
    }

}
