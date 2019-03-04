package com.foxconn.bandon.uart.data.receive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UartDataQueue {

    private static final int MAX = 5;
    private Lock lock;
    private Condition produce;
    private Condition consume;
    private List<int[]> dataBuffer = new ArrayList<>();

    public UartDataQueue() {
        this.lock = new ReentrantLock();
        this.produce = lock.newCondition();
        this.consume = lock.newCondition();
    }

    public void add(int[] data) {
        lock.lock();
        try {
            if (dataBuffer.size() < MAX) {
                dataBuffer.add(0, data);
                consume.signalAll();
            } else {
                produce.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public int[] get() {
        lock.lock();
        int[] data = null;
        try {
            if (dataBuffer.size() > 0) {
                data = dataBuffer.get(dataBuffer.size() - 1);
                dataBuffer.remove(dataBuffer.size() - 1);
                produce.signalAll();
            } else {
                consume.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return data;
    }

    public int size(){
        return dataBuffer.size();
    }
}
