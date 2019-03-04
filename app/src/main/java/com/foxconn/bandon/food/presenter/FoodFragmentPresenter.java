package com.foxconn.bandon.food.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.food.IFoodContract;
import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.food.model.FridgeFoodRepository;
import com.foxconn.bandon.food.photo.PhotoUtils;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;
import java.util.Calendar;
import java.util.List;

public class FoodFragmentPresenter implements IFoodContract.Presenter {
    private static final String TAG = FoodFragmentPresenter.class.getSimpleName();
    private IFoodContract.View mView;
    private FridgeFoodRepository mRepository;
    private UseCaseHandler mUseCaseHandler;

    public FoodFragmentPresenter(IFoodContract.View view, FridgeFoodRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mView.setPresenter(this);
        mUseCaseHandler = UseCaseHandler.getInstance();
    }

    @Override
    public void load(int sPosition) {
        GetFoods get = new GetFoods(mRepository);
        GetFoods.RequestValue value = new GetFoods.RequestValue(sPosition);
        mUseCaseHandler.execute(get, value, new UseCase.UseCaseCallback<GetFoods.ResponseValue>() {
            @Override
            public void onSuccess(GetFoods.ResponseValue response) {
                LogUtils.d(TAG, "sPosition:" + response.getsPosition());
                FridgeFood fridgeFood = response.getFridgeFood();
                if (null != fridgeFood && null != fridgeFood.data && null != fridgeFood.data.listFoodList) {
                    List<FridgeFood.Label> labels = fridgeFood.data.listFoodList;
                    GTMessageManager.getInstance().insertOrUpdate(labels);
                    if (response.getsPosition() == Constant.AREA_VEGETABLE) {
                        mView.updateVegetableView(labels);
                    } else {
                        mView.updateFreezerView(labels);
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void getFridgeFoods() {
        GetFridgeFoods getFoods = new GetFridgeFoods(mRepository);
        GetFridgeFoods.RequestValue value = new GetFridgeFoods.RequestValue();
        mUseCaseHandler.execute(getFoods, value, new UseCase.UseCaseCallback<GetFridgeFoods.ResponseValue>() {
            @Override
            public void onSuccess(GetFridgeFoods.ResponseValue response) {
                ColdRoomFood fridgeFood = response.getFridgeFood();
                if (null != fridgeFood && null != fridgeFood.data && null != fridgeFood.data.list) {
                    List<ColdRoomFood.Label> labels = fridgeFood.data.list;
                    mView.updateFridgeView(labels);
                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void updateLocation(ColdRoomFood.Label label) {
        UpdateLocation update = new UpdateLocation(mRepository);
        UpdateLocation.RequestValue value = new UpdateLocation.RequestValue(label);
        mUseCaseHandler.execute(update, value, new UseCase.UseCaseCallback<UpdateLocation.ResponseValue>() {
            @Override
            public void onSuccess(UpdateLocation.ResponseValue response) {
                String info = response.getResponse().getInfo();
                LogUtils.d(TAG, "updateLocation " + info);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void getFridgeImage(int index) {
        String url = PhotoUtils.getInstance().getImageUrl(index);
        if (!TextUtils.isEmpty(url)) {
            mView.setFridgeImage(url, index);
        } else {
            getCameraPhoto(index);
        }

    }

    @Override
    public void getCameraPhoto(final int index) {
        LogUtils.d(TAG, "getCameraPhoto[" + index + "]");
        GetCameraPhoto get = new GetCameraPhoto(mRepository);
        GetCameraPhoto.RequestValue value = new GetCameraPhoto.RequestValue(index);
        mUseCaseHandler.execute(get, value, new UseCase.UseCaseCallback<GetCameraPhoto.ResponseValue>() {
            @Override
            public void onSuccess(GetCameraPhoto.ResponseValue response) {

                String url = response.getUrl();
                cacheFridgePhoto(url, index);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void cacheFridgePhoto(final String url, final int index) {
        final ObjectKey key = new ObjectKey(Calendar.getInstance().toString());
        final Context context = BandonApplication.getInstance().getApplicationContext();
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .signature(key)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        PreferenceUtils.setString(context, Constant.SP_PHOTOS_NAME, Constant.KEY_PHOTOS + String.valueOf(index), new Gson().toJson(key));
                        mView.setFridgeImage(url, index);
                        return false;
                    }
                })
                .preload();
    }
}
