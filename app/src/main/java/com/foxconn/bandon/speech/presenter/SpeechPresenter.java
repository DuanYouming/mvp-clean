package com.foxconn.bandon.speech.presenter;

import com.foxconn.bandon.recipe.model.RecipeVideo;
import com.foxconn.bandon.speech.ISpeechContact;
import com.foxconn.bandon.speech.model.SpeechRepository;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;

public class SpeechPresenter implements ISpeechContact.Presenter {

    private ISpeechContact.View mView;
    private UseCaseHandler mHandler;
    private SpeechRepository mRepository;

    public SpeechPresenter(ISpeechContact.View mView, SpeechRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
        this.mView.setPresenter(this);
        this.mHandler = UseCaseHandler.getInstance();
    }

    @Override
    public void getRecipeVideos() {
        GetRecipeVideo get = new GetRecipeVideo(mRepository);
        GetRecipeVideo.RequestValues values = new GetRecipeVideo.RequestValues();
        mHandler.execute(get, values, new UseCase.UseCaseCallback<GetRecipeVideo.ResponseValue>() {
            @Override
            public void onSuccess(GetRecipeVideo.ResponseValue response) {
                RecipeVideo video = response.getVideo();
                mView.updateValues(video);
            }

            @Override
            public void onFailure() {

            }
        });
    }

}
