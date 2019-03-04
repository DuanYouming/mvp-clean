package com.foxconn.bandon.uart.data.receive;

import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.PreferenceUtils;

public class UartStatusData extends UartReceiveData {
    private static final int SUB_0 = 0x0;
    private static final int SUB_1 = 0x1;

    public UartStatusData(int[] data) {
        super(data);
        init();
    }

    private void init() {
        int sub = getSubCommand();
        initSUB0();
        if (sub == SUB_0) {
            return;
        }
        initSUB1();
    }

    private void initSUB0() {
        initData1(data[3]);
        initData2(data[4]);
        initData3(data[5]);
        initData4(data[6]);
        initData5(data[7]);
        initData6(data[8]);
        initData7(data[9]);
        initData8(data[10]);
        initData9(data[11]);
    }

    private void initSUB1() {

    }

    public boolean fFrozen;
    public boolean qFrozen;
    public boolean hFrozen;
    public boolean pCI;
    public boolean deo;
    public boolean eco3;
    public boolean eco2;
    public boolean eco1;

    private void initData1(int value) {
        fFrozen = (value & 0x01) > 0;
        qFrozen = (value & (0x01 << 1)) > 0;
        hFrozen = (value & (0x01 << 2)) > 0;
        pCI = (value & (0x01 << 3)) > 0;
        deo = (value & (0x01 << 4)) > 0;
        eco3 = (value & (0x01 << 5)) > 0;
        eco2 = (value & (0x01 << 6)) > 0;
        eco1 = (value & (0x01 << 7)) > 0;
    }

    public boolean fineR;
    public boolean fineF;
    public boolean qIce;
    public boolean iceClean;
    public boolean iceLarge;
    public boolean iceStop;
    public boolean pIceH;
    public boolean pIceN;

    private void initData2(int value) {
        fineR = (value & (0x01)) > 0;
        fineF = (value & (0x01 << 1)) > 0;
        qIce = (value & (0x01 << 2)) > 0;
        iceClean = (value & (0x01 << 3)) > 0;
        iceLarge = (value & (0x01 << 4)) > 0;
        iceStop = (value & (0x01 << 5)) > 0;
        pIceH = (value & (0x01 << 6)) > 0;
        pIceN = (value & (0x01 << 7)) > 0;
    }

    private void initData3(int value) {

    }

    public boolean isDoorNumOn ;
    public boolean isWLANOn ;
    public boolean isKeyTouchVoiceOn ;
    public boolean isKeyLockOn ;
    public boolean isPowerDoorOn ;
    public boolean isWelcomeFlagOn ;
    public boolean isAttemperationOn ;
    public boolean isPCIOn ;

    private void initData4(int value) {
        isDoorNumOn = (value & (0x01)) > 0;
        isWLANOn = (value & (0x01 << 1)) > 0;
        isKeyTouchVoiceOn = (value & (0x01 << 2)) > 0;
        isKeyLockOn = (value & (0x01 << 3)) > 0;
        isPowerDoorOn = (value & (0x01 << 4)) > 0;
        isWelcomeFlagOn = (value & (0x01 << 5)) > 0;
        isAttemperationOn = (value & (0x01 << 6)) > 0;
        isPCIOn = (value & (0x01 << 7)) > 0;
    }

    public boolean cookTimerOn;
    public boolean alarmOn;
    public boolean bz05x3;
    public boolean bz1;
    public boolean doorBz3;
    public boolean doorBz1;
    public boolean lDoorOpen;
    public boolean rDoorOpen;

    private void initData5(int value) {
        cookTimerOn = (value & (0x01)) > 0;
        alarmOn = (value & (0x01 << 1)) > 0;
        bz05x3 = (value & (0x01 << 2)) > 0;
        bz1 = (value & (0x01 << 3)) > 0;
        doorBz3 = (value & (0x01 << 4)) > 0;
        doorBz1 = (value & (0x01 << 5)) > 0;
        lDoorOpen = (value & (0x01 << 6)) > 0;
        rDoorOpen = (value & (0x01 << 7)) > 0;
    }

    private void initData6(int value) {

    }

    public int fTemperature;

    private void initData7(int value) {
        fTemperature = (value & 0x1F);
        PreferenceUtils.setInt(BandonApplication.getInstance(), Constant.SP_SETTINGS, Constant.KEY_FRIDGE_TEMPERATURE, fTemperature);
    }

    public int rTemperature;
    private void initData8(int value) {
        rTemperature = (value & 0x1F);
        PreferenceUtils.setInt(BandonApplication.getInstance(), Constant.SP_SETTINGS, Constant.KEY_FREEZER_TEMPERATURE, rTemperature);
    }

    public int rDoorOpenTime;

    private void initData9(int value) {
        rDoorOpenTime = value;
        PreferenceUtils.setInt(BandonApplication.getInstance(), Constant.SP_SETTINGS, Constant.KEY_DOOR_OPENED_TIMES, rDoorOpenTime);
    }

    @Override
    public String toString() {
        return "UartStatusData{" +
                "fFrozen=" + fFrozen +
                ", qFrozen=" + qFrozen +
                ", hFrozen=" + hFrozen +
                ", pCI=" + pCI +
                ", deo=" + deo +
                ", eco3=" + eco3 +
                ", eco2=" + eco2 +
                ", eco1=" + eco1 +
                ", fineR=" + fineR +
                ", fineF=" + fineF +
                ", qIce=" + qIce +
                ", iceClean=" + iceClean +
                ", iceLarge=" + iceLarge +
                ", iceStop=" + iceStop +
                ", pIceH=" + pIceH +
                ", pIceN=" + pIceN +
                ", cookTimerOn=" + cookTimerOn +
                ", alarmOn=" + alarmOn +
                ", bz05x3=" + bz05x3 +
                ", bz1=" + bz1 +
                ", doorBz3=" + doorBz3 +
                ", doorBz1=" + doorBz1 +
                ", fDoorOpen=" + lDoorOpen +
                ", rDoorOpen=" + rDoorOpen +
                ", isAttemperationOn=" + isAttemperationOn +
                ", isDoorNumOn=" + isDoorNumOn +
                ", isKeyLockOn=" + isKeyLockOn +
                ", isKeyTouchVoiceOn=" + isKeyTouchVoiceOn +
                ", isPCIOn=" + isPCIOn +
                ", isPowerDoorOn=" + isPowerDoorOn +
                ", isWelcomeFlagOn=" + isWelcomeFlagOn +
                ", isWLANOn=" + isWLANOn +
                ", fTemperature=" + fTemperature +
                ", rTemperature=" + rTemperature +
                ", rDoorOpenTime=" + rDoorOpenTime +
                "} " + super.toString();
    }
}
