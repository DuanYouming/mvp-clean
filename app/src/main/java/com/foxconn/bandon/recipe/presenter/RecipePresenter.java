package com.foxconn.bandon.recipe.presenter;


import com.foxconn.bandon.recipe.RecipeContract;
import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;

public class RecipePresenter implements RecipeContract.Presenter {

    private static final String TAG = RecipePresenter.class.getSimpleName();
    private RecipeContract.View view;
    private UseCaseHandler mHandler;
    private RecipeRepository repository;

    public RecipePresenter(RecipeContract.View view, RecipeRepository repository) {
        this.view = view;
        this.repository = repository;
        this.mHandler = UseCaseHandler.getInstance();
        this.view.setPresenter(this);
    }

    @Override
    public void getCategoryListByName(String name) {
        GetCategoriesByName get = new GetCategoriesByName(repository);
        GetCategoriesByName.RequestValue value = new GetCategoriesByName.RequestValue(name);
        mHandler.execute(get, value, new UseCase.UseCaseCallback<GetCategoriesByName.ResponseValue>() {
            @Override
            public void onSuccess(GetCategoriesByName.ResponseValue response) {
                CategoryList list = response.getList();
                if (null != list && null != list.getResult() && list.getResult().getList().size() > 0) {
                    view.update(list);
                } else {
                    view.showExceptionView();
                }
            }

            @Override
            public void onFailure() {
                view.showExceptionView();
            }
        });
    }

    @Override
    public void getCategoryListByID(String id) {
        GetCategoriesByID get = new GetCategoriesByID(repository);
        GetCategoriesByID.RequestValue value = new GetCategoriesByID.RequestValue(id, 1, 21);
        mHandler.execute(get, value, new UseCase.UseCaseCallback<GetCategoriesByID.ResponseValue>() {
            @Override
            public void onSuccess(GetCategoriesByID.ResponseValue response) {
                CategoryList list = response.getList();
                if (null != list && null != list.getResult() && list.getResult().getList().size() > 0) {
                    view.update(list);
                    LogUtils.d(TAG,"CategoryList size:"+list.getResult().getList().size());
                } else {
                    view.showExceptionView();
                }
            }

            @Override
            public void onFailure() {
                view.showExceptionView();
            }
        });
    }
}
