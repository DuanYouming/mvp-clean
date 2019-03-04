package com.foxconn.bandon.speech.presenter;

import com.foxconn.bandon.recipe.model.RecipeVideo;
import com.foxconn.bandon.speech.model.ISpeechDataSource;
import com.foxconn.bandon.speech.model.SpeechRepository;
import com.foxconn.bandon.usecase.UseCase;

public class GetRecipeVideo extends UseCase<GetRecipeVideo.RequestValues, GetRecipeVideo.ResponseValue> {

    private SpeechRepository repository;

    public GetRecipeVideo(SpeechRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ISpeechDataSource.LoadDataCallback<RecipeVideo> callback = new ISpeechDataSource.LoadDataCallback<RecipeVideo>() {
            @Override
            public void onSuccess(RecipeVideo values) {
                getUseCaseCallback().onSuccess(new ResponseValue(values));
            }

            @Override
            public void onFailure() {
                getUseCaseCallback().onFailure();
            }
        };
        repository.getRecipeVideos(callback);
    }

    static final class RequestValues implements UseCase.RequestValues {

    }

    static final class ResponseValue implements UseCase.ResponseValue {
        private RecipeVideo video;

        public ResponseValue(RecipeVideo video) {
            this.video = video;
        }

        public RecipeVideo getVideo() {
            return video;
        }
    }
}
