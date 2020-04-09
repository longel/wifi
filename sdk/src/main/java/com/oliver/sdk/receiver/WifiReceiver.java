package com.oliver.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.oliver.sdk.event.WifiEvent;
import com.oliver.sdk.util.SysUtil;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    public static final String TAG = WifiReceiver.class.getSimpleName();
    private int mLastWifiState = -1;
    private List<OnWifiStateChangeListener> mOnWifiStateChangeListeners;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //        LogUtils.d(TAG, "onReceiver action = " + action);
        if (TextUtils.isEmpty(action)) {
            return;
        }
        WifiEvent stateEvent = new WifiEvent();
        switch (action) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION: //WiFi连接状态发生改变
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (mLastWifiState != wifiState) {
                    mLastWifiState = wifiState;
                    stateEvent.setWifiState(wifiState);
                }
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                int supplicantState = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -100);
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    stateEvent.setWifiInfo(wifiInfo);
                }
                if (supplicantState == WifiManager.ERROR_AUTHENTICATING) { // 密码错误
                    stateEvent.setSupplicantState(supplicantState);
                }

                break;
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                boolean isUpdate = true;
                if (SysUtil.isMinVersionM()) {
                    isUpdate = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                }
                stateEvent.setScanResultState(isUpdate);
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                stateEvent.setNetworkState(info);
                break;
            case WifiManager.RSSI_CHANGED_ACTION:
                stateEvent.setRssiState();
                break;
        }
        if (mOnWifiStateChangeListeners != null) {
            for (OnWifiStateChangeListener listener : mOnWifiStateChangeListeners) {
                listener.onWifiStateChange(stateEvent);
            }
        }
    }

    public void addOnWifiStateChangeListener(OnWifiStateChangeListener listener) {
        if (mOnWifiStateChangeListeners == null) {
            mOnWifiStateChangeListeners = new ArrayList<>();
            mOnWifiStateChangeListeners.add(listener);
            return;
        }
        if (!mOnWifiStateChangeListeners.contains(listener)) {
            mOnWifiStateChangeListeners.add(listener);
        }
    }
    public void removeOnWifiStateChangeListener(OnWifiStateChangeListener listener) {
        if (mOnWifiStateChangeListeners != null && mOnWifiStateChangeListeners.contains(listener)) {
            mOnWifiStateChangeListeners.remove(listener);
        }
    }


    public interface OnWifiStateChangeListener {
        void onWifiStateChange(WifiEvent stateEvent);
    }
}
