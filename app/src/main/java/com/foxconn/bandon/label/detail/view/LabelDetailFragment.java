package com.foxconn.bandon.label.detail.view;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.food.view.FoodsFragment;
import com.foxconn.bandon.gtm.model.FoodMessage;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.gtm.presenter.GetFoodMessage;
import com.foxconn.bandon.label.detail.ILabelDetailFragmentContract;
import com.foxconn.bandon.label.detail.model.LabelDetail;
import com.foxconn.bandon.label.detail.model.LabelDetailRepository;
import com.foxconn.bandon.label.detail.model.LabelItem;
import com.foxconn.bandon.label.detail.presenter.LabelDetailPresenter;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.DateUtils;
import com.foxconn.bandon.utils.DeviceUtils;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LabelDetailFragment extends BaseFragment implements ILabelDetailFragmentContract.View {

    public static final String TAG = LabelDetailFragment.class.getName();
    public static final String KEY_ITEM_NAME = "key_item_name";
    public static final String KEY_ITEM_ID = "key_item_id";
    private static final int DEFAULT_ID = -1;
    private ILabelDetailFragmentContract.Presenter mPresenter;
    private FrameLayout mDialogContainer;
    private Date mCreateDate;
    private Date mExpirationDate;
    private ProgressBar mProgressBar;
    private ImageView mImageItemIcon;
    private TextView mTextExplainContent;
    private TextView mTextLabelName;
    private TextView mTextCreateDate;
    private SimpleDateFormat mDateFormat;
    private TextView mTextExpirationDate;
    private TextView mTextQuantity;
    private int mItemID;
    private int mPosition;
    private int mItemQuantity = 1;
    private String mItemName;
    private String mItemAlias = "";
    private String mIconUrl;
    private String mFoodNutrition;
    private List<View> mAreaViews;
    private DatePickerView mPickerView;
    private TextView mBtnCancel;
    private TextView mBtnConfirm;


    public LabelDetailFragment() {
    }


    public static LabelDetailFragment newInstance() {
        return new LabelDetailFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new LabelDetailPresenter(this, LabelDetailRepository.getInstance(new AppExecutors()));
        return inflater.inflate(R.layout.fragment_label_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleView(view);
        initIconView(view);
        initLabelCreateDateView(view);
        initExpirationDateView(view);
        initQuantityView(view);
        initStoreAreaView(view);
        initExplainContentView(view);
        initButtonsView(view);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LabelDetailRepository.destroyInstance();
        resetData();
    }

    @Override
    public void updateLabelItem(LabelItem item) {
        mIconUrl = item.getData().getFoodTagBigPicture();
        mFoodNutrition = item.getData().getFoodNutrition();
        mPosition = Constant.AREA_FRIDGE;
        mItemAlias = "";
        mCreateDate = new Date();
        mExpirationDate = null;
        mItemQuantity = 1;
        mItemID = DEFAULT_ID;
        updateView();
    }

    @Override
    public void updateLabelDetail(LabelDetail detail) {
        mIconUrl = detail.data.foodTagBigPicture;
        mItemAlias = detail.data.foodTagRename;
        mFoodNutrition = detail.data.foodNutrition;
        mPosition = Integer.valueOf(detail.data.storageRegion);
        mItemQuantity = Integer.valueOf(detail.data.numberSurplus);
        mCreateDate = DateUtils.getDate(detail.data.createTime);
        mExpirationDate = DateUtils.getDate(detail.data.due_date);
        updateView();
    }

    @Override
    public void showExceptionView() {

    }

    @Override
    public void updateOrSaveGTMessage() {
        //GTMessageManager.getInstance().initFoodMessages();
    }

    @Override
    public void deleteGTMessage() {
        GTMessageManager.getInstance().getFoodMessage(mItemID, new UseCase.UseCaseCallback<GetFoodMessage.ResponseValue>() {
            @Override
            public void onSuccess(GetFoodMessage.ResponseValue response) {
                FoodMessage message = response.getMessage();
                if (null == message) {
                    LogUtils.d(TAG, "FoodMessage is null");
                    return;
                }
                GTMessageManager.getInstance().deleteGTMessage(message.getId(), GTMessage.TYPE_FOOD);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startToFoodsView() {
        Bundle bundle = initBundle(FoodsFragment.TAG);
        mListener.startFragment(bundle);
    }

    @Override
    public void setPresenter(ILabelDetailFragmentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void initTitleView(View view) {
        mTextLabelName = view.findViewById(R.id.text_item_name);
        mTextLabelName.setText(mItemName);
        ImageView imageUpdateName = view.findViewById(R.id.image_update_item_name);
        imageUpdateName.setOnClickListener(v -> {
            final UpdateLabelAliasView updateView = new UpdateLabelAliasView(getContext());
            updateView.setCallback(new UpdateLabelAliasView.Callback() {
                @Override
                public void cancel() {
                    dismissDialog(updateView);
                }

                @Override
                public void confirm(String alias) {
                    dismissDialog(updateView);
                    if (!TextUtils.isEmpty(alias)) {
                        mItemAlias = alias;
                    } else {
                        mItemAlias = mItemName;
                    }
                    mTextLabelName.setText(mItemAlias);

                }
            });
            showDialog(updateView);
        });
    }

    private void initIconView(View view) {
        mProgressBar = view.findViewById(R.id.progress_bar);
        mImageItemIcon = view.findViewById(R.id.image_item_icon);
    }

    private void updateView() {
        mProgressBar.setVisibility(View.VISIBLE);
        GlideApp.with(getContext()).asBitmap().centerCrop().load(mIconUrl).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(getContext(), "load failed", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "load success", Toast.LENGTH_LONG).show();
                return false;
            }
        }).into(mImageItemIcon);

        mTextExplainContent.setText(mFoodNutrition);
        if (!TextUtils.isEmpty(mItemAlias)) {
            mTextLabelName.setText(mItemAlias);
        } else {
            mTextLabelName.setText(mItemName);
        }
        mTextCreateDate.setText(mDateFormat.format(mCreateDate));
        mPickerView.setCreateDate(mCreateDate);
        if (null != mExpirationDate) {
            String str = DateUtils.expirationDateFormat(getContext(), mExpirationDate);
            mTextExpirationDate.setText(str);
            mPickerView.setExpirationDate(mExpirationDate);
        }
        mTextQuantity.setText(String.valueOf(mItemQuantity));
        mAreaViews.get(mPosition).callOnClick();
        if (mItemID != DEFAULT_ID) {
            mBtnConfirm.setText(getString(R.string.btn_update));
            mBtnCancel.setText(getString(R.string.btn_delete));
        }
    }

    private void initLabelCreateDateView(View view) {
        mDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        mTextCreateDate = view.findViewById(R.id.text_created_date);
        mCreateDate = new Date();
        mTextCreateDate.setText(mDateFormat.format(mCreateDate));
    }

    private void initExpirationDateView(View view) {
        mTextExpirationDate = view.findViewById(R.id.expire_hint);
        mDialogContainer = view.findViewById(R.id.dialog_container);
        mTextExpirationDate.setText(getString(R.string.food_label_expired_none));
        mPickerView = new DatePickerView(getContext(), mCreateDate, mExpirationDate);
        mTextExpirationDate.setOnClickListener(v -> showDialog(mPickerView));
        mPickerView.setCallback(new DatePickerView.Callback() {
            @Override
            public void cancel() {
                dismissDialog(mPickerView);
            }

            @Override
            public void confirm(Date date) {
                dismissDialog(mPickerView);
                mExpirationDate = date;
                String str = DateUtils.expirationDateFormat(getContext(), mExpirationDate);
                mTextExpirationDate.setText(str);
            }
        });

    }

    private void initQuantityView(View view) {
        ImageView btnDecrease = view.findViewById(R.id.quantity_decrease);
        ImageView btnIncrease = view.findViewById(R.id.quantity_increase);
        mTextQuantity = view.findViewById(R.id.item_quantity);
        mTextQuantity.setText(String.valueOf(mItemQuantity));
        QuantityOpt qOpt = new QuantityOpt(getContext(), btnIncrease, btnDecrease, mTextQuantity);
        qOpt.setCallback(number -> {
            mItemQuantity = number;
            LogUtils.d(TAG, "mItemQuantity:" + mItemQuantity);
        });
    }

    private void initStoreAreaView(View view) {
        Button btnVegetable = view.findViewById(R.id.vegetable_crisper_position);
        Button btnFreezer = view.findViewById(R.id.freezer_section_position);
        Button btnFridge = view.findViewById(R.id.fridge_section_position);

        mAreaViews = new ArrayList<>();
        mAreaViews.add(Constant.AREA_FRIDGE, btnFridge);
        mAreaViews.add(Constant.AREA_VEGETABLE, btnVegetable);
        mAreaViews.add(Constant.AREA_FREEZER, btnFreezer);

        for (View areaView : mAreaViews) {
            areaView.setOnClickListener(clickListener);
        }


    }


    private void initExplainContentView(View view) {
        mTextExplainContent = view.findViewById(R.id.text_explain);
    }

    private void initButtonsView(View view) {
        mBtnConfirm = view.findViewById(R.id.confirm_btn);
        mBtnConfirm.setOnClickListener(v -> {
            if (null != mExpirationDate) {
                mPresenter.save(initRequestBody(getJsonObject()));
            } else {
                showToast(getString(R.string.food_label_expired_none));
            }
        });
        mBtnCancel = view.findViewById(R.id.cancel_btn);
        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (mItemID != DEFAULT_ID) {
                mPresenter.delete(initRequestBody(getDeleteJsonObject()));
            } else {
                finish();
            }
        });

    }

    private void initData() {
        Bundle bundle = getBundle();
        if (null != bundle) {
            mItemName = bundle.getString(KEY_ITEM_NAME);
            mItemID = bundle.getInt(KEY_ITEM_ID, DEFAULT_ID);
            if (!TextUtils.isEmpty(mItemName)) {
                mPresenter.getLabelItem(mItemName);
            } else if (mItemID != DEFAULT_ID) {
                mPresenter.getLabelDetail(mItemID);
            }
        }
    }

    public void resetData() {
        mAreaViews.get(mPosition).setSelected(false);
    }

    private RequestBody initRequestBody(JSONObject data) {
        return RequestBody.create(MediaType.parse("application/json"), data.toString());
    }

    @NonNull
    private JSONObject getJsonObject() {
        JSONObject data = new JSONObject();
        try {
            data.put("deviceId", DeviceUtils.getDeviceId(getContext()));
            data.put("foodTagRename", mItemAlias);
            data.put("foodTagName", mItemName);
            data.put("xCoordinate", "%2");
            data.put("yCoordinate", "%2");
            data.put("storageRegion", String.valueOf(mPosition));
            data.put("numberSurplus", String.valueOf(mItemQuantity));
            data.put("due_date", DateUtils.dateFormat(mExpirationDate));
            if (mItemID != DEFAULT_ID) {
                data.put("id", String.valueOf(mItemID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "save LabelDetail:" + data.toString());
        return data;
    }

    private JSONObject getDeleteJsonObject() {
        JSONObject data = new JSONObject();
        JSONArray idArray = new JSONArray();
        idArray.put(mItemID);
        try {
            data.put("id", idArray);
            data.put("deviceId", DeviceUtils.getDeviceId(getContext()));
            data.put("tag", "1");
            data.put("storageRegion", String.valueOf(mPosition));
        } catch (JSONException e) {
            LogUtils.e(TAG, "JSONException:" + e.toString());
        }
        return data;
    }


    private void showDialog(View view) {
        mDialogContainer.addView(view);
    }

    private void dismissDialog(View view) {
        mDialogContainer.removeView(view);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                LogUtils.d(TAG, "mPosition:" + mPosition);
                mAreaViews.get(mPosition).setSelected(false);
                mPosition = mAreaViews.indexOf(v);
                v.setSelected(true);
            }
        }
    };
}

