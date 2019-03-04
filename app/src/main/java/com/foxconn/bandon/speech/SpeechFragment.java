package com.foxconn.bandon.speech;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxcomm.bandon.aispeech.CloudASR;
import com.foxcomm.bandon.aispeech.SpeechManager;
import com.foxconn.bandon.MainActivity;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.label.select.LabelSelectFragment;
import com.foxconn.bandon.recipe.model.RecipeVideo;
import com.foxconn.bandon.recipe.view.RecipeFragment;
import com.foxconn.bandon.recipe.view.RecipeVideoFragment;
import com.foxconn.bandon.speech.model.SpeechRepository;
import com.foxconn.bandon.speech.presenter.SpeechPresenter;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SpeechFragment extends BaseFragment implements ISpeechContact.View {
    public static final String TAG = SpeechFragment.class.getName();
    public static final String KEY_FROM = "key_from";
    private String mFromTag;
    private TextView mTvSpeechInfo;
    private GifDrawable mGifDrawable;
    private Handler mHandler = new Handler();
    private ISpeechContact.Presenter mPresenter;
    private int mDelayTime;
    private RecipeVideo mRecipeVideo;

    public SpeechFragment() {

    }


    public static SpeechFragment newInstance() {
        return new SpeechFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getBundle();
        if (null != bundle) {
            mFromTag = bundle.getString(KEY_FROM);
            LogUtils.d(TAG, "from:" + mFromTag);
        }
        new SpeechPresenter(this, SpeechRepository.getInstance(new AppExecutors()));
        mPresenter.getRecipeVideos();
        return inflater.inflate(R.layout.fragment_shopping, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GifImageView ivBackground = view.findViewById(R.id.background);
        mGifDrawable = (GifDrawable) ivBackground.getDrawable();
        mGifDrawable.setLoopCount(1);
        mDelayTime = mGifDrawable.getDuration();
        LogUtils.d(TAG, "duration:" + mDelayTime);
        mGifDrawable.stop();
        mTvSpeechInfo = view.findViewById(R.id.text_speech_info);
        ivBackground.setOnLongClickListener(v -> {
            LogUtils.d(TAG, "On Long Click");
            if (!mGifDrawable.isRunning()) {
                LogUtils.d(TAG, "START");
                startGif();
            }
            return false;
        });
        if (TextUtils.isEmpty(mFromTag)) {
            mTvSpeechInfo.setText("您可以这样对我说：\n【西兰花】、【我要做东坡肉】、【咖喱牛肉的菜谱】");
        } else {
            initTextInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.registNavigationCallback(navigationCallback);
        SpeechManager.getInstance().addAsrCallBack(asrCallBack);
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.unregistNavigationCallback(navigationCallback);
        mGifDrawable.stop();
        mGifDrawable.recycle();
        SpeechManager.getInstance().removeAsrCallBack();
        SpeechManager.getInstance().stopCloudASR();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateValues(RecipeVideo value) {
        mRecipeVideo = value;
    }

    @Override
    public void setPresenter(ISpeechContact.Presenter presenter) {
        mPresenter = presenter;
    }

    private CloudASR.ASRCallBack asrCallBack = new CloudASR.ASRCallBack() {
        @Override
        public void onReady() {
            initTextInfo();
        }

        @Override
        public void onBeginning() {
            mTvSpeechInfo.setText("檢測到語音...");
        }

        @Override
        public void onEnd() {
            mTvSpeechInfo.setText("開始識別...");
        }

        @Override
        public void onRmsChanged(float db) {
            //LogUtils.d(TAG, "rms:" + db);
        }

        @Override
        public void onError() {

        }

        @Override
        public void onResults(String result) {
            stopGif();
            mHandler.removeCallbacksAndMessages(null);
            if (TextUtils.isEmpty(result)) {
                mTvSpeechInfo.setText("無法識別,請再說一次...");
                mTvSpeechInfo.postDelayed(SpeechManager.getInstance()::startCloudASR, 1000);
            } else {
                mTvSpeechInfo.setText(result);

                sendResult(result);
            }
        }
    };

    private void initTextInfo() {
        StringBuilder sb = new StringBuilder("請說出您要查找的");
        if (TextUtils.equals(mFromTag, LabelSelectFragment.TAG)) {
            sb.append("食品標籤");
        } else if (TextUtils.equals(mFromTag, RecipeFragment.TAG)) {
            sb.append("食譜");
        }
        mTvSpeechInfo.setText(sb.toString());
    }

    private RecipeVideo.RecipesBean getRecipesBean(String result) {
        if (null == mRecipeVideo) {
            return null;
        }
        List<RecipeVideo.RecipesBean> recipes = mRecipeVideo.getRecipes();
        for (RecipeVideo.RecipesBean recipe : recipes) {
            if (recipe.getName().contains(result)) {
                return recipe;
            }
        }
        return null;
    }


    private void sendResult(String result) {
        if (TextUtils.isEmpty(mFromTag)) {
            if (result.contains("菜谱")) {
                mFromTag = RecipeFragment.TAG;
                result = result.replace("菜谱", "").replace("的", "");
            } else if (result.contains("做")) {
                mFromTag = RecipeFragment.TAG;
                String[] results = result.split("做");
                if (results.length > 1) {
                    result = results[1];
                }
            } else {
                mFromTag = LabelSelectFragment.TAG;
            }
        }

        RecipeVideo.RecipesBean recipe = getRecipesBean(result);
        if (null != recipe) {
            mFromTag = RecipeVideoFragment.TAG;
            Bundle bundle = initBundle(mFromTag);
            bundle.putString("name", recipe.getName());
            bundle.putString("path", recipe.getPath());
            mListener.StartFragmentAndFinish(bundle);
            return;
        }
        Bundle bundle = initBundle(mFromTag);
        bundle.putString(Constant.KEY_SEARCH_RESULT, result);
        mListener.StartFragmentAndFinish(bundle);
    }


    private void startGif() {
        if (null != mGifDrawable) {
            mGifDrawable.reset();
            SpeechManager.getInstance().startCloudASR();
            mHandler.postDelayed(this::onCompleted, mDelayTime);
        }
    }

    private void stopGif() {
        LogUtils.d(TAG, "GifDrawable stop");
        if (null != mGifDrawable) {
            mGifDrawable.seekToFrame(1);
            mGifDrawable.stop();
        }
    }

    private void onCompleted() {
        LogUtils.d(TAG, "onCompleted");
        mTvSpeechInfo.setText("無法識別,請再說一次...");
    }

    private MainActivity.NavigationCallback navigationCallback = new MainActivity.NavigationCallback() {
        @Override
        public boolean onBackClick() {
            return false;
        }

        @Override
        public boolean onVoiceClick() {
            return true;
        }
    };
}
