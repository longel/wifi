package com.oliver.sdk.model;

import android.net.wifi.ScanResult;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public interface IWifiHotspot {

    void onWifiHotspotCreated(ScanResult scanResult);
}
