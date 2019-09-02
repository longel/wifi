package com.oliver.sdk.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.oliver.sdk.constant.Global.WIFI_STATE_DISABLED;
import static com.oliver.sdk.constant.Global.WIFI_STATE_DISABLING;
import static com.oliver.sdk.constant.Global.WIFI_STATE_ENABLED;
import static com.oliver.sdk.constant.Global.WIFI_STATE_ENABLING;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

@IntDef({WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLING, WIFI_STATE_ENABLED})
@Retention(RetentionPolicy.SOURCE)
public @interface WifiState {
}
