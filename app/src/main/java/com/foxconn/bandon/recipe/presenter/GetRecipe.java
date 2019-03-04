package com.foxconn.bandon.recipe.presenter;

import com.foxconn.bandon.recipe.model.IRecipeDataSource;
import com.foxconn.bandon.recipe.model.Recipe;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetRecipe extends UseCase<GetRecipe.RequestValue, GetRecipe.ResponseValue> {

    private RecipeRepository repository;

    public GetRecipe(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        IRecipeDataSource.RecipeCallback callback = new IRecipeDataSource.RecipeCallback() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                Recipe recipe = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(recipe));
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getRecipe(requestValues.getId(), callback);
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String id;

        public RequestValue(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Recipe recipe;

        public ResponseValue(Recipe recipe) {
            this.recipe = recipe;
        }

        public Recipe getRecipe() {
            return recipe;
        }
    }
}
