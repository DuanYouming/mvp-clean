package com.foxconn.bandon.uart;

import com.foxconn.bandon.utils.LogUtils;

import java.util.Calendar;


public class UartUtils {
    private static final String TAG = UartUtils.class.getSimpleName();
    private static final int DATA_HEADER = 0xFA;
    private static final int DATA_END = 0xF5;
    private static final int INDEX_COMMAND = 1;
    private static final int OTHER_DATA_LENGTH = 3;//head end Parity
    private static final int MIN_LENGTH = 8;
    private static final int BASE_YEAR = 2000;


    private static boolean isValid(byte[] data) {
        if (null == data || data.length < MIN_LENGTH) {
            return false;
        }
        int length = data.length;
        int header = data[0] & 0xFF;
        int end = data[length - 1] & 0xFF;
        int datasLength = (length / 2 - 2);
        return header == DATA_HEADER && end == DATA_END && data[3] == datasLength;
    }

    private static int getLength(byte[] data) {
        int indexOfLength = 3;
        return data[indexOfLength] + OTHER_DATA_LENGTH;
    }

    public static int[] format(byte[] data) {
        if (!isValid(data)) {
            LogUtils.d(TAG, "Uart Data is not valid");
            return null;
        }
        int length = getLength(data);
        int[] fData = new int[length];
        for (int i = 0; i < fData.length; i++) {
            if (i == 0) {
                fData[i] = data[i] & 0xFF;
            } else if (i == length - 1) {
                fData[length - 1] = data[data.length - 1] & 0xFF;
            } else {
                fData[i] = ((data[2 * i] & 0xFF) << 4) | (data[2 * i - 1]);
            }
        }
        return fData;
    }

    public static int getCommand(int[] data) {
        return data[INDEX_COMMAND] & 0xFF;
    }


    public static char uartParityCal(char[] data) {
        int parity = 0;
        for (char aData : data) {
            if ((aData != 0xfa) && (aData != 0xf5)) {
                parity ^= aData;
            }
        }
        LogUtils.d(TAG, "UartParityCal() parity:" + String.format("%#02x", parity));
        return (char) (parity & 0xff);
    }

    public static char[] sendDataFormat(char[] data) {
        int length = (data.length - 1) * 2;
        char[] sendData = new char[length];
        for (int i = 0; i < data.length; i++) {
            if ((data[i] != 0xfa) && (data[i] != 0xf5)) {
                sendData[2 * i - 1] = (char) (data[i] & 0x0f);
                sendData[2 * i] = (char) ((data[i] >> 4) & 0x0f);
            }
        }
        sendData[0] = (char) 0xfa;
        sendData[length - 1] = (char) 0xf5;
        return sendData;
    }

    public static void print(int[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02x", data[i]));
            if (i < data.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        LogUtils.d(TAG, "data:" + sb.toString());
    }

    public static void print(char[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02x", (int) data[i]));
            if (i < data.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        LogUtils.d(TAG, "data:" + sb.toString());
    }

    public static char[] getDateData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - BASE_YEAR;
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return getDateData(year, week, month, day, hour, minute, second);
    }

    private static char[] getDateData(int year, int week, int month, int day, int hour, int min, int sec) {
        char[] datas = new char[6];
        datas[0] = (char) (0xFF & year);
        if (week == Calendar.SATURDAY) {
            week = 0;
        }
        datas[1] = (char) ((0xF0 & ((0xf & week) << 4)) | (0xf & month));
        datas[2] = (char) (0xFF & day);
        datas[3] = (char) (0xFF & hour);
        datas[4] = (char) (0xFF & min);
        datas[5] = (char) (0xFF & sec);
        return datas;
    }

}
