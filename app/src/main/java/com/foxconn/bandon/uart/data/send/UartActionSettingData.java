package com.foxconn.bandon.uart.data.send;

import com.foxconn.bandon.uart.data.receive.UartStatusData;

public class UartActionSettingData {
    private char[] datas = new char[10];


    public UartActionSettingData() {
    }

    public UartActionSettingData(UartStatusData statusData) {

        fFrozen = statusData.fFrozen ? 1 : 0;
        qFrozen = statusData.qFrozen ? 1 : 0;
        hFrozen = statusData.hFrozen ? 1 : 0;
        pCI = statusData.pCI ? 1 : 0;
        deo = statusData.deo ? 1 : 0;

        //fineR = statusData.fineR ? 1 : 0;
        //fineF = statusData.fineF ? 1 : 0;
        qIce = statusData.qIce ? 1 : 0;
        iceClean = statusData.iceClean ? 1 : 0;
        iceLarge = statusData.iceLarge ? 1 : 0;
        iceStop = statusData.iceStop ? 1 : 0;
        //pIceH = statusData.pIceH ? 1 : 0;
        //pIceN = statusData.pIceN ? 1 : 0;

        sDoorNum = statusData.isDoorNumOn ? 1 : 0;
        sWLAN = statusData.isWLANOn ? 1 : 0;
        sKeyTouchVoice = statusData.isKeyTouchVoiceOn ? 1 : 0;
        sKeyLock = statusData.isKeyLockOn ? 1 : 0;
        sPowerDoor = statusData.isPowerDoorOn ? 1 : 0;
        sWelcomeFlag = statusData.isWelcomeFlagOn ? 1 : 0;
        sAttemperation = statusData.isAttemperationOn ? 1 : 0;
        sPCI = statusData.isPCIOn ? 1 : 0;
    }

    public int fFrozen;
    public int qFrozen;
    public int hFrozen;
    public int pCI;
    public int deo;
    public int dummy5;
    public int voice;
    public int eco;

    private int getCommandOn1() {
        int value = (0x01 & fFrozen);
        value = value | ((0x01 & qFrozen) << 1);
        value = value | ((0x01 & hFrozen) << 2);
        value = value | ((0x01 & pCI) << 3);
        value = value | ((0x01 & deo) << 4);
        value = value | ((0x01 & dummy5) << 5);
        value = value | ((0x01 & voice) << 6);
        value = value | ((0x01 & eco) << 7);
        return value;
    }

    private int getCommandOff1() {
        int value = getCommandOn1();
        value ^= 0xFF;
        return value;
    }

    public int fineR = 1;
    public int fineF= 1;
    public int qIce;
    public int iceClean;
    public int iceLarge;
    public int iceStop;
    public int pIceH;
    public int pIceN;

    private int getCommandOn2() {
        int value = (0x01 & fineR);
        value = value | ((0x01 & fineF) << 1);
        value = value | ((0x01 & qIce) << 2);
        value = value | ((0x01 & iceClean) << 3);
        value = value | ((0x01 & iceLarge) << 4);
        value = value | ((0x01 & iceStop) << 5);
        value = value | ((0x01 & pIceH) << 6);
        value = value | ((0x01 & pIceN) << 7);
        return value;
    }

    private int getCommandOff2() {
        int value = getCommandOn2();
        value ^= 0xFF;
        return value;
    }

    public int icErr = 0;
    public int dummy1 = 0;
    public int inputCheck = 0;
    public int displayOff = 0;
    public int doorBzStop = 1;
    public int jiko = 0;
    public int lineCheck = 0;
    public int demo = 0;

    private int getCommandOn3() {
        int value = (0x01 & icErr);
        value = value | ((0x01 & dummy1) << 1);
        value = value | ((0x01 & inputCheck) << 2);
        value = value | ((0x01 & displayOff) << 3);
        value = value | ((0x01 & doorBzStop) << 4);
        value = value | ((0x01 & jiko) << 5);
        value = value | ((0x01 & lineCheck) << 6);
        value = value | ((0x01 & demo) << 7);
        return value;
    }

    private int getCommandOff3() {
        int value = getCommandOn3();
        value ^= 0xFF;
        return value;
    }


    public int sDoorNum = 0;
    public int sWLAN = 0;
    public int sKeyTouchVoice = 0;
    public int sKeyLock = 0;
    public int sPowerDoor = 0;
    public int sWelcomeFlag = 0;
    public int sAttemperation = 0;
    public int sPCI = 0;

    private int getCommandOn4() {
        int value = (0x01 & sDoorNum);
        value = value | ((0x01 & sWLAN) << 1);
        value = value | ((0x01 & sKeyTouchVoice) << 2);
        value = value | ((0x01 & sKeyLock) << 3);
        value = value | ((0x01 & sPowerDoor) << 4);
        value = value | ((0x01 & sWelcomeFlag) << 5);
        value = value | ((0x01 & sAttemperation) << 6);
        value = value | ((0x01 & sPCI) << 7);
        return value;
    }

    private int getCommandOff4() {
        int value = getCommandOn4();
        value ^= 0xFF;
        return value;
    }

    private int getCommandOn5() {
        return 0x00;
    }

    private int getCommandOff5() {
        return 0xFF;
    }

    public char[] getDatas() {
        datas[0] = (char) getCommandOn1();
        datas[1] = (char) getCommandOn2();
        datas[2] = (char) getCommandOn3();
        datas[3] = (char) getCommandOn4();
        datas[4] = (char) getCommandOn5();
        datas[5] = (char) getCommandOff1();
        datas[6] = (char) getCommandOff2();
        datas[7] = (char) getCommandOff3();
        datas[8] = (char) getCommandOff4();
        datas[9] = (char) getCommandOff5();
        return datas;
    }

    @Override
    public String toString() {
        return "UartActionSettingData:" + String.format("%02x", getCommandOn1())
                + " " + String.format("%02x", getCommandOn2())
                + " " + String.format("%02x", getCommandOn3())
                + " " + String.format("%02x", getCommandOn4())
                + " " + String.format("%02x", getCommandOn5());
    }
}
