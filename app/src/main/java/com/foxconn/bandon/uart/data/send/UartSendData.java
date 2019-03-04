package com.foxconn.bandon.uart.data.send;

public class UartSendData {

    public static final char DATA_TYPE_EXCEPTION = 0x03;
    public static final char DATA_TYPE_TEMP_SETTINGS = 0X04;
    public static final char DATA_TYPE_MODE_SETTINGS = 0x05;
    public static final char DATA_TYPE_DATE_SETTINGS = 0X06;
    public static final char DATA_TYPE_VOLUME_SETTINGS = 0x07;


    private char start;
    private char command;
    private char subCommand;
    private char[] datas;
    private char parity;
    private char end;


    public UartSendData() {
        this.start = 0xFA;
        this.end = 0xF5;
    }

    public void setCommand(char command) {
        this.command = command;
    }

    public void setSubCommand(char subCommand) {
        this.subCommand = subCommand;
    }


    public void setDatas(char[] datas) {
        this.datas = datas;
    }

    public void setParity(char parity) {
        this.parity = parity;
    }

    public char getCommand() {
        return command;
    }

    public char getSubCommand() {
        return subCommand;
    }

    public char[] getDatas() {
        return datas;
    }

    public char getParity() {
        return parity;
    }

    public char getEnd() {
        return end;
    }
}
