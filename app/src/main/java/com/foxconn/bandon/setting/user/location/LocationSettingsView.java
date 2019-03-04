package com.foxconn.bandon.setting.user.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.NumberStringPicker;
import com.foxconn.bandon.custom.SettingsSearchBar;
import com.foxconn.bandon.device.BindDevice;
import com.foxconn.bandon.device.DeviceManager;
import com.foxconn.bandon.usecase.UseCase;
import com.foxconn.bandon.usecase.UseCaseHandler;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.weather.manager.WeatherManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LocationSettingsView extends FrameLayout {

    private static final String TAG = LocationSettingsView.class.getSimpleName();
    private String mTargetProvince;
    private String mTargetCity;
    private String mTargetDistrict;
    private List<String> mCityList;
    private List<String> mDistrictList;
    private TextView mTextLocation;
    private View mContent;
    private SettingsSearchBar mSearchBar;
    private View mPickerContainer;
    private RecyclerView mLocationListView;
    private NumberStringPicker mProvincePicker;
    private NumberStringPicker mCityPicker;
    private NumberStringPicker mDistrictPicker;
    private LocationAdapter mLocationAdapter;
    private DeviceLocation mDeviceLocation;

    public LocationSettingsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LocationHelper.destroyInstance();
        DeviceManager.destroyInstance();
    }

    private void setup() {
        inflate(getContext(), R.layout.location_settings_view, this);
        initTitle();
        initContent();
        initSearchBar();
        initPickers();
        initListView();
        initButtons();
    }

    private void initTitle() {
        View top = findViewById(R.id.location_top);
        mTextLocation = findViewById(R.id.new_location);
        mDeviceLocation = LocationManager.getDeviceLocation(getContext());
        if (null != mDeviceLocation && !TextUtils.isEmpty(mDeviceLocation.getProvince())) {
            String textLocation = mDeviceLocation.getProvince() + mDeviceLocation.getCity() + mDeviceLocation.getDistrict();
            mTextLocation.setText(textLocation);
        }
        top.setOnClickListener(v -> mContent.setVisibility(VISIBLE));
    }

    private void initContent() {
        mContent = findViewById(R.id.location_content);
        mSearchBar = findViewById(R.id.search_bar);
        mPickerContainer = findViewById(R.id.location_picker_container);
        mLocationListView = findViewById(R.id.location_recycler_view);
    }

    private void initSearchBar() {
        mSearchBar.setStateListener(new SettingsSearchBar.StateListener() {
            @Override
            public void input(String s) {
                if (mLocationListView.getVisibility() == GONE) {
                    mLocationListView.setVisibility(VISIBLE);
                    mPickerContainer.setVisibility(GONE);
                }
                LogUtils.d(TAG, "search for:" + s);
                mLocationAdapter.filter(s);
            }

            @Override
            public void clear() {
                if (mLocationListView.getVisibility() == VISIBLE) {
                    mPickerContainer.setVisibility(VISIBLE);
                    mLocationListView.setVisibility(GONE);
                }
            }
        });
    }

    private void initPickers() {
        mProvincePicker = findViewById(R.id.province_picker);
        mCityPicker = findViewById(R.id.city_picker);
        mDistrictPicker = findViewById(R.id.district_picker);

        mProvincePicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                mTargetProvince = value.toString();
                mCityList = new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).keySet());
                mTargetCity = updatePickerRange(mCityPicker, mCityList, "");
                if (LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).containsKey(mTargetCity)) {
                    mDistrictList = new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).get(mTargetCity));
                } else {
                    mDistrictList = new ArrayList<>();
                }
                LogUtils.d(TAG, "update mDistrictList:" + mDistrictList);
                mTargetDistrict = updatePickerRange(mDistrictPicker, mDistrictList, "");
            }
        });

        mCityPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                LogUtils.d(TAG, "mTargetCity:" + value);
                mTargetCity = (String) value;
                if (LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).containsKey(mTargetCity)) {
                    mDistrictList = new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).get(mTargetCity));
                } else {
                    mDistrictList = new ArrayList<>();
                }
                LogUtils.d(TAG, "update mDistrictList:" + mDistrictList);

                mTargetDistrict = updatePickerRange(mDistrictPicker, mDistrictList, "");

            }
        });

        mDistrictPicker.setScrollListener(new NumberStringPicker.ScrollListener() {
            @Override
            public void onScrolled(Object value) {

            }

            @Override
            public void scrollComplete(Object value) {
                LogUtils.d(TAG, "mTargetDistrict:" + value);
                mTargetDistrict = (String) value;
            }
        });

        initPickerData();
    }

    private void initPickerData() {
        List<String> provinceList = new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().keySet());
        mTargetProvince = updatePickerRange(mProvincePicker, provinceList, mDeviceLocation.getProvince());
        mTargetCity = updatePickerRange(mCityPicker, new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).keySet()), mDeviceLocation.getCity());
        if (!TextUtils.isEmpty(mTargetCity)) {
            mTargetDistrict = updatePickerRange(mDistrictPicker, new ArrayList<>(LocationHelper.getInstance(getContext()).getProvinceMap().get(mTargetProvince).get(mTargetCity)), mDeviceLocation.getDistrict());
        } else {
            mTargetDistrict = updatePickerRange(mDistrictPicker, new ArrayList<>(), "");
        }
    }


    private String updatePickerRange(NumberStringPicker picker, List<String> list, String value) {
        if (list.size() == 0) {
            list.add("");
            picker.setRangeAndLoop(list, false, "");
            return "";
        } else if (list.size() == 1) {
            String scrollTo = TextUtils.isEmpty(value) ? list.get(0) : value;
            picker.setRangeAndLoop(list, false, scrollTo);
            return scrollTo;
        } else {
            String scrollTo = TextUtils.isEmpty(value) ? list.get(1) : value;
            picker.setRangeAndLoop(list, false, scrollTo);
            return scrollTo;
        }
    }

    private void initListView() {
        mLocationAdapter = new LocationAdapter(getContext());
        mLocationAdapter.setUpdateLocationListener(this::updateNewLocation);
        mLocationListView.setAdapter(mLocationAdapter);
        mLocationListView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initButtons() {
        View confirm = findViewById(R.id.ok_btn);
        confirm.setOnClickListener(v -> {
            try {
                updateNewLocation(LocationHelper.getInstance(getContext()).mappingLocation(mTargetProvince, mTargetCity, mTargetDistrict));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        View cancel = findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> mContent.setVisibility(GONE));
    }

    private void updateNewLocation(DeviceLocation deviceLocation) {
        LogUtils.d(TAG, "new location:" + deviceLocation.toString());
        mDeviceLocation = deviceLocation;
        String locationText = deviceLocation.getProvince() + deviceLocation.getCity() + deviceLocation.getDistrict();
        mTextLocation.setText(locationText);
        LocationManager.updateDeviceLocation(getContext(), deviceLocation);
        mContent.setVisibility(GONE);
        WeatherManager.getInstance().getWeather(getContext());
        bindDevice(deviceLocation);
    }

    private void bindDevice(DeviceLocation location) {
        UseCaseHandler handler = UseCaseHandler.getInstance();
        DeviceManager.getInstance().bind(handler, new UseCase.UseCaseCallback<BindDevice.ResponseValue>() {
            @Override
            public void onSuccess(BindDevice.ResponseValue response) {
                LogUtils.d(TAG, "bindDevice:" + response.getResponse().getInfo());
            }

            @Override
            public void onFailure() {

            }
        }, location);
    }

}
