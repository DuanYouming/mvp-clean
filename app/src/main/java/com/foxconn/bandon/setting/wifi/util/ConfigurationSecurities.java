package com.foxconn.bandon.setting.wifi.util;

import android.net.wifi.WifiConfiguration;


public abstract class ConfigurationSecurities {

    public abstract String getWifiConfigurationSecurity(WifiConfiguration wifiConfig);

    public abstract String getScanResultSecurity(String capabilities);

    public abstract void setupSecurity(WifiConfiguration config, String security, final String password);

    public abstract String getDisplaySecurityString(String capabilities);

    public abstract boolean isOpenNetwork(final String security);

    public static ConfigurationSecurities newInstance() {
        return new ConfigurationSecuritiesNew();
    }

}
