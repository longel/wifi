package com.oliver.wifi;

import android.app.Application;

import com.oliver.sdk.WifiAdmin;

/**
 * author : Oliver
 * date   : 2019/8/23
 * desc   :
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WifiAdmin.init(this);
    }
}
