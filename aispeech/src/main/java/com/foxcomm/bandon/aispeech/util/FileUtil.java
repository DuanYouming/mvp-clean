package com.foxcomm.bandon.aispeech.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static byte[] readFile(Context context,String readFileName) {
        byte[] data = null;
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(readFileName);
            int length = is.available();
            data = new byte[length];
            int read = is.read(data);
            LogUtils.d(TAG, "read:" + read);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static synchronized boolean createFileDir(String dirPath) {
        File file = new File(dirPath);
        return !file.exists() && file.mkdirs();
    }

    public static synchronized boolean createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                String parent = file.getParent();
                if (createFileDir(parent)) {
                    return file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static synchronized void write(String path, byte[] retData, int size) {
        FileOutputStream fis = null;
        try {
            fis = new FileOutputStream(new File(path));
            fis.write(retData, 0, size);
            fis.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
