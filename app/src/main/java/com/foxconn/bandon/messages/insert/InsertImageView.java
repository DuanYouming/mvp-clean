package com.foxconn.bandon.messages.insert;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.EmotionItemDecoration;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class InsertImageView extends FrameLayout implements InsertContact.View {
    private static final String TAG = InsertImageView.class.getSimpleName();

    public static final String DEFAULT_EMOTION_PATH = "emotion";
    public static final String DEFAULT_IMAGE_PATH = "image";
    public static final int TYPE_EMOTION = 0;
    public static final int TYPE_IMAGE = 1;

    private EmotionItemDecoration mEmotionItemDecoration;
    private List<InsertImage> mDefaultImages = new ArrayList<>();
    private List<InsertImage> mDefaultEmotions = new ArrayList<>();
    private List<InsertImage> mLocalEmotions = new ArrayList<>();
    private List<InsertImage> mLocalImages = new ArrayList<>();

    private ImageItemAdapter mImageAdapter;
    private ImageItemAdapter mEmotionAdapter;
    private Context context;
    private InsertContact.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private InsertCallback mCallback;
    private InsertImage mSelectedImage;

    public InsertImageView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        InsertImageRepository.destroy();
    }

    @Override
    public void notifyEmotionDataChanged(List<InsertImage> data) {
        LogUtils.d(TAG, "notifyEmotionDataChanged");
        mLocalEmotions.clear();
        mLocalEmotions.addAll(mDefaultEmotions);
        mLocalEmotions.addAll(data);
        mEmotionAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyImageDataChanged(List<InsertImage> data) {
        mLocalImages.clear();
        mLocalImages.addAll(mDefaultImages);
        mLocalImages.addAll(data);
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void initDefaultEmotions(List<InsertImage> data) {
        mDefaultEmotions = data;
        mEmotionAdapter.notifyDataSetChanged();
        mLocalEmotions.clear();
        mLocalEmotions.addAll(mDefaultEmotions);
        mPresenter.loadLocal(Constant.LOCAL_EMOTION_PATH, TYPE_EMOTION);
    }

    @Override
    public void initDefaultImages(List<InsertImage> data) {
        mDefaultImages = data;
        mLocalImages.clear();
        mLocalImages.addAll(mDefaultImages);
        mImageAdapter.notifyDataSetChanged();
        mPresenter.loadLocal(Constant.LOCAL_IMAGE_PATH, TYPE_IMAGE);
    }

    @Override
    public void setPresenter(@NonNull InsertContact.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setCallback(InsertCallback callback) {
        this.mCallback = callback;
    }

    private void setup() {
        inflate(getContext(), R.layout.insert_image, this);
        RadioGroup group = (RadioGroup) findViewById(R.id.radio_group);
        group.setOnCheckedChangeListener(checkedChangeListener);

        View cancelBtn = findViewById(R.id.cancel_btn);
        View confirmBtn = findViewById(R.id.confirm_btn);

        cancelBtn.setOnClickListener(clickListener);
        confirmBtn.setOnClickListener(clickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mImageAdapter = new ImageItemAdapter(context, mLocalImages, TYPE_IMAGE);
        mEmotionAdapter = new ImageItemAdapter(context, mLocalEmotions, TYPE_EMOTION);
        mEmotionItemDecoration = new EmotionItemDecoration(context);
        LogUtils.d(TAG, "setup");
        initEmotion();
        AssetManager assetManager = context.getResources().getAssets();
        new InsertPresenter(this, InsertImageRepository.getInstance(assetManager));
        mPresenter.loadDefault(DEFAULT_EMOTION_PATH, TYPE_EMOTION);
        mPresenter.loadDefault(DEFAULT_IMAGE_PATH, TYPE_IMAGE);

        ImageItemAdapter.SelectedCallback selectedCallback = new ImageItemAdapter.SelectedCallback() {
            @Override
            public void onItemSelected(InsertImage image) {
                mSelectedImage = image;
            }
        };
        mEmotionAdapter.setCallback(selectedCallback);
        mImageAdapter.setCallback(selectedCallback);
    }

    private void initEmotion() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        mRecyclerView.addItemDecoration(mEmotionItemDecoration);
        mRecyclerView.setAdapter(mEmotionAdapter);
    }

    private void initImage() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.removeItemDecoration(mEmotionItemDecoration);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    private void confirm() {
        if (null != mSelectedImage) {
            mCallback.confirm(mSelectedImage);
        }
    }

    private void cancel() {
        mCallback.cancel();
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            if (id == R.id.emotion_btn) {
                initEmotion();
            } else {
                initImage();
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.confirm_btn) {
                confirm();
            } else {
                cancel();
            }
        }
    };

    public interface InsertCallback {
        void cancel();

        void confirm(InsertImage image);
    }

}
