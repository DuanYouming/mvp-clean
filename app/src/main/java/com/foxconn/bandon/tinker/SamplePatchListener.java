package com.foxconn.bandon.tinker;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import java.io.File;
import java.util.Properties;


public class SamplePatchListener extends DefaultPatchListener {
    private static final String TAG = "Tinker.SamplePatchListener";
    private static final long NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN = 60 * 1024 * 1024;
    private final int maxMemory;

    SamplePatchListener(Context context) {
        super(context);
        maxMemory = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        TinkerLog.i(TAG, "application maxMemory:" + maxMemory);
    }

    @Override
    public int patchCheck(String path, String patchMd5) {
        File patchFile = new File(path);
        TinkerLog.i(TAG, "receive a patch file: %s, file size:%d", path, SharePatchFileUtil.getFileOrDirectorySize(patchFile));
        int returnCode = super.patchCheck(path, patchMd5);

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            returnCode = Utils.checkForPatchRecover(NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN, maxMemory);
        }

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            SharedPreferences sp = context.getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            int fastCrashCount = sp.getInt(patchMd5, 0);
            if (fastCrashCount >= SampleUncaughtExceptionHandler.MAX_CRASH_COUNT) {
                returnCode = Utils.ERROR_PATCH_CRASH_LIMIT;
            }
        }

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            Properties properties = ShareTinkerInternals.fastGetPatchPackageMeta(patchFile);
            if (properties == null) {
                returnCode = Utils.ERROR_PATCH_CONDITION_NOT_SATISFIED;
            } else {
                String platform = properties.getProperty(Utils.PLATFORM);
                TinkerLog.i(TAG, "get platform:" + platform);
                if (platform == null || !platform.equals(BuildInfo.PLATFORM)) {
                    returnCode = Utils.ERROR_PATCH_CONDITION_NOT_SATISFIED;
                }
            }
        }

        SampleTinkerReport.onTryApply(returnCode == ShareConstants.ERROR_PATCH_OK);
        return returnCode;
    }
}
