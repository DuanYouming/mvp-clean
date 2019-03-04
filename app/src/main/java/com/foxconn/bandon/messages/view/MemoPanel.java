package com.foxconn.bandon.messages.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.messages.MemoPanelContract;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.messages.presenter.MemoPanelPresenter;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.DragUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.RotationScaleUtils;

import java.util.List;


public class MemoPanel extends FrameLayout implements MemoPanelContract.View {

    private static final String TAG = MemoPanel.class.getSimpleName();

    private static final float MAX_SCALE = 2.0f;
    private static final float MIN_SCALE = 0.5f;
    private static final int MAX_MEMOS = 20;
    private int mMemoCounts;
    private ViewGroup mMemoContainer;
    private ImageView mTrashCan;
    private ImageView mMemoBtn;
    private int[] mTrashCanLocation = {880, 1160};
    private MemoPanelContract.Presenter mPresenter;
    private Callback callback;

    public MemoPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setPresenter(MemoPanelContract.Presenter presenter) {
        mPresenter = presenter;
    }


    private void setup() {
        inflate(getContext(), R.layout.layout_message_board, this);
        mMemoContainer = findViewById(R.id.message_container);
        mTrashCan = findViewById(R.id.btn_recycle);
        mMemoBtn = findViewById(R.id.btn_add_message);
        mMemoBtn.setOnClickListener(view -> {
            if (mMemoCounts < MAX_MEMOS) {
                callback.startMessageBoard(null);
            } else {
                Toast.makeText(getContext(), "您的留言已達到上限", Toast.LENGTH_SHORT).show();
            }
        });
        LogUtils.d(TAG, "mTrashCan Location: " + mTrashCanLocation[0] + "," + mTrashCanLocation[1]);
        mTrashCan.setVisibility(GONE);
        new MemoPanelPresenter(this, MemoRepository.getInstance(BandonDataBase.getInstance(getContext()).memoDao(), new AppExecutors()));
        mPresenter.getMemos();
    }

    public void update() {
        mPresenter.getMemos();
    }

    @Override
    public void updateMemos(List<Memo> memos) {
        LogUtils.d(TAG, "update memos");
        mMemoContainer.removeAllViews();
        mMemoCounts = memos.size();
        for (final Memo memo : memos) {
            LogUtils.d(TAG, "memo:" + memo.toString());
            final MemoView memoView = new MemoView(getContext(), memo);
            memoView.setTag(memo.getID());
            addViewToMemoContainer(memo, memoView);
            DragUtils.enableDrag(memoView, this, 0, new DragUtils.Callback() {
                private boolean isMove = false;
                private float initRotateAngle = Float.MAX_VALUE;
                private float initScale = Float.MAX_VALUE;
                private int containerW;
                private int containerH;

                @Override
                public void onDragMove(int x, int y) {
                    if (mTrashCan.getVisibility() != VISIBLE) {
                        hideAddBtnShowTrashCan();
                    }
                    memo.setLocation(x, y);
                    isMove = true;
                }

                @Override
                public void onFingerMove(int x, int y) {
                    if (x >= mTrashCanLocation[0] && x <= mTrashCanLocation[0] + mTrashCan.getWidth()
                            && y >= mTrashCanLocation[1] && y <= mTrashCanLocation[1] + mTrashCan.getHeight()) {
                        if (!mDragToTrashCan) {
                            mDragToTrashCan = true;
                            playTrashCanOpenAnimation();
                        }
                    } else {
                        if (mDragToTrashCan) {
                            mDragToTrashCan = false;
                            playTrashCanCloseAnimation();
                        }
                    }
                }

                @Override
                public void onDragEnd() {
                    if (mDragToTrashCan && mTrashCan.getVisibility() == VISIBLE) {
                        playTrashMemoAnimation(memo.getID(), memoView);

                    } else {
                        mPresenter.updateMemo(memo);
                    }

                    if (mTrashCan.getVisibility() == VISIBLE) {
                        showAddBtnHideTrashCan();
                    }
                    if (!isMove && !TextUtils.isEmpty(memo.getDrawActions())) {
                        callback.startMessageBoard(memo.getID());
                    }
                    isMove = false;
                    initRotateAngle = Float.MAX_VALUE;
                    initScale = Float.MAX_VALUE;
                }

                @Override
                public void onLongPress() {

                }

                @Override
                public void onHold() {
                    hideAddBtnShowTrashCan();
                }

                @Override
                public void onRotationAndScale(float angle, float scale) {

                    if (initRotateAngle == Float.MAX_VALUE) {
                        initRotateAngle = memoView.getContentImg().getRotation();
                    }

                    //set rotation
                    float adjustAngle = initRotateAngle + angle;


                    if (initScale == Float.MAX_VALUE) {
                        initScale = memoView.getContentImg().getLayoutParams().width / (memoView.getSourceImgW() * 1f);
                    }

                    float adjustScale = initScale * scale;
                    if (adjustScale < MIN_SCALE) {
                        adjustScale = MIN_SCALE;
                    }

                    if (adjustScale > MAX_SCALE) {
                        adjustScale = MAX_SCALE;
                    }


                    containerW = (int) RotationScaleUtils.optimalWidth(adjustAngle, adjustScale, memoView.getSourceImgW(), memoView.getSourceImgH());
                    containerH = (int) RotationScaleUtils.optimalHeight(adjustAngle, adjustScale, memoView.getSourceImgW(), memoView.getSourceImgH());

                    if (containerW <= getWidth() && containerH <= getHeight()) {
                        memo.setScale(adjustScale);
                        memo.setAngle(adjustAngle);
                        memoView.getContentImg().setRotation(adjustAngle);
                        memoView.getContentImg().getLayoutParams().width = (int) (adjustScale * memoView.getSourceImgW());
                        memoView.getContentImg().getLayoutParams().height = (int) (adjustScale * memoView.getSourceImgH());
                        memoView.getContentImg().requestLayout();

                        memoView.getLayoutParams().width = containerW;
                        memoView.getLayoutParams().height = containerH;
                        memoView.requestLayout();
                    }
                }

            });
        }
    }

    private void addViewToMemoContainer(final Memo memo, final MemoView memoView) {
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = memo.getLocationX();
        lp.topMargin = memo.getLocationY();

        memoView.post(() -> {
            int optimalWidth = (int) RotationScaleUtils.optimalWidth(memo.getAngle(), memo.getScale(), memoView.getSourceImgW(), memoView.getSourceImgH());
            int optimalHeight = (int) RotationScaleUtils.optimalHeight(memo.getAngle(), memo.getScale(), memoView.getSourceImgW(), memoView.getSourceImgH());

            float scale = memo.getScale();
            boolean overLimit = false;
            boolean updateToPref = false;
            while (((lp.leftMargin + optimalWidth > mMemoContainer.getWidth()) || (lp.topMargin + optimalHeight > mMemoContainer.getHeight())) && !overLimit) {
                if (lp.leftMargin > 0 || lp.topMargin > 0) {
                    lp.leftMargin = Math.max(lp.leftMargin - 5, 0);
                    lp.topMargin = Math.max(lp.topMargin - 5, 0);
                    memo.setLocation(lp.leftMargin, lp.topMargin);
                    LogUtils.d(TAG, "adjust memo location to:" + lp.leftMargin + "," + lp.topMargin);
                    updateToPref = true;
                } else if (scale > 0.5) {
                    //scale
                    scale = (float) Math.max(scale - 0.05, 0.5);
                    optimalWidth = (int) RotationScaleUtils.optimalWidth(memo.getAngle(), scale, memoView.getSourceImgW(), memoView.getSourceImgH());
                    optimalHeight = (int) RotationScaleUtils.optimalHeight(memo.getAngle(), scale, memoView.getSourceImgW(), memoView.getSourceImgH());
                    memo.setScale(scale);
                    LogUtils.d(TAG, "adjust scale memo:" + scale);
                    updateToPref = true;
                } else {
                    overLimit = true;
                }
            }
            if (updateToPref) {
                //update image view width & height
                memoView.getContentImg().getLayoutParams().width = (int) (scale * memoView.getSourceImgW());
                memoView.getContentImg().getLayoutParams().height = (int) (scale * memoView.getSourceImgH());
                memoView.getContentImg().requestLayout();
                mPresenter.updateMemo(memo);
            }

            memoView.getLayoutParams().width = optimalWidth;
            memoView.getLayoutParams().height = optimalHeight;
            memoView.requestLayout();
        });

        mMemoContainer.addView(memoView, lp);
    }


    private boolean mDragToTrashCan;

    /**
     * Show trash can
     */
    private void hideAddBtnShowTrashCan() {
        LogUtils.d(TAG, "hideAddBtnShowTrashCan");
        mMemoBtn.setEnabled(false);

        mMemoBtn.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMemoBtn.setVisibility(GONE);
            }
        }).withLayer();


        mTrashCan.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                mTrashCan.setVisibility(VISIBLE);
                mTrashCan.setImageResource(R.drawable.ic_memo_trashcan);
            }
        }).withLayer();

    }

    private void playTrashCanOpenAnimation() {
        LogUtils.d(TAG, "open trash can");
        mTrashCan.setImageResource(R.drawable.ic_memo_trashcan_open);
    }

    private void playTrashCanCloseAnimation() {
        LogUtils.d(TAG, "close trash can");
        mTrashCan.setImageResource(R.drawable.ic_memo_trashcan);
    }


    private void playTrashMemoAnimation(String memoId, View memoView) {
        LogUtils.d(TAG, "trash memo");
        mPresenter.deleteMemo(memoId);
        memoView.setOnTouchListener(null);
        mMemoContainer.removeView(memoView);
    }


    private void showAddBtnHideTrashCan() {
        LogUtils.d(TAG, "showAddBtnShowTrashCan");
        mDragToTrashCan = false;
        mTrashCan.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTrashCan.setVisibility(GONE);
            }
        }).withLayer();
        mMemoBtn.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMemoBtn.setVisibility(VISIBLE);
                mMemoBtn.setAlpha(1f);
                mMemoBtn.setEnabled(true);
            }
        }).withLayer();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void startMessageBoard(String id);
    }
}