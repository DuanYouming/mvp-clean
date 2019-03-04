package com.foxconn.bandon;

import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.tinker.TinkerManager;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;


public class Jni {

    static {
        TinkerLoadLibrary.installNavitveLibraryABI(BandonApplication.INSTANCE, "armeabi-v7a");
        System.loadLibrary("native-lib");
    }

    public native static String stringFromJNI(int size);
}
