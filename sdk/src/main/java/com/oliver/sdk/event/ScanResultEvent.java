package com.oliver.sdk.event;

import com.oliver.sdk.model.AccessPoint;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class ScanResultEvent {

    private List<AccessPoint> datas;

    public List<AccessPoint> getDatas() {
        return datas;
    }

    public void setDatas(List<AccessPoint> datas) {
        this.datas = datas;
    }

}
