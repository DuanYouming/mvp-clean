package com.foxcomm.bandon.aispeech.util;

import android.os.Environment;

public class Constants {

    private static final String SD_BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/bandon";
    public static final String KEY = "15405457854584e4";
    public static final String SECRET_KEY = "fd0143ffce07deb02bd4c2c40d0c21aa";
    public static final String PCM_PATH = SD_BASE_PATH + "/speech/asr/";
    public static final String PCM_FILE_TYPE = ".pcm";
    public static final String CLOUD_TTS_RES = "syn_chnsnt_zhilingf";
    public static final String RES_TYPE = "aihome";
    public static final String vad_res = "aicar_vad_v.10.bin";
    public static final String ebnfc_res = "ebnfc.aicar.1.2.0.bin";
    public static final String ebnfr_res = "ebnfr.aicar.1.2.0.bin";
    public static final String wakeup_res = "wakeup_aifar_comm_20180104.bin";
}
