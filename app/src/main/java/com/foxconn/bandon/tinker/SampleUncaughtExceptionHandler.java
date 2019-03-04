package com.foxconn.bandon.tinker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.tinker.TinkerApplicationHelper;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

public class SampleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "Tinker.SampleUncaughtExHandler";

    private final Thread.UncaughtExceptionHandler ueh;
    private static final long   QUICK_CRASH_ELAPSE  = 10 * 1000;
    public static final  int    MAX_CRASH_COUNT     = 3;
    private static final String DALVIK_XPOSED_CRASH = "Class ref in pre-verified class resolved to unexpected implementation";

    public SampleUncaughtExceptionHandler() {
        ueh = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        TinkerLog.e(TAG, "uncaughtException:" + ex.getMessage());
        tinkerFastCrashProtect();
        tinkerPreVerifiedCrashHandler(ex);
        ueh.uncaughtException(thread, ex);
    }

    private void tinkerPreVerifiedCrashHandler(Throwable ex) {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();
        if (applicationLike == null || applicationLike.getApplication() == null) {
            TinkerLog.w(TAG, "applicationlike is null");
            return;
        }

        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            TinkerLog.w(TAG, "tinker is not loaded");
            return;
        }

        Throwable throwable = ex;
        boolean isXposed = false;
        while (throwable != null) {
            if (!isXposed) {
                isXposed = Utils.isXposedExists(throwable);
            }

            // xposed?
            if (isXposed) {
                boolean isCausedByXposed = false;
                if (throwable instanceof IllegalAccessError && throwable.getMessage().contains(DALVIK_XPOSED_CRASH)) {
                    isCausedByXposed = true;
                }

                if (isCausedByXposed) {
                    SampleTinkerReport.onXposedCrash();
                    TinkerLog.e(TAG, "have xposed: just clean tinker");
                    ShareTinkerInternals.killAllOtherProcess(applicationLike.getApplication());
                    TinkerApplicationHelper.cleanPatch(applicationLike);
                    ShareTinkerInternals.setTinkerDisableWithSharedPreferences(applicationLike.getApplication());
                    return;
                }
            }
            throwable = throwable.getCause();
        }
    }

    private boolean tinkerFastCrashProtect() {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();
        if (applicationLike == null || applicationLike.getApplication() == null) {
            return false;
        }
        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            return false;
        }

        final long elapsedTime = SystemClock.elapsedRealtime() - applicationLike.getApplicationStartElapsedTime();
        if (elapsedTime < QUICK_CRASH_ELAPSE) {
            String currentVersion = TinkerApplicationHelper.getCurrentVersion(applicationLike);
            if (ShareTinkerInternals.isNullOrNil(currentVersion)) {
                return false;
            }

            SharedPreferences sp = applicationLike.getApplication().getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            int fastCrashCount = sp.getInt(currentVersion, 0) + 1;
            if (fastCrashCount >= MAX_CRASH_COUNT) {
                SampleTinkerReport.onFastCrashProtect();
                TinkerApplicationHelper.cleanPatch(applicationLike);
                TinkerLog.e(TAG, "tinker has fast crash more than %d, we just clean patch!", fastCrashCount);
                return true;
            } else {
                sp.edit().putInt(currentVersion, fastCrashCount).commit();
                TinkerLog.e(TAG, "tinker has fast crash %d times", fastCrashCount);
            }
        }

        return false;
    }
}
