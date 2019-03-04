package com.foxconn.bandon.recipe.presenter;

import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.IRecipeDataSource;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.usecase.UseCase;

import retrofit2.Call;
import retrofit2.Response;

public class GetCategoriesByID extends UseCase<GetCategoriesByID.RequestValue, GetCategoriesByID.ResponseValue> {

    private RecipeRepository repository;

    GetCategoriesByID(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValue values) {
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
        repository.getCategoryListById(values.id, values.page, values.size, callback);
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String id;
        private int page;
        private int size;

        public RequestValue(String id, int page, int size) {
            this.id = id;
            this.page = page;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
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
