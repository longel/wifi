package com.oliver.sdk.constant;

import android.net.wifi.WifiManager;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class Global {

    /*加密类型*/
    public static final int NONE = 0;
    public static final int WEP = 1;
    public static final int WPA = 2;

    /*连接状态*/
    public static final int DISCONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;

    /*信号强度*/
    public static final int WEAK = 0;
    public static final int KIND = 1;
    public static final int NICE = 2;
    public static final int BEST = 3;


    /*Wifi状态*/
    public static final int WIFI_STATE_DISABLING = WifiManager.WIFI_STATE_DISABLING;
    public static final int WIFI_STATE_DISABLED = WifiManager.WIFI_STATE_DISABLED;
    public static final int WIFI_STATE_ENABLING = WifiManager.WIFI_STATE_ENABLING;
    public static final int WIFI_STATE_ENABLED = WifiManager.WIFI_STATE_ENABLED;


    /*密码正则规则*/
    public static final String PASSWORD_MATCHER = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()=-+_;',./?><|[]{}";

}
