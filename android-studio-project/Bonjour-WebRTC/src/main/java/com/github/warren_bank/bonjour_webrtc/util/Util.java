package com.github.warren_bank.bonjour_webrtc.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public final class Util {

    public static WifiManager getWifiManager(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        return wifiMgr;
    }

    public static int getWlanIpAddress_int(Context context) {
        return getWlanIpAddress_int(context, getWifiManager(context));
    }

    public static int getWlanIpAddress_int(Context context, WifiManager wifiMgr) {
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = (wifiInfo != null)
          ? wifiInfo.getIpAddress()
          : 2130706433;  // '127.0.0.1'
        return ip;
    }

    public static String getWlanIpAddress_String(Context context) {
        return getWlanIpAddress_String(context, getWlanIpAddress_int(context));
    }

    public static String getWlanIpAddress_String(Context context, int ip) {
        String ipAddress = Formatter.formatIpAddress(ip);
        return ipAddress;
    }

    public static InetAddress getWlanIpAddress_InetAddress(Context context) throws UnknownHostException {
        return getWlanIpAddress_InetAddress(context, getWlanIpAddress_int(context));
    }

    public static InetAddress getWlanIpAddress_InetAddress(Context context, int ip) throws UnknownHostException {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.putInt(ip);
        buffer.position(0);
        byte[] bytes = new byte[4];
        buffer.get(bytes);
        return InetAddress.getByAddress(bytes);
    }
}
