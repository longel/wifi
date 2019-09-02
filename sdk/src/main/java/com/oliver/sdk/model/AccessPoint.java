package com.oliver.sdk.model;

import android.net.wifi.ScanResult;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class AccessPoint extends WifiHotspot implements MultiItemEntity {

    public static final int LAYOUT_TYPE_HEADER = 1;
    public static final int LAYOUT_TYPE_INACTIVE = 2;
    public static final int LAYOUT_TYPE_ACTIVE = 3;

    private int itemLayoutType;
    private boolean isWifiEnable; // 只有ItemType为LAYOUT_TYPE_HEADER的才会赋值

    public boolean hasWifiEnable() {
        return isWifiEnable;
    }

    public void setWifiEnable(boolean wifiEnable) {
        isWifiEnable = wifiEnable;
    }

    @Override
    public int getItemType() {
        return itemLayoutType;
    }

    public void setItemLayoutType(int itemLayoutType) {
        this.itemLayoutType = itemLayoutType;
    }

    @Override
    public void onWifiHotspotCreated(ScanResult scanResult) {
        super.onWifiHotspotCreated(scanResult);
        if (isConnected() || isConnecting()) {
            setItemLayoutType(LAYOUT_TYPE_ACTIVE);
        } else {
            setItemLayoutType(LAYOUT_TYPE_INACTIVE);
        }
    }

    @Override
    public String toString() {
        return get2String()
                + "AccessPoint{" +
                "itemLayoutType=" + itemLayoutType +
                '}';
    }
}
