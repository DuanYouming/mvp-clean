package com.foxconn.bandon.speech;

import com.foxconn.bandon.base.BasePresenter;
import com.foxconn.bandon.base.BaseView;
import com.foxconn.bandon.recipe.model.RecipeVideo;


public interface ISpeechContact {

    interface Presenter extends BasePresenter {
        void getRecipeVideos();
    }

    interface View extends BaseView<Presenter> {
        void updateValues(RecipeVideo value);
    }

}
