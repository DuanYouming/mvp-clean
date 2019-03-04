package com.foxconn.bandon.setting.wifi.model;

import android.support.annotation.NonNull;
public class WifiDevice implements Comparable<WifiDevice> {


    private String BSSID;
    private String SSID;
    private String capabilities;
    private int level;
    private boolean isConnected;
    private boolean isOpen;
    private String password;

    public WifiDevice(String BSSID, String SSID, String capabilities, int level, boolean isConnected, boolean isOpen, String password) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.capabilities = capabilities;
        this.level = level;
        this.isConnected = isConnected;
        this.isOpen = isOpen;
        this.password = password;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public int getLevel() {
        return level;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int compareTo(@NonNull WifiDevice o) {

        if (this.isConnected != o.isConnected) {
            return isConnected ? -1 : 1;
        } else if (this.level != o.level) {
            return -(Integer.compare(this.level, o.level));
        } else {
            return this.SSID.compareTo(o.SSID);
        }
    }



}
