package com.foxconn.bandon.messages.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.main.view.MainFragment;
import com.foxconn.bandon.messages.MessageBoardContract;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.messages.presenter.MessageBoardPresenter;
import com.foxconn.bandon.messages.model.MemoRepository;
import com.foxconn.bandon.messages.insert.InsertImage;
import com.foxconn.bandon.messages.insert.InsertImageView;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;

import java.util.UUID;


public class MessageBoardFragment extends BaseFragment implements MessageBoardContract.View {

    public static final String TAG = MessageBoardFragment.class.getName();
    public static final String KEY_MEMO_ID = "key_memo_id";
    public static final String KEY_UPDATE_MEMO = "key_update_memo";
    protected static final int MODE_PEN = 0;
    protected static final int MODE_ERASER = 1;
    private static final int CONTAINER_WIDTH = 782;
    private static int mSelected = 0;
    private DrawingPanel mDrawingPanel;
    private ImagePanel mImagePanel;
    private View mStrokeWidthPane;
    private View mRootView;
    private View mColorPane;
    private int[] mButtons;
    private int[] mColors;
    private RadioButton mPenButton;
    private RadioButton mEraserButton;
    private RadioButton mMovingButton;
    private View mColorButton;
    private MessageBoardContract.Presenter mPresenter;
    private Memo mMemo;
    private ConfirmView mConfirmView;
    private InsertImageView mInsertView;
    private EmptyDialog mEmptyDialog;
    private LoadingView mLoadingView;


    public MessageBoardFragment() {

    }

    public static MessageBoardFragment newInstance() {
        return new MessageBoardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_message_board, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDrawingBoard(view);
        initStrokeWidthPane(view);
        initColorPane(view);
        initFunctionButtons(view);
        initRadioButton(view);
        new MessageBoardPresenter(this, MemoRepository.getInstance(BandonDataBase.getInstance(getContext()).memoDao(), new AppExecutors()));
        initMemo();
    }

    private void initMemo() {
        Bundle bundle = getBundle();
        if (null == bundle) {
            return;
        }
        String id = bundle.getString(KEY_MEMO_ID);
        if (null != id) {
            mPresenter.getMemoById(id);
        }
    }

    @Override
    public void updateMemo(Memo memo) {
        mMemo = memo;
        if (null != mMemo) {
            mDrawingPanel.setActionData(memo.getDrawActions());
            mImagePanel.setActionData(memo.getImageActions());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MemoRepository.destroy();
        mListener.removeView(mConfirmView);
        mListener.removeView(mEmptyDialog);
        mListener.removeView(mLoadingView);
        mListener.removeView(mInsertView);
    }

    @Override
    public void showLoadingView() {
        mLoadingView = new LoadingView(getContext());
        mListener.addView(mLoadingView);
    }

    @Override
    public void hideLoadingView() {
        mListener.removeView(mLoadingView);
    }

    @Override
    public void showDialog() {
        mEmptyDialog = new EmptyDialog(getContext());
        EmptyDialog.Callback callback = () -> mListener.removeView(mEmptyDialog);
        mEmptyDialog.setCallback(callback);
        mListener.addView(mEmptyDialog);
    }

    @Override
    public void startMain() {
        Bundle bundle = initBundle(MainFragment.TAG);
        bundle.putBoolean(KEY_UPDATE_MEMO, true);
        mListener.startFragment(bundle);
    }

    @Override
    public void setPresenter(MessageBoardContract.Presenter presenter) {
        if (null == presenter) {
            LogUtils.d(TAG, "setPresenter：presenter is null");
            return;
        }
        mPresenter = presenter;
    }

    private void initDrawingBoard(View view) {
        mDrawingPanel = view.findViewById(R.id.drawing_pane);
        mDrawingPanel.setDrawable(true);
        mDrawingPanel.setCallback(new DrawingPanel.Callback() {
            @Override
            public void onActionDown() {
                mStrokeWidthPane.setVisibility(View.GONE);
                mColorPane.setVisibility(View.GONE);
            }

            @Override
            public void onActionUp() {
                if (mPenButton.isEnabled() || mEraserButton.isEnabled()) {
                    mStrokeWidthPane.setVisibility(View.VISIBLE);
                }
            }
        });
        mImagePanel = view.findViewById(R.id.image_panel);
        mImagePanel.setDraggable(false);
    }

    private void initRadioButton(View view) {
        mPenButton = view.findViewById(R.id.pen_btn);
        mEraserButton = view.findViewById(R.id.eraser_btn);
        mMovingButton = view.findViewById(R.id.move_btn);
        mPenButton.setOnCheckedChangeListener(checkedChangeListener);
        mEraserButton.setOnCheckedChangeListener(checkedChangeListener);
        mMovingButton.setOnCheckedChangeListener(checkedChangeListener);
        mPenButton.setChecked(true);
    }

    private void initFunctionButtons(View view) {

        View clearButton = view.findViewById(R.id.clear_btn);
        clearButton.setOnClickListener(functionClickListener);
        clearButton.setEnabled(true);

        View saveButton = view.findViewById(R.id.save_btn);
        saveButton.setOnClickListener(functionClickListener);
        saveButton.setEnabled(true);

        View undoButton = view.findViewById(R.id.undo_btn);
        undoButton.setOnClickListener(functionClickListener);
        undoButton.setEnabled(true);

        mColorButton = view.findViewById(R.id.color_btn);
        mColorButton.setOnClickListener(functionClickListener);
        mColorButton.setEnabled(true);

        View insertButton = view.findViewById(R.id.insert_btn);
        insertButton.setOnClickListener(functionClickListener);
        insertButton.setEnabled(true);
    }

    private void initColorPane(View view) {
        mColorPane = view.findViewById(R.id.colors);
        mButtons = new int[]{R.id.color_black, R.id.color_blue, R.id.color_red, R.id.color_green, R.id.color_grey};
        mColors = new int[]{R.color.pen_black, R.color.pen_blue, R.color.pen_red, R.color.pen_green, R.color.pen_grey};
        for (int i = 0; i < mButtons.length; i++) {
            View button = view.findViewById(mButtons[i]);
            button.setOnClickListener(colorBtnClickListener);
            button.setTag(i);
            if (mSelected == i) {
                button.setSelected(true);
                mDrawingPanel.setPaintColor(ContextCompat.getColor(getContext(), mColors[i]));
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initStrokeWidthPane(View view) {
        mStrokeWidthPane = view.findViewById(R.id.stroke_width_pane);
        final View container = view.findViewById(R.id.seek_bar_container);
        final View thumb = view.findViewById(R.id.seek_bar_thumb);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) thumb.getLayoutParams();
        int margin = (int) ((CONTAINER_WIDTH * DrawingPanel.DEFAULT_WIDTH) / 100F);
        lp.setMarginStart(margin);
        thumb.setLayoutParams(lp);
        container.setOnTouchListener((view1, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    final int left = (int) Math.min(Math.max(0, event.getX() - thumb.getWidth() / 2), container.getWidth() - thumb.getWidth());
                    lp.leftMargin = left;
                    thumb.setLayoutParams(lp);
                    float strokeWidth = 100F * left / container.getWidth();
                    mDrawingPanel.setPaintStrokeWidth(strokeWidth);
                    break;
            }
            return true;
        });
    }

    private void clear() {
        mConfirmView = new ConfirmView(getContext());
        mConfirmView.setCallback(new ConfirmView.Callback() {
            @Override
            public void confirm() {
                mImagePanel.reset();
                mDrawingPanel.reset();
                mListener.destroyView();
            }

            @Override
            public void cancel() {
                mListener.removeView(mConfirmView);
            }
        });
        mListener.addView(mConfirmView);
    }

    private void save() {
        showLoadingView();
        Bitmap bitmap = new DrawMerge(mDrawingPanel, mImagePanel).toBitmap();
        if (null == mMemo) {
            String id = UUID.randomUUID().toString();
            long times = System.currentTimeMillis();
            String path = "image_" + times + ".png";
            String drawActions = mDrawingPanel.getActionData();
            String imageActions = mImagePanel.getImageActionData();
            mMemo = new Memo(id, path, drawActions, imageActions, null, 0, 0, times);
        } else {
            mMemo.setDrawActions(mDrawingPanel.getActionData());
            mMemo.setImageActions(mImagePanel.getImageActionData());
        }
        mMemo.setBitmap(bitmap);
        mPresenter.save(mMemo);
    }

    private void undo() {
        LogUtils.d(TAG, "undo");
        mDrawingPanel.deleteLastPath();
    }

    private void selectColor() {
        mColorPane.setVisibility(View.VISIBLE);
        mStrokeWidthPane.setVisibility(View.GONE);
    }

    private void insert() {
        mInsertView = new InsertImageView(getContext());
        mInsertView.setCallback(new InsertImageView.InsertCallback() {
            @Override
            public void cancel() {
                mListener.destroyView();
            }

            @Override
            public void confirm(InsertImage image) {
                mImagePanel.add(image);
                mListener.removeView(mInsertView);
            }
        });
        mListener.addView(mInsertView);
    }

    private void usePen() {
        LogUtils.d(TAG, "usePen");
        mEraserButton.setChecked(false);
        mMovingButton.setChecked(false);
        mDrawingPanel.setDrawable(true);
        mImagePanel.setDraggable(false);
        mColorButton.setEnabled(true);
        mDrawingPanel.setMode(MODE_PEN);
        mStrokeWidthPane.setVisibility(View.VISIBLE);
        mColorPane.setVisibility(View.GONE);

    }

    private void useEraser() {
        LogUtils.d(TAG, "useEraser");
        mPenButton.setChecked(false);
        mMovingButton.setChecked(false);
        mDrawingPanel.setDrawable(true);
        mImagePanel.setDraggable(false);
        mDrawingPanel.setMode(MODE_ERASER);
        mStrokeWidthPane.setVisibility(View.VISIBLE);
        mColorPane.setVisibility(View.GONE);
    }

    private void useMoving() {
        LogUtils.d(TAG, "useMoving");
        mPenButton.setChecked(false);
        mEraserButton.setChecked(false);
        mDrawingPanel.setDrawable(false);
        mImagePanel.setDraggable(true);
        mStrokeWidthPane.setVisibility(View.GONE);
        mColorPane.setVisibility(View.GONE);
    }

    private View.OnClickListener colorBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            LogUtils.d(TAG, "colorBtnClickListener:index" + index + "  selected:" + mSelected);
            if (index != mSelected) {
                view.setSelected(true);
                int color = ContextCompat.getColor(getContext(), mColors[index]);
                mDrawingPanel.setPaintColor(color);
                mRootView.findViewById(mButtons[mSelected]).setSelected(false);
                mSelected = index;
            }
            LogUtils.d(TAG, "colorBtnClickListener:selected" + mSelected);
            mColorPane.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener functionClickListener = view -> {
        int id = view.getId();
        switch (id) {
            case R.id.clear_btn:
                clear();
                break;
            case R.id.save_btn:
                save();
                break;
            case R.id.undo_btn:
                undo();
                break;
            case R.id.color_btn:
                selectColor();
                break;
            case R.id.insert_btn:
                LogUtils.d(TAG, "insert");
                insert();
                break;
            default:
                break;

        }
    };

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton button, boolean checked) {
            int id = button.getId();
            if (checked) {
                if (id == R.id.pen_btn) {
                    usePen();
                } else if (id == R.id.eraser_btn) {
                    useEraser();
                } else {
                    useMoving();
                }
            } else {
                if (id == R.id.pen_btn)
                    mColorButton.setEnabled(false);
            }
        }
    };
}
