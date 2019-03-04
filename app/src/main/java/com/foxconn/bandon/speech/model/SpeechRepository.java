package com.foxconn.bandon.speech.model;


import android.content.Context;

import com.foxconn.bandon.recipe.model.RecipeVideo;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SpeechRepository implements ISpeechDataSource {
    private static final String TAG = SpeechRepository.class.getSimpleName();
    private static SpeechRepository instance;
    private AppExecutors mExecutors;

    private SpeechRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static SpeechRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (SpeechRepository.class) {
                if (null == instance) {
                    instance = new SpeechRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void getRecipeVideos(LoadDataCallback<RecipeVideo> callback) {
        Runnable runnable = () -> {
            String jsonStr = getJsonStr();
            Gson gson = new Gson();
            RecipeVideo recipeVideo = gson.fromJson(jsonStr, RecipeVideo.class);
            if (null != recipeVideo) {
                callback.onSuccess(recipeVideo);
            } else {
                callback.onFailure();
            }
        };
        mExecutors.diskIO().execute(runnable);
    }

    private String getJsonStr() {
        Context context = BandonApplication.getInstance();
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = context.getAssets().open("recipes.json");
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != isr) {
                    isr.close();
                }
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }


}
