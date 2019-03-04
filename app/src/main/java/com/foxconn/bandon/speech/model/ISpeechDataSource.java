package com.foxconn.bandon.speech.model;

import com.foxconn.bandon.recipe.model.RecipeVideo;

public interface ISpeechDataSource {

    interface LoadDataCallback<T> {
        void onSuccess(T values);

        void onFailure();
    }

    void getRecipeVideos(LoadDataCallback<RecipeVideo> callback);
}
