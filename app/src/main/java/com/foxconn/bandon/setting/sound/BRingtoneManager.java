package com.foxconn.bandon.setting.sound;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;

import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class BRingtoneManager {
    private static final String TAG = BRingtoneManager.class.getSimpleName();

    private static volatile BRingtoneManager instance;
    private Context mContext;
    private Ringtone mRingtonePlayer;
    private MediaPlayer mMediaPlayer;


    private BRingtoneManager() {
        this.mContext = BandonApplication.getInstance().getApplicationContext();
    }

    public static BRingtoneManager getInstance() {
        if (null == instance) {
            synchronized (BRingtoneManager.class) {
                if (null == instance) {
                    instance = new BRingtoneManager();
                }
            }
            instance = new BRingtoneManager();
        }
        return instance;
    }


    public void play(Uri uri) {
        if (uri == null) return;
        stop();
        mRingtonePlayer = android.media.RingtoneManager.getRingtone(mContext, uri);
        mRingtonePlayer.play();
    }

    public void stop() {
        LogUtils.d(TAG, "stop ringtone");
        if (mRingtonePlayer != null) {
            mRingtonePlayer.stop();
        }
    }

    public void playAlarm() {
        try {
            Uri uri = getRingtone();
            this.mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            LogUtils.e(TAG, "play alarm failure:" + e.toString());
        }
    }


    public void stopAlarm() {
        LogUtils.d(TAG, "stop alarm ringtone");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void initRingtone() {
        String ringtone = PreferenceUtils.getString(mContext, Constant.SP_SETTINGS, Constant.KEY_ALARM_RINGTONE, null);
        if (null == ringtone) {
            PreferenceUtils.setString(mContext, Constant.SP_SETTINGS, Constant.KEY_ALARM_RINGTONE, Settings.System.DEFAULT_ALARM_ALERT_URI.toString());
        }
    }

    public Map<String, Uri> getRingtoneMap() {
        HashMap<String, Uri> mRingtoneMap = new HashMap<>();
        RingtoneManager ringtoneMgr = new RingtoneManager(mContext);
        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        int alarmsCount = alarmsCursor.getCount();
        if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
            return null;
        }
        Uri[] alarms = new Uri[alarmsCount];
        while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
            int currentPosition = alarmsCursor.getPosition();
            alarms[currentPosition] = ringtoneMgr.getRingtoneUri(currentPosition);

            Ringtone r = RingtoneManager.getRingtone(mContext, alarms[currentPosition]);
            //save ringtone name and uri
            mRingtoneMap.put(r.getTitle(mContext), alarms[currentPosition]);
        }
        alarmsCursor.close();
        return mRingtoneMap;
    }

    public Uri getRingtone() {
        String ringtone = PreferenceUtils.getString(mContext, Constant.SP_SETTINGS, Constant.KEY_ALARM_RINGTONE, null);
        return Uri.parse(ringtone);
    }

    public void updateRingtone(Uri uri) {
        PreferenceUtils.setString(mContext, Constant.SP_SETTINGS, Constant.KEY_ALARM_RINGTONE, uri.toString());
    }
}
