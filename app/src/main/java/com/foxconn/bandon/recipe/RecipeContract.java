package com.foxconn.bandon.recipe;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.recipe.model.CategoryList;

public interface RecipeContract {

    interface Presenter extends BasePresenter {
        void getCategoryListByName(String name);

        void getCategoryListByID(String id);
    }

    interface View extends BaseView<Presenter> {

        void update(CategoryList list);

        void showExceptionView();
    }
}
