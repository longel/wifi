package com.oliver.sdk.action;

import com.oliver.sdk.model.AccessPoint;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class ScanAction extends ScanResultAction<AccessPoint> {

    @Override
    public AccessPoint create() {
        return new AccessPoint();
    }
}
