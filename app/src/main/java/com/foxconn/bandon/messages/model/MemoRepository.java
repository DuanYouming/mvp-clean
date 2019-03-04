package com.foxconn.bandon.messages.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class MemoRepository implements IMemoDataSource {

    private static final String TAG = MemoRepository.class.getSimpleName();
    private static final int ERROR = -1;
    private static final float SCALE = 0.5F;
    private static MemoRepository INSTANCE;
    private AppExecutors mExecutors;
    private MemoDao mDao;


    private MemoRepository(MemoDao dao, AppExecutors executors) {
        mDao = dao;
        mExecutors = executors;
    }

    public static MemoRepository getInstance(MemoDao dao, AppExecutors executors) {
        if (null == INSTANCE) {
            synchronized (MemoRepository.class) {
                if(null == INSTANCE) {
                    INSTANCE = new MemoRepository(dao, executors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
    }

    @Override
    public void getMemos(final LoadMemosCallback callback) {
        LogUtils.d(TAG, "getMemos");
        Runnable runnable = () -> {
            final List<Memo> memos = mDao.getMemos();
            mExecutors.mainThread().execute(() -> {
                if (memos.isEmpty()) {
                    callback.onFailure();
                } else {
                    callback.onSuccess(memos);
                }
            });
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getMemoById(final String id, final GetMemoCallback callback) {
        LogUtils.d(TAG, "getMemoById");
        Runnable runnable = () -> {
            final Memo memo = mDao.getMemoById(id);
            mExecutors.mainThread().execute(() -> {
                if (null == memo) {
                    LogUtils.d(TAG, "memo is null");
                    callback.onFailure();
                } else {
                    callback.onSuccess(memo);
                }
            });

        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void insertMemo(final Memo memo, final GetMemoCallback callback) {
        LogUtils.d(TAG, "insertMemo");
        Runnable runnable = () -> {
            final boolean flag = saveBitmap(memo.getBitmap(), memo.getPath());

            if (flag) {
                LogUtils.d(TAG, "save bitmap to sdcard");
                memo.setBitmap(null);
                mDao.insertMemo(memo);
            }
            mExecutors.mainThread().execute(() -> {
                if (flag) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure();
                }
            });
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteMemoById(final String id, final GetMemoCallback callback) {
        LogUtils.d(TAG, "deleteMemoById");
        Runnable runnable = () -> {
            mDao.deleteMemoById(id);
            callback.onSuccess(null);
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateMemo(final Memo memo, final GetMemoCallback callback) {
        LogUtils.d(TAG, "updateMemo");
        Runnable runnable = () -> {
            mDao.updateMemo(memo);
            mExecutors.mainThread().execute(() -> callback.onSuccess(null));
        };
        mExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllMemos() {
        LogUtils.d(TAG, "deleteAllMemos");
        Runnable runnable = () -> mDao.deleteAllMemos();
        mExecutors.diskIO().execute(runnable);
    }


    private boolean saveBitmap(Bitmap bitmap, String name) {
        if (null == bitmap) {
            return false;
        }
        Bitmap scale = ImageUtils.scaleBitmap(bitmap, SCALE);
        LogUtils.d(TAG, " scale width：" + scale.getWidth() + "  height:" + scale.getHeight());
        int left = getLeft(scale);
        if (left == ERROR) {
            return false;
        }
        int right = getRight(scale);
        int top = getTop(scale, left, right);
        int bottom = getBottom(scale, left, right);

        bitmap = Bitmap.createBitmap(bitmap, left * 2, top * 2, (right - left) * 2, (bottom - top) * 2);
        File file = new File(Constant.LOCAL_MESSAGE_PATH);
        if (!file.exists()) {
            LogUtils.d(TAG, "mkdirs:" + file.mkdirs());
        }
        File f = new File(file, name);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!scale.isRecycled()) {
                scale.recycle();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return true;
    }

    private int getLeft(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitmap.getPixel(i, j) != Color.TRANSPARENT) {
                    return i;
                }
            }
        }
        return ERROR;
    }

    private int getRight(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = 0; j < height; j++) {
                if (bitmap.getPixel(i, j) != Color.TRANSPARENT) {
                    return i;
                }
            }
        }
        return ERROR;
    }

    private int getTop(Bitmap bitmap, int left, int right) {
        int height = bitmap.getHeight();
        for (int j = 0; j < height; j++) {
            for (int i = left; i < right; i++) {
                if (bitmap.getPixel(i, j) != Color.TRANSPARENT) {
                    return j;
                }
            }
        }
        return ERROR;
    }

    private int getBottom(Bitmap bitmap, int left, int right) {
        int height = bitmap.getHeight();
        for (int j = height - 1; j >= 0; j--) {
            for (int i = left; i < right; i++) {
                if (bitmap.getPixel(i, j) != Color.TRANSPARENT) {
                    return j;
                }
            }
        }
        return ERROR;
    }

}
