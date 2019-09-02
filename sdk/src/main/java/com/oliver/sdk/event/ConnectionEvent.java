package com.oliver.sdk.event;


import com.oliver.sdk.annotations.Connection;

/**
 * author : Oliver
 * date   : 2019/8/23
 * desc   :
 */

public class ConnectionEvent {

    private int connectionState;

    @Connection
    public int getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(@Connection int state) {
        this.connectionState = state;
    }
}
