package com.oliver.wififragment.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.wififragment.anno.ItemType;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class AccessPoint extends WifiHotspot implements MultiItemEntity {

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

    @ItemType
    public int getItemLayoutType() {
        return itemLayoutType;
    }

    public void setItemLayoutType(@ItemType int itemLayoutType) {
        this.itemLayoutType = itemLayoutType;
    }

    @Override
    public String toString() {
        return get2String()
                + "AccessPoint{" +
                "itemLayoutType=" + itemLayoutType +
                '}';
    }
}
