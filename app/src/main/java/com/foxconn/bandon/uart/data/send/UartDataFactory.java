package com.foxconn.bandon.uart.data.send;

import com.foxconn.bandon.uart.UartUtils;

public class UartDataFactory {

    private static final String TAG = UartDataFactory.class.getSimpleName();

    private static UartDataFactory instance;

    private UartDataFactory() {

    }

    public static UartDataFactory getInstance() {
        if (null == instance) {
            synchronized (UartDataFactory.class) {
                if (null == instance) {
                    instance = new UartDataFactory();
                }
            }
        }
        return instance;
    }

    public UartSendData getUartSendData(char command, char subCommand, char[] datas) {
        UartSendData data = new UartSendData();
        int len = datas.length;
        char num = (char) ((len + 2) & 0xF);
        char sub = (char) ((subCommand << 4) & 0xF0);
        sub = (char) (sub | num);
        data.setCommand(command);
        data.setDatas(datas);
        data.setSubCommand(sub);
        char parity = UartUtils.uartParityCal(datas);
        parity ^= data.getCommand();
        parity ^= data.getSubCommand();
        data.setParity(parity);
        return data;
    }

    public char[] uartSendDataConvertToArray(UartSendData data) {
        char[] datas = data.getDatas();
        int len = datas.length;
        char parity = UartUtils.uartParityCal(datas);
        parity ^= data.getCommand();
        parity ^= data.getSubCommand();
        char[] sendData = new char[len + 5];
        sendData[0] = 0xFa;
        sendData[1] = data.getCommand();
        sendData[2] = data.getSubCommand();
        System.arraycopy(datas, 0, sendData, 3, len);
        sendData[len + 3] = parity;
        sendData[len + 4] = 0xF5;
        return sendData;
    }


}
