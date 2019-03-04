package com.foxconn.bandon.recipe.http;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeRetrofitUtils {
    private static final String API_BASE_URL = "http://apicloud.mob.com/v1/cook/";
    private volatile static RecipeRetrofitUtils INSTANCE;
    private RecipeRetrofitService mService;


    private RecipeRetrofitUtils() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mService = retrofit.create(RecipeRetrofitService.class);
    }

    public static RecipeRetrofitUtils getInstance() {
        if (null == INSTANCE) {
            synchronized (RecipeRetrofitUtils.class) {
                if (null == INSTANCE) {
                    INSTANCE = new RecipeRetrofitUtils();
                }
            }
        }
        return INSTANCE;
    }

    public RecipeRetrofitService getService() {
        return mService;
    }

}
