package com.foxconn.bandon.recipe.presenter;

import com.foxconn.bandon.recipe.CategoryFragmentContract;
import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

public class CategoryFragmentPresenter implements CategoryFragmentContract.Presenter {
    private static final String TAG = CategoryFragmentPresenter.class.getSimpleName();

    private UseCaseHandler mHandler;
    private RecipeRepository repository;
    private CategoryFragmentContract.View view;

    public CategoryFragmentPresenter(RecipeRepository repository, CategoryFragmentContract.View view) {
        this.repository = repository;
        this.view = view;
        mHandler = UseCaseHandler.getInstance();
        this.view.setPresenter(this);
    }

    @Override
    public void getCategoryList(final String name, String id, int page, int size) {
        LogUtils.d(TAG,"getCategoryList");
        GetCategoriesByID get = new GetCategoriesByID(repository);
        GetCategoriesByID.RequestValue value = new GetCategoriesByID.RequestValue(id, page, size);
        mHandler.execute(get, value, new UseCase.UseCaseCallback<GetCategoriesByID.ResponseValue>() {
            @Override
            public void onSuccess(GetCategoriesByID.ResponseValue response) {
                CategoryList list = response.getList();
                if (null != list) {
                    view.update(list, name);
                }
            }
            @Override
            public void onFailure() {

            }
        });
    }
}
