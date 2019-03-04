package com.foxconn.bandon.food.view;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.foxconn.bandon.MainActivity;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.cropper.CropperFragment;
import com.foxconn.bandon.food.IFoodContract;
import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.presenter.FoodFragmentPresenter;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.label.add.AddLabelFragment;
import com.foxconn.bandon.label.detail.view.LabelDetailFragment;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android_cameraarray_api.CaptureManager;


public class FoodsFragment extends BaseFragment implements IFoodContract.View {
    public static final String TAG = FoodsFragment.class.getName();
    private FrameLayout mContainer;
    private VegetableView mVegetableView;
    private FridgeView mFridgeView;
    private FreezerView mFreezerView;
    private IFoodContract.Presenter mPresenter;
    private List<View> tabViews = new ArrayList<>();
    private List<View> itemViews = new ArrayList<>();
    private int mSelectedIndex;
    private TextView mTvVegetable;
    private TextView mTvFridge;
    private TextView mTvFreezer;

    public FoodsFragment() {
    }

    public static FoodsFragment newInstance() {
        return new FoodsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        mListener.registNavigationCallback(navigationCallback);
        CaptureManager.getInstance().registerCaptureCallback(captureCallback);
        new FoodFragmentPresenter(this, FridgeFoodRepository.getInstance(getActivity(), new AppExecutors()));
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabViews(view);
        initAddLabelButton(view);
        mSelectedIndex = Constant.AREA_FRIDGE;
        mPresenter.load(Constant.AREA_VEGETABLE);
        mPresenter.load(Constant.AREA_FREEZER);
        mPresenter.load(Constant.AREA_FRIDGE);
        mPresenter.getFridgeFoods();
        for (int i = 0; i < FridgeView.IMAGES_NUM; i++) {
            mPresenter.getFridgeImage(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tabViews.get(mSelectedIndex).callOnClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.unregistNavigationCallback(navigationCallback);
        CaptureManager.getInstance().unregisterCaptureCallback();
        FridgeFoodRepository.destroyInstance();
        tabViews.clear();
        itemViews.clear();
    }

    @Override
    public void updateVegetableView(List<FridgeFood.Label> labels) {
        if (isDetached()) {
            LogUtils.d(TAG, "fragment is detached");
            return;
        }
        boolean isExpired = haveExpiredFood(labels);
        Drawable drawable = getDrawable(isExpired);
        mTvVegetable.setCompoundDrawables(mTvVegetable.getCompoundDrawables()[0], null, drawable, null);
        mVegetableView.setLabels(labels);
    }

    @Override
    public void updateFridgeView(List<ColdRoomFood.Label> labels) {
        if (isDetached()) {
            LogUtils.d(TAG, "fragment is detached");
            return;
        }
        boolean isExpired = isExpired(labels);
        Drawable drawable = getDrawable(isExpired);
        Drawable[] drawables = mTvFridge.getCompoundDrawables();
        mTvFridge.setCompoundDrawables(drawables[0], null, drawable, null);
        mFridgeView.setLabels(labels);
    }

    @Override
    public void updateFreezerView(List<FridgeFood.Label> labels) {
        if (isDetached()) {
            LogUtils.d(TAG, "fragment is detached");
            return;
        }
        boolean isExpired = haveExpiredFood(labels);
        Drawable drawable = getDrawable(isExpired);
        Drawable[] drawables = mTvFreezer.getCompoundDrawables();
        mTvFreezer.setCompoundDrawables(drawables[0], null, drawable, null);
        mFreezerView.setLabels(labels);
    }

    @Override
    public void setFridgeImage(String url, int index) {
        LogUtils.d(TAG, "setFridgeImage:" + url);
        if (isDetached()) {
            LogUtils.d(TAG, "fragment is detached");
            return;
        }
        mFridgeView.setImageUrl(url, index);
    }

    @Override
    public void setPresenter(IFoodContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void initTabViews(View view) {
        tabViews.add(Constant.AREA_FRIDGE, view.findViewById(R.id.fridge_section));
        tabViews.add(Constant.AREA_VEGETABLE, view.findViewById(R.id.vegetable_section));
        tabViews.add(Constant.AREA_FREEZER, view.findViewById(R.id.freezer_section));
        for (int i = 0; i < tabViews.size(); i++) {
            tabViews.get(i).setOnClickListener(tabClickListener);
        }

        mTvVegetable = view.findViewById(R.id.text_vegetable);
        mTvFridge = view.findViewById(R.id.text_fridge);
        mTvFreezer = view.findViewById(R.id.text_freezer);

        mContainer = view.findViewById(R.id.container);
        mFridgeView = new FridgeView(getActivity());
        itemViews.add(Constant.AREA_FRIDGE, mFridgeView);
        mVegetableView = new VegetableView(getActivity());
        itemViews.add(Constant.AREA_VEGETABLE, mVegetableView);
        mFreezerView = new FreezerView(getActivity());
        itemViews.add(Constant.AREA_FREEZER, mFreezerView);
        mVegetableView.setClickCallback(clickCallback);
        mFreezerView.setClickCallback(clickCallback);
        mFridgeView.setCallback(fridgeViewCallback);

    }

    private void initAddLabelButton(View view) {
        view.findViewById(R.id.image_add).setOnClickListener(v -> {
            Bundle bundle = initBundle(AddLabelFragment.TAG);
            mListener.startFragment(bundle);
        });
    }


    private void selectView(boolean isSelect, int index) {
        tabViews.get(index).setSelected(isSelect);
        if (isSelect) {
            mContainer.addView(itemViews.get(index));
        } else {
            mContainer.removeView(itemViews.get(index));
        }
    }

    @Nullable
    private Drawable getDrawable(boolean isExpired) {
        if (isExpired && null != getActivity()) {
            int size = 34;
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification_red);
            drawable.setBounds(0, 0, size, size);
            return drawable;
        }
        return null;
    }

    private boolean haveExpiredFood(List<FridgeFood.Label> labels) {
        for (FridgeFood.Label label : labels) {
            LogUtils.d(TAG, "label:" + label.foodTagRename);
            if (TextUtils.equals(label.iconColor, Constant.FOOD_TYPE_EXPIRED)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExpired(List<ColdRoomFood.Label> labels) {
        for (ColdRoomFood.Label label : labels) {
            LogUtils.d(TAG, "label:" + label.foodTagRename);
            if (TextUtils.equals(label.iconColor, Constant.FOOD_TYPE_EXPIRED)) {
                return true;
            }
        }
        return false;
    }


    View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                selectView(false, mSelectedIndex);
                mSelectedIndex = tabViews.indexOf(v);
                selectView(true, mSelectedIndex);
            }
        }
    };

    private void startLabelDetailFragment(int id) {
        Bundle bundle = initBundle(LabelDetailFragment.TAG);
        bundle.putInt(LabelDetailFragment.KEY_ITEM_ID, id);
        mListener.startFragment(bundle);
    }

    FridgeCommonView.ClickCallback clickCallback = this::startLabelDetailFragment;

    FridgeView.Callback fridgeViewCallback = new FridgeView.Callback() {
        @Override
        public void updateLocation(ColdRoomFood.Label label) {
            mPresenter.updateLocation(label);
        }

        @Override
        public void onClick(int id) {
            startLabelDetailFragment(id);
        }

        @Override
        public void startToCropper(int index) {
            Bundle bundle = initBundle(CropperFragment.TAG);
            bundle.putInt(CropperFragment.KEY_PHOTO_INDEX, index);
            mListener.startFragment(bundle);
        }
    };
    MainActivity.NavigationCallback navigationCallback = new MainActivity.NavigationCallback() {
        @Override
        public boolean onBackClick() {
            mListener.home();
            return true;
        }

        @Override
        public boolean onVoiceClick() {
            return false;
        }
    };

    private CaptureManager.CaptureCallback captureCallback = () -> {
        for (int i = 0; i < FridgeView.IMAGES_NUM; i++) {
            mPresenter.getFridgeImage(i);
        }
    };
}
