
package com.foxconn.bandon.tinker;



import android.util.Log;

import com.foxconn.bandon.utils.LogUtils;
import com.tencent.tinker.lib.util.TinkerLog;

public class MyLogImp implements TinkerLog.TinkerLogImp {
    private static final String TAG = "Tinker.MyLogImp";

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG   = 1;
    public static final int LEVEL_INFO    = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR   = 4;
    public static final int LEVEL_NONE    = 5;
    private static int level = LEVEL_VERBOSE;

    public static int getLogLevel() {
        return level;
    }

    public static void setLevel(final int level) {
        MyLogImp.level = level;
        LogUtils.w(TAG, "new log level: " + level);

    }

    @Override
    public void v(String s, String s1, Object... objects) {
        if (level <= LEVEL_VERBOSE) {
            final String log = objects == null ? s1 : String.format(s1, objects);
            LogUtils.v(s, log);
        }
    }

    @Override
    public void i(String s, String s1, Object... objects) {
        if (level <= LEVEL_INFO) {
            final String log = objects == null ? s1 : String.format(s1, objects);
            LogUtils.i(s, log);
        }
    }

    @Override
    public void w(String s, String s1, Object... objects) {
        if (level <= LEVEL_WARNING) {
            final String log = objects == null ? s1 : String.format(s1, objects);
            LogUtils.w(s, log);
        }
    }

    @Override
    public void d(String s, String s1, Object... objects) {
        if (level <= LEVEL_DEBUG) {
            final String log = objects == null ? s1 : String.format(s1, objects);
            LogUtils.d(s, log);
        }
    }

    @Override
    public void e(String s, String s1, Object... objects) {
        if (level <= LEVEL_ERROR) {
            final String log = objects == null ? s1 : String.format(s1, objects);
            LogUtils.e(s, log);
        }
    }

    @Override
    public void printErrStackTrace(String s, Throwable throwable, String s1, Object... objects) {
        String log = objects == null ? s1 : String.format(s1, objects);
        if (log == null) {
            log = "";
        }
        log = log + "  " + Log.getStackTraceString(throwable);
        LogUtils.e(s, log);
    }
}
