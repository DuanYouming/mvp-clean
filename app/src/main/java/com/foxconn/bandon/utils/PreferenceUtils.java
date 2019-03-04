package com.foxconn.bandon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.foxconn.bandon.tinker.BandonApplication;

public class PreferenceUtils {

    private static SharedPreferences getSPUtils(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            name = "bandon";
        }
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void registerListener(String name, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        Context context = BandonApplication.getInstance();
        SharedPreferences preferences = getSPUtils(context, name);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }
    public static void unregisterListener(String name, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        Context context = BandonApplication.getInstance();
        SharedPreferences preferences = getSPUtils(context, name);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static int getInt(Context context, String name, String key, int defValue) {
        return getSPUtils(context, name).getInt(key, defValue);
    }

    public static void setInt(Context context, String name, String key, int value) {
        SharedPreferences preferences = getSPUtils(context, name);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defValue) {
        return getSPUtils(context, name).getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences preferences = getSPUtils(context, name);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String name, String key, String defValue) {
        return getSPUtils(context, name).getString(key, defValue);
    }

    public static void setString(Context context, String name, String key, String value) {
        SharedPreferences preferences = getSPUtils(context, name);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(Context context, String name, String key) {
        SharedPreferences pref = getSPUtils(context, name);
        pref.edit().remove(key).apply();
    }

    public static void clear(Context context, String name) {
        SharedPreferences pref = getSPUtils(context, name);
        pref.edit().clear().apply();
    }

}