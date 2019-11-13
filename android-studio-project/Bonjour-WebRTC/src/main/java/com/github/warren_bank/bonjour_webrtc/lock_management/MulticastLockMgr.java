package com.github.warren_bank.bonjour_webrtc.lock_management;

import com.github.warren_bank.bonjour_webrtc.util.Util;

import android.content.Context;
import android.net.wifi.WifiManager;

public final class MulticastLockMgr {
    private static WifiManager.MulticastLock multicastLock;

    public static void acquire(Context context) {
        release();

        WifiManager wifiMgr = Util.getWifiManager(context);
        multicastLock = wifiMgr.createMulticastLock(
            "MulticastLock"
        );
        multicastLock.setReferenceCounted(false);
        multicastLock.acquire();
    }

    public static void release() {
        if (multicastLock != null) {
            multicastLock.release();
            multicastLock = null;
        }
    }
}
