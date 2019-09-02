package com.oliver.sdk.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.oliver.sdk.constant.Global.NONE;
import static com.oliver.sdk.constant.Global.WEP;
import static com.oliver.sdk.constant.Global.WPA;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

@IntDef({NONE, WEP, WPA})
@Retention(RetentionPolicy.SOURCE)
public @interface Encryption {
}