package com.oliver.sdk.event;

import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;

/**
 * author : Oliver
 * date   : 2019/8/23
 * desc   :
 */

public class WifiEvent {

    public static final int NO_STATE_UPDATE = 0;
    public static final int WIFI_STATE_UPDATE = 0x01;
    public static final int SUPPLICANT_STATE_UPDATE = 0x02;
    public static final int SCAN_RESULT_UPDATE = 0x04;
    public static final int NETWORK_STATE_UPDATE = 0x08;
    public static final int RSSI_UPDATE = 0x10;

    private int mUpdateId = NO_STATE_UPDATE;
    private boolean enable;
    private boolean connect;
    private int ssid;
    private int mWifiState;
    private int mSupplicantState;
    private WifiInfo mWifiInfo;
    private NetworkInfo mNetworkInfo;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public int getSsid() {
        return ssid;
    }

    public void setSsid(int ssid) {
        this.ssid = ssid;
    }

    public int getWifiState() {
        return mWifiState;
    }

    public static int getRssiUpdate() {
        return RSSI_UPDATE;
    }

    public int getSupplicantState() {
        return mSupplicantState;
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        mNetworkInfo = networkInfo;
    }

    public void setWifiState(int wifiState) {
        mWifiState = wifiState;
        mUpdateId = WIFI_STATE_UPDATE;
    }

    public void setSupplicantState(int supplicantState) {
        mSupplicantState = supplicantState;
        mUpdateId = SUPPLICANT_STATE_UPDATE;
    }

    public void setWifiInfo(WifiInfo wifiInfo) {
        mWifiInfo = wifiInfo;
    }

    public void setScanResultState(boolean isUpdate) {
        mUpdateId = SCAN_RESULT_UPDATE;
    }

    public void setNetworkState(NetworkInfo networkInfo) {
        mNetworkInfo = networkInfo;
        mUpdateId = NETWORK_STATE_UPDATE;
    }

    public void setRssiState() {
        mUpdateId = RSSI_UPDATE;
    }

    public int getUpdateId() {
        return mUpdateId;
    }

    @Override
    public String toString() {
        return "WifiStateEvent{" +
                "mUpdateId=" + mUpdateId +
                ", enable=" + enable +
                ", connect=" + connect +
                ", ssid=" + ssid +
                ", mWifiState=" + mWifiState +
                ", mSupplicantState=" + mSupplicantState +
                ", mWifiInfo=" + mWifiInfo +
                ", mNetworkInfo=" + mNetworkInfo +
                '}';
    }
}
