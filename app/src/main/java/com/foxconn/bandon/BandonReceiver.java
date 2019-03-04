package com.foxconn.bandon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.foxconn.bandon.setting.clock.ClockManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.LogUtils;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

public class BandonReceiver extends BroadcastReceiver {
    private static final String TAG = BandonReceiver.class.getSimpleName();
    private static final String ACTION_BANDON_CLOCK = ClockManager.ACTION_BANDON_CLOCK;
    private static final String ACTION_BANDON_DEBUG = "com.foxconn.bandon.ACTION_DEBUG";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) {
            LogUtils.d(TAG, "intent is null");
            return;
        }
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_BANDON_CLOCK)) {
            ClockManager.getInstance().onClockRings(intent);
        } else if (TextUtils.equals(action, ACTION_BANDON_DEBUG)) {
            debug(intent);
        }
    }

    private void debug(Intent intent) {
        String action = intent.getStringExtra("action");
        if(TextUtils.equals(action,"install")) {
            LogUtils.d(TAG, "TinkerInstaller");
            TinkerInstaller.onReceiveUpgradePatch(BandonApplication.INSTANCE, Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
        }else if(TextUtils.equals(action,"restart")) {
            ShareTinkerInternals.killAllOtherProcess(BandonApplication.INSTANCE);
            android.os.Process.killProcess(android.os.Process.myPid());
        }else if(TextUtils.equals(action,"lib")){
            TinkerLoadLibrary.installNavitveLibraryABI(BandonApplication.INSTANCE, "armeabi-v7a");
        }
    }

}
