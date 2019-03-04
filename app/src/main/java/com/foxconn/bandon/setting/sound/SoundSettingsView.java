package com.foxconn.bandon.setting.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SpacesItemDecoration;
import com.foxconn.bandon.custom.StatusSwitch;
import com.foxconn.bandon.custom.VolumeSeekBar;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.PreferenceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class SoundSettingsView extends FrameLayout {

    public static final String TAG = SoundSettingsView.class.getSimpleName();

    private static final String VOLUME_ENTRY_KEY = "volume";

    private AudioManager mAudioManager;
    private StatusSwitch mIsSilence;
    private VolumeSeekBar mVolumeSeekBar;
    private BaseSettingView.DismissCallback mCallback;
    private boolean mIsMute;
    private final MediaPlayer mMediaPlayer = new MediaPlayer();
    private Map<String, Uri> mRingtoneMap;
    private TextView mRingtone;
    private String mRingtoneNow;
    private RecyclerView mRingtoneRecyclerView;
    private RingtoneAdapter mRingtoneAdapter;
    private boolean isPrepared = false;
    private boolean isCompleted = true;
    boolean mRingToneSetting = false;
    TextView mCancel, mConfirm;
    View mRingToneContainer;

    public SoundSettingsView(@NonNull Context context) {
        super(context);
    }

    public SoundSettingsView(@NonNull Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mCallback = callback;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDismiss();
    }


    protected void onDismiss() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }


    protected void setup() {

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        inflate(getContext(), R.layout.layout_sound_view, this);
        mIsSilence = findViewById(R.id.is_silence);
        mVolumeSeekBar = findViewById(R.id.volume_seekbar);
        FrameLayout silenceContainer = findViewById(R.id.is_silence_container);
        mRingtone = findViewById(R.id.ringtone);

        mRingtoneRecyclerView = findViewById(R.id.ringtone_recycler_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRingtoneRecyclerView.setLayoutManager(layoutManager);
        mRingtoneRecyclerView.addItemDecoration(new SpacesItemDecoration(45));
        mRingtoneAdapter = new RingtoneAdapter();
        mRingtoneRecyclerView.setAdapter(mRingtoneAdapter);

        mRingToneContainer = findViewById(R.id.ringtone_container);
        mCancel = findViewById(R.id.cancel_btn);
        mConfirm = findViewById(R.id.confirm_btn);

        initRingtone();
        loadAlarmRingtoneList();

        loadSettings();
        initMediaPlayer();

        mVolumeSeekBar.setLevelListener(level -> {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, level, AudioManager.FLAG_PLAY_SOUND);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level * 2, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            playAlarm();
        });

        mIsSilence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mute();
            } else {
                unMute();
            }
        });

        silenceContainer.setOnClickListener(v -> mIsSilence.setChecked(!mIsSilence.isChecked()));

        mRingToneContainer.setOnClickListener(v -> {
            if (!mRingToneSetting) {
                mRingtoneRecyclerView.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.VISIBLE);
                mConfirm.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = mRingToneContainer.getLayoutParams();
                params.height = 857;
                mRingToneContainer.setLayoutParams(params);
                mRingToneContainer.requestLayout();
            }
        });

        mConfirm.setOnClickListener(view -> {
            shrink();
            LogUtils.d(TAG, "Confirm onClick");
            mRingtone.setText(mSelectedName);
            mRingtoneNow = mSelectedName;
            mRingtoneAdapter.notifyDataSetChanged();
            BRingtoneManager.getInstance().stop();
            BRingtoneManager.getInstance().updateRingtone(mRingtoneMap.get(mRingtoneNow));
        });

        mCancel.setOnClickListener(view -> {
            shrink();
            LogUtils.d(TAG, "Cancel onClick");
            mRingtone.setText(mRingtoneNow);
            mSelectedName = mRingtoneNow;
            mRingtoneAdapter.notifyDataSetChanged();
            BRingtoneManager.getInstance().stop();
        });

        ImageView closeBtn = findViewById(R.id.close);
        closeBtn.setOnClickListener(view -> mCallback.onDismiss(TAG));
    }


    private void shrink() {
        mRingtoneRecyclerView.setVisibility(View.INVISIBLE);
        mCancel.setVisibility(View.INVISIBLE);
        mConfirm.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = mRingToneContainer.getLayoutParams();
        params.height = 122;
        mRingToneContainer.setLayoutParams(params);
        mRingToneContainer.requestLayout();
    }

    private void loadSettings() {
        int alarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if (alarmVolume == 0) {
            mIsMute = true;
            alarmVolume = getPersistentVolume();
        } else {
            mIsMute = false;
        }

        mIsSilence.setChecked(mIsMute);

        mVolumeSeekBar.setEnable(!mIsMute);
        mVolumeSeekBar.scrollByLevel(alarmVolume);
    }

    private void mute() {

        mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_MUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        //persistent now volume
        int alarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        persistentVolume(alarmVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        mIsMute = true;
        mVolumeSeekBar.setEnable(false);
    }


    private void unMute() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_UNMUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
        int alarmVolume = getPersistentVolume();
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVolume * 2, AudioManager.FLAG_PLAY_SOUND);
        mIsMute = false;
        mVolumeSeekBar.setEnable(true);
    }

    private void persistentVolume(int level) {
        PreferenceUtils.setInt(getContext(), Constant.SP_SETTINGS, VOLUME_ENTRY_KEY, level);
    }

    private int getPersistentVolume() {
        return PreferenceUtils.getInt(getContext(), Constant.SP_SETTINGS, VOLUME_ENTRY_KEY, 1);
    }


    private void initMediaPlayer() {

        //Uri uri = BRingtoneManager.getInstance().getRingtone();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mMediaPlayer.setDataSource(getContext(), uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(mp -> {
                LogUtils.d(TAG, "onPrepared()");
                isPrepared = true;
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                LogUtils.d(TAG, "Completion");
                isCompleted = true;
            });

        } catch (IOException e) {
            LogUtils.e(TAG, "IOException:" + e.toString());
        }

    }

    private void playAlarm() {
        if (!isPrepared || !isCompleted) {
            LogUtils.d(TAG, "Media Player is not prepared");
            return;
        }
        mMediaPlayer.start();
        isCompleted = false;
    }

    private void initRingtone() {
        Uri uri = BRingtoneManager.getInstance().getRingtone();
        Ringtone r = RingtoneManager.getRingtone(getContext(), uri);
        mRingtone.setText(r.getTitle(getContext()));
        mRingtoneNow = r.getTitle(getContext()).replaceAll(".*（", "").replaceAll("）", "");
    }


    private void loadAlarmRingtoneList() {
        new Thread(() -> {
            mRingtoneMap = BRingtoneManager.getInstance().getRingtoneMap();
            new Handler(Looper.getMainLooper()).post(this::setClickListenerToRingtone);

        }).start();
    }

    private void setClickListenerToRingtone() {
        LogUtils.d(TAG, "setClickListenerToRingtone");
        mRingtoneAdapter.update();
        mRingtoneAdapter.notifyDataSetChanged();
    }


    class RingtoneItem extends RecyclerView.ViewHolder {
        RadioButton mRadioButton;
        TextView mRingToneName;

        RingtoneItem(View itemView) {
            super(itemView);
            mRingToneName = itemView.findViewById(R.id.ringtone_text);
            mRadioButton = itemView.findViewById(R.id.ringtone_radio);
            mRadioButton.setClickable(false);
        }
    }

    private String mSelectedName;

    class RingtoneAdapter extends RecyclerView.Adapter<RingtoneItem> {

        private ArrayList<String> mRingtoneList = null;

        public void update() {
            if (mRingtoneMap != null) {
                TreeSet<String> sortSet = new TreeSet<>(mRingtoneMap.keySet());
                mRingtoneList = new ArrayList<>(sortSet);

                for (String name : mRingtoneList) {
                    if (mRingtoneNow.contains(name)) {
                        mRingtoneNow = name;
                    }
                }
                mSelectedName = mRingtoneNow;
            }
        }

        @Override
        public RingtoneItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ringtone_selector_item, parent, false);
            return new RingtoneItem(view);
        }

        @Override
        public void onBindViewHolder(final RingtoneItem holder, int position) {
            String name = mRingtoneList.get(position);
            if (TextUtils.equals(mSelectedName, name)) {
                holder.mRadioButton.setChecked(true);
                mSelectedName = name;
            } else {
                holder.mRadioButton.setChecked(false);
            }
            holder.mRingToneName.setText(name);

            holder.itemView.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                String newName = mRingtoneList.get(pos);
                if (!holder.mRadioButton.isChecked()) {
                    mSelectedName = newName;
                    notifyDataSetChanged();
                    Uri uri = mRingtoneMap.get(mRingtoneList.get(pos));
                    BRingtoneManager.getInstance().play(uri);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mRingtoneList != null ? mRingtoneList.size() : 0;
        }

    }

}