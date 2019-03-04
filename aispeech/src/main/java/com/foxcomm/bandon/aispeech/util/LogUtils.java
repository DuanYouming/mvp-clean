package com.foxcomm.bandon.aispeech.util;

import android.util.Log;

public class LogUtils {

    private static boolean debug = true;
    private static final String TAG = "Bandon::";

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(TAG+tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(TAG+tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(TAG+tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(TAG+tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            Log.w(TAG+tag, msg);
        }
    }
}

