package com.oliver.sdk.util;

import android.os.Build;

/**
 * author : Oliver
 * date   : 2018/9/26
 * desc   :
 */

public class SysUtil {

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    public static boolean isMinVersionM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isMinVersionP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static String getSystemSerial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }
}
