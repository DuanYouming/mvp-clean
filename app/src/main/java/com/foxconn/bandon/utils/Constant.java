package com.foxconn.bandon.utils;

import android.os.Environment;


public class Constant {

    private static final String SD_BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/bandon";
    public static final String LOCAL_EMOTION_PATH = SD_BASE_PATH + "/insert/emotion";
    public static final String LOCAL_IMAGE_PATH = SD_BASE_PATH + "/insert/image";
    public static final String LOCAL_MESSAGE_PATH = SD_BASE_PATH + "/message/";
    public static final String WALLPAPER = SD_BASE_PATH + "/wallpaper";
    public static final String WALLPAPER_DEFAULT = WALLPAPER + "/default";
    public static final String WALLPAPER_FILE = WALLPAPER_DEFAULT + "/wallpaper_default.jpg";
    public static final String VIDEO = SD_BASE_PATH + "/video/";

    public static final String USER_ICON_PATH = SD_BASE_PATH + "/users";
    public static final String FRIDGE_IMAGES_PATH = SD_BASE_PATH + "/fridge";
    public static final String CAMERA_PHOTOS_PATH = SD_BASE_PATH + "/camera";
    public static final String WEATHER_KEY = "40B9FB67A17031E23EB612C590E64A5650ECCB94142B6232CC14F3B08DE391BC";
    public static final String IP_URL = "http://ip-api.com/json/";

    public static final String SP_PHOTOS_NAME = "cropper";
    public static final String KEY_PHOTOS = "crop_photo_";
    public static final String SP_SETTINGS = "settings";

    // Ice maker
    public static final String KEY_ICE_MAKER_MODE = "ice_maker_mode";
    public static final int ICE_MAKER_MODE_OFF = 0;
    public static final int ICE_MAKER_MODE_NORMAL = 1;
    public static final int ICE_MAKER_MODE_EXPRESS = 2; // not allowed during express/hot hybrid cooling
    public static final String KEY_ICE_MAKER_CUBE_SIZE = "ice_maker_cube_size";
    public static final int ICE_MAKER_CUBE_SIZE_NORMAL = 0;
    public static final int ICE_MAKER_CUBE_SIZE_LARGE = 1;

    //Food Area saving
    public static final int AREA_FRIDGE = 0;
    public static final int AREA_VEGETABLE = 1;
    public static final int AREA_FREEZER = 2;

    public static final String FOOD_TYPE_EXPIRED = "0";
    public static final String FOOD_TYPE_SOON_EXPIRE = "1";


    // Cooling operation
    public static final String KEY_FROZEN_OPERATION = "cooling_operation";
    public static final int FROZEN_NORMAL = 0;
    public static final int FROZEN_FRESH = 1;
    public static final int FROZEN_EXPRESS = 2;
    public static final int FROZEN_HEAT = 3;

    // Temperature
    public static final String KEY_FREEZER_TEMPERATURE = "freezer_temperature";
    public static final String KEY_FRIDGE_TEMPERATURE = "fridge_temperature";
    public static final String KEY_VARYING_STATUS = "varying_status";
    public static final int VARYING_UNFREEZE = 1;
    public static final int VARYING_REFRIGERATE = 0;
    public static final int REFRIGERATOR_TEMP_MIN = -1;
    public static final int REFRIGERATOR_TEMP_MAX = 9;
    public static final int FREEZER_TEMP_MIN = -23;
    public static final int FREEZER_TEMP_MAX = -15;

    // Cleaner
    public static final String KEY_CLEANER = "cleaner";
    public static final int CLEANER_OFF = 0;
    public static final int CLEANER_NORMAL = 1;
    public static final int CLEANER_EX = 2;

    //door open times
    public static final String KEY_DOOR_OPENED_TIMES = "key_door_opened_times";

    //power saving
    public static final String KEY_POWER_SAVING_MODE = "power_saving_mode";
    public static final int POWER_SAVING_MODE_OFF = 0;
    public static final int POWER_SAVING_MODE_ON = 1;

    //Alarm
    public static final String KEY_ALARM_RINGTONE = "alarm_ringtone";

    //wallpaper
    public static final String KEY_WALLPAPER_CACHE = "wallpaper_cache_key";
    public static final String KEY_WALLPAPER_UPDATE = "wallpaper_cache_update";

    //device
    public static final String KEY_DEVICE_REGISTER = "device_register_key";

    public static final String KEY_IS_UPGRADING_APK = "is_upgrading_apk";
    public static final String BASE_URL = "http://39.108.193.155:8888";


    public static final String JD_HTML5_SHOPPING_URL = "https://coupon.m.jd.com/union?&mtm_source=kepler-m&mtm_subsource=7846642c723f4eff98dedd989ffc9fa1&mopenbp5=HASH_MAC&returl=https%3A%2F%2Funion-click.jd.com%2Fjdc%3Fd%3DKtj4xR";
    public static final String JD_HTML5_SHOPPING_URL_WITH_KEYWORD = "https://coupon.m.jd.com/union?&mtm_source=kepler-m&mtm_subsource=7846642c723f4eff98dedd989ffc9fa1&mopenbp5=HASH_MAC&returl=https%3A%2F%2Fsearch.jd.com%2Fsearch%3Fkeyword%3DSEARCH_KEYWORD%26enc%3Dutf-8%26qrst%3D1%26rt%3D1%26stop%3D1%26vt%3D2%26wq%3DSEARCH_KEYWORD%26stock%3D1%23J_searchWrap%26ad_od%3D1";
    public static final String KEY_SEARCH_RESULT = "key_search_result";

}
