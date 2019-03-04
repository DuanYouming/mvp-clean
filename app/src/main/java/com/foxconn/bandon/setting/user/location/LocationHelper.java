package com.foxconn.bandon.setting.user.location;

import android.content.Context;
import android.text.TextUtils;

import com.foxconn.bandon.utils.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LocationHelper {
    private static final String TAG = LocationHelper.class.getSimpleName();
    private static LocationHelper instance;
    private Map<String, Map<String, ArrayList<String>>> mProvinceMap = new HashMap<>();
    private Map<String, DeviceLocation> mAllLocationMap = new HashMap<>();
    private JSONObject mLocJson;

    private LocationHelper(Context context) {
        loadLocation(context);
    }

    public static LocationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocationHelper(context);
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public Map<String, Map<String, ArrayList<String>>> getProvinceMap() {
        return mProvinceMap;
    }


    private void loadLocation(Context context) {
        try (InputStream raw = context.getAssets().open("china_administrative_divisions_with_geo.js");
             BufferedReader buffIn = new BufferedReader(new InputStreamReader(raw, "UTF8"))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = buffIn.readLine()) != null) {
                content.append(line);
            }
            mLocJson = new JSONObject(content.toString());
            Iterator<?> provinceKeys = mLocJson.keys();
            String provinceKey;
            Iterator<?> cityKeys;
            String cityKey;
            Iterator<?> districtKeys;
            String districtKey;
            mProvinceMap = new HashMap<>();
            Map<String, ArrayList<String>> cityMap;
            ArrayList<String> districtList;
            mAllLocationMap = new HashMap<>();
            while (provinceKeys.hasNext()) {
                provinceKey = (String) provinceKeys.next();
                if (mLocJson.get(provinceKey) instanceof JSONObject) {
                    cityMap = new HashMap<>();
                    mProvinceMap.put(provinceKey, cityMap);
                    cityKeys = mLocJson.getJSONObject(provinceKey).keys();
                    while (cityKeys.hasNext()) {
                        cityKey = (String) cityKeys.next();
                        if (mLocJson.getJSONObject(provinceKey).get(cityKey) instanceof JSONObject) {
                            districtList = new ArrayList<>();
                            cityMap.put(cityKey, districtList);
                            districtKeys = mLocJson.getJSONObject(provinceKey).getJSONObject(cityKey).keys();
                            while (districtKeys.hasNext()) {
                                districtKey = (String) districtKeys.next();
                                if (mLocJson.getJSONObject(provinceKey).getJSONObject(cityKey).get(districtKey) instanceof JSONObject) {
                                    districtList.add(districtKey);
                                    mAllLocationMap.put(provinceKey + cityKey + districtKey, mappingLocation(provinceKey, cityKey, districtKey));
                                }
                            }
                            if (districtList.size() == 0) {
                                mAllLocationMap.put(provinceKey + cityKey, mappingLocation(provinceKey, cityKey, ""));
                            }
                        }
                    }
                    if (cityMap.size() == 0) {
                        mAllLocationMap.put(provinceKey, mappingLocation(provinceKey, "", ""));
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "Exception:" + e.toString());
        }
    }

    public DeviceLocation mappingLocation(String province, String city, String district) throws JSONException {
        JSONObject provinceJson = mLocJson.getJSONObject(province);
        DeviceLocation dLocation = new DeviceLocation();
        dLocation.setProvince(province);
        dLocation.setCity(city);
        dLocation.setDistrict(district);

        if (!TextUtils.isEmpty(city) && provinceJson.get(city) instanceof JSONObject) {
            JSONObject cityJson = provinceJson.getJSONObject(city);

            if (!TextUtils.isEmpty(district) && cityJson.get(district) instanceof JSONObject) {
                JSONObject districtJson = cityJson.getJSONObject(district);

                dLocation.setLat(districtJson.getDouble("lat"));
                dLocation.setLon(districtJson.getDouble("lon"));
            } else {
                dLocation.setLat(cityJson.getDouble("lat"));
                dLocation.setLon(cityJson.getDouble("lon"));
            }
        } else {
            dLocation.setLat(provinceJson.getDouble("lat"));
            dLocation.setLon(provinceJson.getDouble("lon"));
        }


        return dLocation;
    }

    public Map<String, DeviceLocation> getAllLocationMap() {
        return mAllLocationMap;
    }
}
