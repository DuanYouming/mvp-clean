package com.foxconn.bandon.messages.insert;


import android.content.res.AssetManager;
import com.foxconn.bandon.utils.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertImageRepository implements IInsertImageDataSource {

    private static final String TAG = InsertImageRepository.class.getSimpleName();
    private AssetManager manager;
    private static InsertImageRepository INSTANCE;

    private InsertImageRepository(AssetManager manager) {
        this.manager = manager;
    }

    public static InsertImageRepository getInstance(AssetManager manager) {
        if (INSTANCE == null) {
            INSTANCE = new InsertImageRepository(manager);
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
    }

    @Override
    public void loadDefaultImage(String path, Callback callback) {
        List<InsertImage> data = new ArrayList<>();
        try {
            String[] fileNames = manager.list(path);
            for (String fileName : fileNames) {
                data.add(new InsertImage(path + "/" + fileName, false));
            }
            LogUtils.d(TAG, "data size:" + data.size());
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure();
        }
        callback.onSuccess(data);
    }

    @Override
    public void loadLocalImage(String path, Callback callback) {
        List<InsertImage> data = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            LogUtils.d(TAG, "loadLocalImage size:" + files.length);
            for (File f : files) {
                if (f.getName().toLowerCase().endsWith("jpg") || f.getName().toLowerCase().endsWith("png") || f.getName().toLowerCase().endsWith("jpeg")) {
                    data.add(new InsertImage(f.getPath(), true));
                }
            }
        }
        callback.onSuccess(data);
    }
}
