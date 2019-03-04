package com.foxconn.bandon.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.foxconn.bandon.setting.sound.BRingtoneManager;
import com.foxconn.bandon.setting.user.face.FaceManager;
import com.foxconn.bandon.utils.LogUtils;


public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate()");
        BRingtoneManager.getInstance().initRingtone();
        FaceManager.getInstance().initFaceDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy()");
        FaceManager.getInstance().destroyFaceDB();
    }

}
