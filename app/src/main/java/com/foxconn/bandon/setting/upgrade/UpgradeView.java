package com.foxconn.bandon.setting.upgrade;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UpgradeView extends BaseSettingView {

    public static final String TAG = UpgradeView.class.getSimpleName();
    private Handler mHandler = new Handler();

    public UpgradeView(@NonNull Context context) {
        super(context);
    }

    public UpgradeView(@NonNull Context context, DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
    }


    @Override
    protected void setup() {
        mTitle.setText(R.string.system_upgrade_title);
        inflate(getContext(), R.layout.layout_upgrade_view, mContentView);
        final Context context = getContext();

        String pkgName = context.getPackageName();
        String versionName = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String fVersionName = versionName;
        ((TextView) findViewById(R.id.current_version)).setText(versionName);
        OtaAPI.getInfo(mHandler, new OtaAPI.Callback() {
            @Override
            public void onResult(String result) {
                try {
                    JSONObject info = new JSONObject(result);

                    final String newVersionName = info.getString("versionName");
                    final String apkUrl = info.getString("apk_url");

                    ((TextView) findViewById(R.id.latest_version)).setText(newVersionName);

                    if (!fVersionName.equals(newVersionName)) {
                        View upgradeButton = findViewById(R.id.btn_upgrade);

                        upgradeButton.setVisibility(VISIBLE);
                        upgradeButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setEnabled(false);
                                ((TextView) v).setText("更新中...");

                                final File destinationFile = new File(Environment.getExternalStorageDirectory(),
                                        "bandon" + newVersionName + ".apk");
                                LogUtils.d(TAG, "download to:" + destinationFile.toString());

                                DownloadManager downloadManager;
                                downloadManager = (DownloadManager)
                                        context.getSystemService(Context.DOWNLOAD_SERVICE);
                                String url = Constant.BASE_URL + "/ecfood" + apkUrl;

                                DownloadManager.Request request =
                                        new DownloadManager.Request(Uri.parse(url));
                                request.setNotificationVisibility(
                                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationUri(Uri.fromFile(destinationFile));

                                final long downloadId = downloadManager.enqueue(request);
                                BroadcastReceiver receiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                        if (downloadId == id) {
                                            context.unregisterReceiver(this);
                                            installApk(destinationFile);
                                        }
                                    }
                                };

                                IntentFilter filter = new IntentFilter(
                                        DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                                context.registerReceiver(receiver, filter);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void installApk(File file) {
        if (file.exists()) {
            PreferenceUtils.setBoolean(getContext(), null, Constant.KEY_IS_UPGRADING_APK, true);
            try {
                if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 24) {
                    LogUtils.d(TAG, "installApk pm install -i " + getContext().getPackageName() + " --user 0 " + file.getAbsolutePath());
                    Runtime.getRuntime().exec("pm install -i " + getContext().getPackageName() + " --user 0 " + file.getAbsolutePath());
                } else {
                    LogUtils.d(TAG, "installApk pm install -r" + file.getAbsolutePath());
                    Runtime.getRuntime().exec("pm install -r " + file.getAbsolutePath());
                }

            } catch (IOException e) {
                LogUtils.e(TAG, "exception:" + e.toString());
            }
        }
    }

    @Override
    protected void close() {
        mDismissCallback.onDismiss(TAG);
    }

}
