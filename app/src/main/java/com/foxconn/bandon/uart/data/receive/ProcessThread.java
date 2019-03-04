package com.foxconn.bandon.uart.data.receive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.foxconn.bandon.uart.UartService;
import com.foxconn.bandon.uart.UartUtils;
import com.foxconn.bandon.utils.LogUtils;

public class ProcessThread extends Thread {
    private static final String TAG = ProcessThread.class.getSimpleName();

    private UartDataQueue queue;
    private boolean isLoop;
    private Handler handler;

    public ProcessThread(UartDataQueue queue, Handler handler) {
        this.queue = queue;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        while (isLoop) {
            int[] data = queue.get();
            if (null != data) {
                UartUtils.print(data);
                LogUtils.d(TAG, "ProcessUartData()-->Queue size:" + queue.size());
                Message msg = handler.obtainMessage();
                msg.what = UartUtils.getCommand(data);
                Bundle bundle = new Bundle();
                bundle.putIntArray(UartService.KEY_BUNDLE_DATA, data);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    }

    public void setLoop(boolean loop) {
        this.isLoop = loop;
    }
}
