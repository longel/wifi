package com.oliver.sdk.action;

import com.oliver.sdk.model.WifiHotspot;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class ScanResultAction {

    private List<WifiHotspot> datas;

    public List<WifiHotspot> getDatas() {
        return datas;
    }

    public void setDatas(List<WifiHotspot> datas) {
        this.datas = datas;
    }

}
