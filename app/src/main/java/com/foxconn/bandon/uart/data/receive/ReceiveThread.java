package com.foxconn.bandon.uart.data.receive;


import com.foxconn.bandon.uart.UartUtils;
import com.foxconn.bandon.utils.LogUtils;

public class ReceiveThread extends Thread {
    private static final String TAG  = ReceiveThread.class.getSimpleName();

    private UartDataQueue queue;
    private int[] data;

    public ReceiveThread(UartDataQueue queue,int[] data) {
        this.queue = queue;
        this.data = data;
    }

    @Override
    public void run() {
        super.run();
        UartUtils.print(data);
        queue.add(data);
        LogUtils.d(TAG, "ReceiveUartData()-->Queue size:" + queue.size());
    }
}
