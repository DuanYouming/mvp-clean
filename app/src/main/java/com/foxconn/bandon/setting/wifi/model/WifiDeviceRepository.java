package com.foxconn.bandon.setting.wifi.model;

import com.foxconn.bandon.utils.AppExecutors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class WifiDeviceRepository implements IWifiDeviceDataSource {

    private static final String FILE_NAME = "wifi_devices.txt";
    private AppExecutors mExecutors;
    private static WifiDeviceRepository INSTANCE;
    private File mCache;

    private WifiDeviceRepository(AppExecutors executors, File file) {
        this.mExecutors = executors;
        this.mCache = file;
    }

    public static WifiDeviceRepository getInstance(AppExecutors executors, File file) {
        if (null == INSTANCE) {
            synchronized (WifiDeviceRepository.class) {
                if (null == INSTANCE) {
                    return new WifiDeviceRepository(executors, file);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getDevices(final GetDevicesCallback callback) {
        Runnable runnable = () -> {
            List<WifiDevice> devices = getDevices();
            callback.onSuccess(devices);
        };
        mExecutors.diskIO().execute(runnable);

    }

    @Override
    public void saveDevices(final List<WifiDevice> devices) {
        Runnable runnable = () -> save(devices);
        mExecutors.diskIO().execute(runnable);

    }

    private void save(List<WifiDevice> devices) {
        File file = new File(mCache, FILE_NAME);
        Gson gson = new Gson();
        String result = gson.toJson(devices);
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file, false));
            bfw.write(result);
            bfw.newLine();
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<WifiDevice> getDevices() {
        File file = new File(mCache, FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = sb.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<WifiDevice>>() {
        }.getType();
        return gson.fromJson(result, type);
    }
}
