package com.foxconn.bandon.recipe.model;


import com.foxconn.bandon.recipe.http.RecipeRetrofitUtils;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;

import retrofit2.Call;

public class RecipeRepository implements IRecipeDataSource {
    private static final String TAG = RecipeRepository.class.getSimpleName();
    private static RecipeRepository instance;
    private AppExecutors mExecutors;

    private RecipeRepository(AppExecutors executors) {
        this.mExecutors = executors;
    }

    public static RecipeRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (RecipeRepository.class) {
                if (null == instance) {
                    instance = new RecipeRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public void getRecipe(final String id, final RecipeCallback callback) {
        Runnable runnable = () -> {
            Call<Recipe> call = RecipeRetrofitUtils.getInstance().getService().getRecipe(id);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getCategoryListByName(final String name, final CategoryListCallback callback) {
        Runnable runnable = () -> {
            Call<CategoryList> call = RecipeRetrofitUtils.getInstance().getService().getCategoryListByName(name);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getCategoryListById(final String id, final int page, final int size, final CategoryListCallback callback) {
        LogUtils.d(TAG,"getCategoryListById");
        Runnable runnable = () -> {
            Call<CategoryList> call = RecipeRetrofitUtils.getInstance().getService().getCategoryListById(id, page, size);
            call.enqueue(callback);
        };
        mExecutors.networkIO().execute(runnable);

    }

}
