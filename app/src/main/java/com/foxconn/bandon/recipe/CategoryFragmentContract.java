package com.foxconn.bandon.recipe;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.recipe.model.CategoryList;

public interface CategoryFragmentContract {

    interface Presenter extends BasePresenter {
        void getCategoryList(String name, String id, int page, int size);
    }

    interface View extends BaseView<Presenter> {
        void update(CategoryList list, String name);

        void showExceptionView();
    }
}
