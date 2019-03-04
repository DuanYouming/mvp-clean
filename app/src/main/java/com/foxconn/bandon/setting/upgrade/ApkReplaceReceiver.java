package com.foxconn.bandon.setting.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;


public class ApkReplaceReceiver extends BroadcastReceiver {

    private static final String TAG = ApkReplaceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!PreferenceUtils.getBoolean(context, Constant.SP_SETTINGS, Constant.KEY_IS_UPGRADING_APK, false)) {
            return;
        }
        if(null==intent){
            return;
        }
        String action = intent.getAction();
        if(TextUtils.equals(action,Intent.ACTION_MY_PACKAGE_REPLACED)){
            PreferenceUtils.setBoolean(context, Constant.SP_SETTINGS, Constant.KEY_IS_UPGRADING_APK, false);
            LogUtils.d(TAG, "restart app");
            PackageManager packageManager = context.getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            }
        }
    }

}