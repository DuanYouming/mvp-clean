package com.foxconn.bandon.recipe.view;


import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

public class RecipeVideoFragment extends BaseFragment {
    public static final String TAG = RecipeVideoFragment.class.getName();
    private VideoView mVideoView;
    private VideoController mController;
    private ImageView mPreview;
    private ImageView mRestart;
    private Bitmap mPreviewBitmap;
    private TextView mTvCategory;
    private String mName;
    private String mPath;

    public RecipeVideoFragment() {

    }


    public static RecipeVideoFragment newInstance() {
        return new RecipeVideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getBundle();
        if (null != bundle) {
            mName = bundle.getString("name", "东坡肉");
            mPath = bundle.getString("path", "Dongpo_Pork.mp4");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_vedio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoView = view.findViewById(R.id.video);
        mController = view.findViewById(R.id.controller);
        mPreview = view.findViewById(R.id.image_preview);
        mRestart = view.findViewById(R.id.image_restart);
        mTvCategory = view.findViewById(R.id.category);
        setupVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mVideoView && mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mVideoView && mVideoView.canPause()) {
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPlaybackVideo();
        if (null != mPreviewBitmap && !mPreviewBitmap.isRecycled()) {
            mPreviewBitmap.recycle();
            mPreviewBitmap = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupVideoView() {
        final String path = Constant.VIDEO + mPath;
        mTvCategory.setText(mName);
        Uri uri = Uri.parse(path);

        mVideoView.setOnPreparedListener(mp -> {
            mPreview.setVisibility(View.GONE);
            mRestart.setVisibility(View.GONE);
            mVideoView.start();
            mListener.cancelSleep();
        });

        mVideoView.setOnCompletionListener(mp -> {
            mPreview.setImageBitmap(mPreviewBitmap);
            mPreview.setVisibility(View.VISIBLE);
            mRestart.setVisibility(View.VISIBLE);
            mListener.canSleep();
        });

        mVideoView.setOnErrorListener((mp, what, extra) -> {
            stopPlaybackVideo();
            return true;
        });

        mController.setOnStateChangedListener(new VideoController.OnStateChangedListener() {
            @Override
            public void onStart() {
                mListener.cancelSleep();
            }

            @Override
            public void onPause() {
                mListener.canSleep();
            }
        });

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            mPreviewBitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            LogUtils.d(TAG, "IllegalArgumentException:" + e.toString());
        }

        mVideoView.setVideoURI(uri);
        mVideoView.setMediaController(mController);
        mController.setMediaPlayer(mVideoView);
        mRestart.setOnClickListener(v -> mVideoView.setVideoPath(path));
    }

    private void stopPlaybackVideo() {
        try {
            mVideoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
