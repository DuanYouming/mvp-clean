package com.foxconn.bandon.setting.wifi.util;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;

public class ConfigurationSecuritiesNew extends ConfigurationSecurities {

    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;
    private static final int SECURITY_EAP = 3;

    enum PskType {
        UNKNOWN,
        WPA,
        WPA2,
        WPA_WPA2
    }

    private static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private static int getSecurity(String capabilities) {
        if (capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    @Override
    public String getWifiConfigurationSecurity(WifiConfiguration wifiConfig) {
        return String.valueOf(getSecurity(wifiConfig));
    }

    @Override
    public String getScanResultSecurity(String capabilities) {
        return String.valueOf(getSecurity(capabilities));
    }

    @Override
    public void setupSecurity(WifiConfiguration config, String security, String password) {
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        final int sec = security == null ? SECURITY_NONE : Integer.valueOf(security);
        final int passwordLen = password == null ? 0 : password.length();
        switch (sec) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (passwordLen != 0) {
                    if ((passwordLen == 10 || passwordLen == 26 || passwordLen == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (passwordLen != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

            case SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                break;

            default:
                break;
        }
    }

    private static PskType getPskType(String capabilities) {
        boolean wpa = capabilities.contains("WPA-PSK");
        boolean wpa2 = capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        } else {
            return PskType.UNKNOWN;
        }
    }

    @Override
    public String getDisplaySecurityString(final String capabilities) {
        final int security = getSecurity(capabilities);
        if (security == SECURITY_PSK) {
            switch (getPskType(capabilities)) {
                case WPA:
                    return "WPA";
                case WPA_WPA2:
                case WPA2:
                    return "WPA2";
                default:
                    return "?";
            }
        } else {
            switch (security) {
                case SECURITY_NONE:
                    return "OPEN";
                case SECURITY_WEP:
                    return "WEP";
                case SECURITY_EAP:
                    return "EAP";
            }
        }

        return "?";
    }

    @Override
    public boolean isOpenNetwork(String security) {
        return String.valueOf(SECURITY_NONE).equals(security);
    }

}
