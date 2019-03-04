package com.foxconn.bandon.setting.wifi.util;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.tinker.BandonApplication;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiUtils {
    private static String TAG = WifiUtils.class.getSimpleName();
    private static int mNumOpenNetworksKept = Settings.Secure.getInt(BandonApplication.getInstance().getContentResolver(), Settings.Global.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
    private static final String BSSID_ANY = "any";
    public static final ConfigurationSecurities ConfigSec = ConfigurationSecurities.newInstance();

    public static boolean changePasswordAndConnect(final android.net.wifi.WifiManager wifiMgr, final WifiConfiguration config, final String newPassword) {
        ConfigSec.setupSecurity(config, ConfigSec.getWifiConfigurationSecurity(config), newPassword);
        final int networkId = wifiMgr.updateNetwork(config);
        if (networkId == -1) {
            LogUtils.d(TAG, "updateNetwork fail");
            return false;
        }
        wifiMgr.disconnect();
        return connectToConfiguredNetwork(wifiMgr, config);
    }

    public static boolean connectToNewNetwork(final android.net.wifi.WifiManager wifiMgr, final WifiDevice device) {
        final String security = ConfigSec.getScanResultSecurity(device.getCapabilities());
        if (ConfigSec.isOpenNetwork(security)) {
            checkForExcessOpenNetworkAndSave(wifiMgr, mNumOpenNetworksKept);
        }

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = convertToQuotedString(device.getSSID());
        config.BSSID = device.getBSSID();
        ConfigSec.setupSecurity(config, security, device.getPassword());
        int id = -1;
        try {
            id = wifiMgr.addNetwork(config);
        } catch (NullPointerException e) {
            LogUtils.e(TAG, "NullPointerException:" + e.toString());
        }
        if (id == -1) {
            LogUtils.e(TAG, "network id is error");
            return false;
        }
        config = getWifiConfiguration(wifiMgr, config, security);
        return config != null && connectToConfiguredNetwork(wifiMgr, config);
    }

    public static boolean connectToConfiguredNetwork(final android.net.wifi.WifiManager wifiMgr, WifiConfiguration config) {
        return wifiMgr.enableNetwork(config.networkId, false) && wifiMgr.reassociate();
    }

    public static boolean disConnectCurrent(final android.net.wifi.WifiManager manager) {
        String currentSSID = manager.getConnectionInfo().getSSID();
        LogUtils.d(TAG, "current SSID:" + currentSSID);
        List<WifiConfiguration> configurations = manager.getConfiguredNetworks();
        for (WifiConfiguration configuredConfig : configurations) {
            if (currentSSID.equals(configuredConfig.SSID)) {
                return manager.disableNetwork(configuredConfig.networkId);
            }
        }
        return false;
    }

    public static String getCurrentSsid(android.net.wifi.WifiManager manager) {
        WifiInfo info = manager.getConnectionInfo();
        if (null != info && null != info.getBSSID()) {
            return info.getSSID().replace("\"", "");
        }
        return null;
    }

    public static void autoConnect(final android.net.wifi.WifiManager manager) {
        List<WifiConfiguration> configurations = manager.getConfiguredNetworks();
        if (null != configurations && configurations.size() > 0) {
            int id = configurations.get(0).networkId;
            manager.enableNetwork(id, false);
        }
    }


    public static WifiConfiguration getWifiConfiguration(final android.net.wifi.WifiManager wifiMgr, final WifiDevice device, String hotspotSecurity) {
        final String ssid = convertToQuotedString(device.getSSID());
        if (ssid.length() == 0) {
            return null;
        }

        final String bssid = device.getBSSID();
        if (bssid == null) {
            return null;
        }

        if (hotspotSecurity == null) {
            hotspotSecurity = ConfigSec.getScanResultSecurity(device.getCapabilities());
        }

        final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
        if (configurations == null) {
            return null;
        }

        for (final WifiConfiguration config : configurations) {
            if (config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if (config.BSSID == null || BSSID_ANY.equals(config.BSSID) || bssid.equals(config.BSSID)) {
                final String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
                if (hotspotSecurity.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }


    public static List<WifiDevice> filtrate(WifiManager manager, List<ScanResult> results) {
        List<WifiDevice> newResults = new ArrayList<>();
        for (ScanResult result : results) {
            if (!TextUtils.isEmpty(result.SSID) && WifiManager.calculateSignalLevel(result.level, 4) >= 0) {
                boolean isConnected = TextUtils.equals(getConnectedName(manager), result.BSSID);
                String security = ConfigSec.getScanResultSecurity(result.capabilities);
                boolean isOpen = ConfigSec.isOpenNetwork(security);
                WifiDevice device = new WifiDevice(result.BSSID, result.SSID, result.capabilities, result.level, isConnected, isOpen, null);
                newResults.add(device);
            }
        }
        Collections.sort(newResults);
        return newResults;
    }

    public static int getCurrentWifiLevel(android.net.wifi.WifiManager manager) {
        WifiInfo info = manager.getConnectionInfo();
        if (null != info && null != info.getBSSID()) {
            return android.net.wifi.WifiManager.calculateSignalLevel(info.getRssi(), 4);
        }
        return 0;
    }

    private static void checkForExcessOpenNetworkAndSave(final android.net.wifi.WifiManager wifiMgr, final int numOpenNetworksKept) {
        final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
        sortByPriority(configurations);
        int tempCount = 0;
        for (int i = configurations.size() - 1; i >= 0; i--) {
            final WifiConfiguration config = configurations.get(i);
            if (ConfigSec.isOpenNetwork(ConfigSec.getWifiConfigurationSecurity(config))) {
                tempCount++;
                if (tempCount >= numOpenNetworksKept) {
                    wifiMgr.removeNetwork(config.networkId);
                }
            }
        }
    }

    private static void sortByPriority(final List<WifiConfiguration> configurations) {
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configurations.sort(Comparator.comparingInt(object -> object.priority));
        } else {
            Collections.sort(configurations, (o1, o2) -> o1.priority >= o2.priority ? 0 : 1);
        }*/
        Collections.sort(configurations, (o1, o2) -> o1.priority >= o2.priority ? 0 : 1);
    }

    private static WifiConfiguration getWifiConfiguration(final android.net.wifi.WifiManager wifiMgr, final WifiConfiguration configToFind, String security) {
        final String ssid = configToFind.SSID;
        if (ssid.length() == 0) {
            return null;
        }
        final String bssid = configToFind.BSSID;

        if (security == null) {
            security = ConfigSec.getWifiConfigurationSecurity(configToFind);
        }

        final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();

        for (final WifiConfiguration config : configurations) {
            if (config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if (config.BSSID == null || BSSID_ANY.equals(config.BSSID) || bssid == null || bssid.equals(config.BSSID)) {
                final String configSecurity = ConfigSec.getWifiConfigurationSecurity(config);
                if (security.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }

    private static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        final int lastPos = string.length() - 1;
        if (lastPos > 0 && (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }
        return "\"" + string + "\"";
    }

    private static String getConnectedName(android.net.wifi.WifiManager manager) {
        if (null != manager) {
            return manager.getConnectionInfo().getBSSID();
        }
        return null;
    }

}
