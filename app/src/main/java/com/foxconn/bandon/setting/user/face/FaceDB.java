package com.foxconn.bandon.setting.user.face;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FaceDB {
    private final String TAG = this.getClass().toString();
    public static String appid = "DRwp78R8SLupgsJmrzbb9NWw4z34Tqjwzb7q6LcV9mD7";
    public static String ft_key = "G3LwCuywp3GATQZRyjYNu5jb8FtKMT4EQn8DUu8v5hfa";
    public static String fd_key = "G3LwCuywp3GATQZRyjYNu5jiHf9VGPNJco6wVfLH4X2K";
    public static String fr_key = "G3LwCuywp3GATQZRyjYNu5kCwGC8WXJ1GxdMj4Jp6hia";
    private static FaceDB mInstance;
    private String mDBPath;
    private List<FaceRegist> mRegister;
    private AFR_FSDKEngine mFREngine;
    private AFR_FSDKVersion mFRVersion;
    private boolean mUpgrade;

    public class FaceRegist {
        public String name;
        public Map<String, AFR_FSDKFace> mFaceList;

        FaceRegist(String name) {
            this.name = name;
            mFaceList = new LinkedHashMap<>();
        }
    }

    private FaceDB(String path) {
        mDBPath = path;
        mRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            LogUtils.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            LogUtils.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
        loadFaces();
    }

    protected static FaceDB initFaceDB(String path) {
        if (null == mInstance) {
            synchronized (FaceManager.class) {
                if (null == mInstance) {
                    mInstance = new FaceDB(path);
                }
            }
        }
        return mInstance;
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
                mUpgrade = true;
            }

            for (String name = bos.readString(); name != null; name = bos.readString()) {
                if (new File(mDBPath + "/" + name + ".data").exists()) {
                    mRegister.add(new FaceRegist(name));
                }
            }

            bos.close();
            fs.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadFaces() {
        if (loadInfo()) {
            try {
                for (FaceRegist face : mRegister) {
                    LogUtils.d(TAG, "load name:" + face.name + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.name + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            String keyFile = bos.readString();
                            face.mFaceList.put(keyFile, afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                    LogUtils.d(TAG, "load name: size = " + face.mFaceList.size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFace(String name, AFR_FSDKFace face, Bitmap faceImage, Bitmap icon) {
        OutputStream stream = null;
        try {
            // save face
            String keyPath = mDBPath + "/" + System.nanoTime() + ".jpg";
            File keyFile = new File(keyPath);
            stream = new FileOutputStream(keyFile);
            if (faceImage.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                LogUtils.d(TAG, "saved face bitmap to jpg!");
            }
            saveUserIcon(icon, name);
            //check if already registered.
            boolean add = true;
            for (FaceRegist frFace : mRegister) {
                if (frFace.name.equals(name)) {
                    frFace.mFaceList.put(keyPath, face);
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.
                FaceRegist frFace = new FaceRegist(name);
                frFace.mFaceList.put(keyPath, face);
                mRegister.add(frFace);
            }

            if (saveInfo()) {
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frFace : mRegister) {
                    bos.writeString(frFace.name);
                }
                bos.close();
                fs.close();
                fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.writeString(keyPath);
                bos.close();
                fs.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!faceImage.isRecycled()) {
                faceImage.recycle();
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean delete(String name) {
        try {
            //check if already registered.
            deleteUserIcon(name);
            boolean find = false;
            for (FaceRegist frFace : mRegister) {
                if (frFace.name.equals(name)) {
                    File delFile = new File(mDBPath + "/" + name + ".data");
                    if (delFile.exists()) {
                        LogUtils.d(TAG, "delete file[" + delFile.getName() + "] " + delFile.delete());
                    }
                    mRegister.remove(frFace);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.name);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<FaceRegist> getRegisters() {
        return mRegister;
    }

    private void saveUserIcon(Bitmap bitmap, String name) {
        OutputStream stream = null;
        try {
            File parent = new File(Constant.USER_ICON_PATH);
            if (!parent.exists()) {
                if (parent.mkdirs()) {
                    LogUtils.d(TAG, "make dir " + Constant.USER_ICON_PATH + " success!");
                }
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            String fileName = name + ".jpg";
            File file = new File(parent, fileName);
            stream = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                LogUtils.d(TAG, "saved user icon");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteUserIcon(String name) {
        File parent = new File(Constant.USER_ICON_PATH);
        String fileName = name + ".jpg";
        File file = new File(parent, fileName);
        if (file.exists()) {
            if (file.delete()) {
                LogUtils.d(TAG, "delete user[ " + name + "] icon success");
            }
        }
    }
}
