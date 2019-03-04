package com.foxconn.bandon.uart.data.receive;

public class UartReceiveData {
    public static final int COMMAND_STATUS = 0x10;
    public static final int COMMAND_NOTIFICATION = 0x20;
    protected int[] data;
    private int subCommand;

    UartReceiveData(int[] data) {
        this.data = data;
        this.subCommand = ((data[2] >> 4) & 0xFF);
    }

    int getSubCommand() {
        return subCommand;
    }

}
