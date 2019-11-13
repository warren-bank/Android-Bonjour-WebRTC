package com.github.warren_bank.bonjour_webrtc.lock_management;

import com.github.warren_bank.bonjour_webrtc.util.Util;

import android.content.Context;
import android.net.wifi.WifiManager;

public final class WifiLockMgr {
    private static WifiManager.WifiLock wifiLock;

    public static void acquire(Context context) {
        release();

        WifiManager wifiMgr = Util.getWifiManager(context);
        wifiLock = wifiMgr.createWifiLock(
            WifiManager.WIFI_MODE_FULL_HIGH_PERF,
            "WifiLock"
        );
        wifiLock.acquire();
    }

    public static void release() {
        if (wifiLock != null) {
            wifiLock.release();
            wifiLock = null;
        }
    }
}
