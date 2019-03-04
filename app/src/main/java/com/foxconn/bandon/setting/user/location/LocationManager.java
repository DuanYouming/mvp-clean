package com.foxconn.bandon.setting.user.location;

import android.content.Context;
import com.foxconn.bandon.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class LocationManager {

    private static final String LABEL_SHARED_PREFERENCES = "location";
    private static final String LABEL_ENTRY_KEY_PREFIX = "device_location";
    private static Gson sGson = new GsonBuilder().create();

    public static void updateDeviceLocation(Context context, DeviceLocation deviceLocation) {
        PreferenceUtils.setString(context, LABEL_SHARED_PREFERENCES, LABEL_ENTRY_KEY_PREFIX, sGson.toJson(deviceLocation));
    }

    public static DeviceLocation getDeviceLocation(Context context) {
        return sGson.fromJson(PreferenceUtils.getString(context, LABEL_SHARED_PREFERENCES, LABEL_ENTRY_KEY_PREFIX, "{}"), DeviceLocation.class);
    }

}
