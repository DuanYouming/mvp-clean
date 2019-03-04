package com.foxconn.bandon.setting.user.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BandonDataBase;
import com.foxconn.bandon.setting.user.face.FaceDB;
import com.foxconn.bandon.setting.user.face.FaceManager;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.guo.android_extend.image.ImageConverter;
import java.util.ArrayList;
import java.util.List;

public class UserInfoRepository implements IUserInfoDataSource {
    private static final String TAG = UserInfoRepository.class.getSimpleName();
    private static UserInfoRepository instance;
    private AppExecutors mExecutors;
    private UserDao userDao;

    private UserInfoRepository(AppExecutors executors) {
        this.mExecutors = executors;
        this.userDao = BandonDataBase.getInstance(BandonApplication.getInstance()).getUserDao();
    }

    public static UserInfoRepository getInstance(AppExecutors executors) {
        if (null == instance) {
            synchronized (UserInfoRepository.class) {
                if (null == instance) {
                    instance = new UserInfoRepository(executors);
                }
            }
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


    @Override
    public void registerUser(final String name, final Bitmap bitmap, final LoadUserCallback callback) {
        Runnable runnable = () -> {
            final String msg;
            if (register(name, bitmap)) {
                String url = Constant.USER_ICON_PATH + "/" + name + ".jpg";
                String icon = ImageUtils.encoderBitmap(bitmap);
                UserInfo user = new UserInfo(name, url, icon, SystemClock.currentThreadTimeMillis());
                if (null == userDao.getUser(name)) {
                    userDao.insertUser(user);
                    msg = BandonApplication.getInstance().getString(R.string.register_success);
                } else {
                    userDao.updateUser(user);
                    msg = BandonApplication.getInstance().getString(R.string.register_already);
                }
            }else {
                msg = BandonApplication.getInstance().getString(R.string.fr_error);
            }
            mExecutors.mainThread().execute(() -> callback.onSuccess(msg));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteUser(final String name, final LoadUserCallback callback) {
        Runnable runnable = () -> {
            boolean success = FaceManager.getInstance().getFaceDB().delete(name);
            final String msg;
            if (success) {
                msg = BandonApplication.getInstance().getString(R.string.delete_success);
                userDao.deleteUser(name);
            } else {
                msg = BandonApplication.getInstance().getString(R.string.delete_failure);
            }
            mExecutors.mainThread().execute(() -> callback.onSuccess(msg));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getUsers(final GetUsersCallback callback) {
        Runnable runnable = () -> {
            final List<UserInfo> users = userDao.getUsers();
            mExecutors.mainThread().execute(() -> callback.onSuccess(users));
        };
        mExecutors.diskIO().execute(runnable);
    }

/*    private List<UserInfo> getUsers() {
        List<FaceDB.FaceRegist> registers = FaceManager.getInstance().getFaceDB().getRegisters();
        List<UserInfo> users = new ArrayList<>();
        for (FaceDB.FaceRegist register : registers) {
            String name = register.name;
            String keyPath = Constant.USER_ICON_PATH + "/" + name + ".jpg";
            LogUtils.d(TAG, "name：" + name + "  path:" + keyPath);
            UserInfo userInfo = new UserInfo(name, keyPath, 0);
            users.add(userInfo);
        }
        return users;
    }*/


    private boolean register(String name, Bitmap bitmap) {
        AFR_FSDKFace mAFR_FSDKFace;
        byte[] data = covertToData(bitmap);
        List<AFD_FSDKFace> results = getFaceList(bitmap, data);
        String msg;
        if (null == results || results.size() == 0) {
            msg = BandonApplication.getInstance().getString(R.string.fd_error);
            LogUtils.d(TAG, msg);
            return false;
        }

        AFR_FSDKFace result = new AFR_FSDKFace();
        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFD_FSDKError.MOK) {
            msg = BandonApplication.getInstance().getString(R.string.fr_error);
            LogUtils.d(TAG, msg);
            return false;
        }
        error = engine.AFR_FSDK_GetVersion(version);
        LogUtils.d(TAG, "FR=" + version.toString() + "," + error.getCode());
        error = engine.AFR_FSDK_ExtractFRFeature(data, bitmap.getWidth(), bitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(results.get(0).getRect()), results.get(0).getDegree(), result);
        LogUtils.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
        if (error.getCode() == AFD_FSDKError.MOK) {
            mAFR_FSDKFace = result.clone();
            int width = results.get(0).getRect().width();
            int height = results.get(0).getRect().height();
            Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas face_canvas = new Canvas(face_bitmap);
            face_canvas.drawBitmap(bitmap, results.get(0).getRect(), new Rect(0, 0, width, height), null);
            FaceManager.getInstance().getFaceDB().addFace(name, mAFR_FSDKFace, face_bitmap, bitmap);
            msg = BandonApplication.getInstance().getString(R.string.register_success);
            LogUtils.d(TAG, msg);
            return true;
        } else {
            msg = BandonApplication.getInstance().getString(R.string.no_feature);
            LogUtils.d(TAG, msg);
            return false;
        }
    }

    private List<AFD_FSDKFace> getFaceList(Bitmap bitmap, byte[] data) {
        AFD_FSDKEngine engine = new AFD_FSDKEngine();
        AFD_FSDKVersion version = new AFD_FSDKVersion();
        List<AFD_FSDKFace> result = new ArrayList<>();
        AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        if (err.getCode() != AFD_FSDKError.MOK) {
            engine.AFD_FSDK_UninitialFaceEngine();
            return null;
        }

        err = engine.AFD_FSDK_GetVersion(version);
        LogUtils.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
        err = engine.AFD_FSDK_StillImageFaceDetection(data, bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
        LogUtils.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
        engine.AFD_FSDK_UninitialFaceEngine();
        return result;
    }

    private byte[] covertToData(Bitmap bitmap) {
        byte[] data = new byte[bitmap.getWidth() * bitmap.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        convert.initial(bitmap.getWidth(), bitmap.getHeight(), ImageConverter.CP_PAF_NV21);
        if (convert.convert(bitmap, data)) {
            Log.d(TAG, "convert ok!");
        }
        convert.destroy();
        return data;
    }

}
