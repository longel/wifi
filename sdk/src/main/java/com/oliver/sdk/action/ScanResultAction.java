package com.oliver.sdk.action;

import com.oliver.sdk.model.WifiHotspot;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class ScanResultAction<T extends WifiHotspot> {

    private List<T> datas;

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public T create() {
        return null;
    }
}
