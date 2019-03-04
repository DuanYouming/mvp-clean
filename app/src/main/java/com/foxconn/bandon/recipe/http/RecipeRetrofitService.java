package com.foxconn.bandon.recipe.http;


import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.Recipe;
import com.foxconn.bandon.recipe.model.RecipeCategory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeRetrofitService {

    @GET("category/query?key=146f910d83f58")
    Call<RecipeCategory> getRecipeCategories();

    @GET("menu/search?key=146f910d83f58")
    Call<CategoryList> getCategoryListById(@Query("cid") String cid, @Query("page") int page, @Query("size") int size);

    @GET("menu/search?key=146f910d83f58")
    Call<CategoryList> getCategoryListByName(@Query("name") String name);

    @GET("menu/query?key=146f910d83f58")
    Call<Recipe> getRecipe(@Query("id") String id);

}
