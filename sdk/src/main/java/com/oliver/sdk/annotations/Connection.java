package com.oliver.sdk.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.oliver.sdk.constant.Global.CONNECTED;
import static com.oliver.sdk.constant.Global.CONNECTING;
import static com.oliver.sdk.constant.Global.DISCONNECTED;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

@IntDef({DISCONNECTED, CONNECTING, CONNECTED})
@Retention(RetentionPolicy.SOURCE)
public @interface Connection {
}
