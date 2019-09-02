package com.oliver.sdk.event;

import android.net.wifi.WifiInfo;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class SupplicantStateEvent {

    private WifiInfo mWifiInfo;

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public void setWifiInfo(WifiInfo wifiInfo) {
        mWifiInfo = wifiInfo;
    }
}
