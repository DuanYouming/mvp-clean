package com.foxconn.bandon.recipe.presenter;

import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.IRecipeDataSource;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetCategoriesByName extends UseCase<GetCategoriesByName.RequestValue, GetCategoriesByName.ResponseValue> {

    private RecipeRepository repository;

    GetCategoriesByName(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        IRecipeDataSource.CategoryListCallback callback = new IRecipeDataSource.CategoryListCallback() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                CategoryList list = response.body();
                getUseCaseCallback().onSuccess(new ResponseValue(list));
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getCategoryListByName(requestValues.getName(), callback);
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String name;

        public RequestValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private CategoryList list;

        public ResponseValue(CategoryList list) {
            this.list = list;
        }

        public CategoryList getList() {
            return list;
        }
    }
}
