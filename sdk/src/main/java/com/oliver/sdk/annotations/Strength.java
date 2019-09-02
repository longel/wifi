package com.oliver.sdk.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.oliver.sdk.constant.Global.BEST;
import static com.oliver.sdk.constant.Global.KIND;
import static com.oliver.sdk.constant.Global.NICE;
import static com.oliver.sdk.constant.Global.WEAK;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

@IntDef({WEAK, KIND, NICE, BEST})
@Retention(RetentionPolicy.SOURCE)
public @interface Strength {
}
