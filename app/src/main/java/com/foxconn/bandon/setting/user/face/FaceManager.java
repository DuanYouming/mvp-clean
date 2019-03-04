package com.foxconn.bandon.setting.user.face;

import com.foxconn.bandon.tinker.BandonApplication;

import java.io.File;

public class FaceManager {
    private FaceDB mFaceDB;
    private static volatile FaceManager mInstance;

    public static FaceManager getInstance() {
        if (null == mInstance) {
            synchronized (FaceManager.class) {
                if (null == mInstance) {
                    mInstance = new FaceManager();
                }
            }
        }
        return mInstance;
    }

    public void initFaceDB() {
        File cacheDir = BandonApplication.getInstance().getExternalCacheDir();
        if (null != cacheDir) {
            mFaceDB = FaceDB.initFaceDB(cacheDir.getPath());
        }
    }

    public FaceDB getFaceDB() {
        if (null == mFaceDB) {
            initFaceDB();
        }
        return mFaceDB;
    }

    public void destroyFaceDB() {
        getFaceDB().destroy();
    }


}
