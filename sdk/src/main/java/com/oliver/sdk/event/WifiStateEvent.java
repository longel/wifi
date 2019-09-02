package com.oliver.sdk.event;

import com.oliver.sdk.annotations.WifiState;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class WifiStateEvent {

    private int state;

    @WifiState
    public int getState() {
        return state;
    }

    public void setState(@WifiState int state) {
        this.state = state;
    }
}
