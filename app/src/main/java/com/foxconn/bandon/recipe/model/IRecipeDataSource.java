package com.foxconn.bandon.recipe.model;

import retrofit2.Callback;

public interface IRecipeDataSource {

    interface CategoryListCallback extends Callback<CategoryList> {

    }

    interface RecipeCallback extends Callback<Recipe> {

    }

    void getRecipe(String id, RecipeCallback callback);

    void getCategoryListByName(String name, CategoryListCallback callback);

    void getCategoryListById(String id, int page, int size, CategoryListCallback callback);

}
